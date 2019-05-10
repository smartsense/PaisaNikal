package aepsapp.easypay.com.aepsandroid.common

import aepsapp.easypay.com.aepsandroid.R
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.Fabric
import java.util.*


/**
 * Created by Viral on 10-03-2018.
 */
class AEPSApplication : Application() {

    var encrptedString: String? = null
    var mobileKey: String? = null
    var hmacKey: String? = null
    var apiTime: Long = 0
    private var isAppisOnForeground = false

    override fun onCreate() {
        super.onCreate()
        if (DeviceInfo.isInternetConnected(applicationContext)) {
            try {
                Fabric.with(this, Crashlytics())
            } catch (e: Exception) {
            }
        }

        //URLProvider.instance.setBaseAddressHost(applicationContext, AppConstants.BASE_URL)
        URLProvider.instance.setBaseContext(applicationContext, AppConstants.BASE_URL_CONTEXT)

        try {
            MobileAds.initialize(this, getString(R.string.google_mob_adz))
            Fresco.initialize(this)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        /* val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
         Fabric.with(this, Crashlytics.Builder().core(core).build())
         apiTime = System.currentTimeMillis()
         //  Fabric.with(this, new Crashlytics());

         SharedPreferenceHelper.initSharedPreferenceHelper(this)
         UiUtil.initLogos()
         //For Firebase Configuration (UAT SDK)
         *//*  FirebaseOptions options = new FirebaseOptions.Builder().
                setApplicationId("1:696734674084:android:922632c1167f6382")
                .setApiKey("AIzaSyA_pvWU1RIgtS5wQkvG8tCcKXWIRSD8Ss4")
                .setDatabaseUrl("https://firedemo-49b5d.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(this, options, getString(R.string.app_name));*//*

        //For Firebase Configuration (Production/Live SDK)
        val options = FirebaseOptions.Builder()
                .setApplicationId("1:388503048082:android:1f0ecfca2c24532a")
                .setApiKey("AIzaSyBAYBLw5xWyJU_uSOIFHayRSpBduDw-2D0")
                .setDatabaseUrl("https://firedemo-49b5d.firebaseio.com/")
                .build()
        FirebaseApp.initializeApp(this, options, getString(R.string.app_name))*/

    }

    public override fun attachBaseContext(base: Context) {
        MultiDex.install(base)
        super.attachBaseContext(base)
    }


    /*  fun setCrashlyticsInfo() {
          Crashlytics.setUserIdentifier(Preference.getStringPreference(applicationContext, AppConstants.PREF_MOBILE))
          Crashlytics.setUserName(Preference.getStringPreference(applicationContext, AppConstants.PREF_MERCHANT_NAME))
      }
  */

    fun changeLanguage(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        val context = createConfigurationContext(config)

        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun checkAPITime(): Boolean {
        var isValid = false
        val currentTime = System.currentTimeMillis()
        if (currentTime - apiTime <= 300000) {
            apiTime = currentTime
            isValid = true
        }
        return isValid
    }
}