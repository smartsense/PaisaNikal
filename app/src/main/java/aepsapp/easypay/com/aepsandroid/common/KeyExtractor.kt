package aepsapp.easypay.com.aepsandroid.common

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Viral on 10-07-2017.
 */

abstract class KeyExtractor protected constructor() {

    protected var formatter: SimpleDateFormat? = null
    protected abstract fun getKey(data: String): JSONObject


    fun getActualKey(data: String): JSONObject {
        return getKey(data)
    }

    init {
        formatter = SimpleDateFormat("yyyy", Locale.US)
        formatter!!.timeZone = TimeZone.getTimeZone("IST")
    }


}
