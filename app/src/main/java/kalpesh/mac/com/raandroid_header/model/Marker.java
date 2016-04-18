package kalpesh.mac.com.raandroid_header.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fatherjim on 15/04/2016.
 */
public class Marker implements Parcelable {

    private String name;
    private String address;
    private String postcode;
    private double latitude, longitude;
    private int correct_info;

    public Marker(String nm, String add, String pc) {
        this.name = nm;
        this.address = add;
        this.postcode = pc;
        correct_info = 1;
    }

    protected Marker(Parcel in) {
        name = in.readString();
        address = in.readString();
        postcode = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        correct_info = in.readInt();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setLatitude(double lat) {
        latitude = lat;
    }

    public void setLongitude(double lo) {
        longitude = lo;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setInfoIncorrect() {
        correct_info = 0;
    }

    public int isInfoCorrect() {
        return correct_info;
    }

    public static final Creator<Marker> CREATOR = new Creator<Marker>() {
        @Override
        public Marker createFromParcel(Parcel in) {
            return new Marker(in);
        }

        @Override
        public Marker[] newArray(int size) {
            return new Marker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(postcode);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(correct_info);
    }
}
