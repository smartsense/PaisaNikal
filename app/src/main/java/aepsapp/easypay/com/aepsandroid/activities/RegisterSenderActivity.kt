package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.fragments.FragmentKyc
import aepsapp.easypay.com.aepsandroid.fragments.FragmentNonkyc
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.register_sender_activity.*

class RegisterSenderActivity : AppCompatActivity() {

    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_sender_activity)

        setSupportActionBar(sender_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("New Sender")

        fragment = FragmentNonkyc()
        changeFragment(fragment)

        radioGroup.setOnCheckedChangeListener({ group: RadioGroup?, checkedId: Int ->
            if (checkedId == R.id.nonKyc) {
                fragment = FragmentNonkyc()
                changeFragment(fragment)
            } else {
                fragment = FragmentKyc()
                changeFragment(fragment)
            }
        })
    }

    fun changeFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        val temp = supportFragmentManager.findFragmentById(R.id.container)
        if (temp == null) {
            ft.add(R.id.container, fragment)
        } else {
            ft.replace(R.id.container, fragment)
        }
        ft.commitAllowingStateLoss()
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
        super.onBackPressed()
        val home = Intent(this@RegisterSenderActivity, MainActivity::class.java)
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(home)
        finish()
    }
}