package aepsapp.easypay.com.aepsandroid.entities

import java.io.Serializable

/**
 * Created by Viral on 16-03-2017.
 */

class EPCoreEntity<T> : Serializable {

    var HEADER: EPHeader? = null
    var DATA: T? = null


    class EPHeader : Serializable {
        var TXN_AMOUNT: Double = 0.toDouble()
        //service type
        var ST: String? = null
        //Agent Code
        var AID: String? = null
        //Partner Code
        var MID: String? = null
        //Utility Code
        var OP: String? = null
        var PAYABLE_AMOUNT: Double = 0.toDouble()
        var CUSTOMER_CHARGE: Double = 0.toDouble()
        var totalPrice: Double = 0.toDouble()
        var PAYMENT: PaymentEntity? = null
        var ORDER_ID: String? = null
        var REQUEST_ID: Long = 0
        var UDID: String? = null

    }


}
