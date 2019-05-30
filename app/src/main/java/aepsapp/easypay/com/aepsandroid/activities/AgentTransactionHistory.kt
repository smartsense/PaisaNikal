package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.volley.VolleyLog
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONException
import org.json.JSONObject

class AgentTransactionHistory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agent_transaction_history)

        getData()
    }

    private fun getData() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.AID = Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE)
            header.CUSTOMER_CHARGE = 0.0
            header.PAYABLE_AMOUNT = 0.0
            header.REQUEST_ID = System.currentTimeMillis()
            header.TXN_AMOUNT = 0.0
            header.UDID = Preference.getStringPreference(this@AgentTransactionHistory, AppConstants.PREF_UDID)
            header.totalPrice = 0.0

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AgentTransactionHistory, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AgentTransactionHistory, AppConstants.PREF_SCREEN))
            data.addProperty("mobile", Preference.getStringPreference(this@AgentTransactionHistory, AppConstants.MOBILE_NO))
            data.addProperty("OP", "AEPS")
            data.addProperty("ST", "REMWALTOACC")
            data.addProperty("AID", Preference.getStringPreference(this@AgentTransactionHistory, AppConstants.PREF_AGENT_CODE))
            data.addProperty("TRANSACTION_TODT", "")
            data.addProperty("TRANSACTION_FROMDT", "")

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).requestDMTCharge(Utils.generateURL(this, URLGenerator.URL_AGENT_TRANSACTION_HISTORY), objCore,
                    object : VolleyJsonRequest.OnJsonResponse {
                        override fun responseReceived(jsonObj: JSONObject) {

                        }

                        override fun errorReceived(code: Int, message: String) {
                            Utils.showAlert(this@AgentTransactionHistory, message)
                        }
                    }, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

}