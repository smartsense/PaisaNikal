package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.AepsAddDialog
import aepsapp.easypay.com.aepsandroid.entities.BankDetailEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.android.volley.VolleyLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_aeps.*
import org.json.JSONException
import org.json.JSONObject

class AepsActivity : AppCompatActivity() {

    private var bankIIN: String? = null
    private var aadharNo: String? = null
    private var mobileNo: String? = null
    private var amount: Double = 0.0
    private var name: String? = null
    private var customerName: String? = null
    private var selectedBank: BankDetailEntity? = null
    //private var objService: ServiceEntity? = null
    private var serviceType: String? = null
    lateinit var mAdView: AdView

    companion object {
        const val SEARCH_BANK = 2
        const val TAG = "AepsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aeps)

        setSupportActionBar(aeps_toolbar)

        serviceType = getString(R.string.type_balanceinfo)
        //objService = intent.getSerializableExtra(AppConstants.OBJ_SERVICE) as ServiceEntity?
        /*   objService?.let {
               supportActionBar!!.title = it.subcategory
               if (it.serviceType.equals(AppConstants.TYPE_BALANCEINFO)) {

               }
           }*/

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //MobileAds.initialize(this@AepsActivity, getString(R.string.google_mob_adz))

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

        aeps_txttnc.paintFlags = aeps_txttnc.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        aeps_txttnc.setOnClickListener {
            startActivity(Intent(this@AepsActivity, TermsAndConditionActivity::class.java))
        }
        aeps_txtaddcustomer.setOnClickListener {
            val dialog = AepsAddDialog()
            dialog.show(supportFragmentManager, "add")
        }

        aeps_btnproceed.setOnClickListener {
            if (aeps_btnproceed.isEnabled) {
                if (isValid) {
                    if (TextUtils.isEmpty(customerName?.trim())) {
                        aeps_btnproceed.isEnabled = false
                        addCustomer()
                    } else {
                        proceedFurther()
                        aeps_btnproceed.isEnabled = false
                    }
                }
            }
        }

        aepssearch_edtbank.setOnClickListener {
            val intent = Intent(this@AepsActivity, SearchBankActivity::class.java)
            startActivityForResult(intent, SEARCH_BANK)
        }

        aeps_txttnc.setOnClickListener {
            val intent = Intent(this@AepsActivity, TermsAndConditionActivity::class.java)
            startActivity(intent)
        }


        getAgentLimit()

        aeps_balance.setOnCheckedChangeListener { buttonView, isChecked ->
            aepssearch_edtmobile.requestFocus()
            aepssearch_edtamount.setText("")
            aepssearch_edtamount.isEnabled = !isChecked
            serviceType = if (isChecked) getString(R.string.type_balanceinfo) else getString(R.string.type_widthdrawal)
        }

        aeps_balance.isChecked = true


    }

