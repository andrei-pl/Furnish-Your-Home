package com.project.furnishyourhome.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

public class CustomListItem implements Parcelable {
    private String title;
    private Bitmap bitmap;
    private byte[] byteArray;
    private double price;
    private String dimensions;
    private String material;
    private String info;

    private Store store;

    public CustomListItem(){}

    public CustomListItem(String title, Bitmap bitmap){
        this.title = title;
        this.bitmap = bitmap;
    }

    private CustomListItem(Parcel in) {
        this.title = in.readString();
        in.readByteArray(byteArray);
        this.bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        out.writeByteArray(byteArray);
    }

    public static final Parcelable.Creator<CustomListItem> CREATOR = new Parcelable.Creator<CustomListItem>() {
        public CustomListItem createFromParcel(Parcel in) {
            return new CustomListItem(in);
        }

        public CustomListItem[] newArray(int size) {
            return new CustomListItem[size];
        }
    };

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
