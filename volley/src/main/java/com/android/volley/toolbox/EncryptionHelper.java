package com.android.volley.toolbox;


import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ADMIN on 7/19/2016.
 */
public class EncryptionHelper {

    private static final String TAG = "EncryptionHelper";
    private static final String ALGO = "AES";

    public static String encrypt(byte[] plainText, String key) {
        if (!TextUtils.isEmpty(key)) {
            try {
                byte[] authKey = key.getBytes();
                Cipher cipher = Cipher.getInstance(ALGO);
                SecretKeySpec secretKey = new SecretKeySpec(authKey, ALGO);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] cipherText = cipher.doFinal(plainText);
                String encryptedString = new String(Base64.encode(cipherText, Base64.DEFAULT), "UTF-8");
                return encryptedString.replaceAll("\n", "");
            } catch (Exception e) {
                Log.e(TAG, "encrypt: ", e);
            }
        }
        return null;
    }

    public static String encrypt(String plainText, String key) {
        if (!TextUtils.isEmpty(plainText) && !TextUtils.isEmpty(key))
            return encrypt(plainText.getBytes(), key);
        else
            return null;
    }

    public static String decrypt(String encryptedText, String key) {
        if (!TextUtils.isEmpty(key)) {
            try {
                byte[] authKey = key.getBytes();
                Cipher cipher = Cipher.getInstance(ALGO);
                SecretKeySpec secretKey = new SecretKeySpec(authKey, ALGO);
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] cipherText = Base64.decode(encryptedText.getBytes("UTF8"), Base64.DEFAULT);
                String decryptedString = new String(cipher.doFinal(cipherText), "UTF-8");
                return decryptedString;
            } catch (Exception e) {
                Log.e(TAG, "decrypt: ", e);
            }
        }
        return null;
    }
}
