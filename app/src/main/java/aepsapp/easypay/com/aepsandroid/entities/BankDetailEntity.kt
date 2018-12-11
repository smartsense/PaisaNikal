package aepsapp.easypay.com.aepsandroid.entities

import java.io.Serializable

/**
 * Created by Viral on 15-03-2018.
 */
class BankDetailEntity:Serializable {

    var createdDt: Any? = null
    var updatedDt: Any? = null
    var createdBy: Any? = null
    var updatedBy: Any? = null
    var status: Int? = null
    var versionNo: Any? = null
    var olderStatus: Any? = null
    var bankId: Int? = null
    var bankName: String? = null
    var bank_Iin: String? = null
    var bankCode: String? = null
    var acquirerId: String? = null

    override fun toString(): String {
        return bankName!!
    }
}