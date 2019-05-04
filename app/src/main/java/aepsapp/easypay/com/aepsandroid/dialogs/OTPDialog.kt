package aepsapp.easypay.com.aepsandroid.dialogs

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("ValidFragment")
/**
 * Created by Viral on 29-12-2016.
 */

class OTPDialog(private val otpChange: OnOTPChange) : DialogFragment() {

    internal var dateFormatter = SimpleDateFormat("mm:ss")

    //private val OTP_TIMEOUT: Long = 300000
    private val OTP_TIMEOUT: Long = 180000
    private var minuteCountDown = OTP_TIMEOUT
    private var timeHandler: Handler? = null
    private var txtOtpTime: TextView? = null
    private var txtOtpDetail: TextView? = null
    private var btnVarify: Button? = null
    private var btnResend: Button? = null
    private var edtOTP: EditText? = null
    private val smsReceiver: BroadcastReceiver? = null
    //private var shake: Animation? = null
    private var othLentgh = 4

    private val timeRunnable = object : Runnable {
        override fun run() {
            minuteCountDown -= 1000
            if (minuteCountDown > 0) {
                txtOtpTime!!.text = dateFormatter.format(minuteCountDown)
                timeHandler!!.postDelayed(this, 1050)
            } else {
                btnResend!!.isEnabled = true
                txtOtpTime!!.visibility = View.GONE
                txtOtpDetail!!.setTextColor(Color.RED)
                txtOtpDetail!!.text = getString(R.string.varificaiton_time_expired)
            }
        }
    }

    fun changeOTPLenght(otpLength: Int) {
        othLentgh = otpLength
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_otp)

        // Log.v(TAG, "Fragment: " + getParentFragment().toString());
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
        //shake = AnimationUtils.loadAnimation(activity, R.anim.shake)
        txtOtpTime = dialog.findViewById(R.id.varifyotp_txttime) as TextView
        txtOtpDetail = dialog.findViewById(R.id.varifyotp_txttimedetail) as TextView
        btnVarify = dialog.findViewById(R.id.varifyotp_btnsubmit) as Button
        btnResend = dialog.findViewById(R.id.varifyotp_btnresend) as Button
        edtOTP = dialog.findViewById(R.id.varifyotp_edtotp) as EditText
        edtOTP!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
        val imgClose = dialog.findViewById(R.id.dialogotp_imgclose) as ImageView
        val txtName = dialog.findViewById(R.id.otp_balance) as TextView
        startTimer()

        imgClose.setOnClickListener {
            otpChange.onCloseDialog()
            dismiss()
            /*if (activity is AddBeneficiaryName) {
                dismiss()
                activity!!.finish()
            } else {
                dismiss()
            }*/
        }
        btnResend!!.setOnClickListener {
            startTimer()
            edtOTP!!.setText("")
            txtOtpDetail!!.text = getString(R.string.time_left)
            //btnResend!!.isEnabled = false
            otpChange.onResendClick()
        }

        btnVarify!!.setOnClickListener(View.OnClickListener {
            val otp = edtOTP!!.text.toString()
            if (TextUtils.isEmpty(otp)) {
                edtOTP!!.error = getString(R.string.enter_otp)
                //edtOTP!!.startAnimation(shake)
                return@OnClickListener
            } else if (otp.length < othLentgh) {
                edtOTP!!.error = getString(R.string.enter_6_digit_otp)
                //edtOTP!!.startAnimation(shake)
                return@OnClickListener
            }
            otpChange.onOTPEntered(otp)
        })
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        timeHandler!!.removeCallbacks(timeRunnable)

        super.onDismiss(dialog)
    }

    private fun startTimer() {
        if (timeHandler == null) {
            timeHandler = Handler()
        } else {
            timeHandler!!.removeCallbacks(timeRunnable)
        }
        txtOtpTime!!.visibility = View.VISIBLE
        minuteCountDown = OTP_TIMEOUT
        timeHandler!!.post(timeRunnable)
        txtOtpDetail!!.setTextColor(Color.BLACK)
    }


    fun showResendOTP() {
        btnResend!!.isEnabled = true
        txtOtpTime!!.visibility = View.GONE
        txtOtpDetail!!.setTextColor(Color.RED)
        txtOtpDetail!!.text = getString(R.string.varificaiton_time_expired)
    }

    fun clearOTP() {
        edtOTP!!.setText("")
    }

    fun setOtp(otp: String) {
        edtOTP!!.setText(otp)
    }

    fun dismissKeyBoard() {
        val manager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(edtOTP!!.windowToken, 0)
    }

    companion object {

        private val TAG = "OTPDialog"
    }
}
