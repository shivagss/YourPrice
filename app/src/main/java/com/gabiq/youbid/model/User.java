package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
}
