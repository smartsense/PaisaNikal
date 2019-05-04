package aepsapp.easypay.com.aepsandroid.fragments

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.activities.FundTransferActivity
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.OTPDialog
import aepsapp.easypay.com.aepsandroid.entities.BeneficiaryEntity
import aepsapp.easypay.com.aepsandroid.entities.DmtEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.fragment_benelist.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class FragmentListBene : Fragment() {

    var _position = -1
    var mAdapter: MoviesAdapter? = null
    var dmtEntity: DmtEntity? = null
    var recycleView: RecyclerView? = null
    var RESPONSE_CODE = 0
    var otpDialog: OTPDialog? = null
    private var listBene: List<BeneficiaryEntity>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_benelist, container, false)

        val args = arguments!!
        dmtEntity = args.getParcelable("dmtEntity") as DmtEntity?

        recycleView = v.findViewById(R.id.recycleView) as RecyclerView

        //getBeneData()
        setRecycleData()

        v.delete.setOnClickListener {
            if (dmtEntity != null && dmtEntity!!.bENEFICIARYDATA != null && dmtEntity!!.bENEFICIARYDATA.size > 0)
                if (_position >= 0)
                    otpCall()
                else
                    Toast.makeText(activity!!, "Please select beneficiary", Toast.LENGTH_SHORT).show()
        }

        v.fundTransfer.setOnClickListener {
            if (dmtEntity != null && dmtEntity!!.bENEFICIARYDATA != null && dmtEntity!!.bENEFICIARYDATA.size > 0)
                if (_position >= 0) {
                    val intent = Intent(activity!!, FundTransferActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("dmtEntity", dmtEntity)
                    bundle.putInt("position", _position)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    activity!!.finish()
                } else {
                    Toast.makeText(activity!!, "Please select beneficiary", Toast.LENGTH_SHORT).show()
                }
        }

        return v
    }

    fun getBeneData() {
        try {
            //Preference.savePreference(activity!!, AppConstants.PREF_OP, "DMTNUR")
            Preference.savePreference(activity!!, AppConstants.PREF_ST, "REMDOMESTIC")

            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
            data.addProperty("userId", Preference.getStringPreference(activity!!, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(activity!!, AppConstants.PREF_SCREEN))

            val objCore = EPCoreEntity<JsonObject>()
            //objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_SEARCH_CUSTOMER_DMT), objCore, seachCustomerDmt, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val seachCustomerDmt = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val resCode = jsonObj.getInt("RESP_CODE")
            //if (resCode == AppConstants.SUCCESS_DATA) {
            val data = jsonObj.getJSONObject("DATA")
            val statusCustomer = data.getString("SENDER_CUSTTYPE")
            Preference.savePreference(activity!!, AppConstants.CUSTOMER_KYC_STATUS, statusCustomer)
            val gson = Gson()
            dmtEntity = gson.fromJson(data.toString(), DmtEntity::class.java)
            setRecycleData()
            //}
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(activity!!, message)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            //fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
            getBeneData()
        }
    }

    private fun otpCall() {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_FOR", AppConstants.CUSTOMER_VERIFY)

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
                showOtpDialog()
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(activity!!, message)
        }
    }


    private fun showOtpDialog() {
        otpDialog = OTPDialog(object : OnOTPChange {
            override fun onResendClick() {
                if (_position >= 0) {
                    otpDialog!!.dismiss()
                    otpCall()
                }
            }

            override fun onOTPEntered(otp: String) {
                deleteBeneficiary(otp)
            }

            override fun onCloseDialog() {

            }
        })
        val bundle = Bundle()
        otpDialog?.arguments = bundle
        otpDialog?.show(fragmentManager, "otp")
    }

    private fun deleteBeneficiary(otp: String) {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(activity!!, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(activity!!, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(activity!!, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
            data.addProperty("BENE_ID", dmtEntity!!.bENEFICIARYDATA[_position].bENEID)
            data.addProperty("REQUEST_CODE", RESPONSE_CODE)
            data.addProperty("OTP", otp)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(activity!!).request(Utils.generateURL(activity!!, URLGenerator.URL_DELETE_BENE_DMT), objCore, deleteBeneRes, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity!!, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    var deleteBeneRes = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            otpDialog?.dismiss()
            Utils.showAlert(activity!!, "Beneficiary deleted successfully", View.OnClickListener {
                //mAdapter!!.notifyItemRemoved(_position)
                /*dmtEntity!!.bENEFICIARYDATA.removeAt(_position)
                _position = -1
                mAdapter!!.notifyDataSetChanged()*/
                _position = -1
                getBeneData()
            })
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(activity!!, message, View.OnClickListener {
                if (code == 420) {
                    otpDialog?.clearOTP()
                } else {
                    otpDialog?.dismiss()
                }
            })
        }
    }

    private fun setRecycleData() {
        _position = -1

        //Collections.sort(dmtEntity!!.bENEFICIARYDATA)

        if (dmtEntity != null && dmtEntity?.bENEFICIARYDATA != null) {

            Collections.sort(dmtEntity!!.bENEFICIARYDATA, object : Comparator<BeneficiaryEntity> {
                override fun compare(lhs: BeneficiaryEntity, rhs: BeneficiaryEntity): Int {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return if (lhs.bENENAME.toLowerCase() < rhs.bENENAME.toLowerCase()) -1 else if (lhs.bENENAME.toLowerCase() > rhs.bENENAME.toLowerCase()) 1 else 0
                }
            })

            mAdapter = MoviesAdapter(dmtEntity!!.bENEFICIARYDATA as ArrayList<BeneficiaryEntity>) { position ->
                _position = position
            }
            recycleView!!.layoutManager = LinearLayoutManager(activity)
            recycleView!!.setItemAnimator(DefaultItemAnimator())
            recycleView!!.adapter = mAdapter
        }
    }

    inner class MoviesAdapter(private val moviesList: ArrayList<BeneficiaryEntity>, var clicked: (Int) -> Unit) : RecyclerView.Adapter<MoviesAdapter.MyViewHolder>() {

        private var lastCheckedPosition = -1

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var bankName: TextView
            var checkbox: RadioButton
            var name: TextView
            var isfcCode: TextView
            var accountNo: TextView
            var contraintLayout: ConstraintLayout

            init {
                checkbox = view.findViewById(R.id.checkbox) as RadioButton
                bankName = view.findViewById(R.id.bankName) as TextView
                name = view.findViewById(R.id.name) as TextView
                isfcCode = view.findViewById(R.id.isfcCode) as TextView
                accountNo = view.findViewById(R.id.accountNo) as TextView
                contraintLayout = view.findViewById(R.id.contraintLayout) as ConstraintLayout
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bene_list_row, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val movie = moviesList[position]
            holder.name.setText(movie.bENENAME)
            holder.bankName.setText(movie.bENEBANKNAME)
            holder.isfcCode.setText(movie.bANKIFSCCODE)
            holder.accountNo.setText(movie.bANKACCOUNTNO)

            holder.checkbox.setChecked(position == lastCheckedPosition)

            holder.checkbox.setOnClickListener {
                lastCheckedPosition = position
                clicked(position)
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return moviesList.size
        }
    }
}