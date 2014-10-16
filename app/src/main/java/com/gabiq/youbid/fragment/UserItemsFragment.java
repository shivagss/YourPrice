package com.gabiq.youbid.fragment;

import android.os.Bundle;

import com.gabiq.youbid.model.Item;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class UserItemsFragment extends GridFragment {

    @Override
    protected ParseQueryAdapter.QueryFactory<Item> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                ParseQuery query = new ParseQuery("Item");
                query.whereEqualTo("createdBy", ParseUser.getCurrentUser());

                return query;
            }
        };
    }


}
