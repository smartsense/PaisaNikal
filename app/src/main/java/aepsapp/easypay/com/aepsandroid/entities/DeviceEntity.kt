package aepsapp.easypay.com.aepsandroid.entities

/**
 * Created by Viral on 16-03-2018.
 */
data class DeviceEntity(var id: String, var name: String) {
    /*  var id: String? = null
    var name: String? = null
*/
    override fun toString(): String {
        return name
    }
}