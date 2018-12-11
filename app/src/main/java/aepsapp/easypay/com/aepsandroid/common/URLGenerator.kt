package aepsapp.easypay.com.aepsandroid.common

import android.content.Context

/**
 * Created by Viral on 13-03-2018.
 */
class URLGenerator : URLProvider() {

    override fun generateURL(context: Context, endPoint: String): String {
        //return Preference.getStringPreferenceUrl(context, AppConstants.BASE_URL) + getBaseAddress(context) + endPoint
        return BASE_ADDRESS_HOST + getBaseAddress(context) + endPoint
    }

    override fun generateContextURL(context: Context, endPoint: String): String {
        //return Preference.getStringPreferenceUrl(context, AppConstants.BASE_URL) + getBaseUrlContext(context) + endPoint
        return BASE_ADDRESS_HOST + getBaseUrlContext(context) + endPoint
    }

    companion object {

        //private const val BASE_CONTEXT = "app-mpm/"
        //private const val BASE_CONTEXT = "web-mpm/"

        //new context
        //private const val BASE_CONTEXT = "aepsappctx/"
        //http://192.168.10.32:8080/web-mpm

        private fun getBaseUrlContext(context: Context): String {
            return Preference.getStringPreferenceUrl(context, AppConstants.BASE_URL_CONTEXT)
        }

        private fun getBaseAddress(context: Context): String {
            return getBaseUrlContext(context) + "mobileapp/"
        }

        //var BASE_ADDRESS = getBaseUrlContext(context) + "mobileapp/"

        const val URL_GET_CONFIG: String = "getconfig"
        const val URL_LOGIN: String = "login"
        const val URL_GET_SERVICES = "getServiceCategories"
        const val URL_SEARCH_CUSTOMER: String = "customersearch"
        const val URL_ADD_CUSTOMER: String = "addcustomer"
        const val URL_GET_BANKS: String = "getallbank"
        const val URL_DEVICE_LIST: String = "getdeviceList"
        const val URL_CUSTOMER_CHARGE: String = "getcustomercharge"
        const val URL_GET_BALANCE: String = "aepsgetbalance"
        const val URL_WITHDRAWAL: String = "aepsaccountwithdrawal"
        const val URL_DEPOSIT: String = "aepsaccountdeposit"
        const val URL_VALIDATE_DEVICE: String = "validateDevice"
        const val URL_TERMS_CONDITION: String = "tncmaha.jsp"
        const val URL_GET_RD_HASH: String = "getrdhashdata"
        const val URL_AEPS_LIMIT: String = "getagentaepsworkingbalance"
        const val URL_GET_AADHAR_DETAILS = "getAadharDataWithBiometric"
        const val URL_ADD_EKYC_CUSTOMER = "addKyccustomer"
        const val URL_ACK_AADHAR_DTL: String = "acknowledgeAadharDtlEpMoney"
        const val IMAGE_PATH = "getAepsApmDocument?documentPath="
        const val URL_OTP = "sendotpbeforlogin"
        const val VERIFY_OTP = "verifyotpbeforlogin"
        const val URL_FORGOT_PASSWORD = "forgotpasswordwithotp"
        const val URL_GET_GEO_DETAILS = "getAgentgeodetails"
        const val URL_SSO_TOKEN = "/varify/ssotoken"
        const val URL_LOGIN_WITHOUT_OTP = "login"

        //DMT
        const val URL_SEARCH_CUSTOMER_DMT = "general/dmt-customersearch"
        const val URL_OTP_GENERATE_DMT = "general/dmt-generateotp"
        const val URL_OTP_VERIFY_DMT = "general/dmt-verifyotp"
        const val URL_ADD_CUSTOMER_DMT = "general/dmt-addcustomer"
        const val URL_DELETE_BENE_DMT = "general/dmt-deletebeneficiary"
        const val URL_MONEY_TRANSFER_DMT = "txn/dmt-moneytransfer"
        const val URL_ADD_BENEFICIARY_DMT = "general/dmt-addbeneficiary"
        const val URL_TRANSACTION_DMT = "txn/dmt-moneytransfer"
        const val URL_TRANSACTION_HISTORY_DMT = "general/dmt-transactionhistory"
        //const val URL_TRANSACTION_HISTORY_DMT = "txn/list-transaction"
        const val URL_TRANSACTION_REFUND_DMT = "txn/dmt-refund"
        const val URL_TRANSACTION_CHARGE_DMT = "txn/dmt-getcharges"
        const val URL_BENELIST_DMT = "general/list-beneficiary"
        const val URL_WORKING_BALANCE = "/agentWorkingBalance"
        //mobileapp/txn/dmt-moneytransfer
    }
}