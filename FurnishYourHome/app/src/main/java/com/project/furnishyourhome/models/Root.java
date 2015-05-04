package com.project.furnishyourhome.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

/**
 * Created by Andrey on 27.4.2015 г..
 */
public class Root implements Parcelable {
    private String objectId;
    private Store store;
    private Furniture furniture;

    public Root(){};

    public Root(Parcel in) {
        super();
        this.objectId = in.readString();
        this.store = in.readParcelable(Store.class.getClassLoader());
        this.furniture = in.readParcelable(Furniture.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    public static final Parcelable.Creator<Root> CREATOR = new Parcelable.Creator<Root>() {
        public Root createFromParcel(Parcel in) {

            return new Root(in);
        }

        public Root[] newArray(int size) {

            return new Root[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.objectId);
        out.writeParcelable(this.store,flags);
        out.writeParcelable(this.furniture,flags);
    }

    public Store getStore(){
        return this.store;
    }

    public void setStore(Store store){
        this.store = store;
    }

    public Furniture getFurniture(){
        return this.furniture;
    }

    public void setFurniture(Furniture furniture){
        this.furniture = furniture;
    }
}