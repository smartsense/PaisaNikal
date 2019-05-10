package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.BankDetailEntity
import aepsapp.easypay.com.aepsandroid.entities.DeviceEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.entities.TransactionEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.mantradevice.model.PidData
import aepsapp.easypay.com.aepsandroid.mantradevice.model.PidOptions
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.android.volley.VolleyLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_fingerprint_scan.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class FingerprintScanActivity : AppCompatActivity() {

    private var serviceType: String? = null
    private val gson = Gson()
    private var devices = mutableListOf<DeviceEntity>()
    private var aadharNo: String? = null
    private var mobileNo: String? = null
    private var amount: Double = 0.0
    private var objBank: BankDetailEntity? = null
    private var fingerData: String? = null
    private var custCharge = 0.0
    private var pidData: PidData? = null
    private var serializer: Serializer? = null
    private var devicePackageName = "com.mantra.rdservice"
    private var mLastClickTime: Long = 0
    private var MIN_CLICK_INTERVAL: Long = 5000
    private var deviceSelected = "MANTRA"

    companion object {
        const val TAG = "FingerprintScanActivity"
        const val FINGERSCAN_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint_scan)

        setSupportActionBar(finger_toolbar)

        finger_toolbar.setNavigationOnClickListener { finish() }

        serializer = Persister()

        aadharNo = intent.getStringExtra(AppConstants.AADHAR_NO)
        objBank = intent.getSerializableExtra(AppConstants.OBJ_BANK) as BankDetailEntity?
        mobileNo = intent.getStringExtra(AppConstants.MOBILE_NO)
        amount = intent.getDoubleExtra(AppConstants.AMOUNT, 0.0)
        serviceType = intent.getStringExtra(AppConstants.TYPE_SERVICE)

        finger_txtaadhar.text = aadharNo
        finger_txtamount.text = Utils.formatAmount(amount)
        if (objBank != null && objBank!!.bankName != null)
            finger_txtbank.text = objBank!!.bankName
        finger_txtmobile.text = mobileNo

        //getDeviceList()
        finger_btnscan.setOnClickListener {
            if (!Utils.isAppInstalled(this@FingerprintScanActivity, devicePackageName)) {
                return@setOnClickListener
            }
            openMantraApp()
        }

        finger_spdevices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        devicePackageName = "com.mantra.rdservice"
                        deviceSelected = "MANTRA"
                    }
                    1 -> {
                        devicePackageName = "com.scl.rdservice"
                        deviceSelected = "MORPHO"
                    }
                    2 -> {
                        devicePackageName = "com.tatvik.bio.tmf20"
                        deviceSelected = "TATVIK"
                    }
                    else -> {
                        devicePackageName = "com.mantra.rdservice"
                        deviceSelected = "MANTRA"
                    }
                }
            }
        }

        finger_spdevices.visibility = View.VISIBLE
        kyc_lbldevice.visibility = View.VISIBLE
    }


    private fun openMantraApp() {
        try {
            val pidOption = PidOptions.getPIDOptions("")
            if (pidOption != null) {
                android.util.Log.e("PidOptions", pidOption)
                val intent2 = Intent()
                intent2.setPackage(devicePackageName)
                intent2.action = "in.gov.uidai.rdservice.fp.CAPTURE"
                intent2.putExtra("PID_OPTIONS", pidOption)
                startActivityForResult(intent2, EKYCActivity.FINGERSCAN_CODE)
            }
        } catch (e: Exception) {
            android.util.Log.e("Error", e.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                EKYCActivity.FINGERSCAN_CODE -> {
                    if (data != null) {
                        val result = data.getStringExtra("PID_DATA")
                        if (result != null) {
                            try {
                                pidData = serializer!!.read(PidData::class.java, result)
                                getFingerData(result)
                                finger_spdevices.visibility = View.GONE
                                kyc_lbldevice.visibility = View.GONE
                            } catch (e: Exception) {
                                finger_spdevices.visibility = View.VISIBLE
                                kyc_lbldevice.visibility = View.VISIBLE
                                Utils.showAlert(this@FingerprintScanActivity, getString(R.string.unable_capture))
                            }
                        }
                    }
                }
            }
        } else {
            Utils.showToast(this@FingerprintScanActivity, getString(R.string.not_fingerprint_captured), FancyToast.ERROR)
        }
    }

    private fun getDeviceList() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = serviceType
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_SCREEN))
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@FingerprintScanActivity).request(Utils.generateURL(this@FingerprintScanActivity, URLGenerator.URL_DEVICE_LIST), objCore, deviceResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@FingerprintScanActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val deviceResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val data = jsonObj.get(AppConstants.KEY_DATA).toString()
            val deviceArray = JSONArray(data.replace("\\", ""))

            if (deviceArray.length() > 0) {
                for (i in 0 until deviceArray.length()) {
                    val objDevice = deviceArray.getJSONObject(i)
                    devices.add(DeviceEntity(objDevice.getString("id"), objDevice.getString("name")))
                }
                finger_spdevices.adapter = ArrayAdapter<DeviceEntity>(this@FingerprintScanActivity, R.layout.spinner_item, devices)
                finger_spdevices.setSelection(deviceArray.length() - 1)
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@FingerprintScanActivity, message, FancyToast.ERROR)
        }
    }

    private fun getFingerData(data: String) {

        if (isValidResponse(data)) {
            fingerData = data
            finger_btnscan.text = getString(R.string.proceed)
            finger_btnscan.setOnClickListener {
                val currentClickTime = SystemClock.uptimeMillis()
                val elapsedTime = currentClickTime - mLastClickTime
                mLastClickTime = currentClickTime
                if (elapsedTime <= MIN_CLICK_INTERVAL) {
                    return@setOnClickListener
                } else {
                    getCustomerCharge()
                }
            }
        }
    }

    private fun isValidResponse(data: String): Boolean {
        try {
            val objJson = XML.toJSONObject(data)
            val pidJson = objJson.optJSONObject("PidData")
            if (pidJson != null) {
                val respJson = pidJson.optJSONObject("Resp")
                if (respJson != null) {
                    val code = respJson.optInt("errCode")
                    if (code > 0) {
                        Utils.showAlert(this@FingerprintScanActivity, respJson.getString("errInfo"))
                        return false
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return true
    }

    private fun getCustomerCharge() {
        try {
            custCharge = 0.0

            val header = EPCoreEntity.EPHeader()
            header.ST = serviceType
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_AGENT_CODE)
            header.TXN_AMOUNT = amount

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_SCREEN))
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@FingerprintScanActivity).request(Utils.generateURL(this@FingerprintScanActivity, URLGenerator.URL_CUSTOMER_CHARGE), objCore, chargeResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@FingerprintScanActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val chargeResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val chargeArray = jsonObj.getJSONArray("CHRG_LIST")
            for (i in 0 until chargeArray.length()) {
                val objCharge = chargeArray.getJSONObject(i)
                custCharge += objCharge.getDouble("CHRG_VALUE")
            }

            if (custCharge > 0) {
                Utils.showAlert(this@FingerprintScanActivity, String.format(getString(R.string.charge_msg), Utils.formatAmount(custCharge), Utils.formatAmount(custCharge + amount)), "", View.OnClickListener {
                    callService()
                })
            } else {
                callService()
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@FingerprintScanActivity, message, FancyToast.ERROR)
        }
    }

    private fun callService() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = serviceType
            header.OP = "AEPS"
            header.TXN_AMOUNT = amount
            header.PAYABLE_AMOUNT = amount + custCharge
            header.AID = Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_AGENT_CODE)

            val objData = JsonObject()
            objData.addProperty("cust_Mob", mobileNo)
            objData.addProperty("userId", Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_USER_ID))
            objData.addProperty("screenName", Preference.getStringPreference(this@FingerprintScanActivity, AppConstants.PREF_SCREEN))
            if (objBank != null && objBank!!.bank_Iin != null)
                objData.addProperty("IIN", objBank!!.bank_Iin)
            else
                objData.addProperty("IIN", "")

            objData.addProperty("AadharNumber", aadharNo)
            objData.addProperty("isAgree", true)
            objData.addProperty("BiometricData", fingerData)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = objData

            val endPoint = when (serviceType) {
                AppConstants.TYPE_WITHDRAWAL -> URLGenerator.URL_WITHDRAWAL
                AppConstants.TYPE_DEPOSIT -> URLGenerator.URL_DEPOSIT
                AppConstants.TYPE_BALANCEINFO -> URLGenerator.URL_GET_BALANCE
                else -> URLGenerator.URL_GET_BALANCE
            }

            VolleyJsonRequest.getInstance(this@FingerprintScanActivity).request(Utils.generateURL(this@FingerprintScanActivity, endPoint), objCore, serviceResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@FingerprintScanActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val serviceResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {

            val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)

            val transaction = gson.fromJson<TransactionEntity>(objData.toString(), TransactionEntity::class.java)
            val intent = Intent(this@FingerprintScanActivity, StatusActivity::class.java)
            intent.putExtra(AppConstants.OBJ_TXN, transaction)
            intent.putExtra(AppConstants.MOBILE_NO, mobileNo)
            intent.putExtra(AppConstants.BANK_NAME, objBank!!.bankName)
            intent.putExtra(AppConstants.TXN_MSG, jsonObj.getString("RESP_MSG"))
            startActivity(intent)
            finish()
        }

        override fun errorReceived(code: Int, message: String) {
            finger_btnscan.text = getString(R.string.scan_finger)
            finger_btnscan.setOnClickListener({
                openMantraApp()
            })
            Utils.showToast(this@FingerprintScanActivity, message, FancyToast.ERROR)
        }
    }

}