    @Synchronized
    private fun getAgentLimit() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_AGENT_CODE)
            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_SCREEN))
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data
            VolleyJsonRequest.getInstance(this@AepsActivity).request(Utils.generateURL(this@AepsActivity, URLGenerator.URL_AEPS_LIMIT), objCore, limitResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AepsActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val limitResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getString("RESP_CODE").equals("200")) {
                val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)
                if (objData.has("effectiveBalance")) {
                    val balance = objData.getDouble("effectiveBalance")
                    aeps_toolbar.setSubtitle(String.format(getString(R.string.aeps_balance), Utils.formatAmount(balance)))
                }
            }
        }

        override fun errorReceived(code: Int, message: String) {
            //Utils.showToast(this@AepsActivity, message)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == SEARCH_BANK) {
                selectedBank = data.getSerializableExtra(AppConstants.OBJ_BANK) as BankDetailEntity?
                selectedBank?.let {
                    aepssearch_edtbank.setText(it.bankName)
                }
            }
        }
    }

    private fun addCustomer() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_SCREEN))
            data.addProperty("cust_Mob", mobileNo)
            data.addProperty("cust_Name", name)
            data.addProperty("cust_isAgree", aeps_chktnc.isChecked)
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@AepsActivity).request(Utils.generateURL(this@AepsActivity, URLGenerator.URL_ADD_CUSTOMER), objCore, addResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AepsActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }


    private val addResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            proceedFurther()
        }

        override fun errorReceived(code: Int, message: String) {
            // Utils.showToast(this@AepsActivity, message)
            Log.e(TAG, message)
            proceedFurther()
        }
    }

    private fun proceedFurther() {
        val intent = Intent(this@AepsActivity, FingerprintScanActivity::class.java)
        intent.putExtra(AppConstants.AADHAR_NO, aadharNo)
        intent.putExtra(AppConstants.OBJ_BANK, selectedBank)
        intent.putExtra(AppConstants.MOBILE_NO, mobileNo)
        intent.putExtra(AppConstants.AMOUNT, amount)
        intent.putExtra(AppConstants.TYPE_SERVICE, serviceType)
        startActivity(intent)
        aeps_btnproceed.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        aepssearch_edtmobile.addTextChangedListener(numberWatcher)
    }

    override fun onPause() {
        super.onPause()
        aepssearch_edtmobile.removeTextChangedListener(numberWatcher)
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
                aepssearch_edtname.isEnabled = true
                aepssearch_edtname.setText("")
            }
        }
    }

    private val isValid: Boolean
        get() {
            mobileNo = aepssearch_edtmobile.text.toString()
            if (TextUtils.isEmpty(mobileNo!!.trim()) || mobileNo!!.length < 10) {
                aepssearch_edtmobile.error = getString(R.string.enter_mobile)
                return false
            }
            val chare = mobileNo!![0]
            if (Character.getNumericValue(chare) in 0..4) {
                aepssearch_edtmobile.requestFocus()
                aepssearch_edtmobile.error = getString(R.string.mobile_no_start_from)
                return false
            }
            name = aepssearch_edtname.text.toString()
            if (TextUtils.isEmpty(name!!.trim())) {
                aepssearch_edtname.error = getString(R.string.enter_name)
                return false
            }
            bankIIN = aepssearch_edtbank.text.toString()
            if (TextUtils.isEmpty(bankIIN)) {
                aepssearch_edtbank.error = getString(R.string.enter_bank)
                return false
            }
            aadharNo = aepssearch_edtaadhar.text.toString()
            if (TextUtils.isEmpty(aadharNo!!.trim()) || aadharNo!!.length < 12 || aadharNo!!.toLong() == 0L) {
                aepssearch_edtaadhar.error = getString(R.string.enter_aadhar)
                return false
            }
            if (!aeps_balance.isChecked) {

                val amountText = aepssearch_edtamount.text.toString()
                if (TextUtils.isEmpty(amountText.trim())) {
                    aepssearch_edtamount.error = getString(R.string.enter_amount)
                    return false
                } else
                    amount = amountText.toDouble()

                if (!(amount in 100..10000)) {
                    aepssearch_edtamount.error = getString(R.string.enter_min_amount)
                    return false
                }
            }
            if (!aeps_chktnc.isChecked) {
                Utils.showAlert(this@AepsActivity, getString(R.string.agree_tnc))
                return false
            }
            return true
        }

    private fun searchCustomer(mobileNo: String) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AepsActivity, AppConstants.PREF_SCREEN))
            data.addProperty("cust_Mob", mobileNo)
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@AepsActivity).request(Utils.generateURL(this@AepsActivity, URLGenerator.URL_SEARCH_CUSTOMER), objCore, searchResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AepsActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val searchResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)
            customerName = objData.get("SEDNER_FNAME").toString()
            customerName?.let {
                aepssearch_edtname.setText(it)
                aepssearch_edtname.isEnabled = false
            }
            val custDocRejected = objData.getBoolean("CUSTDOC_REJECT_FLAG")

        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@AepsActivity, message, FancyToast.ERROR)
        }
    }

}
