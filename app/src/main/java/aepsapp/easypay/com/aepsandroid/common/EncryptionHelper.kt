package aepsapp.easypay.com.aepsandroid.common


import android.text.TextUtils
import android.util.Base64
import java.nio.charset.Charset

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Created by ADMIN on 7/19/2016.
 */
object EncryptionHelper {

    private val TAG = "EncryptionHelper"


    fun encrypt(plainText: String, key: String): String {
        if (!TextUtils.isEmpty(plainText)) {
            try {
                val authKey = key.toByteArray()
                val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                val secretKey = SecretKeySpec(authKey, "AES")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                val cipherText = cipher.doFinal(plainText.toByteArray(charset("UTF8")))
                val encryptedString = String(Base64.encode(cipherText, Base64.DEFAULT), Charset.forName("UTF-8"))
                return encryptedString.replace("\n".toRegex(), "")
            } catch (e: Exception) {
                Log.e(TAG, "encrypt: ", e)
            }

        }
        return ""
    }

    fun decrypt(encryptedText: String, key: String): String {
        if (!TextUtils.isEmpty(encryptedText)) {
            try {
                val authKey = key.toByteArray()
                val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
                val secretKey = SecretKeySpec(authKey, "AES")
                cipher.init(Cipher.DECRYPT_MODE, secretKey)
                val cipherText = Base64.decode(encryptedText.toByteArray(charset("UTF8")), Base64.DEFAULT)
                return String(cipher.doFinal(cipherText), Charset.forName("UTF-8"))
            } catch (e: Exception) {
                Log.e(TAG, "decrypt: ", e)
            }

        }
        return ""
    }
}
