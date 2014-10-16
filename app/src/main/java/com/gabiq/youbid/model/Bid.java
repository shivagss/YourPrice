package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
        return new User(getParseUser("buyer"));
    }

    public void setBuyer(User user){
        put("buyer", ParseUser.createWithoutData(ParseUser.class, user.getParseUser().getObjectId()));
    }

    public String getState() {
        return getString("state");
    }

    public void setState(String state) {
        put("state", state);
    }
}
