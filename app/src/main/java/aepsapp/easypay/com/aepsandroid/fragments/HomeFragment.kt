package aepsapp.easypay.com.aepsandroid.fragments

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.activities.AepsActivity
import aepsapp.easypay.com.aepsandroid.activities.FundTransferActivity
import aepsapp.easypay.com.aepsandroid.activities.RegisterSenderActivity
import aepsapp.easypay.com.aepsandroid.adapters.MenuAdapter
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.OTPDialog
import aepsapp.easypay.com.aepsandroid.entities.DmtEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.entities.ServiceEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.VolleyLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    private val gson = Gson()
    lateinit var mAdView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        home_recycler.layoutManager = GridLayoutManager(activity, 2) as RecyclerView.LayoutManager?

        setMenuItems()

        //getServices()

        val logo = Preference.getStringPreference(activity!!, AppConstants.PREF_LOGO)
        if (!TextUtils.isEmpty(logo)) {
            home_imglogo.setImageURI(Utils.generateContextURL(activity!!, URLGenerator.IMAGE_PATH + URLEncoder.encode(logo, "UTF-8")))
        }

        val adView = AdView(activity)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = getString(R.string.google_mob_adz_unit_id)

        mAdView = activity!!.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }

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

    private fun setMenuItems() {
        val services = mutableListOf<ServiceEntity>()
        services.add(ServiceEntity(getString(R.string.aeps), R.drawable.ic_aeps))
        services.add(ServiceEntity(getString(R.string.imps_cap), R.drawable.ic_imps)) //first it was DMT
        services.add(ServiceEntity(getString(R.string.neft_cap), R.drawable.ic_neft))
        //disable temporarily
        //services.add(ServiceEntity(getString(R.string.e_keyc), R.drawable.ic_ekyc))
        home_recycler.adapter = MenuAdapter(activity!!, services) { position ->
            when (position) {
                0 -> {
                    val intent = Intent(activity, AepsActivity::class.java)
                    intent.putExtra(AppConstants.OBJ_SERVICE, services[position])
                    startActivity(intent)
                }
                //disable temporarily
                /*1 -> {
                    val intent = Intent(activity, EKYCActivity::class.java)
                    intent.putExtra(AppConstants.OBJ_SERVICE, services[position])
                    startActivity(intent)
                }*/
                1 -> {
                    //imps
                    Preference.savePreference(activity!!, AppConstants.PREF_OP, "EPDMTNUR")
                    showDialog(services[position])
                }
                2 -> {
                    //neft
                    Preference.savePreference(activity!!, AppConstants.PREF_OP, "DMTNUR")
                    showDialog(services[position])
                }
            }
        }
    }

    var _alertDialog: AlertDialog? = null
    private fun showDialog(services: ServiceEntity) {

        val dialogBuilder = AlertDialog.Builder(activity)
        // ...Irrelevant code for customizing the buttons and title
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_label_editor, null)
        dialogBuilder.setView(dialogView)

        _alertDialog = dialogBuilder.create()
        _alertDialog!!.setCancelable(false)
        //alertDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _alertDialog!!.show()

        val editText = dialogView.findViewById(R.id.edtmobile) as EditText
        val btnSubmit = dialogView.findViewById(R.id.btnSubmit) as Button
        val btncancel = dialogView.findViewById(R.id.btncancel) as ImageView
        val newSender = dialogView.findViewById(R.id.newSender) as TextView

        val str = "New Sender? <font color=\"#3F51B5\">Click here</font>"
        newSender.setText(Html.fromHtml(str))

        newSender.setOnClickListener {
            _alertDialog!!.dismiss()
            Preference.savePreference(activity!!, AppConstants.MOBILE_NO_DMT, "")
            val i = Intent(activity, RegisterSenderActivity::class.java)
            startActivity(i)
        }

        btncancel.setOnClickListener { _alertDialog!!.dismiss() }

        btnSubmit.setOnClickListener {
            val data = editText.text.toString()
            if (data != null && data.length == 10) {
                val firstChar: Char = data.get(0)
                if (!firstChar.toString().equals("0")) {
                    Preference.savePreference(activity!!, AppConstants.MOBILE_NO_DMT, data)
                    makeValidateCall(data)
                } else {
                    Preference.savePreference(activity!!, AppConstants.MOBILE_NO_DMT, "")
                    Toast.makeText(activity, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Preference.savePreference(activity!!, AppConstants.MOBILE_NO_DMT, "")
                Toast.makeText(activity, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var mobileNumber = ""

    private fun makeValidateCall(mobile: String) {
        try {
            mobileNumber = mobile
            Preference.savePreference(activity!!, AppConstants.PREF_ST, "REMDOMESTIC")
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", mobile)
            data.addProperty("userId", Preference.getStringPreference(activity!!, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(activity!!, AppConstants.PREF_SCREEN))

            val objCore = EPCoreEntity<JsonObject>()
            //objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_SEARCH_CUSTOMER_DMT), objCore, seachCustomerDmt, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private fun showRegistrationDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        // ...Irrelevant code for customizing the buttons and title
        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_label_editor, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        val textInputLayout = dialogView.findViewById(R.id.textInputLayout) as TextInputLayout
        val editText = dialogView.findViewById(R.id.edtmobile) as EditText
        val btnSubmit = dialogView.findViewById(R.id.btnSubmit) as Button
        val btncancel = dialogView.findViewById(R.id.btncancel) as ImageView
        val newSender = dialogView.findViewById(R.id.newSender) as TextView
        val txttitle = dialogView.findViewById(R.id.txttitle) as TextView
        val msgText = dialogView.findViewById(R.id.msgText) as TextView

        txttitle.setText("Sender Registration")
        textInputLayout.visibility = View.GONE
        newSender.visibility = View.GONE
        editText.visibility = View.GONE
        btnSubmit.setText("Register")
        msgText.visibility = View.VISIBLE
        msgText.setText("\n \t\tEntered Mobile No. is not registered. \n \t\tDo you want to register?")

        btncancel.setOnClickListener { alertDialog.dismiss() }
        btnSubmit.setOnClickListener {
            _alertDialog!!.dismiss()
            alertDialog.dismiss()
            val i = Intent(activity, RegisterSenderActivity::class.java)
            startActivity(i)
        }
    }

    private fun customVerificationDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        // ...Irrelevant code for customizing the buttons and title
        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_label_editor, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        val textInputLayout = dialogView.findViewById(R.id.textInputLayout) as TextInputLayout
        val editText = dialogView.findViewById(R.id.edtmobile) as EditText
        val btnSubmit = dialogView.findViewById(R.id.btnSubmit) as Button
        val btncancel = dialogView.findViewById(R.id.btncancel) as ImageView
        val newSender = dialogView.findViewById(R.id.newSender) as TextView
        val txttitle = dialogView.findViewById(R.id.txttitle) as TextView
        val msgText = dialogView.findViewById(R.id.msgText) as TextView

        textInputLayout.visibility = View.GONE
        txttitle.setText("Customer Verification")
        newSender.visibility = View.GONE
        editText.visibility = View.GONE
        btnSubmit.setText("Verify")
        msgText.visibility = View.VISIBLE
        msgText.setText("\n \t\tCustomer Verification is Pending. \n \t\tDo you want to continue with verification?")

        btncancel.setOnClickListener { alertDialog.dismiss() }
        btnSubmit.setOnClickListener {
            alertDialog.dismiss()
            showOtpDialog()
        }
    }

    private fun otpCall() {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", mobileNumber)
            data.addProperty("REQUEST_FOR", AppConstants.BENE_VERIFY)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_OTP_GENERATE_DMT), objCore, otpResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    var RESPONSE_CODE = 0

    private val otpResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                RESPONSE_CODE = jsonObj.getInt("RESPONSE_CODE")
                val RESP_MSG = jsonObj.getString("RESP_MSG")
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(activity!!, message)
        }
    }

    var otpDialog: OTPDialog? = null

    fun showOtpDialog() {
        otpCall()
        otpDialog = OTPDialog(object : OnOTPChange {
            override fun onResendClick() {
                otpCall()
            }

            override fun onOTPEntered(otp: String) {
                verifyOTP(otp)
            }

            override fun onCloseDialog() {

            }
        })
        val bundle = Bundle()
        otpDialog!!.arguments = bundle
        otpDialog!!.show(fragmentManager, "otp")
    }


    private fun verifyOTP(otp: String) {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("REQUEST_CODE", RESPONSE_CODE)
            data.addProperty("BENE_ID", "")
            data.addProperty("OTP", otp)
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_FOR", AppConstants.CUSTOMER_VERIFY)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_OTP_VERIFY_DMT), objCore, otpVerifyResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val otpVerifyResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val resCode = jsonObj.getInt("RESP_CODE")
            if (resCode == AppConstants.SUCCESS_DATA) {
                _alertDialog!!.dismiss()
                otpDialog!!.dismiss()
                makeValidateCall(Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))

            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(activity!!, message)
        }
    }

    private val seachCustomerDmt = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val resCode = jsonObj.getInt("RESP_CODE")
            if (resCode == AppConstants.SUCCESS_DATA) {
                _alertDialog!!.dismiss()
                val data = jsonObj.getJSONObject("DATA")
                val statusCustomer = data.getString("SENDER_CUSTTYPE")
                Preference.savePreference(activity!!, AppConstants.CUSTOMER_KYC_STATUS, statusCustomer)
                val gson = Gson()
                val data1 = gson.fromJson(data.toString(), DmtEntity::class.java)
                val intent = Intent(activity, FundTransferActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("dmtEntity", data1)
                intent.putExtras(bundle)
                startActivity(intent)
            } else if (resCode == AppConstants.SUCCESS_CUSTOMER_REGISTRATION) {
                showRegistrationDialog()
            } else if (resCode == AppConstants.SUCCESS_CUSTOMER_OTP_VERIFY) {
                customVerificationDialog()
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(activity!!, message)
        }
    }

    private fun getServices() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(activity!!, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(activity!!, AppConstants.PREF_SCREEN))

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_GET_SERVICES), objCore, serviceResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val serviceResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val objData = jsonObj.getJSONArray(AppConstants.KEY_DATA)
            val serviceType = object : TypeToken<List<ServiceEntity>>() {}.type

            var services = gson.fromJson<List<ServiceEntity>>(objData.toString(), serviceType)
            if (services != null && !services.isEmpty()) {
                services = services.filter { it.category?.toLowerCase() == "aeps" && it.status == 0 }
                /*   home_recycler.adapter = MenuAdapter(activity!!, services, { position ->
                       val intent = Intent(activity, AepsActivity::class.java)
                       intent.putExtra(AppConstants.OBJ_SERVICE, services[position])
                       startActivity(intent)
                   })*/
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(activity!!, message, FancyToast.ERROR)
        }
    }
}
