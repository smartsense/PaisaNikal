package aepsapp.easypay.com.aepsandroid.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AgentBankDetail : Parcelable {

    @SerializedName("createdDt")
    @Expose
    var createdDt: String = ""
    @SerializedName("updatedDt")
    @Expose
    var updatedDt: String = ""
    @SerializedName("createdBy")
    @Expose
    var createdBy: Int? = null
    @SerializedName("updatedBy")
    @Expose
    var updatedBy: Int? = null
    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("versionNo")
    @Expose
    var versionNo: Int? = null
    @SerializedName("olderStatus")
    @Expose
    var olderStatus: String = ""
    @SerializedName("bankDetailId")
    @Expose
    var bankDetailId: Int? = null
    @SerializedName("bankName")
    @Expose
    var bankName: String = ""
    @SerializedName("ifsc")
    @Expose
    var ifsc: String = ""
    @SerializedName("accountNo")
    @Expose
    var accountNo: String = ""
    @SerializedName("bankHolderName")
    @Expose
    var bankHolderName: String = ""
    @SerializedName("updatedByName")
    @Expose
    var updatedByName: String = ""
    @SerializedName("createdByName")
    @Expose
    var createdByName: String = ""

    protected constructor(`in`: Parcel) {
        this.createdDt = `in`.readValue(String::class.java.classLoader) as String
        this.updatedDt = `in`.readValue(String::class.java.classLoader) as String
        this.createdBy = `in`.readValue(Int::class.java.classLoader) as Int
        this.updatedBy = `in`.readValue(Int::class.java.classLoader) as Int
        this.status = `in`.readValue(Int::class.java.classLoader) as Int
        this.versionNo = `in`.readValue(Int::class.java.classLoader) as Int
        this.olderStatus = `in`.readValue(String::class.java.classLoader) as String
        this.bankDetailId = `in`.readValue(Int::class.java.classLoader) as Int
        this.bankName = `in`.readValue(String::class.java.classLoader) as String
        this.ifsc = `in`.readValue(String::class.java.classLoader) as String
        this.accountNo = `in`.readValue(String::class.java.classLoader) as String
        this.bankHolderName = `in`.readValue(String::class.java.classLoader) as String
        this.updatedByName = `in`.readValue(String::class.java.classLoader) as String
        this.createdByName = `in`.readValue(String::class.java.classLoader) as String
    }

    constructor() {}

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(createdDt)
        dest.writeValue(updatedDt)
        dest.writeValue(createdBy)
        dest.writeValue(updatedBy)
        dest.writeValue(status)
        dest.writeValue(versionNo)
        dest.writeValue(olderStatus)
        dest.writeValue(bankDetailId)
        dest.writeValue(bankName)
        dest.writeValue(ifsc)
        dest.writeValue(accountNo)
        dest.writeValue(bankHolderName)
        dest.writeValue(updatedByName)
        dest.writeValue(createdByName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AgentBankDetail> = object : Parcelable.Creator<AgentBankDetail> {


            override fun createFromParcel(`in`: Parcel): AgentBankDetail {
                return AgentBankDetail(`in`)
            }

            override fun newArray(size: Int): Array<AgentBankDetail?> {
                return arrayOfNulls(size)
            }

        }
    }

}