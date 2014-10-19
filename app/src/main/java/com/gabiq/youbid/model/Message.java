package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {

    public Bid getBid() {
        return (Bid) getParseObject("bid");
    }

    public void setBid(Bid bid) {
        put("bid", bid);
    }

    public Item getItem() {
        return (Item) getParseObject("item");
    }

    public void setItem(Item item) {
        put("item", item);
    }

    public String getBody() {
        return getString("body");
    }

    public void setBody(String body) {
        put("body", body);
    }

    public ParseUser getSender() {
        return getParseUser("sender");
    }

    public void setSender(ParseUser user)
    {
        put("sender", user);
    }

}
