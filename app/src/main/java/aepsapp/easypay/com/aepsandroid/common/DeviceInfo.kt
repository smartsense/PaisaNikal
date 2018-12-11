package aepsapp.easypay.com.aepsandroid.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import org.json.JSONException
import org.json.JSONObject
import java.net.NetworkInterface
import java.util.*

/**
 * Created by ADMIN on 22-Aug-16.
 */
object DeviceInfo {

    private val TAG = "DeviceInfo"

    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        // for now eat exceptions
        return ""
    }

    fun isInternetConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting

    }

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver,
                Settings.Secure.ANDROID_ID)
    }

    @SuppressLint("MissingPermission")
    fun getDeviceInformation(context: Context): JSONObject {
        var objJson: JSONObject = JSONObject()
        try {
            objJson.put("sdk", Build.VERSION.SDK_INT)
            objJson.put("device", android.os.Build.DEVICE)
            objJson.put("device", android.os.Build.MODEL)
            objJson.put("product", android.os.Build.PRODUCT)
            objJson.put("ipAddress", getIPAddress(true))
            objJson.put("manufacture", Build.MANUFACTURER)
            val serviceName = Context.TELEPHONY_SERVICE
            val m_telephonyManager = context.getSystemService(serviceName) as TelephonyManager
            objJson.put("IMEI", m_telephonyManager.deviceId)
            objJson.put("IMSI", m_telephonyManager.subscriberId)
        } catch (e: JSONException) {
            Log.e(TAG, "getDeviceInformation: ", e)
        }

        return objJson
    }

    val deviceDtl: String
        get() {
            return "${Build.MANUFACTURER} ${Build.MODEL}"
        }
}
