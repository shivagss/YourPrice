package com.gabiq.youbid.model;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.Serializable;

public class UserCache implements Serializable {
    private String objectId;
    private String name;
    private String photoUrl;
    private String website;
    private String about;
    private String locationText;

    public UserCache() {
        super();
    }

    public UserCache(ParseUser parseUser) {
        loadFromParseUser(parseUser);
    }

    public void loadFromParseUser(ParseUser parseUser) {
        if (parseUser == null) return;
        objectId = parseUser.getObjectId();
        name = parseUser.getString("name");
        website = parseUser.getString("website");
        about = parseUser.getString("about");
        locationText = parseUser.getString("locationText");
        ParseFile parseFile = parseUser.getParseFile("photo");
        photoUrl = null;
        if (parseFile != null) {
            photoUrl = parseFile.getUrl();
        }
    }

    public String getObjectId() {
        return objectId;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getWebsite() {
        return website;
    }

    public String getAbout() {
        return about;
    }

    public String getLocationText() {
        return locationText;
    }

}
