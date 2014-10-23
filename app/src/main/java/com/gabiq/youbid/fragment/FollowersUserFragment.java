package com.gabiq.youbid.fragment;

import com.gabiq.youbid.model.Followers;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowersUserFragment extends ParseUsersListFragment {

    @Override
    public ParseQuery<Followers> getFollowUsersParseQuery(ParseUser user) {
        ParseQuery<Followers> followingParseQuery = ParseQuery.getQuery(Followers.class);
        followingParseQuery.whereEqualTo("following", user);

        return followingParseQuery;
    }
    @Override
    protected ArrayList<ParseUser> loadData(List<Followers> followersList) {
        ArrayList<ParseUser> list = new ArrayList<ParseUser>();
        for(Followers followers : followersList){
            list.add(followers.getFollower());
        }
        return list;
    }

    protected String getTitle(){
        return "Followers";
    }
}
