package aepsapp.easypay.com.aepsandroid.helper

import aepsapp.easypay.com.aepsandroid.interfaces.KeyGeneratorEP

/**
 * Created by Viral on 10-07-2017.
 */

class KeyGeneratorImpl : KeyGeneratorEP {

    override fun getString(data: String, length: Int): String {
        return data.substring(0, length) + data.substring(data.length - length, data.length)
    }
}
