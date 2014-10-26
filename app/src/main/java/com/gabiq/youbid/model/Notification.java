package com.gabiq.youbid.model;


import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

@Table(name = "Notifications")
public class Notification extends Model {
    @Column(name = "type")
    private String type;

    @Column(name = "senderId")
    private String senderId;

    @Column(name = "message")
    private String message;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "bidId")
    private String bidId;

    @Column(name = "itemId")
    private String itemId;

    @Column(name = "senderName")
    private String senderName;

    @Column(name = "senderPhotoUrl")
    private String senderPhotoUrl;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhotoUrl() {
        return senderPhotoUrl;
    }

    public void setSenderPhotoUrl(String senderPhotoUrl) {
        this.senderPhotoUrl = senderPhotoUrl;
    }

    public static List<Notification> getAll() {
        return new Select()
                .from(Notification.class)
                .orderBy("createdAt DESC")
                .limit(30)
                .execute();
    }

    public void loadUserInfo() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        final String senderId = getSenderId();
        query.getInBackground(senderId, new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                // check if request is outdated

                if (e == null && parseUser != null) {
                    User user = new User(parseUser);
                    setSenderName(parseUser.getString("name"));
                    ParseFile parseFile = parseUser.getParseFile("photo");
                    if (parseFile != null) {
                        setSenderPhotoUrl(parseFile.getUrl());
                    }

                    save();

                } else {
                    // something went wrong
                    Log.e("ERROR", "Error reading user in notification"+e);
                }
            }
        });

    }

}
