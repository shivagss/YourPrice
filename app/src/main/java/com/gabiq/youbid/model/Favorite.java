package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Favorite")
public class Favorite  extends ParseObject {
    public static void setFavorite(Item item, boolean value) {
        if (value) {
            Favorite favorite = new Favorite();
            favorite.setItem(item);
            favorite.setUser(ParseUser.getCurrentUser());
            favorite.saveInBackground();
        } else {

        }

    }

//    public static void isFavorite(Item item, ) {
//
//    }

    public Item getItem() {
        return (Item) getParseObject("item");
    }

    public void setItem(Item item) {
        put("item", item);
        put("itemId", item.getObjectId());
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public User getUser() {
        User user = null;
        try {
            user = new User(getParseUser("user").fetch());
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return user;
    }
}


