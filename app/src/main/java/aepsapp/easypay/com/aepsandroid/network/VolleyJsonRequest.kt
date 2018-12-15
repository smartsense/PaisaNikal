package aepsapp.easypay.com.aepsandroid.network

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.activities.SplashActivity
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.widgets.EPProgressDialog
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.*
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.*
import com.android.volley.toolbox.EncryptionHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.PublicKey
import java.util.*


class VolleyJsonRequest private constructor() {
    var publicKey: PublicKey? = null
    private var application: AEPSApplication? = null
    private var progressDialog: EPProgressDialog? = null
    private var appHeaders: HashMap<String, String>? = null
    private var rsaUtils: RSAEncryptionUtils? = null
    private var devicePublicKey: String? = null
    private var currentDigest: String? = null
    private val gson = Gson()

    init {
        initPublicKey()
    }

    fun initPublicKey() {
        application = context!!.applicationContext as AEPSApplication
        rsaUtils = RSAEncryptionUtils.instance
        devicePublicKey = rsaUtils!!.getPublicKey(RSAEncryptionUtils.PKCS8_PEM)
        val key = Preference.getStringPreference(context!!, AppConstants.ENC_KEY)
        currentDigest = Utils.getAppSignature(application!!)
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(application!!.encrptedString)) {
            val encryptionKey = EncryptionHelper.decrypt(application!!.encrptedString, key)
            publicKey = rsaUtils!!.stringToPublicKey(encryptionKey!!)
        }
    }


    fun stringRequest(url: String, agentCode: String, contactNo: String, tokenNo: String, cs: String, onResponse: OnJsonResponse) {
        val stringReq = object : StringRequest(Request.Method.POST, url, object : Response.Listener<String> {
            override fun onResponse(response: String) {
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_LONG).show()
                //do other things with the received JSONObject
                try {
                    val jsonObj = JSONObject(response)
                    Log.e("onResponse****", response.toString())
                    onResponse.responseReceived(jsonObj)
                } catch (t: Throwable) {
                    Log.e("My App", "Could not parse malformed JSON: \"$response\"")
                }

            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                //onResponse.errorReceived(error)
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show()
            }
        }) {

            override fun getBodyContentType(): String {
                val pars = HashMap<String, String>()
                pars["Content-Type"] = "application/x-www-form-urlencoded"
                return "application/x-www-form-urlencoded"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val pars = HashMap<String, String>()
                pars["Content-Type"] = "application/x-www-form-urlencoded"
                return pars
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                val params1 = JSONObject()
                params.put("agentCode", agentCode)
                params.put("token", tokenNo)
                params.put("contactNo", contactNo)
                params.put("ts", cs)
                /*val json = JSONObject()
                json.put("JSON_INPUT", params1.t)*/
                //params["JSON_INPUT"] = params1.toString()

                return params
            }
        }

        //add to the request queue
        VolleyRequestQueue.getInstance(context!!).addToRequestQueue(stringReq)
    }

    @Throws(InternetNotAvailableException::class)
    fun request(url: String, requestObject: EPCoreEntity<JsonObject>?, onResponse: OnJsonResponse, isProgressShow: Boolean, isNewPublicKey: Boolean = false): JsonObjectRequest? {
        var jsObjRequest: JsonObjectRequest? = null
        Log.e("VollyURL    --->>", url)
        var requestId: Long = 0

        if (DeviceInfo.isInternetConnected(context!!)) {
            try {
                /* if (!application!!.checkAPITime()) {
                     application!!.apiTime = System.currentTimeMillis()
                     Utils.showToast(context!!, context!!.getString(R.string.timeout))
                     redirectToLogin()
                     return null
                 }*/

                if (publicKey == null || isNewPublicKey) {
                    initPublicKey()
                    /*Utils.showToast(context!!, context!!.getString(R.string.invalid_session), FancyToast.ERROR)
                    redirectToLogin(true)
                    return null*/
                }

                if (isProgressShow) {
                    if (context != null) {
                        showProgressDialog(context!!)
                    }
                }

                val randomToken = Utils.generateToken(16)

                var requestJson: String? = null
                if (requestObject != null) {
                    requestId = System.currentTimeMillis()
                    if (requestObject.HEADER == null)
                        requestObject.HEADER = EPCoreEntity.EPHeader()
                    requestObject.HEADER!!.UDID = Utils.getUDID(context!!)
                    requestObject.HEADER!!.REQUEST_ID = requestId

                    requestJson = gson.toJson(requestObject)
                    Log.e(TAG, "VollyRequest: $requestJson")
                }
                jsObjRequest = object : JsonObjectRequest(url, if (!TextUtils.isEmpty(requestJson)) EncryptionHelper.encrypt(requestJson, randomToken) else null, Response.Listener { response ->
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }
                    if (response != null) {
                        Log.e("Vollyresponse", response.toString())
                        try {
                            val respCode = response.getInt(AppConstants.KEY_RESP_CODE)
                            if (respCode in intArrayOf(AppConstants.SUCCESS_VALIDATION, AppConstants.SUCCESS_DATA, AppConstants.SUCCESS_PENDING, AppConstants.SUCCESS_TRANSACTION, AppConstants.SUCCESS_CUSTOMER_OTP_VERIFY, AppConstants.SUCCESS_CUSTOMER_REGISTRATION, AppConstants.SUCCESS_DMT_PENDING))
                            // if (checkValid(response))
                                onResponse.responseReceived(response)
                            /* else {
                                 Utils.showToast(context!!, context!!.getString(R.string.data_mismatch))
                                 redirectToLogin()
                             }*/
                            else {
                                onResponse.errorReceived(respCode, response.getString(AppConstants.KEY_RESP_MSG))
                            }
                        } catch (e: JSONException) {
                            Log.e(TAG, "onResponse: ", e)
                        }

                    }
                }, Response.ErrorListener { error ->
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }

                    /*  if (error != null)
                          Crashlytics.logException(error)
                      else
                          Crashlytics.log(1, "Encrpytion", url)*/

                    if (error.networkResponse?.statusCode == 321)
                        Utils.showToast(context!!, context!!.getString(R.string.certificate_error), FancyToast.ERROR)
                    else if (error.networkResponse?.statusCode == 322)
                        Utils.showToast(context!!, context!!.getString(R.string.data_mismatch), FancyToast.ERROR)
                    else {
                        Utils.showToast(context!!, context!!.getString(R.string.error_occured), FancyToast.ERROR)
                        redirectToLogin(true)
                    }

                    Utils.showToast(context!!, context!!.getString(R.string.error_occured), FancyToast.ERROR)
                    if (error == null || error.networkResponse?.statusCode != 321) {
                        redirectToLogin()
                        Log.e("ERROR", error.networkResponse?.statusCode.toString())
                    }
                }) {
                    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject>? {
                        try {
                            Log.e("parseNetworkResponse", "" + response.toString())
                            var descKey: String? = response.headers["encKey"]
                            descKey = rsaUtils!!.decryptWithPrivate(descKey!!)
                            Log.e("parseNetworkResponse", "" + descKey.toString())
                            var jsonString: String? = String(response.data, Charset.forName(
                                    HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET)))
                            jsonString = EncryptionHelper.decrypt(jsonString, descKey)

                            //Log.e("parseNetworkResponse", "" + jsonString.toString())

                            if (!TextUtils.isEmpty(jsonString)) {
                                val objJson = JSONObject(jsonString)
                                val resRequestId = objJson.getLong(AppConstants.REQUEST_ID)
                                if (resRequestId == requestId)
                                    return Response.success(objJson,
                                            HttpHeaderParser.parseCacheHeaders(response))
                                else {
                                    val net = NetworkResponse(322, null, null, false)
                                    val e = VolleyError(net)
                                    return Response.error(e)
                                }
                            } else {
                                val net = NetworkResponse(321, null, null, false)
                                val e = VolleyError(net)
                                return Response.error(e)
                            }
                        } catch (e: UnsupportedEncodingException) {
                            return Response.error(ParseError(e))
                        } catch (je: JSONException) {
                            return Response.error(ParseError(je))
                        }

                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        if (!TextUtils.isEmpty(application!!.mobileKey)) {
                            if (appHeaders == null) {
                                appHeaders = HashMap()
                            }
                            appHeaders!!.put("digest", EncryptionHelper.encrypt(currentDigest, randomToken))
                            appHeaders!!.put("encKey", rsaUtils!!.encryptWithPublic(publicKey!!, randomToken)!!)
                            appHeaders!!.put("encKey2", EncryptionHelper.encrypt(devicePublicKey, randomToken))
                            //appHeaders!!.put("Content-Type", "application/json")

                            Log.e("getHeaders", "****" + rsaUtils!!.encryptWithPublic(publicKey!!, randomToken)!!)
                        } else {
                            throw RuntimeException("Key is not valid")
                        }
                        return appHeaders!!
                    }
                }

            } catch (e: ArithmeticException) {
                Log.e("caught", "request: ", e)
            }

            VolleyRequestQueue.getInstance(context!!).addToRequestQueue(jsObjRequest!!)
        } else {
            throw InternetNotAvailableException(context!!.getString(R.string.internet_not_available))
        }
        return jsObjRequest
    }

    @Throws(InternetNotAvailableException::class)
    fun requestDMTCharge(url: String, requestObject: EPCoreEntity<JsonObject>?, onResponse: OnJsonResponse, isProgressShow: Boolean, isNewPublicKey: Boolean = false): JsonObjectRequest? {
        var jsObjRequest: JsonObjectRequest? = null
        Log.e("VollyURL    --->>", url)
        var requestId: Long = 0

        if (DeviceInfo.isInternetConnected(context!!)) {
            try {
                /* if (!application!!.checkAPITime()) {
                     application!!.apiTime = System.currentTimeMillis()
                     Utils.showToast(context!!, context!!.getString(R.string.timeout))
                     redirectToLogin()
                     return null
                 }*/

                if (publicKey == null || isNewPublicKey) {
                    initPublicKey()
                    /*Utils.showToast(context!!, context!!.getString(R.string.invalid_session), FancyToast.ERROR)
                    redirectToLogin(true)
                    return null*/
                }

                if (isProgressShow) {
                    if (context != null) {
                        showProgressDialog(context!!)
                    }
                }

                val randomToken = Utils.generateToken(16)

                var requestJson: String? = null
                if (requestObject != null) {
                    requestId = System.currentTimeMillis()
                    if (requestObject.HEADER == null)
                        requestObject.HEADER = EPCoreEntity.EPHeader()
                    requestObject.HEADER!!.UDID = Utils.getUDID(context!!)
                    requestObject.HEADER!!.REQUEST_ID = requestId

                    requestJson = gson.toJson(requestObject)
                    Log.e(TAG, "VollyRequest: $requestJson")
                }
                jsObjRequest = object : JsonObjectRequest(url, if (!TextUtils.isEmpty(requestJson)) EncryptionHelper.encrypt(requestJson, randomToken) else null, Response.Listener { response ->
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }
                    if (response != null) {
                        Log.e("Vollyresponse", response.toString())
                        try {
                            if (response.has(AppConstants.KEY_CHRG_LIST)) {
                                val chargeArr = response.getJSONArray(AppConstants.KEY_CHRG_LIST)
                                if (chargeArr.getJSONObject(0).getDouble("chrgValue") > 0)
                                    onResponse.responseReceived(response)
                            } else {
                                onResponse.errorReceived(AppConstants.FAILED_DMT_CHARGE, if (response.has(AppConstants.KEY_RESP_MSG)) response.getString(AppConstants.KEY_RESP_MSG) else "")
                            }
                        } catch (e: JSONException) {
                            Log.e(TAG, "onResponse: ", e)
                        }

                    }
                }, Response.ErrorListener { error ->
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }

                    /*  if (error != null)
                          Crashlytics.logException(error)
                      else
                          Crashlytics.log(1, "Encrpytion", url)*/

                    if (error.networkResponse?.statusCode == 321)
                        Utils.showToast(context!!, context!!.getString(R.string.certificate_error), FancyToast.ERROR)
                    else if (error.networkResponse?.statusCode == 322)
                        Utils.showToast(context!!, context!!.getString(R.string.data_mismatch), FancyToast.ERROR)
                    else {
                        Utils.showToast(context!!, context!!.getString(R.string.error_occured), FancyToast.ERROR)
                        redirectToLogin(true)
                    }

                    Utils.showToast(context!!, context!!.getString(R.string.error_occured), FancyToast.ERROR)
                    if (error == null || error.networkResponse?.statusCode != 321) {
                        redirectToLogin()
                        Log.e("ERROR", error.networkResponse?.statusCode.toString())
                    }
                }) {
                    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject>? {
                        try {
                            Log.e("parseNetworkResponse", "" + response.toString())
                            var descKey: String? = response.headers["encKey"]
                            descKey = rsaUtils!!.decryptWithPrivate(descKey!!)
                            Log.e("parseNetworkResponse", "" + descKey.toString())
                            var jsonString: String? = String(response.data, Charset.forName(
                                    HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET)))
                            jsonString = EncryptionHelper.decrypt(jsonString, descKey)

                            //Log.e("parseNetworkResponse", "" + jsonString.toString())

                            if (!TextUtils.isEmpty(jsonString)) {
                                val objJson = JSONObject(jsonString)
                                val resRequestId = objJson.getLong(AppConstants.REQUEST_ID)
                                if (resRequestId == requestId)
                                    return Response.success(objJson,
                                            HttpHeaderParser.parseCacheHeaders(response))
                                else {
                                    val net = NetworkResponse(322, null, null, false)
                                    val e = VolleyError(net)
                                    return Response.error(e)
                                }
                            } else {
                                val net = NetworkResponse(321, null, null, false)
                                val e = VolleyError(net)
                                return Response.error(e)
                            }
                        } catch (e: UnsupportedEncodingException) {
                            return Response.error(ParseError(e))
                        } catch (je: JSONException) {
                            return Response.error(ParseError(je))
                        }

                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        if (!TextUtils.isEmpty(application!!.mobileKey)) {
                            if (appHeaders == null) {
                                appHeaders = HashMap()
                            }
                            appHeaders!!.put("digest", EncryptionHelper.encrypt(currentDigest, randomToken))
                            appHeaders!!.put("encKey", rsaUtils!!.encryptWithPublic(publicKey!!, randomToken)!!)
                            appHeaders!!.put("encKey2", EncryptionHelper.encrypt(devicePublicKey, randomToken))
                            //appHeaders!!.put("Content-Type", "application/json")

                            Log.e("getHeaders", "****" + rsaUtils!!.encryptWithPublic(publicKey!!, randomToken)!!)
                        } else {
                            throw RuntimeException("Key is not valid")
                        }
                        return appHeaders!!
                    }
                }

            } catch (e: ArithmeticException) {
                Log.e("caught", "request: ", e)
            }

            VolleyRequestQueue.getInstance(context!!).addToRequestQueue(jsObjRequest!!)
        } else {
            throw InternetNotAvailableException(context!!.getString(R.string.internet_not_available))
        }
        return jsObjRequest
    }

    private fun redirectToLogin(isClearPref: Boolean = false) {
        val starter = Intent(context!!, SplashActivity::class.java)
        URLProvider.instance.setBaseContext(context!!, AppConstants.BASE_URL_CONTEXT)
        starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
        starter.putExtra("error", true)
        if (isClearPref) {
            Preference.clearPreference(context!!, AppConstants.PREF_AGENT_ID)
            Preference.clearPreference(context!!, AppConstants.PREF_AGENT_MOBILE)
        }
        context!!.startActivity(starter)
        if (context is Activity)
            (context as Activity).finish()
    }

    private fun checkValid(response: JSONObject?): Boolean {
        var isValid = true

        try {
            val objData = response!!.getJSONObject(AppConstants.KEY_DATA)
            val udid = objData.optString("udid")
            if (!TextUtils.isEmpty(udid) && udid != Preference.getStringPreference(context!!, AppConstants.PREF_UDID))
                isValid = false

        } catch (e: JSONException) {
            Log.w(TAG, "JSONException No DATA found")
        }

        return isValid
    }


    interface OnJsonResponse {
        fun responseReceived(jsonObj: JSONObject)

        fun errorReceived(code: Int, message: String)
    }

    private fun showProgressDialog(context: Context) {

        try {
            progressDialog = progressDialog ?: EPProgressDialog(context)
            progressDialog!!.isIndeterminate = true
            progressDialog!!.setCancelable(false)
            progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context,R.drawable.ep_progress));

            if ((!(context as AppCompatActivity).isFinishing()) && !context.isFinishing()) {
                if (progressDialog != null && !progressDialog!!.isShowing())
                    progressDialog!!.show()
            }
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
    }

    companion object {
        var jsonRequest: VolleyJsonRequest? = null
        var context: Context? = null
        fun getInstance(mContext: Context): VolleyJsonRequest {
            context = mContext
            jsonRequest = jsonRequest ?: VolleyJsonRequest()
            return jsonRequest!!
        }

        fun getNewInstance(mContext: Context): VolleyJsonRequest {
            context = mContext
            jsonRequest = jsonRequest ?: VolleyJsonRequest()
            return jsonRequest!!
        }
    }

}
