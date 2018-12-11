package aepsapp.easypay.com.aepsandroid.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by Viral on 26-03-2018.
 */
class TransactionEntity : Serializable {

    @SerializedName("STAN")
    var stan: String? = null

    @SerializedName("RRN")
    var rrn: String? = null

    @SerializedName("Aadhar")
    var aadhar: String? = null

    @SerializedName("IIN")
    var iin: String? = null

    @SerializedName("TxnAmount")
    var txnAmount: String? = null

    @SerializedName("ResponseCode")
    var responseCode: String? = null

    @SerializedName("AccountType")
    var accountType: String? = null

    @SerializedName("BalanceType")
    var balanceType: String? = null

    @SerializedName("CurrancyCode")
    var currancyCode: String? = null

    @SerializedName("BalanceIndicator")
    var balanceIndicator: String? = null

    @SerializedName("BalanceAmount")
    var balanceAmount: String? = null

    @SerializedName("AccountTypeLedger")
    var accountTypeLedger: String? = null

    @SerializedName("BalanceTypeLedger")
    var balanceTypeLedger: String? = null

    @SerializedName("CurrancyCodeLedger")
    var currancyCodeLedger: String? = null

    @SerializedName("BalanceIndicatorLedger")
    var balanceIndicatorLedger: String? = null

    @SerializedName("BalanceAmountLedger")
    var balanceAmountLedger: String? = null

    @SerializedName("AccountTypeActual")
    var accountTypeActual: String? = null

    @SerializedName("BalanceTypeActual")
    var balanceTypeActual: String? = null

    @SerializedName("CurrancyCodeActual")
    var currancyCodeActual: String? = null

    @SerializedName("BalanceIndicatorActual")
    var balanceIndicatorActual: String? = null

    @SerializedName("BalanceAmountActual")
    var balanceAmountActual: String? = null

    @SerializedName("Status")
    var status: String? = null

    @SerializedName("UIDAIAuthenticationCode")
    var uidaiAuthenticationCode: String? = null

    @SerializedName("RetailerTxnId")
    var retailerTxnId: String? = null

    @SerializedName("TerminalId")
    var terminalId: String? = null

    @SerializedName("txnDate")
    var txnDate: String? = null

    /*@SerializedName("txnCharge")
    var txnCharge: Double = 0.0*/

    @SerializedName("paidAmount")
    var paidAmount: Double? = null

    /*@SerializedName("CARD_BALANCE")
    var cardBalance: Double = 0.0*/

}