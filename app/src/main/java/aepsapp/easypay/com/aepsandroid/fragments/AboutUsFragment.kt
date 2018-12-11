package aepsapp.easypay.com.aepsandroid.fragments


import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.Log
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.fragment_about_us.*


/**
 * A simple [Fragment] subclass.
 */
class AboutUsFragment : Fragment() {
    lateinit var mAdView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

    override fun onStart() {
        super.onStart()
        about_txtabout.movementMethod = ScrollingMovementMethod()

        //MobileAds.initialize(activity, getString(R.string.google_mob_adz))

        val adView = AdView(activity)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = getString(R.string.google_mob_adz_unit_id)

        mAdView = activity!!.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        /*mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("onAdLoaded", "*****")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.e("onAdFailedToLoad", "*****" + errorCode.toString())
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("onAdOpened", "*****")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.e("onAdLeftApplication", "*****")
            }

            override fun onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.e("onAdClosed", "*****")
            }
        }*/
    }
}
