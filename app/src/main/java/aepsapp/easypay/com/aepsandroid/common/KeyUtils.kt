package aepsapp.easypay.com.aepsandroid.common

import aepsapp.easypay.com.aepsandroid.helper.KeyGeneratorImpl
import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Viral on 08-07-2017.
 */

class KeyUtils : KeyExtractor() {

    public override fun getKey(encData: String): JSONObject {
        var jsonData: JSONObject? = null
        val splitData = encData.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val firstKey = splitData[0]
        val keyResp = splitData[1]
        val secondKey = splitData[2]

        val generator = KeyGeneratorImpl()

        val encKey = generator.getString(firstKey, 4) + generator.getString(secondKey, 4)

        try {
            if (!TextUtils.isEmpty(keyResp)) {
                jsonData = JSONObject(EncryptionHelper.decrypt(keyResp, encKey))
                val tokenData = jsonData.getString("token")

                val token = generator.getString(tokenData, 6) + formatter!!.format(System.currentTimeMillis())

                val keyData = jsonData.getString("data")

                if (!TextUtils.isEmpty(keyData)) {
                    val actualyKey = EncryptionHelper.decrypt(keyData, token)
                    jsonData.put("DATA", actualyKey)
                }
            }

        } catch (e: JSONException) {
            Log.e(TAG, "getKey: ", e)
        }

        return jsonData!!
    }

    companion object {

        private val TAG = "KeyUtils"
    }


}
