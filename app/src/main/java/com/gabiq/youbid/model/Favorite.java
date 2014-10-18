package com.gabiq.youbid.model;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Favorite")
public class Favorite  extends ParseObject {
    public static void setFavorite(final Item item, boolean value) {
        if (value) {
            ParseQuery<Favorite> query = ParseQuery.getQuery("Favorite");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("itemId", item.getObjectId());
            query.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (count == 0) {
                        Favorite favorite = new Favorite();
                        favorite.setItem(item);
                        favorite.setUser(ParseUser.getCurrentUser());
                        favorite.saveInBackground();
                    }
                }
            });
        } else {
            ParseQuery<Favorite> query = ParseQuery.getQuery("Favorite");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("itemId", item.getObjectId());
            query.getFirstInBackground(new GetCallback<Favorite>() {
                public void done(Favorite favorite, ParseException e) {
                    if (favorite != null) {
                        favorite.deleteInBackground();
                    }
                }
            });
        }
    }

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


