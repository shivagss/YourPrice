package com.gabiq.youbid.fragment;


import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Favorite;
import com.gabiq.youbid.model.Item;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class FavoriteItemsFragment extends GridFragment {

    @Override
    protected String getEmptyLabel() {
        return getActivity().getString(R.string.favorites_empty_list_label);
    }

    @Override
    protected ParseQueryAdapter.QueryFactory<Item> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                ParseQuery<Favorite> favoritesQuery = ParseQuery.getQuery("Favorite");
                favoritesQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                ParseQuery itemQuery = new ParseQuery("Item");
                itemQuery.whereMatchesKeyInQuery("objectId", "itemId", favoritesQuery);
                itemQuery.whereNotEqualTo("state", "disabled");
                itemQuery.orderByDescending("createdAt");
                itemQuery.include("createdBy");

                return itemQuery;
            }
        };
    }
}
