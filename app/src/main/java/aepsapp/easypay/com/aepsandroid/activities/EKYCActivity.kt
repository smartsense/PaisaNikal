package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.AadharDataEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.mantradevice.model.PidData
import aepsapp.easypay.com.aepsandroid.mantradevice.model.PidOptions
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import com.android.volley.VolleyLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_ekyc.*
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class EKYCActivity : AppCompatActivity() {

    var mobileNo: String? = null
    var aadharNo: String? = null
    var fingerData: String? = null
    var name: String? = null
    var isCustomerExists = false
    private var pidData: PidData? = null
    private var serializer: Serializer? = null
    private var devicePackageName = "com.mantra.rdservice"
    private var mLastClickTime: Long = 0
    private var MIN_CLICK_INTERVAL: Long = 5000
    lateinit var mAdView: AdView

    companion object {
        const val TAG = "EKYCActivity"
        val FINGERSCAN_CODE = 4
    }

    var deviceSelected = "MANTRA"
    //var devices = mutableListOf<DeviceEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ekyc)

        setSupportActionBar(ekyc_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        serializer = Persister()

        //getDeviceList()

        //MobileAds.initialize(this@EKYCActivity, getString(R.string.google_mob_adz))

        val adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        //val adSize = AdSize(300, 50)
        //adView.adSize = adSize
        //adView.adUnitId = "ca-app-pub-8528774229304942~3806772862"
        adView.adUnitId = getString(R.string.google_mob_adz_unit_id)

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        /*mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("onAdLoaded", "*****")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.e("onAdFailedToLoad", "*****" + errorCode.toString())
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("onAdOpened", "*****")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.e("onAdLeftApplication", "*****")
            }

            override fun onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.e("onAdClosed", "*****")
            }
        }*/

        ekyc_btnproceed.setOnClickListener {
            if (isValid) {
                val currentClickTime = SystemClock.uptimeMillis()
                val elapsedTime = currentClickTime - mLastClickTime
                mLastClickTime = currentClickTime
                if (elapsedTime <= MIN_CLICK_INTERVAL) {
                    return@setOnClickListener
                } else {
                    getRDHashData()
                }
            }
            /*  val intent = Intent(this@EKYCActivity, AadharDetailActivity::class.java)
              startActivity(intent)*/
        }

        ekyc_txttnc.setOnClickListener {
            val intent = Intent(this@EKYCActivity, TermsAndConditionActivity::class.java)
            startActivity(intent)
        }


        ekyc_device.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    }

    override fun onResume() {
        super.onResume()
        ekyc_edtmobile.addTextChangedListener(numberWatcher)
    }

    override fun onPause() {
        super.onPause()
        ekyc_edtmobile.removeTextChangedListener(numberWatcher)
    }

    private val numberWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length == 10) {
                searchCustomer(s.toString())
            } else {
                isCustomerExists = false
                ekyc_edtcustomer.setText("")
                ekyc_edtcustomer.isEnabled = true
                ekyc_layout_verified.visibility = View.GONE
                ekyc_aadhar_layout.visibility = View.VISIBLE
            }
        }

    }


    private fun searchCustomer(mobileNo: String) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_SCREEN))
            data.addProperty("cust_Mob", mobileNo)
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@EKYCActivity).request(Utils.generateURL(this@EKYCActivity, URLGenerator.URL_SEARCH_CUSTOMER), objCore, searchResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@EKYCActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val searchResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)

            isCustomerExists = true
            name = objData.get("SEDNER_FNAME").toString()
            name?.let {
                ekyc_edtcustomer.setText(it)
                ekyc_edtcustomer.isEnabled = false
            }
            val kycStatus = objData.optString("SENDER_CUSTTYPE")
            if (kycStatus != null && kycStatus.equals(AppConstants.TYPE_KYC)) {
                ekyc_layout_verified.visibility = View.VISIBLE
                ekyc_aadhar_layout.visibility = View.GONE
                ekyc_btnok.setOnClickListener {
                    ekyc_edtmobile.setText("")
                }
            } else {
                ekyc_layout_verified.visibility = View.GONE
                ekyc_aadhar_layout.visibility = View.VISIBLE
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@EKYCActivity, message, FancyToast.ERROR)
        }
    }


    private fun openMantraApp(wadh: String, pidOpt: String) {
        try {
            //val pidOption = if (!TextUtils.isEmpty(pidOpt)) pidOpt else PidOptions.getPIDOptions(wadh)
            val pidOption = PidOptions.getPIDOptions(wadh)
            if (pidOption != null) {
                android.util.Log.e("PidOptions", pidOption)
                val intent2 = Intent()
                intent2.setPackage(devicePackageName)
                intent2.action = "in.gov.uidai.rdservice.fp.CAPTURE"
                intent2.putExtra("PID_OPTIONS", pidOption)
                startActivityForResult(intent2, FINGERSCAN_CODE)
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
                            } catch (e: Exception) {
                                Utils.showAlert(this@EKYCActivity, getString(R.string.unable_capture))
                            }

                        }
                    }
                }
            }
        } else {
            Utils.showToast(this@EKYCActivity, getString(R.string.not_fingerprint_captured), FancyToast.WARNING)
        }
    }

    private fun getFingerData(data: String) {

        if (isValidResponse(data)) {
            fingerData = data
            getCustomerDetails(fingerData!!)
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
                        Utils.showAlert(this@EKYCActivity, respJson.get("errInfo").toString())
                        return false
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return true
    }

    private fun getCustomerDetails(fingerData: String) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_SCREEN))
            data.addProperty("CUSTOMER_AADHARNUMBER", aadharNo)
            data.addProperty("CUSTOMER_MOBILE", mobileNo)
            data.addProperty("DEVICE_DATAXML", fingerData)
            data.addProperty("DEVICE_CERTEXPIRYDATE", "")
            data.addProperty("DEVICE_FINGERDATA", "1~RightThumb")
            data.addProperty("DEVICE_HMACXML", "")
            data.addProperty("DEVICE_SERIALNUMBER", "")
            data.addProperty("DEVICE_SESSIONKEY", "")
            data.addProperty("DEVICE_TIMESTAMP", "")
            data.addProperty("DEVICE_TYPE", "1")
            data.addProperty("DEVICE_VERSIONNUMBER", "")

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@EKYCActivity).request(Utils.generateURL(this@EKYCActivity, URLGenerator.URL_GET_AADHAR_DETAILS), objCore, aadharResponse, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@EKYCActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val aadharResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)
            // Utils.showAlert(this@EKYCActivity, "" + jsonObj.toString())

            parseAadharData(objData.toString())
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@EKYCActivity, message, FancyToast.ERROR)

            // parseAadharData(aadharHardCoded)
        }
    }

    private fun parseAadharData(aadharData: String) {
        val gson = Gson()
        val aadharData = gson.fromJson<AadharDataEntity>(aadharData, AadharDataEntity::class.java)

        val intent = Intent(this@EKYCActivity, AadharDetailActivity::class.java)
        intent.putExtra(AppConstants.OBJ_AADHAR, aadharData)
        intent.putExtra(AppConstants.CUST_MOBILE, mobileNo)
        intent.putExtra(AppConstants.IS_CUSTOMER_EXISTS, isCustomerExists)
        startActivity(intent)
    }

    private val aadharHardCoded = """ {

    "AadhaarName": "Arun",
    "AadhaarPhoto": "data:image/jpeg;base64,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",
    "City": "Thiruvananthapuram",
    "ContactPerson": "S/O: C Brahu",
    "DOB": "25-09-1988",
    "District": "Thiruvananthapuram",
    "Email": "-",
    "Gender": "M",
    "HouseNo": "Anupam",
    "LandMark": "-",
    "Location": "Moonnankkuzhy P O",
    "Phone": "-",
    "PinCode": "695615",
    "PostOffice": "Vembayam",
    "State": "Kerala",
    "Street": "Mannayam",
    "SubDistrict": "Nedumangad",
    "AADHAR_VERIFICATIONCODE": 10950,
    "AadhaarNo": "744946042162",
    "ReqRefNum": "869807"

}"""


    private fun getRDHashData() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_SCREEN))
            data.addProperty("device_Id", deviceSelected)
            data.addProperty("cust_Mob", mobileNo)
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@EKYCActivity).request(Utils.generateURL(this@EKYCActivity, URLGenerator.URL_GET_RD_HASH), objCore, rdhashResponse, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@EKYCActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val rdhashResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)

            val wadh = objData.get("wadh").toString()
            val pidOptions = objData.get("pidOpt").toString()
            openMantraApp(wadh, pidOptions)

        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@EKYCActivity, message, FancyToast.ERROR)
        }
    }


    private val isValid: Boolean
        get() {
            mobileNo = ekyc_edtmobile.text.toString()
            if (TextUtils.isEmpty(mobileNo) || mobileNo!!.length < 10) {
                ekyc_edtmobile.error = getString(R.string.mobile_no)
                return false
            }
            val chare = mobileNo!![0]
            if (Character.getNumericValue(chare) in 0..4) {
                ekyc_edtmobile.error = getString(R.string.mobile_no_start_from)
                return false
            }

            name = ekyc_edtcustomer.text.toString()
            if (TextUtils.isEmpty(name)) {
                ekyc_edtcustomer.error = getString(R.string.enter_name)
                return false
            }

            aadharNo = ekyc_edtaadhar.text.toString()
            if (TextUtils.isEmpty(aadharNo) || aadharNo!!.length < 12) {
                ekyc_edtaadhar.error = getString(R.string.aadhar_valid_no)
                return false
            }


            if (!ekyc_chktnc.isChecked) {
                Utils.showAlert(this@EKYCActivity, getString(R.string.agree_tnc))
                return false
            }

            if (!Utils.isAppInstalled(this@EKYCActivity, devicePackageName)) {
                return false
            }

            return true
        }

    /* private fun getDeviceList() {
         try {
             val header = EPCoreEntity.EPHeader()
             header.ST = "AEPS"
             header.OP = "AEPS"
             header.AID = Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_AGENT_CODE)

             val data = JsonObject()
             data.addProperty("userId", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_USER_ID))
             data.addProperty("screenName", Preference.getStringPreference(this@EKYCActivity, AppConstants.PREF_SCREEN))
             val objCore = EPCoreEntity<JsonObject>()
             objCore.HEADER = header
             objCore.DATA = data

             VolleyJsonRequest.getInstance(this@EKYCActivity).request(Utils.generateURL(URLGenerator.URL_DEVICE_LIST), objCore, deviceResp, true)
         } catch (e: JSONException) {
             Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
         } catch (e: InternetNotAvailableException) {
             Utils.showToast(this@EKYCActivity, getString(R.string.internet_not_available))
         }
     }

     private val deviceResp = object : VolleyJsonRequest.OnJsonResponse {
         override fun responseReceived(jsonObj: JSONObject) {
             val data = jsonObj.getString(AppConstants.KEY_DATA)
             val deviceArray = JSONArray(data.replace("\\", ""))

             if (deviceArray.length() > 0) {
                 var mantraPos = 0
                 for (i in 0 until deviceArray.length()) {
                     val objDevice = deviceArray.getJSONObject(i)
                     devices.add(DeviceEntity(objDevice.getString("id"), objDevice.getString("name")))
                     if (objDevice.getString("name").equals("mantra", true))
                         mantraPos = i
                 }
                 ekyc_device.adapter = ArrayAdapter<DeviceEntity>(this@EKYCActivity, R.layout.spinner_item, devices)
                 ekyc_device.setSelection(mantraPos)
             }
         }

         override fun errorReceived(code: Int, message: String) {
             Utils.showToast(this@EKYCActivity, message)
         }
     }*/
}
