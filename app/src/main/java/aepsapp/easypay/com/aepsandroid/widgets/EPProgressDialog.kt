package aepsapp.easypay.com.aepsandroid.widgets

import aepsapp.easypay.com.aepsandroid.R
import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log



/**
 * Created by ADMIN on 05-Sep-16.
 */
class EPProgressDialog(context: Context) : ProgressDialog(context) {
    private val animation: AnimationDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_layout)


        /* ImageView la = (ImageView) findViewById(R.ID.progress_img);
        la.setBackgroundResource(R.drawable.ep_progress);
        animation = (AnimationDrawable) la.getBackground();*/
    }

    override fun dismiss() {
        try {
            if (isShowing)
                super.dismiss()
            // animation.stop();
        } catch (e: Exception) {
            Log.e("espresso dialog", e.toString())
            e.printStackTrace()
        }

    }
}
