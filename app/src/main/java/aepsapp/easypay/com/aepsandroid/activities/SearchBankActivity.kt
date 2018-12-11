package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.BankDetailEntity
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.android.volley.VolleyLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_search_bank.*
import org.json.JSONException
import org.json.JSONObject

class SearchBankActivity : AppCompatActivity() {

    private val gson = Gson()
    private var bankList: List<BankDetailEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_bank)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@SearchBankActivity, R.color.black_transparent)))
        setSupportActionBar(bankdetails_toolbar)

        bankdetails_toolbar.setNavigationOnClickListener { finish() }

        getAllBanks()
    }

    override fun onResume() {
        super.onResume()
        bankdetails_edtsearchcity.addTextChangedListener(watcher)
    }

    override fun onPause() {
        super.onPause()
        bankdetails_edtsearchcity.removeTextChangedListener(watcher)
    }

    private fun getAllBanks() {
        try {
            val header = EPCoreEntity.EPHeader()
            header.ST = "AEPS"
            header.OP = "AEPS"
            header.AID = Preference.getStringPreference(this@SearchBankActivity, AppConstants.PREF_AGENT_CODE)

            val data = JsonObject()
            data.addProperty("userId", Preference.getStringPreference(this@SearchBankActivity, AppConstants.PREF_USER_ID))
            data.addProperty("screenName", Preference.getStringPreference(this@SearchBankActivity, AppConstants.PREF_SCREEN))

            val objCore = EPCoreEntity<JsonObject>()
            objCore.HEADER = header
            objCore.DATA = data

            VolleyJsonRequest.getInstance(this@SearchBankActivity).request(Utils.generateURL(this@SearchBankActivity,URLGenerator.URL_GET_BANKS), objCore, bankResp, true)
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@SearchBankActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    private val bankResp = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val objData = jsonObj.getJSONArray(AppConstants.KEY_DATA)
            val bankType = object : TypeToken<List<BankDetailEntity>>() {}.type
            bankList = gson.fromJson<List<BankDetailEntity>>(objData.toString(), bankType)
            setBanks(bankList!!)
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@SearchBankActivity, message, FancyToast.ERROR)
        }
    }

    private val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (bankList != null && bankList!!.size > 0) {
                if (s!!.length >= 2) {
                    val filtered = bankList!!.filter {
                        it.bankName!!.toLowerCase().contains(s.toString().toLowerCase())
                    }
                    setBanks(filtered)
                }
            }
        }
    }

    private fun setBanks(banks: List<BankDetailEntity>) {
        bankdetails_listview.adapter = ArrayAdapter<BankDetailEntity>(this@SearchBankActivity, android.R.layout.simple_list_item_1, banks)
        bankdetails_listview.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra(AppConstants.OBJ_BANK, banks[position])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
