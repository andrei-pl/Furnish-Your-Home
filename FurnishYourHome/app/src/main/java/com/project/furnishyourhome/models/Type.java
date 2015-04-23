package com.project.furnishyourhome.models;

import android.graphics.Bitmap;


public class Type {
    private String type;
    private Bitmap bitmap;
    private String id;

    public String getId() {
        return id;
    }

    public Type(){
    }

    public Type(String id, Bitmap bitmap, String type){
        this.setId(id);
        this.setBitmap(bitmap);
        this.setType(type);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
