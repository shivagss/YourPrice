package com.gabiq.youbid.model;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Item")
public class Item extends ParseObject{

    public Item() {
        // A default constructor is required by Parse.
    }

    public String getCaption() {
        return getString("caption");
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public String getCaption1() {
        return getString("caption1");
    }

    public void setCaption1(String caption) {
        put("caption1", caption);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }

    public ParseFile getThumbnailFile() {
        return getParseFile("thumbnail");
    }

    public void setThumbnailFile(ParseFile file) {
        put("thumbnail", file);
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

    public User getUser() {
        return new User(getParseUser(getUserId()));
    }

    public String getUserId(){
        return getString("user_Id");
    }
    public void setUserId(){
        put("user_Id", ParseUser.getCurrentUser().getObjectId());
    }

}
