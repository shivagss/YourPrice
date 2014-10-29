package com.gabiq.youbid.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Keyword;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchItemFragment extends GridFragment {
    private String searchKeyword = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected ParseQueryAdapter.QueryFactory<Item> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                if (searchKeyword == null) {
                    ParseQuery query = new ParseQuery("Item");
                    query.orderByDescending("createdAt");
                    query.include("createdBy");
                    return query;
                } else {
                    // caption query
                    ParseQuery captionQuery = new ParseQuery("Item");
                    captionQuery.whereContains("caption", searchKeyword.toLowerCase());
//                    captionQuery.orderByDescending("createdAt");

                    // keyword query
                    ParseQuery<Keyword> keywordQuery = ParseQuery.getQuery("Keyword");
                    keywordQuery.whereContains("keyword", searchKeyword.toLowerCase());
                    ParseQuery itemQuery = new ParseQuery("Item");
                    itemQuery.whereMatchesKeyInQuery("objectId", "itemId", keywordQuery);
//                    itemQuery.orderByDescending("createdAt");

                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(itemQuery);
                    queries.add(captionQuery);

                    ParseQuery mainQuery = ParseQuery.or(queries);
                    mainQuery.include("createdBy");

                    return mainQuery;
                }
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchKeyword = query;
                reloadItems();

                // dismissh keyboard
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    searchKeyword = null;
                    reloadItems();
                }

                return false;
            }
        });
    }
}
