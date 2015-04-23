package com.project.furnishyourhome.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.io.ByteArrayOutputStream;

public class CustomBitmap implements Parcelable {

    private Bitmap bitmap;
    private float endX;
    private float endY;
    private float halfHeight;
    private float halfWidth;
    private float startX;
    private float startY;
    private float x;
    private float y;

    public CustomBitmap(Bitmap bitmap, float x, float y) {
        setBitmap(bitmap);
        halfWidth = (bitmap.getWidth() / 2);
        halfHeight = (bitmap.getHeight() / 2);
        setX(x);
        setY(y);
        calculateBoundaries();
    }

    private CustomBitmap(Parcel in) {
        Bundle bundle = in.readBundle();
        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeByteArray(bundle.getByteArray("bitmap"), 0, bundle.getByteArray("bitmap").length, options);
        x = bundle.getFloat("x");
        y = bundle.getFloat("y");
        Log.d("restored x y", x + " " + y);
        halfWidth = bundle.getFloat("halfWidth");
        halfHeight = bundle.getFloat("halfHeight");
        startX = bundle.getFloat("startX");
        endX = bundle.getFloat("endX");
        startY = bundle.getFloat("startY");
        endY = bundle.getFloat("endY");
    }

    private void calculateBoundaries() {
        startX = (x - halfWidth);
        endX = (x + halfWidth);
        startY = (y - halfHeight);
        endY = (y + halfHeight);
    }

    public int describeContents()
    {
        return 0;
    }

    public Bitmap getBitmap()
    {
        return this.bitmap;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public boolean isInBitmap(float x, float y) {
        calculateBoundaries();
        float f1 = x - halfWidth;
        float f2 = y - halfHeight;
        return (f1 >= startX) && (f1 < endX) && (f2 >= startY) && (f2 < endY);
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public void setX(float x)
    {
        this.x = (x - this.halfWidth);
    }

    public void setY(float y)
    {
        this.y = (y - this.halfHeight);
    }

    public void writeToParcel(Parcel out, int flags) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
        Bundle bundle = new Bundle();
        bundle.putByteArray("bitmap", arrayOfByte);
        bundle.putFloat("x", x);
        bundle.putFloat("y", y);
        Log.d("saved x y", x + " " + y);
        bundle.putFloat("halfWidth", halfWidth);
        bundle.putFloat("halfHeight", halfHeight);
        bundle.putFloat("startX", startX);
        bundle.putFloat("endX", endX);
        bundle.putFloat("startY", startY);
        bundle.putFloat("endY", endY);
        out.writeBundle(bundle);
    }

    public static final Creator<CustomBitmap> CREATOR
            = new Creator<CustomBitmap>() {
        public CustomBitmap createFromParcel(Parcel in) {
            return new CustomBitmap(in);
        }

        public CustomBitmap[] newArray(int size) {
            return new CustomBitmap[size];
        }
    };
}