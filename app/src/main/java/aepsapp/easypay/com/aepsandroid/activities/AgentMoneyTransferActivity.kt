package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.AgentBankDetail
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.android.volley.VolleyLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.agent_money_transfer.*
import org.json.JSONException
import org.json.JSONObject

class AgentMoneyTransferActivity : AppCompatActivity() {

    private var TOTAL_CHRGAMOUNT = 0.0
    private var CHRG_VALUE = 0.0
    private var PAYABLE_AMOUNT = 0.0
    private var alertDialog: AlertDialog? = null
    private var agentDetailsPre = AgentBankDetail()
    private var balance = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agent_money_transfer)

        val data = intent.extras
        if (data != null)
            balance = data.getDouble("balance")

        setSupportActionBar(toolbarMoneyTrnsfer)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Money Transfer")

        getData()

        radioCheckGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.transferBank -> {
                    btnTransferBank.isEnabled = true
                    btnTransferWallet.isEnabled = false
                }
                R.id.transferWallet -> {
                    btnTransferWallet.isEnabled = true
                    btnTransferBank.isEnabled = false
                }
            }
        }

        btnTransferBank.setOnClickListener {

            val gson = Gson()
            val dataes = Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.BANKDETAILS)

            if (dataes != null) {
                agentDetailsPre = gson.fromJson(dataes, AgentBankDetail::class.java)
                if (agentDetailsPre != null)
                    if (!TextUtils.isEmpty(aepssearch_edtbankAmount.text.toString()) && balance >= aepssearch_edtbankAmount.text.toString().toDouble() && aepssearch_edtbankAmount.text.toString().toDouble() >= 5000)
                        callChargeAPI(agentDetailsPre)
                    else
                        Utils.showToast(this@AgentMoneyTransferActivity, "Please enter valid amount", FancyToast.ERROR)
            } else
                getBankDetails()
        }

        btnTransferWallet.setOnClickListener {
            if (!TextUtils.isEmpty(aepssearch_edtbankAmount.text.toString()) && balance >= aepssearch_edtbankAmount.text.toString().toDouble() && aepssearch_edtbankAmount.text.toString().toDouble() >= 100)
                showDialogWallet()
            else
                Utils.showToast(this@AgentMoneyTransferActivity, "Please enter valid amount", FancyToast.ERROR)
        }

        history.setOnClickListener {
            val intent = Intent(this@AgentMoneyTransferActivity, AgentTransactionHistory::class.java)
            startActivity(intent)
        }
    }

    private fun showDialogWallet() {
        val dialogBuilder = AlertDialog.Builder(this)
        // ...Irrelevant code for customizing the buttons and title
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.transfer_funds_wallet_dialog, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog!!.setCancelable(false)
        //alertDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog!!.show()

        val transferAmount = dialogView.findViewById(R.id.transferAmount) as TextView
        val agentCharge = dialogView.findViewById(R.id.agentCharge) as TextView
        val txttitle = dialogView.findViewById(R.id.txttitle) as TextView
        val totalAmount = dialogView.findViewById(R.id.totalAmount) as TextView
        val btnImps = dialogView.findViewById(R.id.btnImps) as Button
        val btnNeft = dialogView.findViewById(R.id.btnNeft) as Button
        val btncancel = dialogView.findViewById(R.id.btncancel) as ImageButton

        txttitle.setText("Fund Transfer to wallet")

        transferAmount.setText(aepssearch_edtbankAmount.text.toString())
        agentCharge.setText("0.0")
        totalAmount.setText(aepssearch_edtbankAmount.text.toString())

        btnNeft.setOnClickListener {
            callWalletTransfer()
        }

        btncancel.setOnClickListener { alertDialog!!.dismiss() }
        btnImps.setOnClickListener { alertDialog!!.dismiss() }

    }

    private fun getBankDetails() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_AGENT_CODE)
            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_SCREEN))
            data.addProperty("agentMstId", Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_AGENT_ID))
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data
            VolleyJsonRequest.getInstance(this@AgentMoneyTransferActivity).request(Utils.generateURL(this@AgentMoneyTransferActivity, URLGenerator.URL_AGENT_BANKDETAILS), objCore,
                    object : VolleyJsonRequest.OnJsonResponse {
                        override fun responseReceived(jsonObj: JSONObject) {
                            if (jsonObj.getInt("RESP_CODE") == 300) {
                                var agentDetailsData: AgentBankDetail = AgentBankDetail()
                                if (jsonObj.has("DATA")) {
                                    val data = jsonObj.getJSONObject("DATA")
                                    if (data.has("agentDetails")) {
                                        val agentBankDetail = data.getJSONObject("agentDetails")
                                        if (agentBankDetail.has("agentBankDetail")) {
                                            val agentBankDetail = agentBankDetail.getJSONObject("agentBankDetail")
                                            val gson = Gson()
                                            val listType = object : TypeToken<AgentBankDetail>() {}.type
                                            agentDetailsData = gson.fromJson<AgentBankDetail>(agentBankDetail.toString(), listType)

                                            Preference.savePreference(this@AgentMoneyTransferActivity, AppConstants.BANKDETAILS, gson.toJson(agentDetailsData))
                                        }
                                    }
                                }
                            }
                        }

                        override fun errorReceived(code: Int, message: String) {

                        }
                    }, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AgentMoneyTransferActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private fun callChargeAPI(agentDetails: AgentBankDetail) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.OP = "AEPS"
            header.ST = "REMWALTOACC"
            header.AID = Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE)
            header.TXN_AMOUNT = java.lang.Double.parseDouble(aepssearch_edtbankAmount.text.toString())

            val data = JsonObject()
            data.addProperty("KEY_KYC_STATUS", Preference.getStringPreference(this, AppConstants.CUSTOMER_KYC_STATUS))
            data.addProperty("BENE_BANKNAME", agentDetails.bankName)
            data.addProperty("BANKIFSC_CODE", agentDetails.ifsc)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).requestDMTCharge(Utils.generateURL(this, URLGenerator.URL_TRANSACTION_CHARGE_DMT), objCore, chargeResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val chargeResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            TOTAL_CHRGAMOUNT = jsonObj.getDouble("TOTAL_CHRGAMOUNT")
            PAYABLE_AMOUNT = jsonObj.getDouble("PAYABLE_AMOUNT")
            if (jsonObj.has("CHRG_LIST")) {
                val listCharge = jsonObj.getJSONArray("CHRG_LIST")
                val obj = listCharge.getJSONObject(0)
                CHRG_VALUE = obj.getDouble("chrgValue")

            }
            showDialogProceed(agentDetailsPre)
            //callBankTransfer()
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(this@AgentMoneyTransferActivity, message, View.OnClickListener {
            })
        }
    }

    private fun callBankTransfer(agentDetails: AgentBankDetail) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "REMWALTOWAL"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_AGENT_CODE)
            header.CUSTOMER_CHARGE = CHRG_VALUE
            header.PAYABLE_AMOUNT = aepssearch_edtbankAmount.text.toString().toDouble()
            header.REQUEST_ID = System.currentTimeMillis()
            header.TXN_AMOUNT = aepssearch_edtbankAmount.text.toString().toDouble()
            header.UDID = Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_UDID)
            header.totalPrice = 0.0
            val data = JsonObject()
            val allIds = Utils.aepsOrderId
            data.addProperty("userId", Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_SCREEN))
            data.addProperty("BENE_BANKNAME", agentDetails.bankName)
            data.addProperty("TRANSFER_TYPE", "NEFT")
            data.addProperty("BENE_NAME", agentDetails.bankHolderName)
            data.addProperty("CN", allIds)
            data.addProperty("BANKIFSC_CODE", agentDetails.ifsc)
            data.addProperty("BANK_ACCOUNTNO", agentDetails.accountNo)
            data.addProperty("REMITTER_TO_BENEINFO", allIds)
            data.addProperty("ORDER_ID", allIds)
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data
            VolleyJsonRequest.getInstance(this@AgentMoneyTransferActivity).request(Utils.generateURL(this@AgentMoneyTransferActivity, URLGenerator.URL_TRANSFER_TO_BANK), objCore,
                    object : VolleyJsonRequest.OnJsonResponse {
                        override fun responseReceived(jsonObj: JSONObject) {
                            if (jsonObj.getInt("RESP_CODE") == 300) {
                                Utils.showAlert(this@AgentMoneyTransferActivity, jsonObj.getString("RESP_MSG"), View.OnClickListener {
                                    alertDialog!!.dismiss()
                                    aepssearch_edtbankAmount.setText("")
                                })
                            }
                        }

                        override fun errorReceived(code: Int, message: String) {
                            Utils.showAlert(this@AgentMoneyTransferActivity, message, View.OnClickListener {
                                alertDialog!!.dismiss()
                                aepssearch_edtbankAmount.setText("")
                            })
                        }
                    }, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AgentMoneyTransferActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private fun showDialogProceed(agentDetails: AgentBankDetail) {
        val dialogBuilder = AlertDialog.Builder(this)
        // ...Irrelevant code for customizing the buttons and title
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.transfer_funds_dialog, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog!!.setCancelable(false)
        //alertDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog!!.show()

        val accountNo = dialogView.findViewById(R.id.accountNo) as TextView
        val beneName = dialogView.findViewById(R.id.beneName) as TextView
        val bank = dialogView.findViewById(R.id.bank) as TextView
        val transferAmount = dialogView.findViewById(R.id.transferAmount) as TextView
        val agentCharge = dialogView.findViewById(R.id.agentCharge) as TextView
        val totalAmount = dialogView.findViewById(R.id.totalAmount) as TextView
        //val maxCharge = dialogView.findViewById(R.id.maxCharge) as TextView
        val txttitle = dialogView.findViewById(R.id.txttitle) as TextView
        val btnImps = dialogView.findViewById(R.id.btnImps) as Button
        val btnNeft = dialogView.findViewById(R.id.btnNeft) as Button
        val btncancel = dialogView.findViewById(R.id.btncancel) as ImageButton

        txttitle.setText("Fund Transfer to bank")
        btnNeft.setText("Proceed")
        btnImps.setText("Cancel")

        accountNo.setText(agentDetails.accountNo)
        beneName.setText(agentDetails.bankHolderName)
        bank.setText(agentDetails.bankName)
        transferAmount.setText(aepssearch_edtbankAmount.text.toString())
        agentCharge.setText(CHRG_VALUE.toString())
        totalAmount.setText((CHRG_VALUE + PAYABLE_AMOUNT).toString())

        btnNeft.setOnClickListener {
            callBankTransfer(agentDetails)
        }

        btncancel.setOnClickListener { alertDialog!!.dismiss() }
        btnImps.setOnClickListener { alertDialog!!.dismiss() }

    }

    private fun callWalletTransfer() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "REMWALTOWAL"
            header.OP = "AEPS"
            header.PAYABLE_AMOUNT = aepssearch_edtbankAmount.text.toString().toDouble()
            header.TXN_AMOUNT = aepssearch_edtbankAmount.text.toString().toDouble()
            header.AID = Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_AGENT_CODE)
            val data = JsonObject()
            val allIds = Utils.aepsOrderId
            data.addProperty("ORDER_ID", allIds)
            data.addProperty("CN", allIds)
            data.addProperty("REMITTER_TO_BENEINFO", allIds)
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data
            VolleyJsonRequest.getInstance(this@AgentMoneyTransferActivity).request(Utils.generateURL(this@AgentMoneyTransferActivity, URLGenerator.URL_TRANSFER_TO_MAINWALLET), objCore,
                    object : VolleyJsonRequest.OnJsonResponse {
                        override fun responseReceived(jsonObj: JSONObject) {
                            if (jsonObj.getInt("RESP_CODE") == 300) {
                                Utils.showAlert(this@AgentMoneyTransferActivity, jsonObj.getString("RESP_MSG"), View.OnClickListener {
                                    alertDialog!!.dismiss()
                                    aepssearch_edtbankAmount.setText("")
                                    getAgentLimit()
                                })
                            }
                        }

                        override fun errorReceived(code: Int, message: String) {
                            Utils.showAlert(this@AgentMoneyTransferActivity, message, View.OnClickListener {
                                alertDialog!!.dismiss()
                                aepssearch_edtbankAmount.setText("")
                            })
                        }
                    }, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AgentMoneyTransferActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    @Synchronized
    private fun getAgentLimit() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_AGENT_CODE)
            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@AgentMoneyTransferActivity, AppConstants.PREF_SCREEN))
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data
            VolleyJsonRequest.getInstance(this@AgentMoneyTransferActivity).request(Utils.generateURL(this@AgentMoneyTransferActivity, URLGenerator.URL_AEPS_LIMIT), objCore, limitResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@AgentMoneyTransferActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val limitResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getString("RESP_CODE").equals("200")) {
                val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)
                if (objData.has("effectiveBalance")) {
                    balance = objData.getDouble("effectiveBalance")
                    getData()
                }
            }
        }

        override fun errorReceived(code: Int, message: String) {
            //Utils.showToast(this@AepsActivity, message)
        }
    }

    private fun getData() {
        currentBalance.setText("Rs. " + balance)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val i = Intent(this@AgentMoneyTransferActivity, AepsActivity::class.java)
        startActivity(i)
        finish()
        super.onBackPressed()
    }

}