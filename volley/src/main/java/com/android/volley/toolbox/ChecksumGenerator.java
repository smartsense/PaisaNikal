package com.android.volley.toolbox;

import android.util.Base64;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ADMIN on 19-Sep-16.
 */
public class ChecksumGenerator {

    public static final String TYPE = "HmacSHA512";

    public static String generateCheckSum(String data, String key) {

       /* try {
            byte[] decodedKey = Hex.decodeHex(key.toCharArray());
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, TYPE);
            Mac mac = Mac.getInstance(TYPE);
            mac.init(keySpec);
            byte[] dataBytes = data.getBytes("UTF-8");
            byte[] signatureBytes = mac.doFinal(dataBytes);
            Base64.encodeToString(signatureBytes, Base64.NO_WRAP);
            String signature = new String(Base64.encodeBase64(signatureBytes),
                    "UTF-8");
        }
        catch (Exception e)
        {
            Log.e("Exception", "generateCheckSum: ", e);
        }
        return signature;*/


        String checkSum = null;
        try {
            Mac mac = Mac.getInstance(TYPE);
            byte[] decodedKey = Hex.decodeHex(key.toCharArray());
            SecretKeySpec secret = new SecretKeySpec(decodedKey, TYPE);
            mac.init(secret);
            byte[] digest = mac.doFinal(data.getBytes("UTF-8"));
            checkSum = Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.v("TAG", "generateCheckSum : " + e.getMessage(), e);
        }
        return checkSum;
    }
}
