package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_terms_condition.*

class TermsAndConditionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_condition)
        setSupportActionBar(terms_toolbar)
        init()

        terms_toolbar.setNavigationOnClickListener({ finish() })
    }

    private fun init() {

        webView.loadUrl("file:///android_asset/aeps.html")
        webView.clearCache(true)
        webView.clearHistory()
        webView.getSettings().setJavaScriptEnabled(true)
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)

    }

}


