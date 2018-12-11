package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BENEFICIARYDETAILS implements Parcelable {

    public static final Parcelable.Creator<BENEFICIARYDETAILS> CREATOR = new Creator<BENEFICIARYDETAILS>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BENEFICIARYDETAILS createFromParcel(Parcel in) {
            return new BENEFICIARYDETAILS(in);
        }

        public BENEFICIARYDETAILS[] newArray(int size) {
            return (new BENEFICIARYDETAILS[size]);
        }

    };
    @SerializedName("BENE_NAME")
    @Expose
    public String bENENAME;
    @SerializedName("BANK_ACCOUNTNO")
    @Expose
    public String bANKACCOUNTNO;
    @SerializedName("BANKIFSC_CODE")
    @Expose
    public String bANKIFSCCODE;
    @SerializedName("BENE_MMID")
    @Expose
    public String bENEMMID;
    @SerializedName("BENE_MOBILENO")
    @Expose
    public String bENEMOBILENO;
    @SerializedName("BENE_BANKNAME")
    @Expose
    public String bENEBANKNAME;

    protected BENEFICIARYDETAILS(Parcel in) {
        this.bENENAME = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKACCOUNTNO = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKIFSCCODE = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEMMID = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEMOBILENO = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEBANKNAME = ((String) in.readValue((String.class.getClassLoader())));
    }

    public BENEFICIARYDETAILS() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(bENENAME);
        dest.writeValue(bANKACCOUNTNO);
        dest.writeValue(bANKIFSCCODE);
        dest.writeValue(bENEMMID);
        dest.writeValue(bENEMOBILENO);
        dest.writeValue(bENEBANKNAME);
    }

    public int describeContents() {
        return 0;
    }

}