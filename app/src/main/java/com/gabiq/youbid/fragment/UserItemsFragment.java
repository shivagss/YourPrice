package com.gabiq.youbid.fragment;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.gabiq.youbid.model.Item;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class UserItemsFragment extends GridFragment {
    private static final String ARG_USER_ID = "userId";
    private ParseUser user;


    public static UserItemsFragment newInstance(String userId) {
        UserItemsFragment fragment = new UserItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String userId = getArguments().getString(ARG_USER_ID);
            user = ParseUser.createWithoutData(ParseUser.class, userId);
        } else {
            user = ParseUser.getCurrentUser();
        }
    }

    @Override
    protected ParseQueryAdapter.QueryFactory<Item> getParseQuery() {

        return new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                ParseQuery query = new ParseQuery("Item");
                query.whereEqualTo("createdBy", user);
                return query;
            }
        };
    }
}
