package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.AadharDataEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.ViewGroup
import com.android.volley.VolleyLog
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_aadhar_detail.*
import org.json.JSONException
import org.json.JSONObject

class AadharDetailActivity : AppCompatActivity() {

    private var aadharData: AadharDataEntity? = null
    private val addressBuilder = StringBuilder()
    private var custMobile: String? = null
    private var isCustomerExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aadhar_detail)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@AadharDetailActivity, R.color.black_transparent)))

        aadhar_btncancel.setOnClickListener {
            if (!isCustomerExists)
                finish()
            else
                ackAadharDetails(false)
        }

        aadharData = intent.getSerializableExtra(AppConstants.OBJ_AADHAR) as AadharDataEntity
        custMobile = intent.getStringExtra(AppConstants.CUST_MOBILE)
        isCustomerExists = intent.getBooleanExtra(AppConstants.IS_CUSTOMER_EXISTS, false)

        if (aadharData != null) {
            setAadharData()
        }

        aadhar_btnaccept.setOnClickListener {
            if (!isCustomerExists)
                addCustomer()
            else
                ackAadharDetails(true)
        }

    }

    private fun addCustomer() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AadharDetailActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AadharDetailActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AadharDetailActivity, AppConstants.PREF_SCREEN))
            data.addProperty("cust_Mob", custMobile)
            data.addProperty("cust_Name", aadharData!!.aadhaarName)
            data.addProperty("cust_Kyctype", "EKYC")
            data.addProperty("cust_City", aadharData!!.city)
            data.addProperty("cust_State", aadharData!!.state)
            data.addProperty("cust_Pincode", aadharData!!.pinCode)
            data.addProperty("cust_Dob", aadharData!!.dOB)
            data.addProperty("cust_isAgree", true)
            data.addProperty("cust_AadharVerificationCode", aadharData!!.aADHARVERIFICATIONCODE)
            data.addProperty("cust_AadharReqRefNo", aadharData!!.reqRefNum)
            data.addProperty("cust_Address", addressBuilder.toString())

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@AadharDetailActivity).request(Utils.generateURL(this@AadharDetailActivity,URLGenerator.URL_ADD_EKYC_CUSTOMER), objCore, addResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AadharDetailActivity, getString(R.string.internet_not_available),FancyToast.ERROR)
        }
    }

    private fun ackAadharDetails(isAccepted: Boolean) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AadharDetailActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AadharDetailActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AadharDetailActivity, AppConstants.PREF_SCREEN))
            data.addProperty("cust_Mob", custMobile)
            data.addProperty("cust_Kycstatus", if (isAccepted) AppConstants.TYPE_VERIFIED else AppConstants.TYPE_REJECTED)
            data.addProperty("cust_Kyctype", "EKYC")
            data.addProperty("cust_isAgree", true)
            data.addProperty("cust_AadharVerificationCode", aadharData!!.aADHARVERIFICATIONCODE)
            data.addProperty("cust_AadharReqRefNo", aadharData!!.reqRefNum)


            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@AadharDetailActivity).request(Utils.generateURL(this@AadharDetailActivity,URLGenerator.URL_ACK_AADHAR_DTL), objCore, if (isAccepted) addResp else cancelResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AadharDetailActivity, getString(R.string.internet_not_available),FancyToast.ERROR)
        }
    }

    private val cancelResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            Utils.showToast(this@AadharDetailActivity, getString(R.string.customer_ekyc_rejected),FancyToast.INFO)
            startMain()
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@AadharDetailActivity, message,FancyToast.ERROR)
        }
    }


    private val addResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            Utils.showToast(this@AadharDetailActivity, getString(R.string.customer_ekyc_message),FancyToast.SUCCESS)

            startMain()
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@AadharDetailActivity, message,FancyToast.ERROR)
        }
    }

    private fun startMain() {
        val intent = Intent(this@AadharDetailActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }


    private fun setAadharData() {
        aadhar_txtname.text = aadharData!!.aadhaarName
        aadhar_txtdob.text = aadharData!!.dOB
        aadhar_txtemail.text = "" + aadharData!!.email
        aadhar_txtphone.text = "" + aadharData!!.phone
        aadhar_txtgender.text = aadharData!!.gender
        aadhar_txtaadhar.text = aadharData!!.aadhaarNo

        addressBuilder.append(aadharData!!.houseNo).append(" " + aadharData!!.landMark).append("\n" + aadharData!!.street).append("\n" + aadharData!!.location).append("\n" + aadharData!!.postOffice)
                .append("," + aadharData!!.city).append("\n" + aadharData!!.state).append("-" + aadharData!!.pinCode)

        aadhar_txtaddress.text = addressBuilder.toString()

        // for image
        val imageData = aadharData!!.aadhaarPhoto!!.substring(aadharData!!.aadhaarPhoto!!.indexOf(",") + 1);
        val decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
        val profileBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size);
        aadhar_imgperson.setImageBitmap(profileBitmap)


    }

}
