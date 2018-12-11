package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CUSTOMERDETAILS implements Parcelable {

    public static final Parcelable.Creator<CUSTOMERDETAILS> CREATOR = new Creator<CUSTOMERDETAILS>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CUSTOMERDETAILS createFromParcel(Parcel in) {
            return new CUSTOMERDETAILS(in);
        }

        public CUSTOMERDETAILS[] newArray(int size) {
            return (new CUSTOMERDETAILS[size]);
        }

    };
    @SerializedName("CUST_NAME")
    @Expose
    public String cUSTNAME;
    @SerializedName("CUSTOMER_MOBILE")
    @Expose
    public String cUSTOMERMOBILE;
    @SerializedName("PANCARD_FLAG")
    @Expose
    public Boolean pANCARDFLAG;
    @SerializedName("PANCARD_NO")
    @Expose
    public String pANCARDNO;

    protected CUSTOMERDETAILS(Parcel in) {
        this.cUSTNAME = ((String) in.readValue((String.class.getClassLoader())));
        this.cUSTOMERMOBILE = ((String) in.readValue((String.class.getClassLoader())));
        this.pANCARDFLAG = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.pANCARDNO = ((String) in.readValue((String.class.getClassLoader())));
    }

    public CUSTOMERDETAILS() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cUSTNAME);
        dest.writeValue(cUSTOMERMOBILE);
        dest.writeValue(pANCARDFLAG);
        dest.writeValue(pANCARDNO);
    }

    public int describeContents() {
        return 0;
    }

}
