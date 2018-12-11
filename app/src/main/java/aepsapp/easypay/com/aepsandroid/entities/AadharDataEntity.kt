package aepsapp.easypay.com.aepsandroid.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class AadharDataEntity : Serializable {

    @SerializedName("AadhaarName")
    var aadhaarName: String? = null

    @SerializedName("AadhaarPhoto")
    var aadhaarPhoto: String? = null

    @SerializedName("City")
    var city: String? = null

    @SerializedName("ContactPerson")
    var contactPerson: String? = null

    @SerializedName("DOB")
    var dOB: String? = null

    @SerializedName("District")
    var district: String? = null

    @SerializedName("Email")
    var email: String? = null

    @SerializedName("Gender")
    var gender: String? = null

    @SerializedName("HouseNo")
    var houseNo: String? = null

    @SerializedName("LandMark")
    var landMark: String? = null

    @SerializedName("Location")
    var location: String? = null

    @SerializedName("Phone")
    var phone: String? = null

    @SerializedName("PinCode")
    var pinCode: String? = null

    @SerializedName("PostOffice")
    var postOffice: String? = null

    @SerializedName("State")
    var state: String? = null

    @SerializedName("Street")
    var street: String? = null

    @SerializedName("SubDistrict")
    var subDistrict: String? = null

    @SerializedName("AADHAR_VERIFICATIONCODE")
    var aADHARVERIFICATIONCODE: Int? = null

    @SerializedName("AadhaarNo")
    var aadhaarNo: String? = null

    @SerializedName("ReqRefNum")
    var reqRefNum: String? = null

}