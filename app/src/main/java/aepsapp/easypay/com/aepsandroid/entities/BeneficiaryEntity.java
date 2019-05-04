package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BeneficiaryEntity implements Parcelable, Comparable<BeneficiaryEntity> {

    public final static Parcelable.Creator<BeneficiaryEntity> CREATOR = new Creator<BeneficiaryEntity>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BeneficiaryEntity createFromParcel(Parcel in) {
            return new BeneficiaryEntity(in);
        }

        public BeneficiaryEntity[] newArray(int size) {
            return (new BeneficiaryEntity[size]);
        }

    };
    @SerializedName("BENE_ID")
    @Expose
    public Integer bENEID;
    @SerializedName("BENE_MOBILENO")
    @Expose
    public String bENEMOBILENO;
    @SerializedName("BENE_NAME")
    @Expose
    public String bENENAME;
    @SerializedName("BENE_NICKNAME")
    @Expose
    public String bENENICKNAME;
    @SerializedName("BENE_BANKNAME")
    @Expose
    public String bENEBANKNAME;
    @SerializedName("BANK_ACCOUNTNO")
    @Expose
    public String bANKACCOUNTNO;
    @SerializedName("BANKIFSC_CODE")
    @Expose
    public String bANKIFSCCODE;
    @SerializedName("BENE_OTP_VERIFIED")
    @Expose
    public Boolean bENEOTPVERIFIED;
    @SerializedName("IS_BENEVERIFIED")
    @Expose
    public Boolean iSBENEVERIFIED;
    @SerializedName("ISPAYTM_BENE")
    @Expose
    public Boolean ISPAYTM_BENE;

    protected BeneficiaryEntity(Parcel in) {
        this.bENEID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.bENEMOBILENO = ((String) in.readValue((String.class.getClassLoader())));
        this.bENENAME = ((String) in.readValue((String.class.getClassLoader())));
        this.bENENICKNAME = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEBANKNAME = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKACCOUNTNO = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKIFSCCODE = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEOTPVERIFIED = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.iSBENEVERIFIED = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.ISPAYTM_BENE = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    public BeneficiaryEntity() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(bENEID);
        dest.writeValue(bENEMOBILENO);
        dest.writeValue(bENENAME);
        dest.writeValue(bENENICKNAME);
        dest.writeValue(bENEBANKNAME);
        dest.writeValue(bANKACCOUNTNO);
        dest.writeValue(bANKIFSCCODE);
        dest.writeValue(bENEOTPVERIFIED);
        dest.writeValue(iSBENEVERIFIED);
        dest.writeValue(ISPAYTM_BENE);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull BeneficiaryEntity o) {
        return this.bENENAME.compareTo(o.bENENAME);
    }
}
