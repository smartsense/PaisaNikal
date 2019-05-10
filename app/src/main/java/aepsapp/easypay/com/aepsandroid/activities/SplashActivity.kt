package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.BuildConfig
import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import aepsapp.easypay.com.aepsandroid.network.VolleyStringRequest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.EncryptionHelper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONException
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SplashActivity"
    }

    private var token = ""
    private var contactNo = ""
    private var agentCode = ""
    private var ts = ""
    private var mobileNumber = ""
    private var password = ""
    private var isFrom = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (intent != null && intent.getStringExtra("isFrom") != null) {
            isFrom = intent.getStringExtra("isFrom")
            if (isFrom.equals("loginScreen") && intent.getStringExtra("mobileNumber") != null && intent.getStringExtra("password") != null) {
                mobileNumber = intent.getStringExtra("mobileNumber")
                password = intent.getStringExtra("password")
            } else if (isFrom.equals("forgotScreen") && intent.getStringExtra("mobileNumber") != null) {
                mobileNumber = intent.getStringExtra("mobileNumber")
                //password = intent.getStringExtra("password")
            }
        }

        if (intent != null && intent.getStringExtra("token") != null
                && intent.getStringExtra("contactNo") != null
                && intent.getStringExtra("agentCode") != null && intent.getStringExtra("ts") != null) {
            token = intent.getStringExtra("token")
            contactNo = intent.getStringExtra("contactNo")
            agentCode = intent.getStringExtra("agentCode")
            ts = intent.getStringExtra("ts")
        }

        val errorOccurred = intent.getBooleanExtra("error", false)
        if (!errorOccurred) {
            progressBar2.visibility = View.VISIBLE
            login_error.visibility = View.GONE
            if (BuildConfig.DEBUG_MODE) {
                getConfig()
            } else {
                if (!DeviceRootUtils.isDeviceRootedFromRootBeer(applicationContext) && !BuildConfig.DEBUG_MODE) {
                    getConfig()
                } else {
                    Preference.clearAll(this@SplashActivity)
                    Utils.showToast(this@SplashActivity, getString(R.string.app_not_run_on_root), FancyToast.ERROR)
                    finish()
                }
            }
        } else {
            progressBar2.visibility = View.GONE
            login_error.visibility = View.VISIBLE
            splash_btlogin.setOnClickListener {
                progressBar2.visibility = View.VISIBLE
                login_error.visibility = View.GONE
                getConfig()
            }
        }
    }

    private fun getConfig() {
        try {
            Preference.clearPreference(this@SplashActivity, AppConstants.ENC_KEY)
            VolleyStringRequest.request(applicationContext, Request.Method.GET, Utils.generateContextURL(this@SplashActivity, URLGenerator.URL_GET_CONFIG), object : VolleyStringRequest.OnStringResponse {
                override fun responseReceived(response: String) {
                    Log.e(TAG, "Response: $response")
                    val encKey = Utils.generateToken(16)
                    val keyUtils = KeyUtils()
                    try {
                        val objData = keyUtils.getActualKey(response)
                        val application = getApplication() as AEPSApplication
                        application.encrptedString = EncryptionHelper.encrypt(objData.getString("DATA"), encKey)
                        if (objData.has("mobileKey"))
                            application.mobileKey = objData.get("mobileKey").toString()
                        val apkNonce = objData.get("apkNonce").toString()
                        val apiKey = objData.get("apiKey").toString()

                        /* val certificateDigest = objData.getString("certificateDigest")
                     if (!AppConstants.IC_CHECK_FOR_DEBUG && !Utils.checkAppSignature(this@SplashActivity, certificateDigest)) {
                         Utils.showToast(this@SplashActivity, getString(R.string.certificate_tempered))
                         finish()
                     }*/

                        Preference.savePreference(this@SplashActivity, AppConstants.ENC_KEY, encKey)

                        startLogin()
                        //checkSafetyNet(apkNonce, apiKey)

                    } catch (e: JSONException) {
                        android.util.Log.e(SplashActivity.TAG, "responseReceived: ", e)
                        // showNotReachableImage(true)
                    }

                }

                override fun errorReceived(code: Int, message: String) {
                    Log.e(SplashActivity.TAG, message)
                    //  showNotReachableImage(true)
                }
            })
        } catch (e: InternetNotAvailableException) {
            Toast.makeText(applicationContext, getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show()
            //showNotReachableImage(true)
        }
    }

    private fun checkSafetyNet(nonce: String, apiKey: String) {
        if ((GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@SplashActivity) == ConnectionResult.SUCCESS)) {
            SafetyNet.getClient(this).attest(nonce.toByteArray(), apiKey)
                    .addOnSuccessListener(this
                    ) { response ->
                        val jwResult = response.jwsResult
                        (application as AEPSApplication).apiTime = System.currentTimeMillis()
                        verifyDevice(jwResult, nonce)
                    }
                    .addOnFailureListener(this) { e ->
                        // An error occurred while communicating with the service.
                        if (e is ApiException) {
                            // An error with the Google Play services API contains some
                            // additional details.
                            val apiException = e as ApiException
                            // You can retrieve the status code using t he
                            // apiException.getStatusCode() method.

                        } else {
                            // A different, unknown type of error occurred.
                            Log.d(SplashActivity.TAG, "Error: " + e.message)
                        }
                    }
        } else {
            Utils.showAlert(this@SplashActivity, getString(R.string.check_google_service))
        }
    }

    private fun verifyDevice(payload: String, appNonce: String) {
        try {

            val data = JsonObject()
            data.addProperty("payload", payload)
            data.addProperty("nonce", appNonce)
            data.addProperty("deviceDtl", DeviceInfo.deviceDtl)

            val core = EPCoreEntity<JsonObject>()
            core.DATA = data

            VolleyJsonRequest.getInstance(this@SplashActivity).request(Utils.generateURL(this@SplashActivity, URLGenerator.URL_VALIDATE_DEVICE), core, object : VolleyJsonRequest.OnJsonResponse {
                override fun responseReceived(jsonObj: JSONObject) {
                    startLogin()
                }

                override fun errorReceived(code: Int, message: String) {
                    Utils.showToast(this@SplashActivity, message, FancyToast.ERROR)
                }
            }, false)
        } catch (e: JSONException) {
            Log.e(SplashActivity.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Toast.makeText(this@SplashActivity, getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLogin() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("contactNo", contactNo)
        intent.putExtra("agentCode", agentCode)
        intent.putExtra("ts", ts)
        intent.putExtra("isFrom", isFrom)
        intent.putExtra("mobileNumber", mobileNumber)
        intent.putExtra("password", password)
        startActivity(intent)
        finish()
    }


}
