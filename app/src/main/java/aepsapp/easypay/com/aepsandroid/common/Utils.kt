package aepsapp.easypay.com.aepsandroid.common

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.dialogs.DialogAlert
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import android.os.StrictMode
import android.text.InputFilter
import android.text.TextUtils
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Viral on 28-12-2016.
 */

object Utils {

    private val indianLocal = Locale("en", "IN")

    private val orderIdFormatter = SimpleDateFormat("ddMMkkssa", Locale.US)
    private val UNIQ_DIGIT_LENGTH = 10


    //return orderIdFormatter.format(System.currentTimeMillis());
    val uniqueOrderId: String
        get() {
            val random = Random()
            val digits = StringBuilder()
            digits.append(random.nextInt(9) + 1)
            for (i in 1 until UNIQ_DIGIT_LENGTH) {
                digits.append(random.nextInt(10) + 0)
            }
            return digits.toString()
        }

    val aepsOrderId: String
        get() {
            return "AEPS" + System.currentTimeMillis()
        }

    //return orderId for DMT
    fun dmtOrderId(from: String): String {
        val firstChar: Char = from.get(0)
        return (firstChar + System.currentTimeMillis().toString())
    }

    // for backspace
    val editTextFilter: InputFilter
        get() = InputFilter { src, start, end, dst, dstart, dend ->
            if (src == "") {
                return@InputFilter src
            }
            if (src.toString().matches("[\\x00-\\x7F]+".toRegex())) {
                src
            } else src
        }

    fun showToast(context: Context?, message: String, type: Int) {
        val toast = FancyToast.makeText(context, message, FancyToast.LENGTH_SHORT, type, false)
        //toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }


    fun getOrdinalSuffix(number: Int): String {
        if (number in 11..13) {
            return number.toString() + "th"
        }
        return when (number % 10) {
            1 -> number.toString() + "ST"
            2 -> number.toString() + "nd"
            3 -> number.toString() + "rd"
            else -> number.toString() + "th"
        }
    }

    fun getUDID(context: Context): String {
        var udid = Preference.getStringPreference(context, AppConstants.PREF_UDID)
        if (TextUtils.isEmpty(udid)) {
            udid = DeviceInfo.getDeviceId(context)
            Preference.savePreference(context, AppConstants.PREF_UDID, udid)
        }
        return udid
    }

    fun formatAmount(amount: String): String {
        return if (!TextUtils.isEmpty(amount)) {
            try {
                formatAmount(java.lang.Double.parseDouble(amount))
            } catch (e: NumberFormatException) {
                amount
            }

        } else
            formatAmount(0.0)
    }

    fun formatAmount(amount: Double): String {
        //return AppConstants.RUPEE_SYMBOL + String.format(" %.2f", Amount);
        val formatter = NumberFormat.getCurrencyInstance(indianLocal)
        return formatter.format(amount).trim { it <= ' ' }
    }


    fun showAlert(context: Context, message: String) {
        /* new AlertDialog.Builder(context).setMessage(message).setTitle("").setView(R.layout.dialog_single_button)
                .setNegativeButton(context.getString(R.string.ok), btnClick).show();*/
        DialogAlert(context).setMessage(message).setNegativeButton(context.getString(R.string.ok), View.OnClickListener { }).show()

    }


    fun showAlert(context: Context, message: String, btnClick: View.OnClickListener) {
        /* new AlertDialog.Builder(context).setMessage(message).setTitle("").setView(R.layout.dialog_single_button)
                .setNegativeButton(context.getString(R.string.ok), btnClick).show();*/
        try {
            DialogAlert(context).setMessage(message).setNegativeButton(context.getString(R.string.ok), btnClick).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showAlert(context: Context, message: String, title: String, yesClick: View.OnClickListener) {
        try {
            DialogAlert(context).setMessage(message)
                    .setPositiveButton(context.getString(R.string.yes), yesClick)
                    .setNegativeButton(context.getString(R.string.no), View.OnClickListener { }).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAlert(context: Context, message: String, title: String, yesClick: View.OnClickListener, noClick: View.OnClickListener) {
        try {
            DialogAlert(context).setMessage(message)
                    .setPositiveButton(context.getString(R.string.yes), yesClick)
                    .setNegativeButton(context.getString(R.string.no), noClick).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAlertNotDismiss(context: Context, message: String, title: String, yesClick: View.OnClickListener) {
        DialogAlert(context).setMessage(message)
                .setPositiveButton(context.getString(R.string.yes), yesClick)
                .setNegativeButton(context.getString(R.string.no), View.OnClickListener {
                }).show()
    }

    fun dipToPixels(context: Context, dipValue: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics).toInt()
    }

    fun setPolicy() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun isValidEmailAddress(email: String): Boolean {
        var result = false
        result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return result
    }

    inline fun generateURL(context: Context, endPoint: String): String {
        val generator = URLProvider.instance
        return generator.getUrl(context, endPoint)
    }

    inline fun generateContextURL(context: Context, endPoint: String): String {
        val generator = URLProvider.instance
        return generator.getContextUrl(context, endPoint)
    }


    fun hideKeyboard(context: Context, windowToken: IBinder) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
    }


    fun generateToken(length: Int): String {
        //val chars = "abcdefghijklmnopqrstuvwxyz".toCharArray()

        val chars = "01234567891234567891234567".toCharArray()
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until length) {
            val c = chars[random.nextInt(26)]
            sb.append(c)
        }
        Log.e("generateToken*****", sb.toString())
        return sb.toString()
    }

    /*fun generateToken(length: Int): String {
        val random = Random()
        val digits = CharArray(length)
        digits[0] = (random.nextInt(9) + '1') as Char
        for (i in 1 until length) {
            digits[i] = (random.nextInt(10) + '0') as Char
        }
        return String(digits)
    }*/

    fun checkForSpecialCharacter(value: String): Boolean {
        val p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val m = p.matcher(value)
        return m.find()
    }


    fun getAppSignature(context: Context): String {
        val packageInfo: PackageInfo = context.packageManager
                .getPackageInfo(context.packageName,
                        PackageManager.GET_SIGNATURES)
        for (signature in packageInfo.signatures) {
            //  val signatureBytes: Byte[] = signature.toByteArray();
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            var currentSignature: String = Base64.encodeToString(md.digest(), Base64.DEFAULT)
            currentSignature = currentSignature.replace("\n", "")
            //  Log.d("REMOVE ME", "Include this string as a value for SIGNATURE:" + currentSignature);
            //compare signatures
            return currentSignature
        }

        return ""
    }

    fun checkDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE !== 0
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        try {
            context.packageManager.getApplicationInfo(packageName, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            redirectToPlayStore(context, packageName)
            return false
        }

    }

    private fun redirectToPlayStore(context: Context, packageName: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    public fun changeDataFormat(_date: String): String {
        val split = _date.split(" ")
        val changeFrmt = split[0].split("-")
        return changeFrmt[2] + "-" + changeFrmt[1] + "-" + changeFrmt[0]
    }

    public fun isDouble(str: String): Boolean {
        try {
            // check if it can be parsed as any double
            val x = java.lang.Double.parseDouble(str)
            // check if the double can be converted without loss to an int
            return if (x == x.toInt().toDouble()) false else true
            // otherwise, this cannot be converted to an int (e.g. "1.2")
            // short version: return x != (int) x;
        } catch (e: NumberFormatException) {
            return false
        }
    }

}
