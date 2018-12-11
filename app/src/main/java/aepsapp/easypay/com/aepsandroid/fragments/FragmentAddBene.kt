package aepsapp.easypay.com.aepsandroid.fragments

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.activities.AepsActivity
import aepsapp.easypay.com.aepsandroid.activities.BeneActivity
import aepsapp.easypay.com.aepsandroid.activities.MainActivity
import aepsapp.easypay.com.aepsandroid.activities.SearchBankActivity
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.OTPDialog
import aepsapp.easypay.com.aepsandroid.entities.BankDetailEntity
import aepsapp.easypay.com.aepsandroid.entities.DmtEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.entities.SenderEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.android.volley.VolleyLog
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.fragment_add_bene.*
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

class FragmentAddBene : Fragment() {

    var beneNameVal = ""
    var accountNoVal = ""
    var confirmAccNoVal = ""
    var isfcNoVal = ""
    var bankIIN = ""
    //var spnrRemittanceType: Spinner? = null
    var btnRegister: Button? = null
    var dmtEntity: DmtEntity? = null
    var senderEntity: SenderEntity? = null
    var beneId = 0
    var RESPONSE_CODE = 0
    var isFrom: String = ""
    private var selectedBank: BankDetailEntity? = null
    var _aepssearch_edtbank: TextInputEditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_bene, container, false)

        val args = arguments!!
        dmtEntity = args.getParcelable("dmtEntity") as DmtEntity?
        senderEntity = args.getParcelable("senderData") as SenderEntity?
        isFrom = args.getString("isFrom")

        //spnrRemittanceType = view.findViewById(R.id.spnrRemittanceType)
        btnRegister = view.findViewById(R.id.btnRegister)
        _aepssearch_edtbank = view.findViewById(R.id._aepssearch_edtbank)

        /*spnrRemittanceType!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                *//*if (position == 1) {
                    //load bank
                    spnrBankName.visibility = View.GONE
                    iscfLayout.visibility = View.VISIBLE
                } else {
                    spnrBankName.visibility = View.VISIBLE
                    iscfLayout.visibility = View.GONE
                }*//*
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }*/

        btnRegister!!.setOnClickListener {
            if (isValid) {
                registerSender(isFrom)
            }
        }

        _aepssearch_edtbank!!.setOnClickListener {
            val intent = Intent(activity!!, SearchBankActivity::class.java)
            startActivityForResult(intent, AepsActivity.SEARCH_BANK)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == AepsActivity.SEARCH_BANK) {
                selectedBank = data.getSerializableExtra(AppConstants.OBJ_BANK) as BankDetailEntity?
                selectedBank?.let {
                    _aepssearch_edtbank!!.setText(it.bankName)
                }
            }
        }
    }

    fun registerSender(what: String?) {
        try {
            if (what.equals("")) {
                val data = JsonObject()
                data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
                data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
                data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
                data.addProperty("CUSTOMER_MOBILE", senderEntity!!.mobile)
                data.addProperty("CUST_LNAME", "")
                data.addProperty("STATE", senderEntity!!.state)
                data.addProperty("BENE_NAME", beneNameVal)
                data.addProperty("CUST_ADDRESS", senderEntity!!.address)
                data.addProperty("PINCODE", senderEntity!!.pincode)
                data.addProperty("BANK_ACCOUNTNO", accountNoVal)
                data.addProperty("CUST_TITLE", "")
                data.addProperty("CITY", senderEntity!!.city)
                data.addProperty("CUST_EMAIL", "")
                data.addProperty("CUST_FNAME", senderEntity!!.name)
                data.addProperty("CUST_ALTMOBILENO", "")
                data.addProperty("BANKIFSC_CODE", isfcCode.text.toString())
                data.addProperty("BENE_MOBILENO", "")
                data.addProperty("CUST_DOB", "")
                //icici0000

                val objCore = EPCoreEntity<JsonObject>()
                objCore.DATA = data

                VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_ADD_CUSTOMER_DMT), objCore, addCustomerResponce, true)
            } else {
                val data = JsonObject()
                data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
                data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
                data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
                data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
                data.addProperty("BANK_CITY", "")
                data.addProperty("BENE_NAME", beneNameVal)
                data.addProperty("BANK_BRANCH", "")
                data.addProperty("BANKIFSC_CODE", isfcCode.text.toString())
                data.addProperty("BANK_ADDRESS", "")
                data.addProperty("BANK_ACCOUNTNO", accountNoVal)
                data.addProperty("BANK_STATE", "")
                data.addProperty("BENE_MOBILENO", "")

                val objCore = EPCoreEntity<JsonObject>()
                objCore.DATA = data

                VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_ADD_BENEFICIARY_DMT), objCore, addCustomerResponce, true)
            }
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    var otpDialog: OTPDialog? = null

    val addCustomerResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == 200) {
                beneId = jsonObj.getInt("BENE_ID")
                RESPONSE_CODE = jsonObj.getInt("RESPONSE_CODE")
                //otpCall()
                otpDialog = OTPDialog(object : OnOTPChange {
                    override fun onResendClick() {
                        otpCall()
                    }

                    override fun onOTPEntered(otp: String) {
                        verifyOTP(otp)
                    }

                    override fun onCloseDialog() {
                        if (isFrom.equals("")) {
                            val intent = Intent(activity!!, MainActivity::class.java)
                            startActivity(intent)
                            activity!!.finish()
                        }
                    }
                })
                val bundle = Bundle()
                otpDialog!!.arguments = bundle
                otpDialog!!.show(fragmentManager, "otp")
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(activity!!, message, View.OnClickListener {
                if (code == 658) {
                    isfcCode.setText("")
                } else if (isFrom.equals("")) {
                    val intent = Intent(activity!!, MainActivity::class.java)
                    startActivity(intent)
                    activity!!.finish()
                } else {
                    clearSelection()
                }
            })
        }
    }

    private fun verifyOTP(otp: String) {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("REQUEST_CODE", RESPONSE_CODE)
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
            data.addProperty("BENE_ID", beneId)
            data.addProperty("OTP", otp)
            if (isFrom.equals("")) {
                data.addProperty("REQUEST_FOR", AppConstants.CUSTOMER_VERIFY)
            } else {
                data.addProperty("REQUEST_FOR", AppConstants.BENE_VERIFY)
            }

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_OTP_VERIFY_DMT), objCore, otpVerifyResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val otpVerifyResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                Utils.showAlert(activity!!, "Beneficiary Added Successfully", View.OnClickListener {
                    otpDialog!!.dismiss()
                    //(context as BeneActivity).setAdapter(1)
                    clearSelection()
                    (context as BeneActivity).setupViewPager("refresh")
                })
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(activity!!, message)
        }
    }

    private fun clearSelection() {
        beneName.setText("")
        accountNo.setText("")
        confirmAccNo.setText("")
        isfcCode.setText("")
        _aepssearch_edtbank!!.setText("")
        checkbox.isChecked = false
    }

    private fun otpCall() {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_FOR", AppConstants.BENE_VERIFY)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_OTP_GENERATE_DMT), objCore, otpResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
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
            Utils.showAlert(activity!!, message)
        }
    }

    val isValid: Boolean
        get() {
            beneNameVal = beneName.text.toString().trim()
            accountNoVal = accountNo.text.toString().trim()
            confirmAccNoVal = confirmAccNo.text.toString().trim()
            isfcNoVal = isfcCode.text.toString().trim()
            bankIIN = _aepssearch_edtbank!!.text.toString().trim()

            if (TextUtils.isEmpty(beneNameVal) || beneNameVal.length < 4) {
                beneName.requestFocus()
                beneName.setError("Enter name")
                return false
            }
            if (TextUtils.isEmpty(accountNoVal) || accountNoVal.length < 6) {
                accountNo.requestFocus()
                accountNo.setError("Enter account no")
                return false
            }
            if (TextUtils.isEmpty(confirmAccNoVal) || confirmAccNoVal.length < 6) {
                confirmAccNo.requestFocus()
                confirmAccNo.setError("Enter confirmation account no")
                return false
            }

            if (accountNoVal != confirmAccNoVal) {
                confirmAccNo.requestFocus()
                confirmAccNo.setError("Account no did not match")
                return false
            }

            if (TextUtils.isEmpty(bankIIN)) {
                //_aepssearch_edtbank!!.requestFocus()
                //_aepssearch_edtbank!!.error = getString(R.string.enter_bank)
                Toast.makeText(activity!!, getString(R.string.enter_bank), Toast.LENGTH_SHORT).show()
                return false
            }

            val p = Pattern.compile("^[A-Za-z]{4}[0-9]{7}\$")
            val m = p.matcher(isfcNoVal)
            if (isfcNoVal.length != 11 || !m.matches()) {
                isfcCode.requestFocus()
                isfcCode.setError("Enter valid IFSC code")
                return false
            }

            if (!checkbox.isChecked) {
                Toast.makeText(activity!!, "Check terms & conditions", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
}