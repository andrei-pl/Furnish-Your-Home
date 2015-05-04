package com.project.furnishyourhome.models.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.furnishyourhome.models.Furniture;
import com.project.furnishyourhome.models.Store;

/**
 * Created by Andrey on 27.4.2015 г..
 */
@ParseClassName("StoresFurnitures")
public class StoreFurnitureParse extends ParseObject {
    protected String storeId;
    protected String furnitureId;

    public StoreFurnitureParse(){}

    public String getStoreId (){
        this.setField("storeId");

        return this.storeId;
    }

    public void setStoreId (String storeId){
        put("storeId", storeId);
    }

    public String getFurnitureId (){
        this.setField("furnitureId");

        return this.furnitureId;
    }

    public void setFurnitureId(String furnitureId){
        put("furnitureId", this.furnitureId);
    }

    public Store getStore(){
        this.setField("storeId");
        StoreParse obj = new StoreParse();

        ParseQuery<StoreParse> query = ParseQuery.getQuery(StoreParse.class);
        try {
            obj = query.get(this.storeId);
            obj.setObjectId(this.storeId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return obj.getStore();
    }

    public Furniture getFurniture (){
        this.setField("furnitureId");
        FurnitureParse obj = new FurnitureParse();

        ParseQuery<FurnitureParse> query = ParseQuery.getQuery(FurnitureParse.class);
        try {
            obj = query.get(this.furnitureId);
            obj.setObjectId(this.furnitureId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return obj.getFurniture();
    }

    private void setField(String colName) {
        ParseObject storeFurniture = getParseObject(colName);

        switch (colName) {
            case "furnitureId":
                //this.furnitureId = storeFurniture.getObjectId().trim();
                this.furnitureId = storeFurniture.getObjectId().trim();
                break;
            case "storeId":
                this.storeId = storeFurniture.getObjectId().trim();
                break;
            default:
                throw new IllegalArgumentException(colName + " is not valid field name!");
        }
    }
}
