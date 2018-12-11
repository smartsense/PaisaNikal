package aepsapp.easypay.com.aepsandroid.common

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

/**
 * Created by ADMIN on 7/19/2016.
 */
object Preference {

    private val SHARED_PREF = "ECUSPOS"
    private var pref: SharedPreferences? = null

    private val encryptionKey: String? = null


    fun savePreference(context: Context, key: String, value: String?) {
        createPreference(context)
        val editor = pref!!.edit()
        editor.putString(key, encryptData(context, value ?: ""))
        editor.apply()
    }

    fun savePrefernceUrl(context: Context, key: String, value: String?) {
        createPreference(context)
        val editor = pref!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringPreferenceUrl(context: Context, key: String): String {
        createPreference(context)
        val value = pref!!.getString(key, "")
        return value
    }

    fun clearPreference(context: Context, key: String) {
        createPreference(context)
        val editor = pref!!.edit()
        editor.remove(key)
        editor.apply()
    }

    fun savePreference(context: Context, key: String, value: Int) {
        createPreference(context)
        val editor = pref!!.edit()
        editor.putInt(key, value)

        editor.apply()
    }


    private fun encryptData(context: Context, value: String = ""): String {
        var value = value
        val application = context.applicationContext as AEPSApplication
        if (!TextUtils.isEmpty(application.mobileKey))
            value = EncryptionHelper.encrypt(value, application.mobileKey!!)

        return value

    }

    private fun decryptData(context: Context, value: String): String {
        var value = value
        val application = context.applicationContext as AEPSApplication
        if (!TextUtils.isEmpty(application.mobileKey))
            value = EncryptionHelper.decrypt(value, application.mobileKey!!)

        return value
    }


    fun savePreference(context: Context, key: String, value: Boolean) {
        createPreference(context)
        val editor = pref!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getStringPreference(context: Context, key: String): String {
        createPreference(context)
        val value = pref!!.getString(key, "")
        return decryptData(context, value)

    }

    fun getNormalStringPreference(context: Context, key: String): String {
        createPreference(context)
        return pref!!.getString(key, "")
    }

    fun saveNormalPreference(context: Context, key: String, value: String) {
        createPreference(context)
        val editor = pref!!.edit()
        editor.putString(key, value)
        editor.apply()
    }


    fun getIntPreference(context: Context, key: String): Int {
        createPreference(context)
        return pref!!.getInt(key, 0)

    }

    fun getBooleanPreference(context: Context, key: String): Boolean {
        createPreference(context)
        return pref!!.getBoolean(key, false)

    }

    fun clearAll(context: Context) {
        createPreference(context)
        val editor = pref!!.edit()
        editor.clear()
        editor.apply()
    }

    private fun createPreference(context: Context) {
        if (pref == null) {
            pref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        }
    }


}
