package aepsapp.easypay.com.aepsandroid.activities

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.entities.DmtEntity
import aepsapp.easypay.com.aepsandroid.entities.SenderEntity
import aepsapp.easypay.com.aepsandroid.fragments.FragmentAddBene
import aepsapp.easypay.com.aepsandroid.fragments.FragmentListBene
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.beneadd_activity.*


class BeneActivity : AppCompatActivity() {
    var adapter: ViewPagerAdapter? = null
    var dmtEntity: DmtEntity? = null
    var senderEntity: SenderEntity? = null
    var isFrom = ""
    var viewpager: ViewPager? = null
    var sliding_tabs: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beneadd_activity)

        setSupportActionBar(sender_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Sender Home")

        val bundle = intent.extras
        dmtEntity = bundle!!.getParcelable("dmtEntity") as DmtEntity?
        if (bundle.getParcelable<SenderEntity>("senderData") != null)
            senderEntity = bundle.getParcelable("senderData") as SenderEntity?

        if (bundle.getString("isFrom") != null)
            isFrom = bundle.getString("isFrom")

        viewpager = findViewById<ViewPager>(R.id.viewpager)
        sliding_tabs = findViewById<TabLayout>(R.id.sliding_tabs)

        viewpager!!.setOffscreenPageLimit(0)
        setupViewPager("")
        sliding_tabs!!.setupWithViewPager(viewpager)
    }

    public fun setupViewPager(isFromToSet: String) {
        if (isFromToSet.equals("")) {
            adapter = ViewPagerAdapter(supportFragmentManager, isFromToSet)
            adapter!!.addFrag(FragmentAddBene(), "Add Beneficiary", dmtEntity, senderEntity, isFrom)
            adapter!!.addFrag(FragmentListBene(), "Beneficiary List", dmtEntity, senderEntity, isFrom)
            viewpager!!.setAdapter(adapter)
        } else {
            viewpager!!.setCurrentItem(1, true)
        }
    }

    override fun onBackPressed() {
        if (isFrom.equals("FundTransfer")) {
            val intent = Intent(this, FundTransferActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("dmtEntity", dmtEntity)
            //bundle.putInt("position", _position)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class ViewPagerAdapter(manager: FragmentManager, isFromToSet: String) : FragmentPagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()
        private var toRefersh = ""

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        fun addFrag(fragment: Fragment, title: String, dmtEntity: DmtEntity?, senderEntity: SenderEntity?, isFrom: String?) {
            val bundle = Bundle()
            bundle.putParcelable("dmtEntity", dmtEntity)
            bundle.putParcelable("senderData", senderEntity)
            bundle.putString("isFrom", isFrom)
            fragment.arguments = bundle
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList.get(position)
        }
    }
}