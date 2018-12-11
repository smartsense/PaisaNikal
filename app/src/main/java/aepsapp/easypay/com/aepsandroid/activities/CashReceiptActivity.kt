package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.AppConstants
import aepsapp.easypay.com.aepsandroid.common.Preference
import aepsapp.easypay.com.aepsandroid.entities.DmtEntity
import aepsapp.easypay.com.aepsandroid.entities.ReceiptEntity
import aepsapp.easypay.com.aepsandroid.entities.SenderEntity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fund_cash_receipt.*


class CashReceiptActivity : AppCompatActivity() {

    var dmtEntity: DmtEntity? = null
    var senderEntity: SenderEntity? = null
    var receiptEntity: ReceiptEntity? = null
    var RESP_MSG = ""
    var UDID = ""
    var dim: LinearLayout.LayoutParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fund_cash_receipt)

        setSupportActionBar(toolBarReceipt)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Cash Receipt")

        val bundle = intent.extras
        if (bundle.getParcelable<DmtEntity>("dmtEntity") != null)
            dmtEntity = bundle!!.getParcelable("dmtEntity") as DmtEntity?

        if (bundle.getParcelable<ReceiptEntity>("receiptEntity") != null)
            receiptEntity = bundle.getParcelable("receiptEntity") as ReceiptEntity?

        RESP_MSG = bundle.getString("RESP_MSG")
        UDID = bundle.getString("UDID")

        dim = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        setData()

        okBtn.setOnClickListener {
            onBackPressed()
        }
    }

    fun setData() {
        if (receiptEntity != null) {
            if (receiptEntity!!.tRANSACTIONDETAILS != null && receiptEntity!!.tRANSACTIONDETAILS.size > 0) {

                val data = receiptEntity!!

                date.setText(data.tRANSACTIONDATE)
                refNo.setText(data.cUSTOMERREFERENCENO)
                senderName.setText(data.cUSTOMERDETAILS.cUSTNAME)
                senderMobile.setText(data.cUSTOMERDETAILS.cUSTOMERMOBILE)
                beneName.setText(data.bENEFICIARYDETAILS.bENENAME)
                accountNo.setText(data.bENEFICIARYDETAILS.bANKACCOUNTNO)
                ifscCode.setText(data.bENEFICIARYDETAILS.bANKIFSCCODE)
                utrNo.setText(UDID)
                shopName.setText(Preference.getStringPreference(this@CashReceiptActivity, AppConstants.PREF_SHOP_NAME))
                remarks.setText(RESP_MSG)

                if (receiptEntity!!.tRANSACTIONDETAILS.size > 0) {
                    val layoutAmount = LinearLayout(this@CashReceiptActivity)
                    for (i in receiptEntity!!.tRANSACTIONDETAILS.indices) {
                        linearLayoutADD.removeAllViews()
                        layoutAmount.orientation = LinearLayout.VERTICAL
                        layoutAmount.addView(getTextView("ORDER ID ", receiptEntity!!.tRANSACTIONDETAILS[i].rEQUESTREFERENCENO, false))
                        layoutAmount.addView(getTextView("TRANSFER AMOUNT ", receiptEntity!!.tRANSACTIONDETAILS[i].tRANSFERAMOUNT.toString(), false))
                        layoutAmount.addView(getTextView("STATUS ", receiptEntity!!.tRANSACTIONDETAILS[i].rESPONSE.toString(), false))
                        layoutAmount.addView(getTextView("TOTAL AMOUNT ", receiptEntity!!.tRANSACTIONDETAILS[i].pAIDAMOUNT.toString(), false))
                        linearLayoutADD!!.addView(layoutAmount)
                    }
                }
            }
        }
    }

    private fun getTextView(label: String, text: String?, bold: Boolean): TextView {
        val textView = TextView(this@CashReceiptActivity)
        textView.setTextColor(Color.WHITE)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.txt_size_medium))
        textView.layoutParams = dim
        textView.text = label + " : " + text
        textView.gravity = Gravity.LEFT or Gravity.CENTER_HORIZONTAL
        //if (bold) textView.setTypeface(null, Typeface.BOLD)
        return textView
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