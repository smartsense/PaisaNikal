package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DmtTransactionEntity {

    public class DATA implements Parcelable {

        public final Parcelable.Creator<DATA> CREATOR = new Creator<DATA>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public DATA createFromParcel(Parcel in) {
                return new DATA(in);
            }

            public DATA[] newArray(int size) {
                return (new DATA[size]);
            }

        };
        @SerializedName("TRANSACTION_DATE")
        @Expose
        public String tRANSACTIONDATE;
        @SerializedName("CUSTOMER_REFERENCE_NO")
        @Expose
        public String cUSTOMERREFERENCENO;
        @SerializedName("SENDER_AVAILBAL")
        @Expose
        public Integer sENDERAVAILBAL;
        @SerializedName("TRANSACTION_DETAILS")
        @Expose
        public List<TRANSACTIONDETAIL> tRANSACTIONDETAILS = null;
        @SerializedName("BENEFICIARY_DETAILS")
        @Expose
        public BENEFICIARYDETAILS bENEFICIARYDETAILS;
        @SerializedName("CUSTOMER_DETAILS")
        @Expose
        public CUSTOMERDETAILS cUSTOMERDETAILS;

        protected DATA(Parcel in) {
            this.tRANSACTIONDATE = ((String) in.readValue((String.class.getClassLoader())));
            this.cUSTOMERREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
            this.sENDERAVAILBAL = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(this.tRANSACTIONDETAILS, (TRANSACTIONDETAIL.class.getClassLoader()));
            this.bENEFICIARYDETAILS = ((BENEFICIARYDETAILS) in.readValue((BENEFICIARYDETAILS.class.getClassLoader())));
            this.cUSTOMERDETAILS = ((CUSTOMERDETAILS) in.readValue((CUSTOMERDETAILS.class.getClassLoader())));
        }

        public DATA() {
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(tRANSACTIONDATE);
            dest.writeValue(cUSTOMERREFERENCENO);
            dest.writeValue(sENDERAVAILBAL);
            dest.writeList(tRANSACTIONDETAILS);
            dest.writeValue(bENEFICIARYDETAILS);
            dest.writeValue(cUSTOMERDETAILS);
        }

        public int describeContents() {
            return 0;
        }
    }

    public class Example implements Parcelable {

        public final Parcelable.Creator<Example> CREATOR = new Creator<Example>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public Example createFromParcel(Parcel in) {
                return new Example(in);
            }

            public Example[] newArray(int size) {
                return (new Example[size]);
            }

        };
        @SerializedName("TRANSACTIONN_FEE")
        @Expose
        public Integer tRANSACTIONNFEE;
        @SerializedName("RESP_CODE")
        @Expose
        public Integer rESPCODE;
        @SerializedName("RESPONSE")
        @Expose
        public String rESPONSE;
        @SerializedName("RESP_MSG")
        @Expose
        public String rESPMSG;
        @SerializedName("DATA")
        @Expose
        public DATA dATA;

        protected Example(Parcel in) {
            this.tRANSACTIONNFEE = ((Integer) in.readValue((Integer.class.getClassLoader())));
            this.rESPCODE = ((Integer) in.readValue((Integer.class.getClassLoader())));
            this.rESPONSE = ((String) in.readValue((String.class.getClassLoader())));
            this.rESPMSG = ((String) in.readValue((String.class.getClassLoader())));
            this.dATA = ((DATA) in.readValue((DATA.class.getClassLoader())));
        }

        public Example() {
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(tRANSACTIONNFEE);
            dest.writeValue(rESPCODE);
            dest.writeValue(rESPONSE);
            dest.writeValue(rESPMSG);
            dest.writeValue(dATA);
        }

        public int describeContents() {
            return 0;
        }
    }

    public class TRANSACTIONDETAIL implements Parcelable {

        public final Parcelable.Creator<TRANSACTIONDETAIL> CREATOR = new Creator<TRANSACTIONDETAIL>() {


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
        public Integer tRANSFERAMOUNT;
        @SerializedName("PAID_AMOUNT")
        @Expose
        public Integer pAIDAMOUNT;
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
            this.tRANSFERAMOUNT = ((Integer) in.readValue((Integer.class.getClassLoader())));
            this.pAIDAMOUNT = ((Integer) in.readValue((Integer.class.getClassLoader())));
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

    public class CUSTOMERDETAILS implements Parcelable {

        public final Parcelable.Creator<CUSTOMERDETAILS> CREATOR = new Creator<CUSTOMERDETAILS>() {


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


    public class BENEFICIARYDETAILS implements Parcelable {

        public final Parcelable.Creator<BENEFICIARYDETAILS> CREATOR = new Creator<BENEFICIARYDETAILS>() {


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
}
