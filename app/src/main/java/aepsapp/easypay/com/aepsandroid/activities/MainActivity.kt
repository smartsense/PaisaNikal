package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.common.*
import aepsapp.easypay.com.aepsandroid.entities.EPCoreEntity
import aepsapp.easypay.com.aepsandroid.exceptions.InternetNotAvailableException
import aepsapp.easypay.com.aepsandroid.fragments.*
import aepsapp.easypay.com.aepsandroid.network.VolleyJsonRequest
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.VolleyLog
import com.google.gson.JsonObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null
    private var shopName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)

        val balance = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_BALANCE)
        val PREF_AGENT_CODE_TO_SHOW_BALANCE = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_AGENT_CODE_TO_SHOW)
        if (!TextUtils.isEmpty(balance) && !TextUtils.isEmpty(PREF_AGENT_CODE_TO_SHOW_BALANCE) && PREF_AGENT_CODE_TO_SHOW_BALANCE.equals("MODEL_ONE_AGENT"))
            main_toolbar.subtitle = String.format(getString(R.string.balance), Utils.formatAmount(balance))

        /*supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)*/
        //supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)

        mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        setDrawerEnabled(false)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout!!.closeDrawers()
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            true
        }

        shopName = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_SHOP_NAME)

        changeFragment(HomeFragment(), shopName)

        nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> changeFragment(HomeFragment(), shopName)
                R.id.menu_contactus -> changeFragment(ContactusFragment(), getString(R.string.contact_us))
                R.id.menu_Aboutus -> changeFragment(AboutUsFragment(), getString(R.string.about_us))
                R.id.menu_faq -> changeFragment(FaqFragment(), getString(R.string.faq))
                R.id.menu_feedback -> changeFragment(FeedbackFragment(), getString(R.string.feddback))
                R.id.menu_logout -> logout()
            }
            mDrawerLayout?.closeDrawer(GravityCompat.START)

            true
        }
    }

    override fun onStart() {
        super.onStart()

        //change here
        val PREF_AGENT_CODE_TO_SHOW_BALANCE = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_AGENT_CODE_TO_SHOW)
        if (!TextUtils.isEmpty(PREF_AGENT_CODE_TO_SHOW_BALANCE) && PREF_AGENT_CODE_TO_SHOW_BALANCE.equals("MODEL_ONE_AGENT")) {
            updateBalance()
        }
    }

    fun updateBalance() {
        try {
            if (Preference.getStringPreference(this@MainActivity, AppConstants.PREF_AGENT_MOBILE).length == 10) {
                val header = EPCoreEntity.EPHeader()
                header.UDID = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_UDID)
                header.AID = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_AGENT_CODE)
                header.REQUEST_ID = Preference.getIntPreference(this@MainActivity, AppConstants.REQUEST_ID).toLong()

                val data = JsonObject()
                data.addProperty("userId", Preference.getStringPreference(this@MainActivity, AppConstants.PREF_USER_ID))
                data.addProperty("screenName", Preference.getStringPreference(this@MainActivity, AppConstants.PREF_SCREEN))
                data.addProperty("MobileNo", Preference.getStringPreference(this@MainActivity, AppConstants.PREF_AGENT_MOBILE))

                val objCore = EPCoreEntity<JsonObject>()
                objCore.HEADER = header
                objCore.DATA = data

                VolleyJsonRequest.getInstance(this@MainActivity).request(Utils.generateURL(this@MainActivity, URLGenerator.URL_WORKING_BALANCE), objCore, updateCustomerBalanceRes, true)
            }
        } catch (e: JSONException) {
            Log.e(VolleyLog.TAG, "validateReceiveMoney: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(this@MainActivity, getString(R.string.internet_not_available), FancyToast.ERROR)
        }
    }

    val updateCustomerBalanceRes = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.getInt("RESP_CODE") == AppConstants.SUCCESS_TRANSACTION) {
                val objData = jsonObj.getJSONObject(AppConstants.KEY_DATA)
                val balance = objData.get("effectiveBalance").toString()
                Preference.savePreference(this@MainActivity, AppConstants.PREF_BALANCE, balance)
                val PREF_AGENT_CODE_TO_SHOW_BALANCE = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_AGENT_CODE_TO_SHOW)
                if (!TextUtils.isEmpty(balance) && !TextUtils.isEmpty(PREF_AGENT_CODE_TO_SHOW_BALANCE) && PREF_AGENT_CODE_TO_SHOW_BALANCE.equals("MODEL_ONE_AGENT"))
                    main_toolbar.subtitle = String.format(getString(R.string.balance), Utils.formatAmount(balance))
            }
        }

        override fun errorReceived(code: Int, message: String) {

        }
    }

    fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        mDrawerLayout!!.setDrawerLockMode(lockMode)
        // toggle.setDrawerIndicatorEnabled(enabled)
    }


    fun changeFragment(fragment: Fragment, title: String? = shopName) {

        title?.let { supportActionBar!!.title = it }

        val fragTransaction = supportFragmentManager.beginTransaction()
        val temp = supportFragmentManager.findFragmentById(R.id.content_frame)
        if (temp == null)
            fragTransaction.add(R.id.content_frame, fragment)
        else
            fragTransaction.replace(R.id.content_frame, fragment)

        fragTransaction.commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
            R.id.main_menu_logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        Utils.showAlert(this@MainActivity, getString(R.string.logout_msg), "", View.OnClickListener {
            Preference.clearPreference(this@MainActivity, AppConstants.PREF_AGENT_ID)
            Preference.clearPreference(this@MainActivity, AppConstants.PREF_AGENT_MOBILE)
            URLProvider.instance.setBaseContext(applicationContext, AppConstants.BASE_URL_CONTEXT)
            //Preference.clearPreference(this@MainActivity, AppConstants.PREF_LATITUDE)
            //Preference.clearPreference(this@MainActivity, AppConstants.PREF_LONGITUTE)
            val intent = Intent(this@MainActivity, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        })
    }
}
