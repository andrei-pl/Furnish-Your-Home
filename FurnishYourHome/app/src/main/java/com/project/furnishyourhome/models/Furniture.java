package com.project.furnishyourhome.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

public class Furniture implements Parcelable {

    private String objectId;
    private String name;
    private Bitmap drawable;
    private byte[] byteArray;
    private double price;
    private String dimensions;
    private String material;
    private String info;
    private Store store;
    private String storeId;
    private String furnitureId;
    private String type;

    public Furniture(){
    }

    public Furniture(String storeId) {
        this.storeId = storeId;
    }

    protected Furniture(Parcel in) {
        super();
        objectId = in.readString();
        name = in.readString();

        in.readByteArray(byteArray);
        drawable = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        price = in.readDouble();
        dimensions = in.readString();
        material = in.readString();
        info = in.readString();
        store = in.readParcelable(Store.class.getClassLoader());
        storeId = in.readString();
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
        out.writeParcelable(store,flags);
        out.writeString(storeId);
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

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }


    public byte[] getImageAsByteArray (){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getDrawable().compress(Bitmap.CompressFormat.PNG, 100, stream);
        //byte[] byteArray = stream.toByteArray();
        byteArray = stream.toByteArray(); // TODO: CHANGED

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
}
