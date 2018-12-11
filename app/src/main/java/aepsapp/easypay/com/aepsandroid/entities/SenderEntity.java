package aepsapp.easypay.com.aepsandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class SenderEntity implements Parcelable {

    public static final Creator<SenderEntity> CREATOR = new Creator<SenderEntity>() {
        @Override
        public SenderEntity createFromParcel(Parcel in) {
            return new SenderEntity(in);
        }

        @Override
        public SenderEntity[] newArray(int size) {
            return new SenderEntity[size];
        }
    };
    String mobile;
    String address;
    String pincode;
    String state;
    String name;
    String city;

    public SenderEntity() {
    }

    public SenderEntity(Parcel in) {
        mobile = in.readString();
        address = in.readString();
        pincode = in.readString();
        state = in.readString();
        name = in.readString();
        city = in.readString();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobile);
        dest.writeString(address);
        dest.writeString(pincode);
        dest.writeString(state);
        dest.writeString(name);
        dest.writeString(city);
    }
}
