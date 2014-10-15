package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Bid")
public class Bid extends ParseObject {
    public Item getItem() {
        return (Item) getParseObject("item");
    }

    public void setItem(Item item) {
        put("item", item);
    }

    public double getPrice() {
        return getDouble("price");
    }

    public void setPrice(double price) {
        put("price", price);
    }

    public User getBuyer() {
        return (User) getParseUser("buyer");
    }

    public void setBuyer(User buyer) {
        put("buyer", buyer);
    }

    public String getState() {
        return getString("state");
    }

    public void setState(String state) {
        put("state", state);
    }
}
