package com.gabiq.youbid;

import android.app.Application;

import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Keyword;
import com.gabiq.youbid.model.User;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AppApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "1pKjrKTD8PkqWMZNbwisMTbSlXnPw8eH16kn0JG2";
    public static final String YOUR_CLIENT_KEY = "lWoRPA81XQ7SW5DcwtIMbQe7nYLQMogGndk0AV7w";

    //public static final String YOUR_APPLICATION_ID = "NYpJPk63AFDdBfu4dQMDjqkiVmug2JE1S6bBQVgT";
    //public static final String YOUR_CLIENT_KEY = "yT2fluuB0Ac3HoFL4IPC9GGPnPNs6j00dLvjYAGf";

    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models here
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Keyword.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Bid.class);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

        //TODO: Remove the following snippets later. Including this snippet for
        // time being to avoid the user authentication each time
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);


    }
}
