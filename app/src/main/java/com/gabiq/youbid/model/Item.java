package com.gabiq.youbid.model;


import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Item")
public class Item extends ParseObject{
    private boolean favorite;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Item() {
        // A default constructor is required by Parse.
    }

    public String getCaption() {
        return getString("caption");
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String caption) {
        put("description", caption);
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

    public ParseGeoPoint getLocation(){ return getParseGeoPoint("location");}
    public void setLocation (ParseGeoPoint location){ put("location", location);}

    public User getUser() {
        User user = null;
        try {
            user = new User(getParseUser("createdBy").fetch());
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return user;
    }

    public void setUser(ParseUser user)
    {
        put("createdBy",user);
    }

    public int getViewCount() {
        return getInt("viewCount");
    }
    public void setViewCount(int viewCount) {
        put("viewCount", viewCount);
    }

    public List<Keyword> getKeywords(){
        List<Keyword> list = new ArrayList<Keyword>();
        List<Keyword> cloudList = null;
        try {
            cloudList = fetch(). getList("keywords");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(cloudList != null){
            list.addAll(cloudList);
        }
        return list;
    }
    public void setKeywords(List<Keyword> list){

//        ParseRelation<ParseObject> relation = getRelation("keywords");
//        List<Keyword> removeList = new ArrayList<Keyword>();
//        for(Keyword keyword : getKeywords()){
//            if(!list.contains(keyword)){
//                removeList.add(keyword);
//                relation.remove(keyword);
//            }
//        }

        addAllUnique("keywords", list);
    }

    public int getLikeCount() {
        return getInt("likeCount");
    }
    public void setLikeCount(int likeCount) {
        put("likeCount", likeCount);
    }

}
