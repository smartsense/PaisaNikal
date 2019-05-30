package aepsapp.easypay.com.aepsandroid.fragments

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.OTPDialog
import aepsapp.easypay.com.aepsandroid.entities.AgentBankDetail
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.bank_detail_fragment.*
import kotlinx.android.synthetic.main.bank_detail_fragment.view.*
import org.json.JSONException
import org.json.JSONObject


class BankDetailFragment : Fragment() {

    var otpDialog: OTPDialog? = null
    lateinit var v: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.bank_detail_fragment, container, false)

        getBankData()

        setEnableViews(v, false)

        v.btnEdit.setOnClickListener {
            setEnableViews(v, true)
        }
        v.btnSaveUpdate.setOnClickListener {
            if (isValid)
                callApiUpdate()
        }

        return v
    }

    private fun setEnableViews(v: View, flag: Boolean) {
        v.bankName.isEnabled = flag
        v.accountNo.isEnabled = flag
        v.accountConfirmNo.isEnabled = flag
        v.ifscCode.isEnabled = flag
        v.accHolderName.isEnabled = flag
        v.btnSaveUpdate.isEnabled = flag
    }

    val isValid: Boolean
        get() {
            if (TextUtils.isEmpty(bankName.text.toString()) || bankName.text.toString().trim().length < 3) {
                bankName.requestFocus()
                bankName.setError("Please enter valid bank name")
                return false
            }
            if (TextUtils.isEmpty(accountNo.text.toString()) || accountNo.text.toString().trim().length < 10) {
                accountNo.requestFocus()
                accountNo.setError("Please enter valid account number")
                return false
            }
            if (TextUtils.isEmpty(accountConfirmNo.text.toString()) || accountConfirmNo.text.toString().trim().length < 10) {
                accountConfirmNo.requestFocus()
                accountConfirmNo.setError("Please enter valid confirm account number")
                return false
            }
            if (!(accountNo.text.toString().equals(accountConfirmNo.text.toString()))) {
                accountConfirmNo.requestFocus()
                accountConfirmNo.setError("Please enter valid account number")
                return false
            }
            if (TextUtils.isEmpty(ifscCode.text.toString()) || ifscCode.text.toString().trim().length != 11) {
                ifscCode.requestFocus()
                ifscCode.setError("Please enter valid IFSC code")
                return false
            }
            if (TextUtils.isEmpty(accHolderName.text.toString()) || accHolderName.text.toString().trim().length < 3) {
                accHolderName.requestFocus()
                accHolderName.setError("Please enter valid holder name")
                return false
            }
            return true
        }

    private fun callApiUpdate() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.AID = Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE)
            header.CUSTOMER_CHARGE = 0.0
            header.PAYABLE_AMOUNT = 0.0
            header.REQUEST_ID = Preference.getIntPreference(activity!!, AppConstants.REQUEST_ID).toLong()
            header.TXN_AMOUNT = 0.0
            header.UDID = Preference.getStringPreference(activity!!, AppConstants.PREF_UDID)
            header.totalPrice = 0.0
            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(activity!!, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(activity!!, AppConstants.PREF_SCREEN))
            data.addProperty("mobile", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_MOBILE))
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data
            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_AGENT_OTP_SAVEUPDATE), objCore,
                    object : VolleyJsonRequest.OnJsonResponse {
                        override fun responseReceived(jsonObj: JSONObject) {
                            Utils.showToast(activity!!, jsonObj.getJSONObject("DATA").getString("statusMessage"), FancyToast.SUCCESS)
                            showOtpDialog()
                        }

                        override fun errorReceived(code: Int, message: String) {
                            Utils.showToast(activity!!, message, FancyToast.ERROR)
                        }
                    }, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    fun showOtpDialog() {
        otpDialog = OTPDialog(object : OnOTPChange {
            override fun onResendClick() {
                callApiUpdate()
            }

            override fun onOTPEntered(otp: String) {
                verifyOTP(otp)
            }

            override fun onCloseDialog() {

            }
        })
        val bundle = Bundle()
        otpDialog!!.arguments = bundle
        otpDialog!!.show(fragmentManager, "otp")
    }

    private fun verifyOTP(otp: String) {
        try {
            val header = EPCoreEntity.EPHeader()
            header.AID = Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE)
            header.CUSTOMER_CHARGE = 0.0
            header.PAYABLE_AMOUNT = 0.0
            header.REQUEST_ID = Preference.getIntPreference(activity!!, AppConstants.REQUEST_ID).toLong()
            header.TXN_AMOUNT = 0.0
            header.UDID = Preference.getStringPreference(activity!!, AppConstants.PREF_UDID)
            header.totalPrice = 0.0
            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(activity!!, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(activity!!, AppConstants.PREF_SCREEN))
            data.addProperty("mobile", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_MOBILE))
            data.addProperty("otp", otp)
            data.addProperty("agentMstId", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_ID))
            val databank = JsonObject()
            val bankDetails = AgentBankDetail()
            databank.addProperty("bankDetailId", bankDetails.bankDetailId)
            databank.addProperty("bankName", bankName.text.toString())
            databank.addProperty("ifsc", ifscCode.text.toString())
            databank.addProperty("accountNo", accountNo.text.toString())
            databank.addProperty("accountName", accHolderName.text.toString())
            databank.addProperty("cnfaccountNo", accountConfirmNo.text.toString())
            data.add("bankDetail", databank)
            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data
            objCore.HEADER = header
            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_AGENT_ADDUPDATE_BANKDETAILS), objCore,
                    object : VolleyJsonRequest.OnJsonResponse {
                        override fun responseReceived(jsonObj: JSONObject) {
                            Utils.showToast(activity!!, jsonObj.getJSONObject("DATA").getString("statusMessage"), FancyToast.SUCCESS)
                            if (otpDialog != null) {
                                otpDialog!!.clearOTP()
                                otpDialog!!.dismiss()
                            }
                            setEnableViews(v, false)
                        }

                        override fun errorReceived(code: Int, message: String) {
                            Utils.showToast(activity!!, message, FancyToast.ERROR)
                        }
                    }, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private fun getBankData() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE)
            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(activity!!, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(activity!!, AppConstants.PREF_SCREEN))
            data.addProperty("agentMstId", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_ID))
            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data
            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_AGENT_BANKDETAILS), objCore,
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

                                            Preference.savePreference(activity!!, AppConstants.BANKDETAILS, gson.toJson(agentDetailsData))

                                            val dataes = Preference.getStringPreference(activity!!, AppConstants.BANKDETAILS)
                                            val agentDetails = gson.fromJson(dataes, AgentBankDetail::class.java)

                                            bankName.setText(agentDetailsData.bankName)
                                            accountNo.setText(agentDetailsData.accountNo)
                                            accountConfirmNo.setText(agentDetailsData.accountNo)
                                            ifscCode.setText(agentDetailsData.ifsc)
                                            accHolderName.setText(agentDetailsData.bankHolderName)
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
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

}