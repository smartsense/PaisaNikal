package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.AEPSApplication
import aepsapp.easypay.com.aepsandroid.common.AppConstants
import aepsapp.easypay.com.aepsandroid.common.Preference
import aepsapp.easypay.com.aepsandroid.common.Utils
import aepsapp.easypay.com.aepsandroid.entities.TransactionEntity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : AppCompatActivity() {
    private var statusCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        setSupportActionBar(status_toolbar)

        status_toolbar.setNavigationOnClickListener {
            redirectToHome()
        }

        val txn = intent.getSerializableExtra(AppConstants.OBJ_TXN) as TransactionEntity
        val status = intent.getStringExtra(AppConstants.TXN_MSG)
        statusCode = intent.getStringExtra(AppConstants.TXN_CODE)
        val mobileNo = intent.getStringExtra(AppConstants.MOBILE_NO)

        status_txtstatus.text = status
        status_txttxnstatus.text = status

        status_txtrrn.text = "" + txn.rrn
        status_txtstan.text = "" + txn.stan
        status_txtagentid.text = Preference.getStringPreference(this@StatusActivity, AppConstants.PREF_AGENT_CODE)
        status_txtcustmobile.text = mobileNo
        status_txtbank.text = intent.getStringExtra(AppConstants.BANK_NAME)

        var amount = txn.balanceAmount
        if (amount != null && TextUtils.isEmpty(amount) || amount.equals("NA"))
            amount = if (txn != null && !txn.balanceAmountActual.equals("NA")) txn.balanceAmountActual!! else txn.balanceAmountLedger!!

        if (!TextUtils.isEmpty(amount) && !amount.equals("NA"))
            status_txtbalance.text = Utils.formatAmount(amount!!.toDouble())
        else
            status_txtbalance.text = amount ?: "NA"

        if (txn.txnAmount != null && !TextUtils.isEmpty(txn.txnAmount) && txn.txnAmount!!.length > 0)
            status_txtamount.text = Utils.formatAmount(txn.txnAmount!!)
        status_txtfee.text = "0"
        //charges are always 0
        //status_txtfee.text = Utils.formatAmount(txn.txnCharge)
        //status_txtbank.text = txn.iin

        if (txn.paidAmount != null && txn.paidAmount!! > 0)
            status_txtpaid.text = Utils.formatAmount(txn.paidAmount!!)

    }

    var isFrom = false
    override fun onBackPressed() {
        if (!isFrom)
            redirectToHome()
        else
            super.onBackPressed()
    }

    private fun redirectToHome() {
        (application as AEPSApplication).apiTime = System.currentTimeMillis()
        if (statusCode.equals("300")) {
            val intent = Intent(this@StatusActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Utils.showAlert(this@StatusActivity, "Do you want to retry ?", "", View.OnClickListener {
                /*val intent = Intent(this@StatusActivity, AepsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)*/
                isFrom = true
                onBackPressed()
            }, View.OnClickListener {
                val intent = Intent(this@StatusActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
        }
    }
}
