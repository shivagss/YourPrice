package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.gabiq.youbid.adapter.UsersListAdapter;
import com.gabiq.youbid.model.Followers;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowingUserFragment extends ParseUsersListFragment {


    @Override
    public ParseQuery<Followers> getFollowUsersParseQuery(ParseUser user) {
        ParseQuery<Followers> followingParseQuery = ParseQuery.getQuery(Followers.class);
        followingParseQuery.whereEqualTo("follower", user);

        return followingParseQuery;
    }

    @Override
    protected ArrayList<ParseUser> loadData(List<Followers> followersList) {
        ArrayList<ParseUser> list = new ArrayList<ParseUser>();
        for(Followers followers : followersList){
            list.add(followers.getFollowing());
        }
        return list;
    }

    protected String getTitle(){
        return "Following";
    }
}
