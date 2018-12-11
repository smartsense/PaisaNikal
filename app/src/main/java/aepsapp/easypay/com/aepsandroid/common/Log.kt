package aepsapp.easypay.com.aepsandroid.common


import aepsapp.easypay.com.aepsandroid.BuildConfig

/**
 * Created by Viral on 06-07-2017.
 */

object Log {
    private val LOG =  BuildConfig.DEBUG_MODE

    fun i(tag: String, string: String) {
        if (LOG) android.util.Log.i(tag, string)
    }

    fun e(tag: String, string: String) {
        if (LOG) android.util.Log.e(tag, string)
    }

    fun e(tag: String, string: String, e: Exception) {
        if (LOG) android.util.Log.e(tag, string, e)
    }

    fun d(tag: String, string: String) {
        if (LOG) android.util.Log.d(tag, string)
    }

    fun v(tag: String, string: String) {
        if (LOG) android.util.Log.v(tag, string)
    }

    fun w(tag: String, string: String) {
        if (LOG) android.util.Log.w(tag, string)
    }
}