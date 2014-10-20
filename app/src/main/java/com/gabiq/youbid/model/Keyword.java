package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Keyword")
public class Keyword extends ParseObject {

    public String getItemId() {
        return getString("itemId");
    }

    public void setItemId(String itemId) {
        put("itemId", itemId);
    }

    public String getKeyword() {
        return getString("keyword");
    }

    public void setKeyword(String keyword) {
        put("keyword", keyword);
    }

    @Override
    public String toString() {
        return getKeyword();
    }
}
