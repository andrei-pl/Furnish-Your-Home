package com.project.furnishyourhome.database;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.project.furnishyourhome.R;
import com.project.furnishyourhome.interfaces.DbTableNames;
import com.project.furnishyourhome.models.Furniture;
import com.project.furnishyourhome.models.Store;
import com.project.furnishyourhome.models.Type;
import com.project.furnishyourhome.models.parse.StoreFurnitureParse;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrey on 23.2.2015 г..
 */
public class UtilitiesDb implements DbTableNames{
    private SQLiteDatabase utilityDb;

    public UtilitiesDb(SQLiteDatabase db) {
        this.utilityDb = db;
    }

    public boolean addItems(ArrayList<StoreFurnitureParse> storesFurnituresList) {

        for (StoreFurnitureParse item : storesFurnituresList){
            String id = item.getObjectId();

            if(isIdExist(id, TABLE_STORESFURNITURES) <= 0) {
                Furniture furniture = item.getFurniture();
                String furnitureId = furniture.getObjectId().trim();

                if(isIdExist(furnitureId, TABLE_FURNITURES) <= 0) {
                    String name = furniture.getName();
                    String material = furniture.getMaterial();
                    String info = furniture.getInfo();
                    String dimension = furniture.getDimensions();
                    String price = String.valueOf(furniture.getPrice());
                    Bitmap drawable = furniture.getDrawable();
                    String typeId = furniture.getFurnitureId();
                    // String storeId = furniture.getStoreId();
                    byte[] imgData = getBitmapAsByteArray(drawable);
                    //Store store = furniture.getStore();

                    //put furniture in DB
                    try {
                        ContentValues reg = new ContentValues();
                        reg.put("_id", furnitureId);
                        reg.put("name", name);
                        reg.put("material", material);
                        reg.put("info", info);
                        reg.put("dimensions", dimension);
                        reg.put("price", price);
                        reg.put("drawable", imgData);
                        reg.put("furnitureId", typeId);
                        //reg.put("storeId", storeId);
                        utilityDb.insert(TABLE_FURNITURES, null, reg);

                        //addStore(store);
                    } catch (Exception e) {
                        return false;
                    }
                }

                // You can get all stores for every furniture and add them in DB every time even if the store already exist
                // without repeating it in DB
                // But is this the best way???
                // ArrayList<Store> stores = furniture.getStores();

                // put store in DB
//                for (Store store : stores){
//                    this.addStore(store);
//                    String storeId = store.getObjectId();
//                }
                Store store = item.getStore();
                if(!this.addStore(store)){
                    return false;
                };

                // Put furnitureId and storeId in the many to many connection table
                try {
                    ContentValues reg = new ContentValues();
                    reg.put("_id", item.getObjectId());
                    reg.put("storeId", store.getObjectId());
                    reg.put("furnitureId", furniture.getObjectId());
                    utilityDb.insert(TABLE_STORESFURNITURES, null, reg);
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean addStore(Store store) {
        try {
            Bitmap bitmap = store.getLogo();
            byte[] storeImgData = getBitmapAsByteArray(bitmap);
            Location location = store.getLocation();
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            String id = store.getObjectId();

            if(isIdExist(id, TABLE_STORES) <= 0) {

                ContentValues reg = new ContentValues();
                reg.put("_id", id);
                reg.put("name", store.getName());
                reg.put("address", store.getAddress());
                reg.put("email", store.getEmail());
                reg.put("webpage", store.getWebpage());
                reg.put("customersPhone", store.getCustomersPhone());
                reg.put("workingHours", store.getWorkingHours());
                reg.put("logo", storeImgData);
                reg.put("latitude", latitude);
                reg.put("longitude", longitude);
                utilityDb.insert(TABLE_STORES, null, reg);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean addTypes (ArrayList<Type> types){

        try {
            for (Type item : types) {
                String type = item.getType();
                Bitmap bitmap = item.getBitmap();
                String id = item.getId();

                if(isIdExist(id, TABLE_TYPES) <= 0) {
                    byte[] imgData = getBitmapAsByteArray(bitmap);
                    ContentValues reg = new ContentValues();
                    reg.put("_id", id);
                    reg.put("type", type);
                    reg.put("icon", imgData);
                    utilityDb.insert(TABLE_TYPES, null, reg);
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public ArrayList<Type> getTypes (){
        ArrayList<Type> types = new ArrayList<>();
        if(getTableCount(TABLE_TYPES) > 0) {

            String sql = "SELECT * FROM " + TABLE_TYPES;
            Cursor allItems = utilityDb.rawQuery(sql, null);

            while (allItems.moveToNext()) {
                String id = allItems.getString(0);
                String type = allItems.getString(1);
                byte[] logo = allItems.getBlob(2);
                Bitmap bitmap = getImage(logo);

                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher);
                }

                Type item = new Type();
                item.setId(id);
                item.setType(type);
                item.setBitmap(bitmap);
                types.add(item);
            }

            allItems.close();
        }

        return types;
    }

    public ArrayList<Furniture> getAllItems() {
        ArrayList<Furniture> temp = new ArrayList<>();

        HashMap<String, ArrayList<Store>> furnituresHashMap = new HashMap<>();

        if(getTableCount(TABLE_STORESFURNITURES) > 0) {
            String sql = "SELECT * FROM " + TABLE_STORESFURNITURES;
            Cursor allItems = utilityDb.rawQuery(sql, null);

            try {
                while (allItems.moveToNext()) {
                    String storeId = allItems.getString(1);
                    String furnitureId = allItems.getString(2);

                    if (!furnituresHashMap.containsKey(furnitureId)) {
                        furnituresHashMap.put(furnitureId, new ArrayList<Store>());
                    }

                    furnituresHashMap.get(furnitureId).add(this.getStore(storeId));
                }

                allItems.close();
            } catch (Exception e) {
                allItems.close();
                return null;
            }

            for (String furnitureId : furnituresHashMap.keySet()) {
                Furniture furniture = this.getFurniture(furnitureId);

                ArrayList<Store> stores = new ArrayList<>(furnituresHashMap.get(furnitureId));
                furniture.setStores(stores);
                temp.add(furniture);
            }
        }
//        if(getTableCount(TABLE_FURNITURES) > 0) {
//            String sql = "SELECT * FROM " + TABLE_FURNITURES;
//
//            Cursor allItems = utilityDb.rawQuery(sql, null);
//
//            while (allItems.moveToNext()) {
//                String id = allItems.getString(0);
//                String name = allItems.getString(1);
//                String material = allItems.getString(2);
//                String info = allItems.getString(3);
//                String dimensions = allItems.getString(4);
//                String price = allItems.getString(5);
//                double priceAsDouble = Double.parseDouble(price);
//                String furnitureId = allItems.getString(7);
////                String storeid = allItems.getString(8);
//                byte[] image = allItems.getBlob(allItems.getColumnIndex("drawable"));
//                Bitmap bitmap = getImage(image);
//
//                //get store for the item
////                Store store = getStore(storeid);
//
//                if (bitmap == null) {
//                    bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher);
//                }
//
//                Furniture baseItem = null;
//                // check type is this a Sofa or Table
//                String type = getType(furnitureId);
//
//                baseItem = new Furniture();
//                baseItem.setType(type);
//
//                assert baseItem != null;
//                baseItem.setObjectId(id);
//                baseItem.setName(name);
//                baseItem.setMaterial(material);
//                baseItem.setInfo(info);
//                baseItem.setDimensions(dimensions);
//                baseItem.setPrice(priceAsDouble);
//                baseItem.setFurnitureId(furnitureId);
//                //baseItem.setStoreId(storeid);
//                baseItem.setDrawable(bitmap);
//                //baseItem.setStore(store);
//
//                temp.add(baseItem);
//            }
//
//            allItems.close();
//        }

        return temp;
    }

    public ArrayList<Store> getStores (){
        ArrayList<Store> stores  = new ArrayList<>();

        if(getTableCount(TABLE_STORES) > 0) {

            String sql = "SELECT * FROM " + TABLE_STORES;
            Cursor allStores = utilityDb.rawQuery(sql, null);

            while (allStores.moveToNext()) {
                Store store = new Store();
                loadStoreData(store, allStores);

                stores.add(store);
            }

            allStores.close();
        }


        return stores;
    }

    private String getType(String furnitureId) {

        if (isIdExist(furnitureId, TABLE_TYPES) == 1) {
            String sql = "SELECT * FROM " + TABLE_TYPES + " WHERE _id='"
                    + furnitureId + "'";

            Cursor c = utilityDb.rawQuery(sql, null);
            String type;
            c.moveToFirst();
            type = c.getString(c.getColumnIndex("type"));

            c.close();
            return type;
        }
        return "";
    }

    private Store getStore (String storeId){
        Store store = new Store();
        if(isIdExist(storeId, TABLE_STORES) == 1) {
            String sql = "SELECT * FROM " + TABLE_STORES + " WHERE _id='"
                    + storeId + "'";

            Cursor c = utilityDb.rawQuery(sql, null);
            c.moveToFirst();
            loadStoreData(store, c);

            c.close();
        }
        return store;
    }

    private Furniture getFurniture (String furnitureId){
        Furniture furniture = new Furniture();
        if(isIdExist(furnitureId, TABLE_FURNITURES) == 1) {
            String sql = "SELECT * FROM " + TABLE_FURNITURES + " WHERE _id='"
                    + furnitureId + "'";

            Cursor c = utilityDb.rawQuery(sql, null);
            c.moveToFirst();
            furniture = loadFurnitureData(c);

            c.close();
        }
        return furniture;
    }

    private Furniture loadFurnitureData(Cursor c) {
        Furniture furniture = new Furniture();

        String id = c.getString(0);
        String name = c.getString(1);
        String material = c.getString(2);
        String info = c.getString(3);
        String dimensions = c.getString(4);
        String price = c.getString(5);
        byte[] drawable = c.getBlob(6);
        Bitmap bitmap = getImage(drawable);
        String typeId = c.getString(7);
        double priceDouble = Double.parseDouble(price);

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher);
        }

        furniture.setObjectId(id);
        furniture.setName(name);
        furniture.setMaterial(material);
        furniture.setInfo(info);
        furniture.setDimensions(dimensions);
        furniture.setPrice(priceDouble);
        furniture.setDrawable(bitmap);
        furniture.setType(this.getType(typeId));
        furniture.setFurnitureId(typeId);

        return furniture;
    }

    private void loadStoreData(Store store, Cursor c) {

        String id = c.getString(0);
        String name = c.getString(1);
        String address = c.getString(2);
        String email = c.getString(3);
        String webpage = c.getString(4);
        String customersPhone = c.getString(5);
        String workingHours = c.getString(6);
        String latitude = c.getString(8);
        String longitude = c.getString(9);
        byte[] image = c.getBlob(7);
        Bitmap bitmap = getImage(image);
        double latDouble = Double.parseDouble(latitude);
        double lonDouble = Double.parseDouble(longitude);
        Location location = new Location("");
        location.setLatitude(latDouble);
        location.setLongitude(lonDouble);

        store.setObjectId(id);
        store.setName(name);
        store.setAddress(address);
        store.setEmail(email);
        store.setWebpage(webpage);
        store.setCustomersPhone(customersPhone);
        store.setWorkingHours(workingHours);
        store.setLogo(bitmap);
        store.setLocation(location);
    }

    private int isIdExist (String id, String table){

        String sql = "SELECT _id FROM " + table + " WHERE _id='"
                + id + "'";
        Cursor c = utilityDb.rawQuery(sql, null);
        c.moveToFirst();
        String idString;

        try {
            idString = c.getString(0);
        } catch (Exception e) {
            c.close();
            return -1;
        }
        c.close();

        if (idString.equals("")) {

            return 0;
        }

        return 1;
    }

    public int getTableCount (String table){

        String sql = "SELECT * FROM " + table;
        Cursor c = utilityDb.rawQuery(sql, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public boolean isDbEmpty (){

        int countStores = getTableCount(TABLE_STORES);
        int countItems = getTableCount(TABLE_FURNITURES);
        int countTypes = getTableCount(TABLE_TYPES);
        int countStoresFurnitures = getTableCount(TABLE_STORESFURNITURES);

        if(countItems == 0 && countStores == 0 && countTypes == 0  && countStoresFurnitures == 0){
            return true;
        }

        return false;
    }

    public void deleteTable(String table){
        String sql = "DELETE FROM " + table;
        utilityDb.execSQL(sql);
    }

    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private Bitmap getImage(byte[] imgByte){

        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }
}
