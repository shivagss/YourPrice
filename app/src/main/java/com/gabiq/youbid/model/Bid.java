package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Bid")
public class Bid extends ParseObject {

    public Bid()
    {

    }

    public String getItemId() {
        return getString("itemId");
    }
    public void setItemId(String id) {
        put("itemId", id);
    }

    public User getBuyer() {
        User user = null;
        try {
            user = new User(getParseUser("createdBy").fetch());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return user;
    }
    public void setBuyer(ParseUser user)
    {
        put("createdBy",user);
    }

    public double getPrice() {
        return getDouble("price");
    }
    public void setPrice(double price) {
        put("price", price);
    }

    public String getState() {
        return getString("state");
    }
    public void setState(String state) {
        put("state", state);
    }

}
