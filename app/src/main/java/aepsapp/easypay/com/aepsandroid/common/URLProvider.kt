package aepsapp.easypay.com.aepsandroid.common

import aepsapp.easypay.com.aepsandroid.BuildConfig
import android.content.Context

/**
 * Created by Viral on 28-04-2017.
 */

abstract class URLProvider {

    fun getUrl(context: Context, endPoint: String): String {
        return generateURL(context, endPoint)
    }

    fun getContextUrl(context: Context, endPoint: String): String {
        return generateContextURL(context, endPoint)
    }

    var BASE_ADDRESS_HOST = BuildConfig.HOST

    //var BASE_ADDRESS_HOST = Preference.getStringPreference()

    fun getBaseAddressHost(context: Context): String {
        return Preference.getStringPreference(context, AppConstants.BASE_URL)
    }

    fun setBaseAddressHost(context: Context, url: String) {
        //AppConstants.BASE_URL = url
        Preference.savePrefernceUrl(context, AppConstants.BASE_URL, url)
    }

    fun setBaseContext(context: Context, ctxUrl: String) {
        Preference.savePrefernceUrl(context, AppConstants.BASE_URL_CONTEXT, ctxUrl)
    }

    protected abstract fun generateURL(context: Context, endPoint: String): String

    protected abstract fun generateContextURL(context: Context, endPoint: String): String

    companion object {

        private var generator: URLProvider? = null

        val instance: URLProvider
            get() {
                if (generator == null)
                    generator = URLGenerator()
                return generator as URLGenerator
            }

    }
}
