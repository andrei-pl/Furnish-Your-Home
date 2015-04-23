package com.project.furnishyourhome.models.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.project.furnishyourhome.models.Store;

import java.io.ByteArrayOutputStream;


@ParseClassName("Stores")
public class StoreParse extends ParseObject {

    private String objectId;

    public StoreParse(){
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public String getCustomersPhone() {
        return getString("customersPhone");
    }

    public void setCustomersPhone(String customersPhone) {
        put("customersPhone", customersPhone);
    }

    public String getEmail() {
        return getString("email");
    }

    public void setEmail(String email) {
        put("email", email);
    }

    public Bitmap getLogo() {
        ParseFile file = getParseFile("logo");
        byte[] imageByte = new byte[0];
        try {
            imageByte = file.getData();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

        return image;
    }

    public void setLogo(Bitmap logo, String fileName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        logo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile parseImage = new ParseFile(fileName, byteArray);
        put("drawable", parseImage);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getWebpage() {
        return getString("webpage");
    }

    public void setWebpage(String webpage) {
        put("webpage", webpage);
    }

    public String getWorkingHours() {
        return getString("workingHours");
    }

    public void setWorkingHours(String workingHours) {
        put("workingHours", workingHours);
    }

    public Location getLocation() {
        ParseGeoPoint geoPoint = getParseGeoPoint("location");
        Location location = new Location("");

        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());
        return location;
    }

    public void setLocation(Location location) {
        put("location", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
    }

    public Store getStore(){
        Store store = new Store();

        store.setName(this.getName());
        store.setAddress(this.getAddress());
        store.setCustomersPhone(this.getCustomersPhone());
        store.setEmail(this.getEmail());
        store.setWebpage(this.getWebpage());
        store.setWorkingHours(this.getWorkingHours());
        store.setLogo(this.getLogo());
        store.setObjectId(this.getObjectId());
        store.setLocation(this.getLocation());

        return store;
    }
}
