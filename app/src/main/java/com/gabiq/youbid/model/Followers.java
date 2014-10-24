package com.gabiq.youbid.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Followers")
public class Followers extends ParseObject {

    private ParseUser follower;
    private ParseUser following;

    public Followers() {
    }

    public Followers(ParseUser follower, ParseUser following) {
        setFollower(follower);
        setFollowing(following);
    }

    public ParseUser getFollower() {
        return getParseUser("follower");
    }

    public void setFollower(ParseUser follower) {
        put("follower", follower);
    }

    public ParseUser getFollowing() {
        return getParseUser("following");
    }

    public void setFollowing(ParseUser following) {
        put("following", following);
    }

}
