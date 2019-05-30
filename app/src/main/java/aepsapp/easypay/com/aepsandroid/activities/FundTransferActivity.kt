package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.DialogAlert
import aepsapp.easypay.com.aepsandroid.dialogs.OTPDialog
import aepsapp.easypay.com.aepsandroid.entities.*
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.VolleyLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.fund_transfer_activity.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class FundTransferActivity : AppCompatActivity() {

    private var dmtEntity: DmtEntity? = null
    private var receiptEntity: ReceiptEntity? = null
    private var senderEntity: SenderEntity? = null
    private var amountVal = 0
    private var position = -1
    private var remarkVal = ""
    private var otpDialog: OTPDialog? = null
    private var RESPONSE_CODE = 0
    private var TOTAL_CHRGAMOUNT = 0.0
    private var PAYABLE_AMOUNT = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fund_transfer_activity)

        setSupportActionBar(toolBar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Fund Transfer")

        val bundle = intent.extras
        dmtEntity = bundle!!.getParcelable("dmtEntity") as DmtEntity?
        if (bundle.getParcelable<SenderEntity>("senderData") != null)
            senderEntity = bundle.getParcelable("senderData") as SenderEntity?

        if (bundle.containsKey("position") && bundle.getInt("position") != null)
            position = bundle.getInt("position")

        if (bundle.containsKey("CASH")) {
            makeValidateCall(Preference.getStringPreference(this@FundTransferActivity, AppConstants.MOBILE_NO_DMT))
        } else {
            setValues(position)
        }

        btnPay.setOnClickListener {
            if (isValid) {
                //transactionCall()
                callChargeApi()
                //showDialog()
            }
        }

        btnAddBene.setOnClickListener {
            val i = Intent(this@FundTransferActivity, BeneActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("dmtEntity", dmtEntity)
            bundle.putParcelable("senderData", senderEntity)
            bundle.putString("isFrom", "FundTransfer")
            i.putExtras(bundle)
            startActivity(i)
            finish()
        }

        spinnerBene.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, _position: Int, id: Long) {
                position = _position - 1
                if (position >= 0) {
                    //check
                    if (Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_OP).equals("EPDMTNUR")) {
                        if (dmtEntity!!.bENEFICIARYDATA[position].ISPAYTM_BENE == false) {
                            val dilog = DialogAlert(this@FundTransferActivity)
                            dilog.setMessage(getString(R.string.validate_otp_msg))
                            dilog.setPositiveButton("Yes", View.OnClickListener {
                                validateBene()
                            })
                            dilog.setNegativeButton("No", View.OnClickListener {
                                setValues(-1)
                            })
                            dilog.setCancelable(false)
                            dilog.show()
                        }
                    } else
                        if (dmtEntity!!.bENEFICIARYDATA[position].bENEOTPVERIFIED) {

                        } else {
                            val dilog = DialogAlert(this@FundTransferActivity)
                            dilog.setMessage(getString(R.string.validate_otp_msg))
                            dilog.setPositiveButton("Yes", View.OnClickListener {
                                validateBene()
                            })
                            dilog.setNegativeButton("No", View.OnClickListener {
                                setValues(-1)
                            })
                            dilog.setCancelable(false)
                            dilog.show()
                        }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        transactionHistory.setOnClickListener {
            val i = Intent(this, TransactionHistoryActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("dmtEntity", dmtEntity)
            i.putExtras(bundle)
            startActivity(i)
        }
    }

    private fun makeValidateCall(mobile: String) {
        try {
            //Preference.savePreference(this@FundTransferActivity, AppConstants.PREF_OP, "DMTNUR")
            Preference.savePreference(this@FundTransferActivity, AppConstants.PREF_ST, "REMDOMESTIC")

            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", mobile)
            data.addProperty("userId", Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_SCREEN))

            val objCore = EPCoreEntity<JsonObject>()
            //objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@FundTransferActivity).request(Utils.generateURL(this@FundTransferActivity, URLGenerator.URL_SEARCH_CUSTOMER_DMT), objCore, seachCustomerDmt, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@FundTransferActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val seachCustomerDmt = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val resCode = jsonObj.getInt("RESP_CODE")
            if (resCode == AppConstants.SUCCESS_DATA) {
                val data = jsonObj.getJSONObject("DATA")
                val statusCustomer = data.getString("SENDER_CUSTTYPE")
                Preference.savePreference(this@FundTransferActivity, AppConstants.CUSTOMER_KYC_STATUS, statusCustomer)
                val gson = Gson()
                dmtEntity = gson.fromJson(data.toString(), DmtEntity::class.java)

                setValues(-1)
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(this@FundTransferActivity, message)
        }
    }

    private fun callChargeApi() {
        try {

            val header = EPCoreEntity.EPHeader()
            header.OP = Preference.getStringPreference(this, AppConstants.PREF_OP)
            header.ST = Preference.getStringPreference(this, AppConstants.PREF_ST)
            header.AID = Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE)
            header.TXN_AMOUNT = java.lang.Double.parseDouble(amount.text.toString())

            val data = JsonObject()
            data.addProperty("KEY_KYC_STATUS", Preference.getStringPreference(this, AppConstants.CUSTOMER_KYC_STATUS))
            data.addProperty("BENE_BANKNAME", dmtEntity!!.bENEFICIARYDATA[position].bENEBANKNAME)
            data.addProperty("BANKIFSC_CODE", dmtEntity!!.bENEFICIARYDATA[position].bANKIFSCCODE)

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
            showDialog()
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(this@FundTransferActivity, message, View.OnClickListener {
                spinnerBene.setSelection(0)
                amount.setText("")
                remark.setText("")
            })
        }
    }

    private fun validateBene() {
        otpCall()
        otpDialog = OTPDialog(object : OnOTPChange {
            override fun onResendClick() {
                otpCall()
            }

            override fun onOTPEntered(otp: String) {
                verifyOTP(otp)
            }

            override fun onCloseDialog() {
                setValues(-1)
            }
        })
        val bundle = Bundle()
        otpDialog!!.arguments = bundle
        otpDialog!!.show(supportFragmentManager, "otp")
    }

    private fun otpCall() {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(this, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(this, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_FOR", AppConstants.BENE_VERIFY)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_OTP_GENERATE_DMT), objCore, otpResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val otpResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                RESPONSE_CODE = jsonObj.getInt("RESPONSE_CODE")
                val RESP_MSG = jsonObj.getString("RESP_MSG")
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(this@FundTransferActivity, message)
        }
    }

    private fun verifyOTP(otp: String) {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(this, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(this, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE))
            data.addProperty("REQUEST_CODE", RESPONSE_CODE)
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("BENE_ID", dmtEntity!!.bENEFICIARYDATA[position].bENEID)
            data.addProperty("OTP", otp)
            data.addProperty("REQUEST_FOR", AppConstants.BENE_VERIFY)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_OTP_VERIFY_DMT), objCore, otpVerifyResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val otpVerifyResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                Utils.showAlert(this@FundTransferActivity, "Beneficiary Verified Successfully", View.OnClickListener {
                    otpDialog!!.dismiss()
                    //move back
                })
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(this@FundTransferActivity, message)
        }
    }

    private fun transactionCall(from: String) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = Preference.getStringPreference(this, AppConstants.PREF_ST)
            header.OP = Preference.getStringPreference(this, AppConstants.PREF_OP)
            header.AID = Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE)
            header.PAYABLE_AMOUNT = java.lang.Double.parseDouble(amount.text.toString())
            header.TXN_AMOUNT = java.lang.Double.parseDouble(amount.text.toString())

            val data = JsonObject()
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("ORDER_ID", Utils.dmtOrderId(from))
            data.addProperty("KEY_KYC_STATUS", Preference.getStringPreference(this, AppConstants.CUSTOMER_KYC_STATUS))
            data.addProperty("BENE_BANKNAME", dmtEntity!!.bENEFICIARYDATA[position].bENEBANKNAME)
            data.addProperty("TRANSFER_TYPE", from)
            data.addProperty("BENE_NAME", dmtEntity!!.bENEFICIARYDATA[position].bENENAME)
            data.addProperty("CN", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("BANKIFSC_CODE", dmtEntity!!.bENEFICIARYDATA[position].bANKIFSCCODE)
            data.addProperty("BANK_ACCOUNTNO", dmtEntity!!.bENEFICIARYDATA[position].bANKACCOUNTNO)
            data.addProperty("BENE_MOBILENO", dmtEntity!!.bENEFICIARYDATA[position].bENEMOBILENO)
            data.addProperty("BENE_ID", dmtEntity!!.bENEFICIARYDATA[position].bENEID)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_TRANSACTION_DMT), objCore, transactionResponse, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    /*private val transactionResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            alertDialog!!.dismiss()
            spinnerBene.setSelection(0)
            amount.setText("")
            remark.setText("")

            val RESP_CODE = jsonObj.getInt("RESP_CODE")
            val RESPONSE = jsonObj.getString("RESPONSE")
            val UDID = jsonObj.getString("UDID")
            val RESP_MSG = jsonObj.getString("RESP_MSG")
            val TRANSACTION_DATE = jsonObj.getJSONObject("DATA").getString("TRANSACTION_DATE")
            val CUSTOMER_REFERENCE_NO = jsonObj.getJSONObject("DATA").getString("CUSTOMER_REFERENCE_NO")
            val transactionDetails = jsonObj.getJSONObject("DATA").getJSONArray("TRANSACTION_DETAILS") as JSONArray
            val customerDetails = jsonObj.getJSONObject("DATA").getJSONObject("CUSTOMER_DETAILS")
            val BENEFICIARY_DETAILS = jsonObj.getJSONObject("DATA").getJSONObject("BENEFICIARY_DETAILS")
            val BENE_NAME = BENEFICIARY_DETAILS.getString("BENE_NAME")
            val BANK_ACCOUNTNO = BENEFICIARY_DETAILS.getString("BANK_ACCOUNTNO")
            val BANKIFSC_CODE = BENEFICIARY_DETAILS.getString("BANKIFSC_CODE")
            val CUST_NAME = customerDetails.getString("CUST_NAME")
            val CUSTOMER_MOBILE = customerDetails.getString("CUSTOMER_MOBILE")

            var jsonTranstion: JSONObject? = null
            var REQUEST_REFERENCE_NO = ""
            var TRANSFER_AMOUNT = ""
            var PAID_AMOUNT = ""
            for (i in 0 until transactionDetails.length()) {
                jsonTranstion = transactionDetails.getJSONObject(i)
                REQUEST_REFERENCE_NO = jsonTranstion.getString("REQUEST_REFERENCE_NO")
                TRANSFER_AMOUNT = jsonTranstion.getString("TRANSFER_AMOUNT")
                PAID_AMOUNT = jsonTranstion.getString("PAID_AMOUNT")
            }

            val intent = Intent(this@FundTransferActivity, CashReceiptActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("dmtEntity", dmtEntity)
            bundle.putParcelable("senderData", senderEntity)
            bundle.putString("RESPONSE", RESPONSE)
            bundle.putString("UDID", UDID)
            bundle.putString("TRANSACTION_DATE", TRANSACTION_DATE)
            bundle.putString("CUSTOMER_REFERENCE_NO", CUSTOMER_REFERENCE_NO)
            bundle.putString("BENE_NAME", BENE_NAME)
            bundle.putString("BANK_ACCOUNTNO", BANK_ACCOUNTNO)
            bundle.putString("BANKIFSC_CODE", BANKIFSC_CODE)
            bundle.putString("CUST_NAME", CUST_NAME)
            bundle.putString("CUSTOMER_MOBILE", CUSTOMER_MOBILE)
            bundle.putString("REQUEST_REFERENCE_NO", REQUEST_REFERENCE_NO)
            bundle.putString("TRANSFER_AMOUNT", TRANSFER_AMOUNT)
            bundle.putString("PAID_AMOUNT", PAID_AMOUNT)
            bundle.putString("RESP_MSG", RESP_MSG)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(this@FundTransferActivity, message, View.OnClickListener {
                onBackPressed()
            })
        }
    }*/

    private val transactionResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            alertDialog!!.dismiss()
            spinnerBene.setSelection(0)
            amount.setText("")
            remark.setText("")

            val RESP_MSG = jsonObj.getString("RESP_MSG")
            val UDID = jsonObj.getString("UDID")
            val data = jsonObj.getJSONObject("DATA")

            val gson = Gson()
            receiptEntity = gson.fromJson(data.toString(), ReceiptEntity::class.java)

            val i = Intent(this@FundTransferActivity, CashReceiptActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("dmtEntity", dmtEntity)
            bundle.putParcelable("receiptEntity", receiptEntity)
            bundle.putString("RESP_MSG", RESP_MSG)
            bundle.putString("UDID", UDID)
            i.putExtras(bundle)
            startActivity(i)
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(this@FundTransferActivity, message, View.OnClickListener {
                alertDialog!!.dismiss()
                setValues(-1)
                amount.setText("")
                remark.setText("")
            })
        }
    }

    var alertDialog: AlertDialog? = null
    private fun showDialog() {

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
        val maxCharge = dialogView.findViewById(R.id.maxCharge) as TextView
        val btnImps = dialogView.findViewById(R.id.btnImps) as Button
        val btnNeft = dialogView.findViewById(R.id.btnNeft) as Button
        val btncancel = dialogView.findViewById(R.id.btncancel) as ImageButton

        if (Preference.getStringPreference(this, AppConstants.PREF_OP).equals("DMTNUR")) {
            btnImps.visibility = View.GONE
            btnNeft.visibility = View.VISIBLE
        } else {
            btnNeft.visibility = View.GONE
            btnImps.visibility = View.VISIBLE
        }

        accountNo.setText(dmtEntity!!.bENEFICIARYDATA[position].bANKACCOUNTNO)
        beneName.setText(dmtEntity!!.bENEFICIARYDATA[position].bENENAME)
        bank.setText(dmtEntity!!.bENEFICIARYDATA[position].bENEBANKNAME)
        transferAmount.setText(amount.text.toString())
        agentCharge.setText(TOTAL_CHRGAMOUNT.toString())
        totalAmount.setText(PAYABLE_AMOUNT.toString())
        //maxCharge.setText(PAYABLE_AMOUNT.toString())

        btncancel.setOnClickListener { alertDialog!!.dismiss() }

        btnImps.setOnClickListener {
            transactionCall("IMPS")
        }

        btnNeft.setOnClickListener {
            transactionCall("NEFT")
        }
    }

    val isValid: Boolean
        get() {
            if (spinnerBene.selectedItemPosition == 0) {
                Toast.makeText(this, "Please select beneficiary", Toast.LENGTH_SHORT).show()
                return false
            }

            if (!TextUtils.isEmpty(amount.text.trim().toString()) && amount.text.toString().length > 0) {
                amountVal = Integer.parseInt(amount.text.toString())
            } else {
                amount.requestFocus()
                amount.error = getString(R.string.enter_amount)
                return false
            }

            if (amountVal == 0) {
                amount.requestFocus()
                amount.error = getString(R.string.enter_valid_amount)
                return false
            }

            if (amountVal < 100) {
                amount.requestFocus()
                amount.error = "Enter Amount Greater than 100"
                return false
            }

            if (amountVal > dmtEntity!!.sENDERAVAILBAL) {
                amount.requestFocus()
                amount.error = "Enter amount less than ${dmtEntity!!.sENDERAVAILBAL}"
                return false
            }

            val PREF_AGENT_CODE_TO_SHOW_BALANCE = Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_AGENT_CODE_TO_SHOW)
            if (!TextUtils.isEmpty(Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_BALANCE)) && Utils.isDouble(Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_BALANCE))) {
                val agentLimit = Preference.getStringPreference(this@FundTransferActivity, AppConstants.PREF_BALANCE).toDouble()
                if (amountVal > agentLimit && !TextUtils.isEmpty(PREF_AGENT_CODE_TO_SHOW_BALANCE) && PREF_AGENT_CODE_TO_SHOW_BALANCE.equals("MODEL_ONE_AGENT")) {
                    amount.requestFocus()
                    amount.error = "Insufficient agent limit"
                    return false
                }
            }

            if (remark.text.toString().length > 0) {
                remarkVal = remark.text.toString()
            }

            return true
        }

    fun setValues(pos: Int) {
        dmtEntity!!.sEDNERFNAME.let { nameLabel.setText(dmtEntity!!.sEDNERFNAME + " (" + dmtEntity!!.sENDERCUSTTYPE + ")") }
        dmtEntity!!.sENDERMOBILENO.let { mobileLabel.setText(String.format("%.0f", dmtEntity!!.sENDERMOBILENO)) }
        dmtEntity!!.sENDERMONTHLYBAL.let { amountLimit.setText("Rs. " + dmtEntity!!.sENDERMONTHLYBAL.toString()) }
        dmtEntity!!.sENDERAVAILBAL.let { balance.setText("Rs. " + dmtEntity!!.sENDERAVAILBAL.toString()) }

        /*val strArry = ArrayList<String>()
        strArry.add("Select Beneficiary")
        for (i in 0 until dmtEntity!!.bENEFICIARYDATA.size) {
            strArry.add(dmtEntity!!.bENEFICIARYDATA[i].bENENAME + " - " + dmtEntity!!.bENEFICIARYDATA[i].bANKACCOUNTNO)
        }*/

        //Collections.sort(dmtEntity!!.bENEFICIARYDATA)

        if (dmtEntity != null && dmtEntity!!.bENEFICIARYDATA != null) {

            Collections.sort(dmtEntity!!.bENEFICIARYDATA, object : Comparator<BeneficiaryEntity> {
                override fun compare(lhs: BeneficiaryEntity, rhs: BeneficiaryEntity): Int {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return if (lhs.bENENAME.toLowerCase() < rhs.bENENAME.toLowerCase()) -1 else if (lhs.bENENAME.toLowerCase() > rhs.bENENAME.toLowerCase()) 1 else 0
                }
            })

            val strArry = ArrayList<BeneficiaryEntity>()
            val data = BeneficiaryEntity()
            data.bENEID = 0
            data.bENEMOBILENO = ""
            data.bENENAME = "Select Beneficiary"
            data.bENEBANKNAME = ""
            data.bENEOTPVERIFIED = false
            data.bANKACCOUNTNO = ""
            strArry.add(data)

            for (i in 0 until dmtEntity!!.bENEFICIARYDATA.size) {
                strArry.add(dmtEntity!!.bENEFICIARYDATA[i])
                /*if (Preference.getStringPreference(this, AppConstants.PREF_OP).equals("EPDMTNUR")) {
                    if (dmtEntity!!.bENEFICIARYDATA[i].ISPAYTM_BENE) {
                        strArry.add(dmtEntity!!.bENEFICIARYDATA[i])
                    }
                } else {
                    strArry.add(dmtEntity!!.bENEFICIARYDATA[i])
                }*/
            }

            spinnerBene.adapter = CustomArrayAdapter(this@FundTransferActivity, R.layout.custom_spinner, strArry)
        }

        spinnerBene.setSelection(pos + 1)
    }

    inner class CustomArrayAdapter(private val mContext: Context, @param:LayoutRes private val mResource: Int,
                                   objects: List<BeneficiaryEntity>) : ArrayAdapter<BeneficiaryEntity>(mContext, mResource, 0, objects) {

        private val mInflater: LayoutInflater
        private val items: List<BeneficiaryEntity>

        init {
            mInflater = LayoutInflater.from(mContext)
            items = objects
        }

        override fun getDropDownView(position: Int, convertView: View?,
                                     parent: ViewGroup): View {
            return createItemView(position, convertView, parent)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent)
        }

        private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = mInflater.inflate(mResource, parent, false)

            val offTypeTv = view.findViewById(R.id.beneName) as TextView
            val numOffersTv = view.findViewById(R.id.beneDetails) as TextView

            val offerData = items[position]

            offTypeTv.setText(offerData.bENENAME)
            numOffersTv.setText(offerData.bENEBANKNAME + " - " + offerData.bANKACCOUNTNO)

            return view
        }
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
        val i = Intent(this@FundTransferActivity, MainActivity::class.java)
        startActivity(i)
        finish()
        super.onBackPressed()
    }
}