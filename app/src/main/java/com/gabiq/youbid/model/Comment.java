package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Comment")
public class Comment extends ParseObject {

    public Comment()
    {

    }

    public String getItemId() {
        return getString("itemId");
    }
    public void setItemId(String id) {
        put("itemId", id);
    }

    public User getUser() {
        User user = null;
        try {
            user = new User(getParseUser("createdBy").fetch());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void setUser(ParseUser user)
    {
        put("createdBy",user);
    }
    public String getBody() {
        return getString("body");
    }
    public void setBody(String body) {
        put("body", body);
    }

}
