package com.gabiq.youbid.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.List;

@ParseClassName("User")
public class User extends ParseUser {

    public ParseFile getProfilePhoto() {
        return getParseFile("profilePhoto");
    }

    public void setProfilePhoto(ParseFile profilePhoto) {
        put("profilePhoto", profilePhoto);;
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);;
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        put("location", location);
    }

    // Favorites accessors
    public void addToFavorites(Item item) {
        ParseRelation<ParseObject> relation = getRelation("favorite");
        relation.add(item);
        saveInBackground();
    }

    public void removeFromFavorites(Item item) {
        ParseRelation<ParseObject> relation = getRelation("favorite");
        relation.remove(item);
        saveInBackground();
    }

    public void getFavorites(FindCallback<ParseObject> findCallback) {
        ParseRelation<ParseObject> relation = getRelation("favorite");
        relation.getQuery().findInBackground(findCallback);
    }
}
