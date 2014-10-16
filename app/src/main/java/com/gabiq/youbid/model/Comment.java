package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("Comment")
public class Comment extends ParseObject {
    public Item getItem() {
        return (Item) getParseObject("item");
    }

    public void setItem(Item item) {
        put("item", item);
    }

    public User getUser() {
        return new User(getParseUser("userId"));
    }

    public void setUser(User user){
        put("userId", user.getParseUser().getObjectId());
    }

    public String getBody() {
        return getString("body");
    }

    public void setBody(String body) {
        put("body", body);
    }


}
