package com.example.hp.TranSaver;

import android.os.Parcel;
import android.os.Parcelable;


public class LocationObj implements Parcelable {
    private double Longitude;
    private double Latitude;
    private String iden;

    public LocationObj() {

    }

    private LocationObj(Parcel in) {
        iden = in.readString();
        Latitude = in.readDouble();
        Longitude = in.readDouble();
    }

    public String getIden() {
        return iden;
    }

    public void setIden(String iden) {
        this.iden = iden;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(iden);
        parcel.writeDouble(Latitude);
        parcel.writeDouble(Longitude);
    }

    public final Parcelable.Creator<LocationObj> CREATOR = new Creator<LocationObj>() {//it is going to read every set of variables of instances of parcelable class
        @Override
        public LocationObj createFromParcel(Parcel parcel) {
            return new LocationObj(parcel);
        }

        @Override
        public LocationObj[] newArray(int i) {
            return new LocationObj[i];
        }
    };

    public int describeContents() {
        return 0;
    }
}
