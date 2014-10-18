package com.gabiq.youbid.fragment;


import com.gabiq.youbid.model.Item;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class MyBidsFragment extends GridFragment {

    @Override
    protected ParseQueryAdapter.QueryFactory<Item> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                ParseQuery<ParseObject> bidQuery = ParseQuery.getQuery("Bid");
                bidQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                ParseQuery itemQuery = new ParseQuery("Item");
                itemQuery.whereMatchesKeyInQuery("objectId", "itemId", bidQuery);
                return itemQuery;
            }
        };
    }}
