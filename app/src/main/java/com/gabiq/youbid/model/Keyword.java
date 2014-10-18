package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Keyword")
public class Keyword extends ParseObject {
    public Item getItem() {
        return (Item) getParseObject("item");
    }

    public void setItem(Item item) {
        put("item", item);
    }

    public String getKeyword() {
        return getString("keyword");
    }

    public void setKeyword(String keyword) {
        put("keyword", keyword);
    }
}
