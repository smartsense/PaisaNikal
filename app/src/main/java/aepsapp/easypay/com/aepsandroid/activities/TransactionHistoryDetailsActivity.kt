package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.OTPDialog
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.entities.TransactionHistoryEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.android.volley.VolleyLog
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.fund_transfer_details_activity.*
import org.json.JSONException
import org.json.JSONObject

class TransactionHistoryDetailsActivity : AppCompatActivity() {

    var tranEntity: TransactionHistoryEntity? = null
    var otpDialog: OTPDialog? = null
    var RESPONSE_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fund_transfer_details_activity)

        setSupportActionBar(_toolBarTransaction)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.extras
        tranEntity = bundle!!.getParcelable("tranEntity") as TransactionHistoryEntity?

        _fromDate.setText(tranEntity!!.tRANSACTIONDATE)
        orderId.setText(tranEntity!!.oRDERID)
        amount.setText(tranEntity!!.pAIDAMOUNT.toString())
        baneName.setText(tranEntity!!.bENENAME)
        ifscCode.setText(tranEntity!!.bANKIFSCCODE)
        bankAcc.setText(tranEntity!!.bANKACCOUNTNO.toString())
        refNo.setText(tranEntity!!.cUSTOMERREFERENCENO.toString())
        txtStatus.setText(tranEntity!!.tRANSACTIONSTATUS)

        if (tranEntity!!.tRANSACTIONSTATUS.equals("FAILED") && tranEntity!!.aID.equals(Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE))
                && tranEntity!!.sT.equals("REMDOMESTIC")) {
            refundBtn.visibility = View.VISIBLE
        } else {
            refundBtn.visibility = View.GONE
        }

        if (tranEntity!!.tRANSACTIONSTATUS.equals("FAILED"))
            refundBtn.setOnClickListener {
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
                otpDialog!!.show(supportFragmentManager, "otp")

            }
    }

    private fun otpCall() {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(this, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(this, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_FOR", AppConstants.CUSTREFUND)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_OTP_GENERATE_DMT), objCore, otpResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val otpResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                RESPONSE_CODE = jsonObj.getInt("RESPONSE_CODE")
                val RESP_MSG = jsonObj.getString("RESP_MSG")
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(this@TransactionHistoryDetailsActivity, message)
        }
    }

    fun verifyOTP(otp: String) {
        try {
            val data = JsonObject()
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_REFERENCE_NO", tranEntity!!.oRDERID)
            data.addProperty("TRANSFER_AMOUNT", tranEntity!!.tRANSFERAMOUNT)
            data.addProperty("PAID_AMOUNT", tranEntity!!.pAIDAMOUNT)
            data.addProperty("RESPONSE", "REFUNDED")
            data.addProperty("OTP", otp)
            data.addProperty("REQUEST_CODE", RESPONSE_CODE)

            val header = EPCoreEntity.EPHeader()
            header.OP = Preference.getStringPreference(this, AppConstants.PREF_OP)
            header.ST = Preference.getStringPreference(this, AppConstants.PREF_ST)
            header.AID = Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_TRANSACTION_REFUND_DMT), objCore, transactionResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val transactionResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                Utils.showAlert(this@TransactionHistoryDetailsActivity, jsonObj.getString("RESP_MSG"), View.OnClickListener {
                    onBackPressed()
                })
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(this@TransactionHistoryDetailsActivity, message)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home ->
                onBackPressed()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
        super.onBackPressed()
    }
}