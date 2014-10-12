package com.gabiq.youbid.model;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Item")
public class Item extends ParseObject {

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

    public void putMinPrice(double minPrice) {
        put("minPrice", minPrice);
    }

    public boolean getHasSold() {
        return getBoolean("hasSold");
    }

    public void putHasSold(Boolean hasSold) {
        put("hasSold", hasSold);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void putLocation(ParseGeoPoint location) {
        put("geoLocation", location);
    }

    public User getUser() {
        return (User)getParseUser("userId");
    }

    public void putUser(User user){
        put("userId", ParseUser.createWithoutData(User.class, user.getObjectId()));
    }

}
