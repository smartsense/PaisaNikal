package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TRANSACTIONDETAIL implements Parcelable {

    public static final Parcelable.Creator<TRANSACTIONDETAIL> CREATOR = new Creator<TRANSACTIONDETAIL>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TRANSACTIONDETAIL createFromParcel(Parcel in) {
            return new TRANSACTIONDETAIL(in);
        }

        public TRANSACTIONDETAIL[] newArray(int size) {
            return (new TRANSACTIONDETAIL[size]);
        }

    };
    @SerializedName("RESP_CODE")
    @Expose
    public Integer rESPCODE;
    @SerializedName("RESPONSE")
    @Expose
    public String rESPONSE;
    @SerializedName("RESP_MSG")
    @Expose
    public String rESPMSG;
    @SerializedName("INIT_PENDING")
    @Expose
    public Boolean iNITPENDING;
    @SerializedName("VERSION")
    @Expose
    public String vERSION;
    @SerializedName("UNIQUE_RESPONSENO")
    @Expose
    public String uNIQUERESPONSENO;
    @SerializedName("ATTEPMTNO")
    @Expose
    public String aTTEPMTNO;
    @SerializedName("TRANSFER_TYPE")
    @Expose
    public String tRANSFERTYPE;
    @SerializedName("LOW_BALANCE_ALERT")
    @Expose
    public String lOWBALANCEALERT;
    @SerializedName("STATUS_CODE")
    @Expose
    public String sTATUSCODE;
    @SerializedName("SUB_STATUS_CODE")
    @Expose
    public String sUBSTATUSCODE;
    @SerializedName("BANK_REFERENCE_NO")
    @Expose
    public String bANKREFERENCENO;
    @SerializedName("EP_REFERENCE_NO")
    @Expose
    public String ePREFERENCENO;
    @SerializedName("REQUEST_REFERENCE_NO")
    @Expose
    public String rEQUESTREFERENCENO;
    @SerializedName("RESPONSE_REFERENCE_NO")
    @Expose
    public Integer rESPONSEREFERENCENO;
    @SerializedName("TRANSFER_AMOUNT")
    @Expose
    public Double tRANSFERAMOUNT;
    @SerializedName("PAID_AMOUNT")
    @Expose
    public Double pAIDAMOUNT;
    @SerializedName("TXN_BENENAME")
    @Expose
    public String tXNBENENAME;

    protected TRANSACTIONDETAIL(Parcel in) {
        this.rESPCODE = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.rESPONSE = ((String) in.readValue((String.class.getClassLoader())));
        this.rESPMSG = ((String) in.readValue((String.class.getClassLoader())));
        this.iNITPENDING = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.vERSION = ((String) in.readValue((String.class.getClassLoader())));
        this.uNIQUERESPONSENO = ((String) in.readValue((String.class.getClassLoader())));
        this.aTTEPMTNO = ((String) in.readValue((String.class.getClassLoader())));
        this.tRANSFERTYPE = ((String) in.readValue((String.class.getClassLoader())));
        this.lOWBALANCEALERT = ((String) in.readValue((String.class.getClassLoader())));
        this.sTATUSCODE = ((String) in.readValue((String.class.getClassLoader())));
        this.sUBSTATUSCODE = ((String) in.readValue((String.class.getClassLoader())));
        this.bANKREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
        this.ePREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
        this.rEQUESTREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
        this.rESPONSEREFERENCENO = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.tRANSFERAMOUNT = ((Double) in.readValue((Integer.class.getClassLoader())));
        this.pAIDAMOUNT = ((Double) in.readValue((Integer.class.getClassLoader())));
        this.tXNBENENAME = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TRANSACTIONDETAIL() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(rESPCODE);
        dest.writeValue(rESPONSE);
        dest.writeValue(rESPMSG);
        dest.writeValue(iNITPENDING);
        dest.writeValue(vERSION);
        dest.writeValue(uNIQUERESPONSENO);
        dest.writeValue(aTTEPMTNO);
        dest.writeValue(tRANSFERTYPE);
        dest.writeValue(lOWBALANCEALERT);
        dest.writeValue(sTATUSCODE);
        dest.writeValue(sUBSTATUSCODE);
        dest.writeValue(bANKREFERENCENO);
        dest.writeValue(ePREFERENCENO);
        dest.writeValue(rEQUESTREFERENCENO);
        dest.writeValue(rESPONSEREFERENCENO);
        dest.writeValue(tRANSFERAMOUNT);
        dest.writeValue(pAIDAMOUNT);
        dest.writeValue(tXNBENENAME);
    }

    public int describeContents() {
        return 0;
    }

}