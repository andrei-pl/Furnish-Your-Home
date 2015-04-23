package com.project.furnishyourhome.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

public class Store implements Parcelable {
    private String objectId;
    private String name;
    private Bitmap logo;
    private byte[] byteArray;
    private String workingHours;
    private String email;
    private String address;
    private String customersPhone;
    private String webpage;
    private Location location;

    public Store (){}

    public Store (Parcel in){
         super();
        objectId 		= in.readString();
        name 			= in.readString();

        in.readByteArray(byteArray);
        logo 			= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        workingHours 	= in.readString();
        email 			= in.readString();
        address 		= in.readString();
        customersPhone 	= in.readString();
        webpage			= in.readString();

        double[] doubleArray = new double[2];
        in.readDoubleArray(doubleArray);
        Location newLocation = new Location("");
        newLocation.setLatitude(doubleArray[0]);
        newLocation.setLongitude(doubleArray[1]);
        this.setLocation(newLocation);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(objectId);
        out.writeString(name);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        logo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        out.writeByteArray(byteArray);

        out.writeString(workingHours);
        out.writeString(email);
        out.writeString(address);
        out.writeString(customersPhone);
        out.writeString(webpage);

        Location loc = getLocation();
        out.writeDoubleArray(new double[]{
                loc.getLatitude(),
                loc.getLongitude()
        });
    }

    public static final Parcelable.Creator<Store> CREATOR = new Creator<Store>(){

        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomersPhone() {
        return customersPhone;
    }

    public void setCustomersPhone(String customersPhone) {
        this.customersPhone = customersPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
