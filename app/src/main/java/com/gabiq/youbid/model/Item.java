package com.gabiq.youbid.model;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("Item")
public class Item extends ParseObject implements Serializable{

    private static final long serialVersionUID = 1212121212L;

    public Item() {
        // A default constructor is required by Parse.
    }

    public String getCaption() {
        return getString("caption");
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }

    public double getMinPrice() {
        return getDouble("minPrice");
    }

    public void setMinPrice(double minPrice) {
        put("minPrice", minPrice);
    }

    public boolean getHasSold() {
        return getBoolean("hasSold");
    }

    public void setHasSold(Boolean hasSold) {
        put("hasSold", hasSold);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        put("geoLocation", location);
    }

    public User getUser() {
        return (User)getParseUser("user");
    }

    public void setUser(User user){
        //put("user", ParseUser.createWithoutData(User.class, user.getObjectId()));
        put("user", user);
    }

}
