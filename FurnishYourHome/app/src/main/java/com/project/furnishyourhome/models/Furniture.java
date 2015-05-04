package com.project.furnishyourhome.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Furniture implements Parcelable {

    private String objectId;
    private String name;
    private Bitmap drawable;
    private byte[] byteArray;
    private double price;
    private String dimensions;
    private String material;
    private String info;
    private String furnitureId;
    private String type;
    private ArrayList<Store> stores;

    public Furniture(){
        this.stores = new ArrayList<>();
    }

    protected Furniture(Parcel in) {
        super();
        this.stores = new ArrayList<>();
        objectId = in.readString();
        name = in.readString();

        in.readByteArray(byteArray);
        drawable = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        price = in.readDouble();
        dimensions = in.readString();
        material = in.readString();
        info = in.readString();
        this.stores = in.createTypedArrayList(Store.CREATOR);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(type);
        out.writeString(objectId);
        out.writeString(name);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        drawable.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        out.writeByteArray(byteArray);

        out.writeDouble(price);
        out.writeString(dimensions);
        out.writeString(material);
        out.writeString(info);
        out.writeTypedList(this.stores);
    }

    public static final Parcelable.Creator<Furniture> CREATOR = new Parcelable.Creator<Furniture>() {
        public Furniture createFromParcel(Parcel in) {

            return new Furniture(in);
        }

        public Furniture[] newArray(int size) {

            return new Furniture[size];
        }
    };

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Bitmap getDrawable() {
        return drawable;
    }

    public void setDrawable(Bitmap drawable) {
        this.drawable = drawable;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public byte[] getImageAsByteArray (){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getDrawable().compress(Bitmap.CompressFormat.PNG, 100, stream);
        //byte[] byteArray = stream.toByteArray();
        byteArray = stream.toByteArray();

        return byteArray;
    }

    public void setFurnitureId(String furnitureId) {
        this.furnitureId = furnitureId;
    }

    public String getFurnitureId() {
        return furnitureId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Store> getStores() {
        return this.stores;
    }

    public void setStores(ArrayList<Store> stores) {
        this.stores = stores;
    }
}
