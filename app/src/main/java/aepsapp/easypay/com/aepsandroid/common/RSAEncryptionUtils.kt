package aepsapp.easypay.com.aepsandroid.common

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class RSAEncryptionUtils public constructor() {
    private var kpg: KeyPairGenerator? = null
    private var kp: KeyPair? = null
    private var publicKey: PublicKey? = null
    private var privateKey: PrivateKey? = null


    init {
        generateKeyPair()
    }


    @Throws(Exception::class)
    private fun generateKeyPair() {
        kpg = KeyPairGenerator.getInstance(CRYPTO_METHOD)
        kpg!!.initialize(CRYPTO_BITS)
        kp = kpg!!.genKeyPair()
        publicKey = kp!!.public
        privateKey = kp!!.private
    }

    fun encryptWithPublic(publicKey: PublicKey, data: String): String? {
        try {
            val cipher = Cipher.getInstance(ALGO)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "encryptWithPublic", e)
        }

        return null
    }

    @Throws(Exception::class)
    fun decryptWithPrivate(result: String): String? {
        try {
            val cipher = Cipher.getInstance(ALGO)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedBytes = cipher.doFinal(Base64.decode(result, Base64.NO_WRAP))
            return String(decryptedBytes)
        } catch (e: Exception) {
            Log.e(TAG, "encryptWithPublic", e)
        }

        return null
    }


    fun getPublicKey(option: String): String? {
        when (option) {
            PKCS1_PEM -> {
                var pkcs1pem = "-----BEGIN RSA PUBLIC KEY-----\n"
                pkcs1pem += Base64.encodeToString(publicKey!!.encoded, Base64.NO_WRAP)
                pkcs1pem += "-----END RSA PUBLIC KEY-----"
                return pkcs1pem
            }

            PKCS8_PEM -> {
                var pkcs8pem = "-----BEGIN PUBLIC KEY-----\n"
                pkcs8pem += Base64.encodeToString(publicKey!!.encoded, Base64.NO_WRAP)
                pkcs8pem += "-----END PUBLIC KEY-----"

                return pkcs8pem
            }

            BASE64_TYPE -> return Base64.encodeToString(publicKey!!.encoded, Base64.NO_WRAP)
            else -> return null
        }

    }

    fun stringToPublicKey(publicKeyString: String): PublicKey? {
        var publicKeyString = publicKeyString
        try {
            if (publicKeyString.contains("-----BEGIN PUBLIC KEY-----") || publicKeyString.contains("-----END PUBLIC KEY-----"))
                publicKeyString = publicKeyString.replace(
                        "-----BEGIN PUBLIC KEY-----", "").replace(
                        "-----END PUBLIC KEY-----", "")
            val keyBytes = Base64.decode(publicKeyString, Base64.NO_WRAP)
            val spec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")

            return keyFactory.generatePublic(spec)

        } catch (e: Exception) {
            Log.e(TAG, "stringToPublicKey", e)
            return null
        }

    }

    companion object {

        //private val ALGO = "RSA/ECB/PKC8"
        //private val ALGO = "RSA/ECB/PKCS5Padding"
        private val ALGO = "RSA/ECB/PKCS1Padding"

        private val TAG = "EPRSAEncryptionUtils"

        private val CRYPTO_METHOD = "RSA"
        private val CRYPTO_BITS = 2048

        private var epRSAInstance: RSAEncryptionUtils? = null

        val PKCS1_PEM = "pkcs1-pem"
        val PKCS8_PEM = "pkcs8-pem"
        val BASE64_TYPE = "base64"

        val instance: RSAEncryptionUtils?
            get() {
                try {
                    epRSAInstance = epRSAInstance ?: RSAEncryptionUtils()
                } catch (e: Exception) {
                    Log.e(TAG, "getInstance", e)
                }
                return epRSAInstance
            }
    }

}
