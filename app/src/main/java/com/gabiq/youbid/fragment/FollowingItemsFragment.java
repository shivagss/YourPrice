package com.gabiq.youbid.fragment;

import com.gabiq.youbid.model.Followers;
import com.gabiq.youbid.model.Item;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class FollowingItemsFragment extends GridFragment {

    @Override
    protected ParseQueryAdapter.QueryFactory<Item> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                ParseQuery<Followers> followingQuery = ParseQuery.getQuery("Followers");
                followingQuery.whereEqualTo("follower", ParseUser.getCurrentUser());
                ParseQuery itemQuery = new ParseQuery("Item");
                itemQuery.whereMatchesKeyInQuery("createdBy", "following", followingQuery);
                itemQuery.orderByDescending("createdAt");
                itemQuery.include("createdBy");

                return itemQuery;
            }
        };
    }
}
