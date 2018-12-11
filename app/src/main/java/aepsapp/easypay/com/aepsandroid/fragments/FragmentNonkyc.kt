package aepsapp.easypay.com.aepsandroid.fragments

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.activities.BeneActivity
import aepsapp.easypay.com.aepsandroid.activities.RegisterSenderActivity
import aepsapp.easypay.com.aepsandroid.common.AppConstants
import aepsapp.easypay.com.aepsandroid.common.Preference
import aepsapp.easypay.com.aepsandroid.entities.SenderEntity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_nonkyc.*

class FragmentNonkyc : Fragment() {

    var senderActivity: RegisterSenderActivity? = null
    var btnNextData: Button? = null
    var mobileNumberVal = ""
    var nameVal = ""
    var addressVal = ""
    //var pincodeVal = ""
    //var stateVal = ""
    //var cityVal = ""
    var sender = SenderEntity()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Activity) {
            senderActivity = context as RegisterSenderActivity
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_nonkyc, container, false)
        btnNextData = view.findViewById(R.id.btnNextData)
        val mobileNumber = view.findViewById(R.id.mobileNumber) as EditText

        if (Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT) != null
                && Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT).length == 10) {
            mobileNumber.setText(Preference.getStringPreference(activity!!, AppConstants.MOBILE_NO_DMT))
        }

        btnNextData!!.setOnClickListener {
            if (isValid) {
                Preference.savePreference(activity!!, AppConstants.MOBILE_NO_DMT, mobileNumberVal)
                sender.name = nameVal
                sender.mobile = mobileNumberVal
                sender.address = addressVal
                //sender.pincode = pincodeVal
                //sender.city = cityVal
                //sender.state = stateVal

                val intent = Intent(activity, BeneActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("senderData", sender)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
        return view
    }

    private val isValid: Boolean
        get() {
            mobileNumberVal = mobileNumber.text.toString().trim()
            nameVal = name.text.toString().trim()
            addressVal = address.text.toString().trim()
            //pincodeVal = pincode.text.toString()
            //stateVal = state.text.toString()
            //cityVal = city.text.toString()

            if (TextUtils.isEmpty(mobileNumberVal) || mobileNumberVal.length != 10) {
                mobileNumber.requestFocus()
                mobileNumber.error = "Enter mobile"
                return false
            }
            if (mobileNumberVal.length == 10) {
                val firstChar: Char = mobileNumberVal.get(0)
                if (firstChar.toString().equals("0")) {
                    mobileNumber.requestFocus()
                    mobileNumber.error = "Enter valid mobile"
                    return false
                }
            }

            if (TextUtils.isEmpty(nameVal)) {
                name.requestFocus()
                name.error = "Enter name"
                return false
            }
            if (nameVal.length < 3) {
                name.requestFocus()
                name.error = "Enter valid name"
                return false
            }

            if (TextUtils.isEmpty(addressVal) || addressVal.length < 6) {
                address.requestFocus()
                address.error = "Enter address"
                return false
            }
            /*if (TextUtils.isEmpty(pincodeVal) || pincodeVal.length != 6) {
                mobileNumber.error = "Enter pincode"
                return false
            }
            if (TextUtils.isEmpty(stateVal)) {
                mobileNumber.error = "Enter state"
                return false
            }
            if (TextUtils.isEmpty(cityVal)) {
                mobileNumber.error = "Enter city"
                return false
            }*/
            return true
        }
}