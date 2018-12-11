package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ReceiptEntity implements Parcelable {

    public static final Parcelable.Creator<ReceiptEntity> CREATOR = new Creator<ReceiptEntity>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ReceiptEntity createFromParcel(Parcel in) {
            return new ReceiptEntity(in);
        }

        public ReceiptEntity[] newArray(int size) {
            return (new ReceiptEntity[size]);
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
    public List<TRANSACTIONDETAIL> tRANSACTIONDETAILS = new ArrayList<TRANSACTIONDETAIL>();
    @SerializedName("BENEFICIARY_DETAILS")
    @Expose
    public BENEFICIARYDETAILS bENEFICIARYDETAILS;
    @SerializedName("CUSTOMER_DETAILS")
    @Expose
    public CUSTOMERDETAILS cUSTOMERDETAILS;

    protected ReceiptEntity(Parcel in) {
        this.tRANSACTIONDATE = ((String) in.readValue((String.class.getClassLoader())));
        this.cUSTOMERREFERENCENO = ((String) in.readValue((String.class.getClassLoader())));
        this.sENDERAVAILBAL = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.tRANSACTIONDETAILS, (aepsapp.easypay.com.aepsandroid.entities.TRANSACTIONDETAIL.class.getClassLoader()));
        this.bENEFICIARYDETAILS = ((BENEFICIARYDETAILS) in.readValue((BENEFICIARYDETAILS.class.getClassLoader())));
        this.cUSTOMERDETAILS = ((CUSTOMERDETAILS) in.readValue((CUSTOMERDETAILS.class.getClassLoader())));
    }

    public ReceiptEntity() {
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