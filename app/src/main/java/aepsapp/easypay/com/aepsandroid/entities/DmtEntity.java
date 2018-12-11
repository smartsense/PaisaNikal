package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DmtEntity implements Parcelable {

    public final static Parcelable.Creator<DmtEntity> CREATOR = new Creator<DmtEntity>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DmtEntity createFromParcel(Parcel in) {
            return new DmtEntity(in);
        }

        public DmtEntity[] newArray(int size) {
            return (new DmtEntity[size]);
        }

    };
    @SerializedName("SENDER_TITLE")
    @Expose
    public String sENDERTITLE;
    @SerializedName("SEDNER_FNAME")
    @Expose
    public String sEDNERFNAME;
    @SerializedName("SENDER_LNAME")
    @Expose
    public String sENDERLNAME;
    @SerializedName("SENDER_CUSTTYPE")
    @Expose
    public String sENDERCUSTTYPE;
    @SerializedName("SEDNER_GENDER")
    @Expose
    public String sEDNERGENDER;
    @SerializedName("SENDER_EMAIL")
    @Expose
    public String sENDEREMAIL;
    @SerializedName("SENDER_MOBILENO")
    @Expose
    public Double sENDERMOBILENO;
    @SerializedName("SENDER_ALTMOBILENO")
    @Expose
    public Double sENDERALTMOBILENO;
    @SerializedName("SENDER_ADDRESS1")
    @Expose
    public String sENDERADDRESS1;
    @SerializedName("SENDER_ADDRESS2")
    @Expose
    public String sENDERADDRESS2;
    @SerializedName("STATE")
    @Expose
    public String sTATE;
    @SerializedName("CITY")
    @Expose
    public String cITY;
    @SerializedName("PINCODE")
    @Expose
    public String pINCODE;
    @SerializedName("SENDER_AVAILBAL")
    @Expose
    public Integer sENDERAVAILBAL;
    @SerializedName("SENDER_MONTHLYBAL")
    @Expose
    public Integer sENDERMONTHLYBAL;
    @SerializedName("SENDER_VERIFICATIONCODE")
    @Expose
    public Boolean sENDERVERIFICATIONCODE;
    @SerializedName("SENDER_KYCSTATUS")
    @Expose
    public String sENDERKYCSTATUS;
    @SerializedName("SENDER_KYCTYPE")
    @Expose
    public String sENDERKYCTYPE;
    @SerializedName("SENDER_REGISTERDATE")
    @Expose
    public String sENDERREGISTERDATE;
    @SerializedName("SENDER_ACTIVATIONDATE")
    @Expose
    public String sENDERACTIVATIONDATE;
    @SerializedName("SENDER_DOB")
    @Expose
    public String sENDERDOB;
    @SerializedName("PREPAID_INSTRUMENTFLAG")
    @Expose
    public Boolean pREPAIDINSTRUMENTFLAG;
    @SerializedName("PANCARD_FLAG")
    @Expose
    public Boolean pANCARDFLAG;
    @SerializedName("PANCARD_NO")
    @Expose
    public String pANCARDNO;
    @SerializedName("AADHARCARD_FLAG")
    @Expose
    public Boolean aADHARCARDFLAG;
    @SerializedName("PROOF_NUMBER")
    @Expose
    public String pROOFNUMBER;
    @SerializedName("BENEFICIARY_DATA")
    @Expose
    public List<BeneficiaryEntity> bENEFICIARYDATA = new ArrayList<>();
    @SerializedName("CUSTDOC_REJECT_FLAG")
    @Expose
    public Boolean cUSTDOCREJECTFLAG;
    @SerializedName("PANCARD_REJECT_REASON")
    @Expose
    public String pANCARDREJECTREASON;
    @SerializedName("PANCARD_REJET_REMARKS")
    @Expose
    public String pANCARDREJETREMARKS;
    @SerializedName("PANCARD_STATUS")
    @Expose
    public String pANCARDSTATUS;

    protected DmtEntity(Parcel in) {
        this.sENDERTITLE = ((String) in.readValue((String.class.getClassLoader())));
        this.sEDNERFNAME = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERLNAME = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERCUSTTYPE = ((String) in.readValue((String.class.getClassLoader())));
        this.sEDNERGENDER = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDEREMAIL = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERMOBILENO = ((Double) in.readValue((Double.class.getClassLoader())));
        this.sENDERALTMOBILENO = ((Double) in.readValue((Double.class.getClassLoader())));
        this.sENDERADDRESS1 = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERADDRESS2 = ((String) in.readValue((String.class.getClassLoader())));
        this.sTATE = ((String) in.readValue((String.class.getClassLoader())));
        this.cITY = ((String) in.readValue((String.class.getClassLoader())));
        this.pINCODE = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERAVAILBAL = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.sENDERMONTHLYBAL = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.sENDERVERIFICATIONCODE = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.sENDERKYCSTATUS = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERKYCTYPE = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERREGISTERDATE = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERACTIVATIONDATE = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERDOB = ((String) in.readValue((String.class.getClassLoader())));
        this.pREPAIDINSTRUMENTFLAG = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.pANCARDFLAG = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.pANCARDNO = ((String) in.readValue((String.class.getClassLoader())));
        this.aADHARCARDFLAG = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.pROOFNUMBER = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.bENEFICIARYDATA, (aepsapp.easypay.com.aepsandroid.entities.BeneficiaryEntity.class.getClassLoader()));
        this.cUSTDOCREJECTFLAG = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.pANCARDREJECTREASON = ((String) in.readValue((String.class.getClassLoader())));
        this.pANCARDREJETREMARKS = ((String) in.readValue((String.class.getClassLoader())));
        this.pANCARDSTATUS = ((String) in.readValue((String.class.getClassLoader())));
    }

    public DmtEntity() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(sENDERTITLE);
        dest.writeValue(sEDNERFNAME);
        dest.writeValue(sENDERLNAME);
        dest.writeValue(sENDERCUSTTYPE);
        dest.writeValue(sEDNERGENDER);
        dest.writeValue(sENDEREMAIL);
        dest.writeValue(sENDERMOBILENO);
        dest.writeValue(sENDERALTMOBILENO);
        dest.writeValue(sENDERADDRESS1);
        dest.writeValue(sENDERADDRESS2);
        dest.writeValue(sTATE);
        dest.writeValue(cITY);
        dest.writeValue(pINCODE);
        dest.writeValue(sENDERAVAILBAL);
        dest.writeValue(sENDERMONTHLYBAL);
        dest.writeValue(sENDERVERIFICATIONCODE);
        dest.writeValue(sENDERKYCSTATUS);
        dest.writeValue(sENDERKYCTYPE);
        dest.writeValue(sENDERREGISTERDATE);
        dest.writeValue(sENDERACTIVATIONDATE);
        dest.writeValue(sENDERDOB);
        dest.writeValue(pREPAIDINSTRUMENTFLAG);
        dest.writeValue(pANCARDFLAG);
        dest.writeValue(pANCARDNO);
        dest.writeValue(aADHARCARDFLAG);
        dest.writeValue(pROOFNUMBER);
        dest.writeList(bENEFICIARYDATA);
        dest.writeValue(cUSTDOCREJECTFLAG);
        dest.writeValue(pANCARDREJECTREASON);
        dest.writeValue(pANCARDREJETREMARKS);
        dest.writeValue(pANCARDSTATUS);
    }

    public int describeContents() {
        return 0;
    }
}
