package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.dialogs.OTPDialog
import aepsapp.easypay.com.aepsandroid.entities.DmtEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.entities.TransactionHistoryEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.interfaces.OnOTPChange
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_transaction_history.*
import org.json.JSONException
import org.json.JSONObject
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryActivity : AppCompatActivity() {
    var dmtEntity: DmtEntity? = null
    private var time: Long = 0
    private val formatter = SimpleDateFormat("dd-MM-yyyy")
    private val gson = Gson()
    private var list: List<TransactionHistoryEntity>? = null
    var agentCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        setSupportActionBar(toolBarTransaction)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.extras
        if (bundle.getParcelable<DmtEntity>("dmtEntity") != null)
            dmtEntity = bundle!!.getParcelable("dmtEntity") as DmtEntity?

        agentCode = Preference.getStringPreference(this@TransactionHistoryActivity, AppConstants.PREF_AGENT_CODE)

        recycleView.layoutManager = LinearLayoutManager(this)

        val c = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = df.format(c)

        dateTo = c
        dateFrom = c
        toValid = c.time
        fromValid = c.time

        fromDate.setText(currentDate)
        toDate.setText(currentDate)

        loadHistory()

        fromBtn.setOnClickListener {
            val datepicker = DatePickerFragment()
            val args = Bundle()
            args.putString("stringVariable", "from")
            datepicker.setArguments(args)
            datepicker.show(supportFragmentManager, "tag")
        }

        toBtn.setOnClickListener {
            val datepicker = DatePickerFragment()
            val args = Bundle()
            args.putString("stringVariable", "to")
            datepicker.setArguments(args)
            datepicker.show(supportFragmentManager, "tag")
        }
    }

    class DatePickerFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val initialYear = c.get(Calendar.YEAR)
            val initialMonth = c.get(Calendar.MONTH)
            val initialDay = c.get(Calendar.DAY_OF_MONTH)

            val from = arguments!!.getString("stringVariable")

            val datePickerDialog = object : DatePickerDialog(activity, null, initialYear, initialMonth, initialDay) {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        val year = datePicker.year
                        val month = datePicker.month
                        val day = datePicker.dayOfMonth
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)

                        (activity as TransactionHistoryActivity).setDate(calendar.timeInMillis, from)
                    }
                    super.onClick(dialog, which)
                }
            }
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            return datePickerDialog
        }
    }

    var fromValid = 0.0.toLong()
    var toValid = 0.0.toLong()
    var dateFrom: Date? = null
    var dateTo: Date? = null
    var format: DateFormat = SimpleDateFormat("yyyy-MM-dd")

    fun setDate(time: Long, from: String) {
        val stamp: Timestamp = Timestamp(time)

        if (from.equals("from")) {
            this.time = time
            fromDate!!.setText(formatter.format(time))
            fromValid = time
            dateFrom = Date(stamp.time)
        } else {
            this.time = time
            toDate!!.setText(formatter.format(time))
            toValid = time
            dateTo = Date(stamp.time)
        }

        if (toValid > 0.0)
            if (dateTo!!.after(dateFrom) || dateTo!!.equals(dateFrom) || formatter.format(time).equals(formatter.format(time))) {
                if (fromDate.text.toString().length > 0 && toDate.text.toString().length > 0)
                    loadHistory()
            } else {
                recycleView.visibility = View.GONE
                noData.visibility = View.VISIBLE
                Toast.makeText(this, "Please select greater ToDate", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadHistory() {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(this, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(this, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("TRANSACTION_TODT", toDate.text.toString())
            data.addProperty("TRANSACTION_FROMDT", fromDate.text.toString())

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_TRANSACTION_HISTORY_DMT), objCore, transactionResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val transactionResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)
                val trasactionData = objData.getJSONArray("TRANSACTION_DETAILS")
                val bankType = object : TypeToken<List<TransactionHistoryEntity>>() {}.type
                list = gson.fromJson<List<TransactionHistoryEntity>>(trasactionData.toString(), bankType)
                setList()
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showAlert(this@TransactionHistoryActivity, message)
        }
    }

    var otpDialog: OTPDialog? = null

    private fun setList() {
        if (list != null && list!!.size > 0) {
            noData.visibility = View.GONE
            recycleView.visibility = View.VISIBLE
            val isToShowRefund = Preference.getStringPreference(this@TransactionHistoryActivity, AppConstants.PREF_OP)
            recycleView.adapter = RecycleAdapter(this, list!!, agentCode, isToShowRefund) { position ->
                otpCall()
                otpDialog = OTPDialog(object : OnOTPChange {
                    override fun onResendClick() {
                        otpCall()
                    }

                    override fun onOTPEntered(otp: String) {
                        verifyOTP(otp, position)
                    }

                    override fun onCloseDialog() {

                    }
                })
                val bundle = Bundle()
                otpDialog!!.arguments = bundle
                otpDialog!!.show(supportFragmentManager, "otp")
                /*val intent = Intent(this, TransactionHistoryDetailsActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("tranEntity", list!!.get(position))
                intent.putExtras(bundle)
                startActivityForResult(intent, 1)*/
            }
        } else {
            recycleView.visibility = View.GONE
            noData.visibility = View.VISIBLE
        }
    }

    private fun otpCall() {
        try {
            val data = JsonObject()
            data.addProperty("OP", Preference.getStringPreference(this, AppConstants.PREF_OP))
            data.addProperty("ST", Preference.getStringPreference(this, AppConstants.PREF_ST))
            data.addProperty("AID", Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE))
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_FOR", AppConstants.CUSTREFUND)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_OTP_GENERATE_DMT), objCore, otpResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    var RESPONSE_CODE = 0
    private val otpResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                RESPONSE_CODE = jsonObj.getInt("RESPONSE_CODE")
                val RESP_MSG = jsonObj.getString("RESP_MSG")
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(this@TransactionHistoryActivity, message)
        }
    }

    fun verifyOTP(otp: String, pos: Int) {
        try {
            val data = JsonObject()
            data.addProperty("CUSTOMER_MOBILE", Preference.getStringPreference(this, AppConstants.MOBILE_NO_DMT))
            data.addProperty("REQUEST_REFERENCE_NO", list!![pos].oRDERID)
            data.addProperty("TRANSFER_AMOUNT", list!![pos].tRANSFERAMOUNT)
            data.addProperty("PAID_AMOUNT", list!![pos].pAIDAMOUNT)
            data.addProperty("RESPONSE", "REFUNDED")
            data.addProperty("OTP", otp)
            data.addProperty("REQUEST_CODE", RESPONSE_CODE)

            val header = EPCoreEntity.EPHeader()
            header.OP = Preference.getStringPreference(this, AppConstants.PREF_OP)
            header.ST = Preference.getStringPreference(this, AppConstants.PREF_ST)
            header.AID = Preference.getStringPreference(this, AppConstants.PREF_AGENT_CODE)

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this).request(Utils.generateURL(this, URLGenerator.URL_TRANSACTION_REFUND_DMT), objCore, refundResponce, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val refundResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_DATA) {
                Utils.showAlert(this@TransactionHistoryActivity, jsonObj.getString("RESP_MSG"), View.OnClickListener {
                    otpDialog!!.dismiss()
                    loadHistory()
                })
            }
        }

        override fun errorReceived(code: Int, message: String) {
            otpDialog!!.clearOTP()
            Utils.showAlert(this@TransactionHistoryActivity, message, View.OnClickListener {
                if (code == 112) {
                    otpDialog!!.dismiss()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                loadHistory()
        }
    }

    class RecycleAdapter(context: Context, val services: List<TransactionHistoryEntity>, code: String, var isToShowRefund: String, val clicked: (Int) -> Unit) : RecyclerView.Adapter<RecycleAdapter.MenuHolder>() {

        private var inflater: LayoutInflater? = null
        private var _code = ""

        init {
            inflater = LayoutInflater.from(context)
            _code = code
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
            return MenuHolder(inflater!!.inflate(R.layout.item_history_view, null))
        }

        override fun getItemCount(): Int {
            return services.size
        }

        override fun onBindViewHolder(holder: MenuHolder, position: Int) {

            val service = services[position]
            holder.txtDate.text = Utils.changeDataFormat(service.tRANSACTIONDATE.toString())
            holder.amount.text = "Rs. " + service.pAIDAMOUNT.toString()
            val data = service.cUSTOMERREFERENCENO.toString() + " / " +
                    service.oRDERID.toString() + " / " +
                    service.bENENAME.toString() + " / " +
                    service.bANKIFSCCODE.toString() + " / " +
                    service.bANKACCOUNTNO.toString() + " / "

            var next = ""
            if (service.tRANSACTIONSTATUS.toString().equals("FAILED"))
                next = "<font color='#EE0000'>" + service.tRANSACTIONSTATUS.toString() + "</font>"
            else if (service.tRANSACTIONSTATUS.toString().equals("REFUNDED") || service.tRANSACTIONSTATUS.toString().equals("SUCCESS"))
                next = "<font color='#81C639'>" + service.tRANSACTIONSTATUS.toString() + "</font>"
            else
                next = "<font color='#00000'>" + service.tRANSACTIONSTATUS.toString() + "</font>"

            holder.txtDetails.text = Html.fromHtml(data + next)


            if (isToShowRefund.equals("DMTNUR") && service.tRANSACTIONSTATUS.equals("FAILED") && service.aID.equals(_code) && service.sT.equals("REMDOMESTIC")) {
                holder.refundBtn.visibility = View.VISIBLE
            } else {
                holder.refundBtn.visibility = View.GONE
            }

            /*if (service.tRANSACTIONSTATUS.equals("FAILED") && service.aID.equals(_code) && service.sT.equals("REMDOMESTIC")) {
                holder.refundBtn.visibility = View.VISIBLE
            } else {
                holder.refundBtn.visibility = View.GONE
            }*/

            holder.refundBtn.setOnClickListener {
                clicked(position)
            }
        }

        class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            //val draweeView = itemView.findViewById<SimpleDraweeView>(R.id.menuitem_img)
            val txtDate = itemView.findViewById<TextView>(R.id.txtDate)
            val amount = itemView.findViewById<TextView>(R.id.amount)
            val txtDetails = itemView.findViewById<TextView>(R.id.txtDetails)
            val refundBtn = itemView.findViewById<Button>(R.id.refundBtn)
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
        val intent = Intent(this, FundTransferActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val bundle = Bundle()
        bundle.putParcelable("dmtEntity", dmtEntity)
        bundle.putString("CASH", "refreshData")
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}