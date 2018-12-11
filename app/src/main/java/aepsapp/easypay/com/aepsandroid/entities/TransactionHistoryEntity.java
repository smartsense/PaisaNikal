package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionHistoryEntity implements Parcelable {

    public final static Parcelable.Creator<TransactionHistoryEntity> CREATOR = new Creator<TransactionHistoryEntity>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TransactionHistoryEntity createFromParcel(Parcel in) {
            return new TransactionHistoryEntity(in);
        }

        public TransactionHistoryEntity[] newArray(int size) {
            return (new TransactionHistoryEntity[size]);
        }

    };
    @SerializedName("TXN_ID")
    @Expose
    public Integer tXNID;
    @SerializedName("EP_REFERENCE_NO")
    @Expose
    public String ePREFERENCENO;
    @SerializedName("BANK_REFERENCE_NO")
    @Expose
    public String bANKREFERENCENO;
    @SerializedName("CUSTOMER_REFERENCE_NO")
    @Expose
    public String cUSTOMERREFERENCENO;
    @SerializedName("BENE_MOBILENO")
    @Expose
    public String bENEMOBILENO;
    @SerializedName("BENE_NAME")
    @Expose
    public String bENENAME;
    @SerializedName("BENE_ID")
    @Expose
    public Integer bENEID;
    @SerializedName("BENE_NICKNAME")
    @Expose
    public String bENENICKNAME;
    @SerializedName("BENE_BANKNAME")
    @Expose
    public String bENEBANKNAME;
    @SerializedName("BANK_ADDRESS")
    @Expose
    public String bANKADDRESS;
    @SerializedName("BANK_ACCOUNTNO")
    @Expose
    public String bANKACCOUNTNO;
    @SerializedName("BANKIFSC_CODE")
    @Expose
    public String bANKIFSCCODE;
    @SerializedName("BENE_CODE")
    @Expose
    public String bENECODE;
    @SerializedName("BENE_STATUS")
    @Expose
    public String bENESTATUS;
    @SerializedName("BENE_OTP_VERIFIED")
    @Expose
    public Boolean bENEOTPVERIFIED;
    @SerializedName("PAID_AMOUNT")
    @Expose
    public Double pAIDAMOUNT;
    @SerializedName("TRANSFER_AMOUNT")
    @Expose
    public Double tRANSFERAMOUNT;
    @SerializedName("CHARGE_AMOUNT")
    @Expose
    public Double cHARGEAMOUNT;
    @SerializedName("TRANSACTION_DATE")
    @Expose
    public String tRANSACTIONDATE;
    @SerializedName("TRANSACTION_STATUS")
    @Expose
    public String tRANSACTIONSTATUS;
    @SerializedName("TRANSACTION_STATUSMESSAGE")
    @Expose
    public String tRANSACTIONSTATUSMESSAGE;
    @SerializedName("ORDER_ID")
    @Expose
    public String oRDERID;
    @SerializedName("AID")
    @Expose
    public String aID;
    @SerializedName("MID")
    @Expose
    public String mID;
    @SerializedName("CP")
    @Expose
    public String cP;
    @SerializedName("ST")
    @Expose
    public String sT;
    @SerializedName("REINIT_TXN_ID")
    @Expose
    public String rEINITTXNID;

    protected TransactionHistoryEntity(Parcel in) {
        this.tXNID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.ePREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
        this.cUSTOMERREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEMOBILENO = ((String) in.readValue((String.class.getClassLoader())));
        this.bENENAME = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.bENENICKNAME = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEBANKNAME = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKADDRESS = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKACCOUNTNO = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKIFSCCODE = ((String) in.readValue((String.class.getClassLoader())));
        this.bENECODE = ((String) in.readValue((String.class.getClassLoader())));
        this.bENESTATUS = ((String) in.readValue((String.class.getClassLoader())));
        this.bENEOTPVERIFIED = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.pAIDAMOUNT = ((Double) in.readValue((Double.class.getClassLoader())));
        this.tRANSFERAMOUNT = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cHARGEAMOUNT = ((Double) in.readValue((Double.class.getClassLoader())));
        this.tRANSACTIONDATE = ((String) in.readValue((String.class.getClassLoader())));
        this.tRANSACTIONSTATUS = ((String) in.readValue((String.class.getClassLoader())));
        this.tRANSACTIONSTATUSMESSAGE = ((String) in.readValue((String.class.getClassLoader())));
        this.oRDERID = ((String) in.readValue((String.class.getClassLoader())));
        this.aID = ((String) in.readValue((String.class.getClassLoader())));
        this.mID = ((String) in.readValue((String.class.getClassLoader())));
        this.cP = ((String) in.readValue((String.class.getClassLoader())));
        this.sT = ((String) in.readValue((String.class.getClassLoader())));
        this.rEINITTXNID = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TransactionHistoryEntity() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(tXNID);
        dest.writeValue(ePREFERENCENO);
        dest.writeValue(bANKREFERENCENO);
        dest.writeValue(cUSTOMERREFERENCENO);
        dest.writeValue(bENEMOBILENO);
        dest.writeValue(bENENAME);
        dest.writeValue(bENEID);
        dest.writeValue(bENENICKNAME);
        dest.writeValue(bENEBANKNAME);
        dest.writeValue(bANKADDRESS);
        dest.writeValue(bANKACCOUNTNO);
        dest.writeValue(bANKIFSCCODE);
        dest.writeValue(bENECODE);
        dest.writeValue(bENESTATUS);
        dest.writeValue(bENEOTPVERIFIED);
        dest.writeValue(pAIDAMOUNT);
        dest.writeValue(tRANSFERAMOUNT);
        dest.writeValue(cHARGEAMOUNT);
        dest.writeValue(tRANSACTIONDATE);
        dest.writeValue(tRANSACTIONSTATUS);
        dest.writeValue(tRANSACTIONSTATUSMESSAGE);
        dest.writeValue(oRDERID);
        dest.writeValue(aID);
        dest.writeValue(mID);
        dest.writeValue(cP);
        dest.writeValue(sT);
        dest.writeValue(rEINITTXNID);
    }

    public int describeContents() {
        return 0;
    }

}
