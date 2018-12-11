package aepsapp.easypay.com.aepsandroid.interfaces

/**
 * Created by Viral on 23-05-2017.
 */

interface OnOTPChange {
    fun onResendClick()
    fun onOTPEntered(otp: String)
    fun onCloseDialog()
}
