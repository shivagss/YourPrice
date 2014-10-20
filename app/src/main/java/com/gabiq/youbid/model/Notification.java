package com.gabiq.youbid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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

    public static List<Notification> getAll() {
        return new Select()
                .from(Notification.class)
                .orderBy("createdAt DESC")
                .limit(30)
                .execute();
    }

}
