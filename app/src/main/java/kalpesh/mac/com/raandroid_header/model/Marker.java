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
    private String city;
    private double latitude, longitude;

    public Marker(String nm, String add, String pc, String ci) {
        this.name = nm;
        this.address = add;
        this.postcode = pc;
        this.city = ci;
    }

    protected Marker(Parcel in) {
        name = in.readString();
        address = in.readString();
        postcode = in.readString();
        city = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
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

    public String getCity() { return city; }

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
        dest.writeString(city);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
