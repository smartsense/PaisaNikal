package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsMessage
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import com.android.volley.VolleyLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity(), LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    protected var mRequestingLocationUpdates: Boolean? = false
    private var isFrom: String? = null
    private var mobileNo: String? = null
    private var password: String? = null
    private var otp: String? = null
    private var smsReceiver: BroadcastReceiver? = null
    protected var mGoogleApiClient: GoogleApiClient? = null
    protected var mLocationRequest: LocationRequest? = null
    protected var mCurrentLocation: Location? = null
    protected val REQUEST_CHECK_SETTINGS = 0x1
    var FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000 / 2
    var UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    protected var mLocationSettingsRequest: LocationSettingsRequest? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var mAdView: AdView
    private var token = ""
    private var contactNo = ""
    private var agentCode = ""
    private var ts = ""
    private var isRequested = false
    private var mLastClickTime: Long = 0
    private var MIN_CLICK_INTERVAL: Long = 5000
    private var isDirectLogin = false
    private var isForgotLogin = false

    companion object {
        const val TAG = "LoginActivity"
        const val PERMISSION_READSMS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        askForSMSPermission()

        if (intent != null && intent.getStringExtra("mobileNumber") != null && intent.getStringExtra("password") != null
                && !intent.getStringExtra("mobileNumber").equals("")) {
            isFrom = intent.getStringExtra("isFrom")
            mobileNo = intent.getStringExtra("mobileNumber")
            password = intent.getStringExtra("password")

            if (isFrom.equals("loginScreen")) {
                isDirectLogin = true
            } else if (isFrom.equals("forgotScreen")) {
                isForgotLogin = true
            }

            login_edtmobile.setText(mobileNo)
            login_edtpassword.setText(password)
        }

        if (intent != null && intent.getStringExtra("token") != null
                && intent.getStringExtra("contactNo") != null
                && intent.getStringExtra("agentCode") != null && intent.getStringExtra("ts") != null) {
            token = intent.getStringExtra("token")
            contactNo = intent.getStringExtra("contactNo")
            agentCode = intent.getStringExtra("agentCode")
            ts = intent.getStringExtra("ts")
        }

        if (!isRequested && token != null && contactNo != null && agentCode != null && ts != null
                && token.length > 0 && contactNo.length > 0 && agentCode.length > 0 && ts.length > 0) {
            isRequested = true
            getDataFromOtherApp(token, contactNo, agentCode, ts)
        }

        val adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        //val adSize = AdSize(300, 50)
        //adView.adSize = adSize
        //adView.adUnitId = "ca-app-pub-8528774229304942~3806772862"
        adView.adUnitId = getString(R.string.google_mob_adz_unit_id)

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        /* val isPermissionGiven = Preference.getBooleanPreference(this@LoginActivity, AppConstants.IS_SMS_PERMISSION_GIVEN)
           if (!isPermissionGiven)
               askForSMSPermission()
           else
               registerSMSReceiver()*/

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buildGoogleApiClient()

        val agentId = Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_AGENT_ID)
        if (!TextUtils.isEmpty(agentId))
            redirectToMain()

        login_btnlogin.text = getString(R.string.login)

        if (isDirectLogin)
            loginApp()

        if (isForgotLogin) {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(AppConstants.CUST_MOBILE, mobileNo)
            startActivity(intent)
        }

        login_btnlogin.setOnClickListener {
            //if (login_layoutotp.visibility == View.VISIBLE) {
            if (isValid) {
                val currentClickTime = SystemClock.uptimeMillis()
                val elapsedTime = currentClickTime - mLastClickTime
                mLastClickTime = currentClickTime
                if (elapsedTime <= MIN_CLICK_INTERVAL) {
                    return@setOnClickListener
                } else {
                    (application as AEPSApplication).apiTime = System.currentTimeMillis()
                    loginApp()
                }
            }
            /*} else {
                val currentClickTime = SystemClock.uptimeMillis()
                val elapsedTime = currentClickTime - mLastClickTime
                mLastClickTime = currentClickTime

                if (elapsedTime <= MIN_CLICK_INTERVAL) {
                    return@setOnClickListener
                } else {
                    generateOTP()
                }
            }*/
        }

        login_txtforgot.paintFlags = login_txtforgot.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        login_txtforgot.setOnClickListener {
            generateOTP(true)
        }
    }

    private fun getDataFromOtherApp(token: String, contactNo: String, agentCode: String, ts: String) {
        try {

            val header = EPCoreEntity.EPHeader()
            header.UDID = Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_UDID)
            header.AID = Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_AGENT_CODE)
            header.REQUEST_ID = Preference.getIntPreference(this@LoginActivity, AppConstants.REQUEST_ID).toLong()

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_SCREEN))
            data.addProperty("token", token)
            data.addProperty("contactNo", contactNo)
            data.addProperty("agentCode", agentCode)
            data.addProperty("ts", ts)
            val core = EPCoreEntity<JsonObject>()
            core.DATA = data

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            //mobileapp/varify/ssotoken
            //val url = "http://192.168.10.32:8090/aepsappctx/mobileapp/varify/ssotoken"
            //val url = "https://uat2yesmoney.easypay.co.in/aepsappctx/varify/ssotoken"

            VolleyJsonRequest.getInstance(this@LoginActivity).request(Utils.generateURL(this@LoginActivity, URLGenerator.URL_SSO_TOKEN), objCore, loginResp, true, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@LoginActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val countDown = object : CountDownTimer(180000, 1000) {
        override fun onFinish() {
            val timerText = SpannableString(getString(R.string.timer_expired))
            timerText.setSpan(ForegroundColorSpan(Color.BLUE), 22, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            login_txttimer.text = timerText
            login_txttimer.setOnClickListener {
                generateOTP()
                login_edtotp.setText("")
            }
        }

        override fun onTick(millisUntilFinished: Long) {
            login_txttimer.text = "${millisUntilFinished / 1000} seconds remaining to verify OTP"
            login_txttimer.setOnClickListener {
            }
        }
    }

    fun buildGoogleApiClient() {
        Log.e(TAG, "Building GoogleApiClient")
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mGoogleApiClient!!.connect()
    }

    fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest!!.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest!!.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)

        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    fun buildLocationSettingsRequest() {
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    protected fun checkLocationSettings() {
        val result = LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        )
        result.setResultCallback(this)
    }

    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status = locationSettingsResult.getStatus()
        when (status.getStatusCode()) {
            LocationSettingsStatusCodes.SUCCESS -> {
                android.util.Log.e(TAG, "All location settings are satisfied.")
                startLocationUpdates()
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                android.util.Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ")

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this@LoginActivity, REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                    android.util.Log.e(TAG, "PendingIntent unable to execute request.")
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> android.util.Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null)
            when (requestCode) {
                // Check for the integer request code originally supplied to startResolutionForResult().
                REQUEST_CHECK_SETTINGS ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            android.util.Log.e(TAG, "User agreed to make required location settings changes.")
                            startLocationUpdates()
                        }
                        Activity.RESULT_CANCELED ->
                            android.util.Log.e(TAG, "User chose not to make required location settings changes.")
                    }
                1 -> {
                    clearSSOData()
                }
            }
    }

    fun clearSSOData() {
        token = ""
        contactNo = ""
        agentCode = ""
        ts = ""
    }

    protected fun startLocationUpdates() {

        val permission: String = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val res: Int = this.checkCallingOrSelfPermission(permission)

        if (res == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        }

        /*LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this).setResultCallback(object : ResultCallback<Status> {
                override fun onResult(status: Status) {
                    mRequestingLocationUpdates = true
                    //setButtonsEnabledState()
                }
            })*/
    }

    var mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            mRequestingLocationUpdates = true
        }

        override fun onLocationAvailability(p0: LocationAvailability?) {
            super.onLocationAvailability(p0)
        }
    }

    protected fun stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)

        /*LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this).setResultCallback(object : ResultCallback<Status> {
            override fun onResult(status: Status) {
                mRequestingLocationUpdates = false
                //setButtonsEnabledState()
            }
        })*/
    }

    public override fun onResume() {
        super.onResume()
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient != null)
            if (mGoogleApiClient!!.isConnected && mRequestingLocationUpdates!!) {
                startLocationUpdates()
            }
    }

    override fun onPause() {
        super.onPause()
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient != null)
            if (mGoogleApiClient!!.isConnected) {
                stopLocationUpdates()
            }
    }

    override fun onStop() {
        super.onStop()
        mGoogleApiClient!!.disconnect()
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    override fun onConnected(p0: Bundle?) {
        android.util.Log.e(TAG, "Connected to GoogleApiClient")

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        val permission: String = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val res: Int = this.checkCallingOrSelfPermission(permission)

        if (res == PackageManager.PERMISSION_GRANTED)
            if (mCurrentLocation == null) {

                mFusedLocationClient!!.lastLocation.addOnSuccessListener(this, OnSuccessListener { location ->
                    if (location != null) {
                        mCurrentLocation = location
                        updateLocationUI()
                    }
                })

                /*mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                //mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
                updateLocationUI()*/
            }
    }

    private fun updateLocationUI() {
        if (mCurrentLocation != null) {
            Log.e("updateLocationUI", "" + mCurrentLocation!!.latitude)
            Log.e("updateLocationUI", "" + mCurrentLocation!!.longitude)

            Preference.savePreference(this@LoginActivity, AppConstants.PREF_LATITUDE, mCurrentLocation!!.latitude.toString())
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_LONGITUTE, mCurrentLocation!!.longitude.toString())

            /*val adz = CustomAdzView(this@LoginActivity)
            adz.loadAdz()*/
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    override fun onLocationChanged(location: Location) {
        mCurrentLocation = location
        //mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
        updateLocationUI()
        //Toast.makeText(this, resources.getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(cause: Int) {
        android.util.Log.e(TAG, "Connection suspended")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        android.util.Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.errorCode)
    }

    private fun generateOTP(isForgot: Boolean = false) {
        mobileNo = login_edtmobile.text.toString()
        if (TextUtils.isEmpty(mobileNo)) {
            login_edtmobile.error = getString(R.string.enter_mobile)
            return
        } else if (mobileNo!!.length < 10) {
            login_edtmobile.error = getString(R.string.enter_valid_mobile)
            return
        }
        val chare = mobileNo!![0]
        if (Character.getNumericValue(chare) in 0..4) {
            login_edtmobile.error = getString(R.string.mobile_no_start_from)
            return
        }
        try {
            val data = JsonObject()
            data.addProperty(AppConstants.MOBILE_NO, mobileNo)
            val core = EPCoreEntity<JsonObject>()
            core.DATA = data

            VolleyJsonRequest.getInstance(this@LoginActivity).request(Utils.generateURL(this@LoginActivity, URLGenerator.URL_OTP), core, object : VolleyJsonRequest.OnJsonResponse {
                override fun responseReceived(jsonObj: JSONObject) {
                    if (!isForgot) {
                        login_edtotp.setText("")
                        //login_layoutotp.visibility = View.VISIBLE
                        login_layoutpwd.visibility = View.VISIBLE
                        login_btnlogin.text = getString(R.string.login)
                        login_edtmobile.isEnabled = false
                        login_txttimer.visibility = View.VISIBLE
                        //countDown.start()
                    } else {
                        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra(AppConstants.CUST_MOBILE, mobileNo)
                        startActivity(intent)
                    }
                }

                override fun errorReceived(code: Int, message: String) {
                    if (code == 205) {
                        Preference.clearAll(this@LoginActivity)
                        //URLProvider.instance.setBaseAddressHost(this@LoginActivity, "http://192.168.10.150:5060/")
                        //URLProvider.instance.setBaseAddressHost(this@LoginActivity, "https://nsk.easypay.co.in/")
                        URLProvider.instance.setBaseContext(this@LoginActivity, "paisanikal-aepsapp/")
                        val intent = Intent(this@LoginActivity, SplashActivity::class.java)
                        intent.putExtra("token", token)
                        intent.putExtra("contactNo", contactNo)
                        intent.putExtra("agentCode", agentCode)
                        intent.putExtra("ts", ts)
                        intent.putExtra("isFrom", "forgotScreen")
                        intent.putExtra("mobileNumber", mobileNo)
                        intent.putExtra("password", password)
                        startActivity(intent)
                    } else
                        Utils.showToast(this@LoginActivity, message, FancyToast.ERROR)
                }

            }, true, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@LoginActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private fun askForSMSPermission() {
        if ((ContextCompat.checkSelfPermission(this@LoginActivity,
                        Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this@LoginActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this@LoginActivity,
                    arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_READSMS)

        } else {
            Preference.savePreference(this@LoginActivity, AppConstants.IS_SMS_PERMISSION_GIVEN, true)
            registerSMSReceiver()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_READSMS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0) {
                    val sms = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val loc = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (sms) {
                        Preference.savePreference(this@LoginActivity, AppConstants.IS_SMS_PERMISSION_GIVEN, true)
                        registerSMSReceiver()
                    } else {
                        Utils.showAlert(this@LoginActivity, getString(R.string.sms_permission))
                    }

                    if (loc) {
                        buildGoogleApiClient()
                    } else {
                        callLocApi()
                        Utils.showAlert(this@LoginActivity, getString(R.string.loc_permission))
                    }
                }
                return
            }
        }
    }

    fun callLocApi() {
        try {

            val header = EPCoreEntity.EPHeader()
            header.UDID = Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_UDID)
            header.AID = Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_AGENT_CODE)
            header.REQUEST_ID = Preference.getIntPreference(this@LoginActivity, AppConstants.REQUEST_ID).toLong()

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@LoginActivity, AppConstants.PREF_SCREEN))
            val core = EPCoreEntity<JsonObject>()
            core.DATA = data

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@LoginActivity).request(Utils.generateURL(this@LoginActivity, URLGenerator.URL_GET_GEO_DETAILS), objCore, object : VolleyJsonRequest.OnJsonResponse {
                override fun responseReceived(jsonObj: JSONObject) {
                    if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                        val jsonData = jsonObj.getJSONObject("DATA")
                        val latitude = jsonData.getString("latitude")
                        val longitude = jsonData.getString("longitude")
                        val postalCode = jsonData.getString("postalCode")

                        Preference.savePreference(this@LoginActivity, AppConstants.PREF_LATITUDE, latitude)
                        Preference.savePreference(this@LoginActivity, AppConstants.PREF_LONGITUTE, longitude)

                        //redirectToMain()
                    }
                }

                override fun errorReceived(code: Int, message: String) {
                    //redirectToMain()
                    //Utils.showToast(this@LoginActivity, message, FancyToast.ERROR)
                }

            }, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@LoginActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    fun updateUI(l: Location) {
        Log.e("updateUI", "*****" + l.latitude)
        Log.e("updateUI", "*****" + l.longitude)
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
                    Log.v(TAG, "Sender: $sender")
                    val messageBody = smsMessage.messageBody
                    Log.v(TAG, "messageBody: $messageBody")

                    if (sender.contains(AppConstants.SMS_HOST)) {
                        val p = Pattern.compile("\\d{6}")
                        val m = p.matcher(messageBody)
                        while (m.find()) {
                            login_edtotp!!.setText(m.group())
                        }
                    }
                }
            }
        }

        registerReceiver(smsReceiver, iFilter)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (smsReceiver != null)
            unregisterReceiver(smsReceiver)

    }

    private val isValid: Boolean
        get() {
            mobileNo = login_edtmobile.text.toString()
            if (TextUtils.isEmpty(mobileNo)) {
                login_edtmobile.requestFocus()
                login_edtmobile.error = getString(R.string.enter_mobile)
                return false
            } else if (mobileNo!!.length < 10) {
                login_edtmobile.requestFocus()
                login_edtmobile.error = getString(R.string.enter_valid_mobile)
                return false
            }
            password = login_edtpassword.text.toString()
            if (TextUtils.isEmpty(password)) {
                login_edtpassword.requestFocus()
                login_edtpassword.error = getString(R.string.enter_password)
                return false
            }
            /*otp = login_edtotp.text.toString()
            if (TextUtils.isEmpty(otp)) {
                login_edtotp.error = getString(R.string.enter_otp)
                return false
            }*/
            return true
        }

    private fun loginApp() {
        try {
            val data = JsonObject()
            data.addProperty(AppConstants.MOBILE_NO, mobileNo)
            data.addProperty("Password", password)

            //data.addProperty("otp", otp)
            val core = EPCoreEntity<JsonObject>()
            core.DATA = data

            VolleyJsonRequest.getInstance(this@LoginActivity).request(Utils.generateURL(this@LoginActivity, URLGenerator.URL_LOGIN_WITHOUT_OTP), core, loginResp, true, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@LoginActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val loginResp = object : VolleyJsonRequest.OnJsonResponse {
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
            val roles = objData.getJSONArray("Roles")
            var agent = ""
            if (roles != null) {
                for (i in 0 until roles.length()) {
                    if (roles.getString(i).equals("MODEL_ONE_AGENT")) {
                        agent = "MODEL_ONE_AGENT"
                    }
                }
            }


            Preference.savePreference(this@LoginActivity, AppConstants.PREF_AGENT_CODE_TO_SHOW, agent)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_BALANCE, balance)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_LOGO, logo)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_ISDIST, isDist)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_AGENT_CODE, agentCode)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_SHOP_NAME, shop)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_AGENT_ID, agentId)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_USER_ID, userId)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_CP_CODE, cpMstCode)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_CP_TYPE, cpType)
            Preference.savePreference(this@LoginActivity, AppConstants.PREF_SCREEN, screenName)

            Preference.savePreference(this@LoginActivity, AppConstants.PREF_AGENT_MOBILE, mobileNo)

            redirectToMain()

            /*if (jsonObj.has("IS_PN") && jsonObj.getString("IS_PN").equals("true")) {
                //AppConstants.BASE_URL = "http://uat5yesmoney.easypay.co.in:5060/"
                //Preference.savePreference(this@LoginActivity, AppConstants.BASE_URL, URLProvider.instance.BASE_ADDRESS_HOST)
                //Log.e("URL", "" + Preference.savePreference(this@LoginActivity, AppConstants.BASE_URL, URLProvider.instance.BASE_ADDRESS_HOST))
                URLProvider.instance.setBaseAddressHost(this@LoginActivity, "http://uat5yesmoney.easypay.co.in:5060/")
                loginApp()
            } else {
                //callLocApi()
                redirectToMain()
            }*/
        }

        override fun errorReceived(code: Int, message: String) {
            if (code == 205) {
                //URLProvider.instance.setBaseAddressHost(this@LoginActivity, "http://uat5yesmoney.easypay.co.in:5060/")
                //VolleyJsonRequest.getInstance(this@LoginActivity).initPublicKey()
                Preference.clearAll(this@LoginActivity)
                //URLProvider.instance.setBaseAddressHost(this@LoginActivity, "http://192.168.10.150:5060/")
                //URLProvider.instance.setBaseAddressHost(this@LoginActivity, "https://nsk.easypay.co.in/")
                URLProvider.instance.setBaseContext(this@LoginActivity, "paisanikal-aepsapp/")
                val intent = Intent(this@LoginActivity, SplashActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("contactNo", contactNo)
                intent.putExtra("agentCode", agentCode)
                intent.putExtra("ts", ts)
                intent.putExtra("isFrom", "loginScreen")
                intent.putExtra("mobileNumber", mobileNo)
                intent.putExtra("password", password)
                startActivity(intent)
                //loginApp()
            } else {
                Utils.showToast(this@LoginActivity, message, FancyToast.ERROR)
                login_edtpassword.setText("")
                login_edtotp.setText("")
            }
        }
    }

    private fun redirectToMain() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivityForResult(intent, 1)
        finish()
    }

}
