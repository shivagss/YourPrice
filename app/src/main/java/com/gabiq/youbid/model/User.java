package com.gabiq.youbid.model;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class User {

    private ParseUser user;

    public User(ParseUser user) {
        this.user = user;
    }

    public ParseUser getParseUser(){
        return user;
    }

    public String getObjectId(){
        return user.getObjectId();
    }

    public String getUsername(){
        return user.getUsername();
    }

    public String getEmail(){
        return user.getEmail();
    }

    public String getName(){
       return user.getString("name");
    }

    public void setName(String name) {
        user.put("name", name);
    }

    public ParseFile getProfilePhoto() {
        return user.getParseFile("profilePhoto");
    }

    public void setProfilePhoto(ParseFile profilePhoto) {
        user.put("profilePhoto", profilePhoto);;
    }

    public String getDescription() {
        return user.getString("description");
    }

    public void setDescription(String description) {
        user.put("description", description);;
    }

    public ParseGeoPoint getLocation() {
        return user.getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        user.put("location", location);
    }

    // Favorites accessors
    public void addToFavorites(Item item) {
        ParseRelation<ParseObject> relation = user.getRelation("favorite");
        relation.add(item);
        user.saveInBackground();
    }

    public void removeFromFavorites(Item item) {
        ParseRelation<ParseObject> relation = user.getRelation("favorite");
        relation.remove(item);
        user.saveInBackground();
    }

    public void getFavorites(FindCallback<ParseObject> findCallback) {
        ParseRelation<ParseObject> relation = user.getRelation("favorite");
        relation.getQuery().findInBackground(findCallback);
    }
}
