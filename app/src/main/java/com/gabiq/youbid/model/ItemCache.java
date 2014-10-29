package com.gabiq.youbid.model;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Date;

public class ItemCache implements Serializable {
    private String objectId;
    private String caption;
    private String description;
    private String photoUrl;
    private String thumbnailUrl;
    private double minPrice;
    private boolean hasSold;
    private int viewCount;
    private int commentCount;
    private int likeCount;
    private Date createdAt;
    private UserCache user;

    public ItemCache() {
        super();
    }

    public ItemCache(ParseObject parseObject) {
        super();
        loadFromParseObject(parseObject);
    }

    void loadFromParseObject(ParseObject parseObject) {
        if (parseObject == null) return;
        objectId = parseObject.getObjectId();
        caption = parseObject.getString("caption");
        description = parseObject.getString("description");
        photoUrl = null;
        ParseFile photoParseFile = parseObject.getParseFile("photo");
        if (photoParseFile != null) {
            photoUrl = photoParseFile.getUrl();
        }
        thumbnailUrl = null;
        ParseFile thumbnailParseFile = parseObject.getParseFile("thumbnail");
        if (thumbnailParseFile != null) {
            thumbnailUrl = thumbnailParseFile.getUrl();
        }
        minPrice = parseObject.getDouble("minPrice");
        hasSold = parseObject.getBoolean("hasSold");
        viewCount = parseObject.getInt("viewCount");
        commentCount = parseObject.getInt("commentCount");
        likeCount = parseObject.getInt("likeCount");
        createdAt = parseObject.getCreatedAt();


        user = new UserCache();
        user.loadFromParseUser(parseObject.getParseUser("createdBy"));
    }

    public String getObjectId() {
        return objectId;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public boolean isHasSold() {
        return hasSold;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserCache getUser() {
        return user;
    }
}
