package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsMessage
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

class ForgotPasswordActivity : AppCompatActivity() {

    private var otp: String? = null
    private var password: String? = null
    private var mobile: String? = null
    private var smsReceiver: BroadcastReceiver? = null

    companion object {
        const val TAG = "ForgotPasswordActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgot_toolbar.setNavigationOnClickListener { finish() }

        mobile = intent.getStringExtra(AppConstants.CUST_MOBILE)
        forgot_mobile.setText(mobile)


        forgot_btnchange.setOnClickListener {
            if (isValid) {
                changePassword()
            }
        }

        countDown.start()
    }

    private fun changePassword() {
        try {
            val data = JsonObject()
            data.addProperty(AppConstants.MOBILE_NO, mobile!!)
            data.addProperty("otp", otp!!)
            data.addProperty("Password", password!!)
            val core = EPCoreEntity<JsonObject>()
            core.DATA = data

            VolleyJsonRequest.getInstance(this@ForgotPasswordActivity).request(Utils.generateURL(this@ForgotPasswordActivity, URLGenerator.URL_FORGOT_PASSWORD), core, object : VolleyJsonRequest.OnJsonResponse {
                override fun responseReceived(jsonObj: JSONObject) {
                    val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)
                    val balance = objData.get("effectiveBalance").toString()
                    val logo = objData.opt("logo").toString()
                    val isDist = objData.getBoolean("isDist")
                    val agentCode = objData.get("agentCode").toString()
                    val shop = objData.optString("shopName").toString()
                    val agentId = objData.get("agentId").toString()
                    val userId = objData.get("userId").toString()
                    val cpMstCode = objData.get("cpMstCode").toString()
                    val cpType = objData.get("cpModelType").toString()
                    val screenName = objData.get("screenName").toString()

                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_BALANCE, balance)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_LOGO, logo)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_ISDIST, isDist)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_AGENT_CODE, agentCode)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_SHOP_NAME, shop)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_AGENT_ID, agentId)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_USER_ID, userId)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_CP_CODE, cpMstCode)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_CP_TYPE, cpType)
                    Preference.savePreference(this@ForgotPasswordActivity, AppConstants.PREF_SCREEN, screenName)

                    redirectToMain()
                    /*if (jsonObj.has("IS_PN") && jsonObj.getString("IS_PN").equals("true")) {
                        URLProvider.instance.setBaseAddressHost(this@ForgotPasswordActivity, "http://uat5yesmoney.easypay.co.in:5060/")
                        changePassword()
                    } else {
                        redirectToMain()
                    }*/
                    //Change in URL
                }

                override fun errorReceived(code: Int, message: String) {
                    if (code == 205) {
                        Preference.clearAll(this@ForgotPasswordActivity)
                        //URLProvider.instance.setBaseAddressHost(this@ForgotPasswordActivity, "http://192.168.10.150:5060/")
                        //URLProvider.instance.setBaseAddressHost(this@ForgotPasswordActivity, "https://nsk.easypay.co.in/")
                        URLProvider.instance.setBaseContext(this@ForgotPasswordActivity, "paisanikal-aepsapp/")
                        val intent = Intent(this@ForgotPasswordActivity, SplashActivity::class.java)
                        startActivity(intent)
                        changePassword()
                    } else {
                        Utils.showToast(this@ForgotPasswordActivity, message, FancyToast.ERROR)
                    }
                }
            }, true, true)
        } catch (e: JSONException) {
            Log.e(TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@ForgotPasswordActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val countDown = object : CountDownTimer(180000, 1000) {
        override fun onFinish() {
            val timerText = SpannableString(getString(R.string.timer_expired))
            timerText.setSpan(ForegroundColorSpan(Color.BLUE), 22, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            forgot_timer.text = timerText
            forgot_timer.setOnClickListener {
                generateOTP()
                forgot_otp.setText("")
            }
        }

        override fun onTick(millisUntilFinished: Long) {
            forgot_timer.text = "${millisUntilFinished / 1000} seconds remaining to verify OTP"
            forgot_timer.setOnClickListener {
            }
        }
    }

    private fun generateOTP() {
        try {
            val data = JsonObject()
            data.addProperty(AppConstants.MOBILE_NO, mobile!!)
            val core = EPCoreEntity<JsonObject>()
            core.DATA = data
            VolleyJsonRequest.getInstance(this@ForgotPasswordActivity).request(Utils.generateURL(this@ForgotPasswordActivity, URLGenerator.URL_OTP), core, object : VolleyJsonRequest.OnJsonResponse {
                override fun responseReceived(jsonObj: JSONObject) {
                    countDown.start()
                }

                override fun errorReceived(code: Int, message: String) {
                    Utils.showToast(this@ForgotPasswordActivity, message, FancyToast.ERROR)
                }

            }, true)
        } catch (e: JSONException) {
            Log.e(TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@ForgotPasswordActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }


    }

    private fun redirectToMain() {
        val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onStart() {
        super.onStart()
        registerSMSReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsReceiver)
        countDown.cancel()
    }

    private fun registerSMSReceiver() {
        val iFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        smsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val data = intent.extras
                val pdus = data!!.get("pdus") as Array<*>

                for (i in pdus.indices) {
                    val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)

                    val sender = smsMessage.displayOriginatingAddress
                    Log.e(TAG, "Sender: $sender")
                    val messageBody = smsMessage.messageBody
                    Log.e(TAG, "messageBody: $messageBody")

                    if (sender.contains(AppConstants.SMS_HOST)) {
                        val p = Pattern.compile("\\d{6}")
                        val m = p.matcher(messageBody)
                        while (m.find()) {
                            forgot_otp!!.setText(m.group())
                        }
                    }
                }
            }
        }

        registerReceiver(smsReceiver, iFilter)
    }


    private val isValid: Boolean
        get() {
            otp = forgot_otp.text.toString()
            if (TextUtils.isEmpty(otp?.trim())) {
                forgot_otp.error = getString(R.string.enter_otp)
                return false
            }
            password = forgot_password.text.toString()
            if (TextUtils.isEmpty(password?.trim())) {
                forgot_password.error = getString(R.string.enter_password)
                return false
            }
            val confirm = forgot_confirm.text.toString()
            if (TextUtils.isEmpty(confirm.trim())) {
                forgot_confirm.error = getString(R.string.enter_confirm_password)
                return false
            }

            if (password?.equals(confirm) != true) {
                forgot_confirm.error = getString(R.string.enter_same_password)
                return false
            }

            return true
        }
}
