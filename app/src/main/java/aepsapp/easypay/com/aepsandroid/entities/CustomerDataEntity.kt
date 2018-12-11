package aepsapp.easypay.com.aepsandroid.entities

import com.google.gson.annotations.SerializedName


class CustomerDataEntity {


    @SerializedName("SENDER_TITLE")
    var sENDERTITLE: String? = null

    @SerializedName("SEDNER_FNAME")
    var sEDNERFNAME: String? = null

    @SerializedName("SENDER_LNAME")
    var sENDERLNAME: String? = null

    @SerializedName("SENDER_CUSTTYPE")
    var sENDERCUSTTYPE: String? = null

    @SerializedName("SEDNER_GENDER")
    var sEDNERGENDER: String? = null

    @SerializedName("SENDER_EMAIL")
    var sENDEREMAIL: String? = null

    @SerializedName("SENDER_MOBILENO")
    var sENDERMOBILENO: Int? = null

    @SerializedName("SENDER_ALTMOBILENO")
    var sENDERALTMOBILENO: Int? = null

    @SerializedName("SENDER_ADDRESS1")
    var sENDERADDRESS1: String? = null

    @SerializedName("SENDER_ADDRESS2")
    var sENDERADDRESS2: String? = null

    @SerializedName("STATE")
    var sTATE: String? = null

    @SerializedName("CITY")
    var cITY: String? = null

    @SerializedName("PINCODE")
    var pINCODE: String? = null

    @SerializedName("SENDER_AVAILBAL")
    var sENDERAVAILBAL: Int? = null

    @SerializedName("SENDER_MONTHLYBAL")
    var sENDERMONTHLYBAL: Int? = null

    @SerializedName("SENDER_VERIFICATIONCODE")
    var sENDERVERIFICATIONCODE: Boolean? = null

    @SerializedName("SENDER_KYCSTATUS")
    var sENDERKYCSTATUS: String? = null

    @SerializedName("SENDER_KYCTYPE")
    var sENDERKYCTYPE: String? = null

    @SerializedName("SENDER_REGISTERDATE")
    var sENDERREGISTERDATE: String? = null

    @SerializedName("SENDER_ACTIVATIONDATE")
    var sENDERACTIVATIONDATE: String? = null

    @SerializedName("SENDER_DOB")
    var sENDERDOB: String? = null

    @SerializedName("PREPAID_INSTRUMENTFLAG")
    var pREPAIDINSTRUMENTFLAG: Boolean? = null

    @SerializedName("PANCARD_FLAG")
    var pANCARDFLAG: Boolean? = null

    @SerializedName("PANCARD_NO")
    var pANCARDNO: String? = null

    @SerializedName("CUSTDOC_REJECT_FLAG")
    var cUSTDOCREJECTFLAG: Boolean? = null

    @SerializedName("PANCARD_REJECT_REASON")
    var pANCARDREJECTREASON: String? = null

    @SerializedName("PANCARD_REJET_REMARKS")
    var pANCARDREJETREMARKS: String? = null

    @SerializedName("PANCARD_STATUS")
    var pANCARDSTATUS: String? = null
}