package aepsapp.easypay.com.aepsandroid.fragments


import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.activities.MainActivity
import aepsapp.easypay.com.aepsandroid.common.Log
import aepsapp.easypay.com.aepsandroid.common.Utils
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.fragment_feedback.*

/**
 * A simple [Fragment] subclass.
 */
class FeedbackFragment : Fragment() {

    lateinit var mAdView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onStart() {
        super.onStart()

        feedback_btnsubmit!!.setOnClickListener { submitFeedback() }

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

    private fun submitFeedback() {
        val feedback = feedback_edtfeedback!!.text.toString()
        if (TextUtils.isEmpty(feedback.trim())) {
            feedback_edtfeedback!!.error = getString(R.string.enter_feedback)
            return
        }

        Utils.showAlert(activity!!, getString(R.string.feedback_submitted), View.OnClickListener {
            (activity as MainActivity).changeFragment(HomeFragment())
        })
    }

    /*private fun submitFeedback() {

        val feedback = edtFeedback!!.text.toString()
        if (TextUtils.isEmpty(feedback.trim { it <= ' ' })) {
            edtFeedback!!.error = getString(R.string.enter_feedback)
            edtFeedback!!.startAnimation(shake)
            return
        }
        try {
            val reqObj = JSONObject()
            reqObj.put("name", Preference.getStringPreference(activity, AppConstants.PREF_MERCHANT_NAME))
            reqObj.put("mobileNumber", Preference.getStringPreference(activity, AppConstants.PREF_MOBILE))
            reqObj.put("feedback", feedback)

            VolleyJsonRequest.getInstance(activity).request(Utils.generateURL(URLGenerator.URL_FEEDBACK), reqObj, feedbackResp, true)
        } catch (e: JSONException) {
            Log.e(TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity, getString(R.string.internet_not_available))
        }

    }*/

}
