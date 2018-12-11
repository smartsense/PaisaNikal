package aepsapp.easypay.com.aepsandroid.network

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.DeviceInfo
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.FileNotFoundException
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

/**
 * Created by ADMIN on 7/18/2016.
 */
object VolleyStringRequest {

    @Throws(InternetNotAvailableException::class)
    fun request(context: Context, method: Int, url: String, onResponse: OnStringResponse) {
        if (DeviceInfo.isInternetConnected(context)) {
            Log.e("URL*****", url)
            val queue = Volley.newRequestQueue(context, HurlStack(null, getSocketFactory(context), context))

            val stringRequest = object : StringRequest(method, url,
                    Response.Listener { response ->
                        onResponse.responseReceived(response)
                    }, Response.ErrorListener {
                /*   if (error != null)
                        onResponse.errorReceived(error.networkResponse.statusCode, error.getMessage());
                    else*/
                onResponse.errorReceived(101, context.getString(R.string.check_server))
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["X-Auth-Token"] = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlYXN5cGF5IiwiY3JlYXRlZCI6MTQ5Nzg2NzIwNDU3MSwiZXhwIjo2MTk3Nzg2NzIwNH0.bQS77TdFV4gH05y2L6b7f6hxQ6cJxs3R7Jmg6W7NefFhiCiv_YBqFjSUlts32ukxRFLYvylEWDGMcYrz2lR_pA"
                    return params
                }
            }
            queue.add(stringRequest)
        } else
            throw InternetNotAvailableException(context.getString(R.string.internet_not_available))
    }

    interface OnStringResponse {
        fun responseReceived(response: String)

        fun errorReceived(code: Int, message: String)
    }

    private fun getSocketFactory(ctx: Context): SSLSocketFactory? {

        var cf: CertificateFactory? = null
        try {

            cf = CertificateFactory.getInstance("X.509")
            val caInput = ctx.resources.openRawResource(R.raw.easypay)
            val ca: Certificate
            try {

                ca = cf!!.generateCertificate(caInput)
                Log.e("CERT", "ca=" + (ca as X509Certificate).subjectDN)
            } finally {
                caInput.close()
            }

            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)

            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            /*HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    Log.e("CipherUsed", session.getCipherSuite());
                    return hostname.compareTo("10.199.89.68") == 0; //The Hostname of your server.

                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);*/
            var context: SSLContext? = null
            context = SSLContext.getInstance("TLS")

            context!!.init(null, tmf.trustManagers, null)
            HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)

            return context.socketFactory

        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

        return null
    }
}
