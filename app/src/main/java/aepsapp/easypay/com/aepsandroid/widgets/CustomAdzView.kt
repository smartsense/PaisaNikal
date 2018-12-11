package aepsapp.easypay.com.aepsandroid.widgets

import aepsapp.easypay.com.aepsandroid.BuildConfig
import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.AppConstants
import aepsapp.easypay.com.aepsandroid.common.Log
import aepsapp.easypay.com.aepsandroid.common.Preference
import aepsapp.easypay.com.aepsandroid.network.VolleyStringRequest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.volley.Request
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class CustomAdzView : WebView {

    private var mContext: Context? = null
    private var adHeight = 50
    private var adWidth = 320
    var isAdLoaded = true

    companion object {
        const val TAG = "CustomAdzView"
    }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs) {
        mContext = context
        init(attrs)
    }

    fun init(attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs,
                R.styleable.CustomAdzView, 0, 0)

        try {
            adHeight = a.getInt(R.styleable.CustomAdzView_adHeight, 50)
            adWidth = a.getInt(R.styleable.CustomAdzView_adWidth, 320)

        } finally {
            a.recycle()
        }

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        loadAdz()
    }


    @SuppressLint("MissingPermission")
    public fun loadAdz() {
        try {
            val android_id = Settings.Secure.getString(mContext!!.contentResolver, Settings.Secure.ANDROID_ID)
            val model = Build.MODEL
            val version = Build.VERSION.SDK_INT
            val deviceMan = android.os.Build.MANUFACTURER
            val versionCode = BuildConfig.VERSION_CODE
            val lati = Preference.getStringPreference(mContext!!, AppConstants.PREF_LATITUDE)
            val longi = Preference.getStringPreference(mContext!!, AppConstants.PREF_LONGITUTE)

            /*val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext!!)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        location: Location? ->
                        location!!.latitude.let { lati = location.latitude.toString() }
                        location.longitude.let { longi = location.longitude.toString() }

                        *//*if (location?.latitude!! != null) {
                            lati = location.latitude.toString()
                        }
                        if (location?.longitude!! != null) {
                            longi = location.longitude.toString()
                        }*//*
                    }
                    .addOnFailureListener {
                        lati = Preference.getStringPreference(mContext!!, AppConstants.PREF_LATITUDE)
                        longi = Preference.getStringPreference(mContext!!, AppConstants.PREF_LONGITUTE)
                    }*/

            if (lati.equals("") || longi.equals("")) {
                Log.e("NO LOCATION FOUND", "*****")
            } else {
                var data = "https://epads.easypay.co.in:8092/api/ad/web?latitude=$lati&longitude=$longi&height=$adHeight&width=$adWidth&gender=1&agegroup=2&household=2&incomesource=2&kioskid=$android_id&city=ahmedabad&state=gujarat&bundleid=${mContext!!.packageName}&appversion=$versionCode&network=wifi&dnt=0&inttype=1&url=http://www.aerserv.com&make=$deviceMan&model=$model&os=android&osv=$version&type=phone&locationsource=2&pchain="
                data = data.replace(" ".toRegex(), "%20")
                Log.e("*****", data)
                getAd(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    private fun getAd(url: String) {
        VolleyStringRequest.request(mContext!!, Request.Method.GET, url, object : VolleyStringRequest.OnStringResponse {
            override fun responseReceived(response: String) {

                val webViewClient = object : WebViewClient() {
                    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                        try {
                            val certificateFactory = CertificateFactory.getInstance("X.509")
                            val inputStream = mContext!!.resources.openRawResource(R.raw.easypay) //(.crt)
                            val certificate = certificateFactory.generateCertificate(inputStream)
                            inputStream.close()

                            // Create a KeyStore containing our trusted CAs
                            val keyStoreType = KeyStore.getDefaultType()
                            val keyStore = KeyStore.getInstance(keyStoreType)
                            keyStore.load(null, null)
                            keyStore.setCertificateEntry("ca", certificate)

                            // Create a TrustManager that trusts the CAs in our KeyStore.
                            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                            val trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
                            trustManagerFactory.init(keyStore)

                            val trustManagers = trustManagerFactory.trustManagers
                            val trustManager = trustManagers[0] as X509TrustManager

                            //Get the X509 trust manager from your ssl certificate
                            //X509TrustManager trustManager = mySslCertificate.getX509TrustManager();

                            //Get the certificate from error object
                            val bundle = SslCertificate.saveState(error.certificate)
                            val x509Certificate: X509Certificate?
                            val bytes = bundle.getByteArray("x509-certificate")
                            if (bytes == null) {
                                x509Certificate = null
                            } else {
                                val certFactory = CertificateFactory.getInstance("X.509")
                                val cert = certFactory.generateCertificate(ByteArrayInputStream(bytes))
                                x509Certificate = cert as X509Certificate
                            }
                            val x509Certificates = arrayOfNulls<X509Certificate>(1)
                            x509Certificates[0] = x509Certificate

                            // check weather the certificate is trusted
                            trustManager.checkServerTrusted(x509Certificates, "ECDH_RSA")

                            android.util.Log.e(TAG, "Certificate from " + error.url + " is trusted.")
                            handler.proceed()
                        } catch (e: Exception) {
                            android.util.Log.e(TAG, "Failed to access " + error.url + ". Error: " + error.primaryError)
                            val builder = AlertDialog.Builder(mContext)
                            var message = "SSL Certificate error."
                            when (error.primaryError) {
                                SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                                SslError.SSL_EXPIRED -> message = "The certificate has expired."
                                SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                                SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                            }
                            message += " Do you want to continue anyway?"

                            builder.setTitle("SSL Certificate Error")
                            builder.setMessage(message)
                            builder.setPositiveButton("continue") { dialog, which -> handler.proceed() }
                            builder.setNegativeButton("cancel") { dialog, which -> handler.cancel() }
                            val dialog = builder.create()
                            dialog.show()
                        }

                    }
                }
                isAdLoaded = true
                setWebViewClient(webViewClient)
                val rawHTML = "<HTML>$response</HTML>"
                loadData(rawHTML, "text/HTML", "UTF-8")
            }

            override fun errorReceived(code: Int, message: String) {
                isAdLoaded = false
                android.util.Log.e(TAG, "errorReceived:$code $message")
            }
        })
    }
}