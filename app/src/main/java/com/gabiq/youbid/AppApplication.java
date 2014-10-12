package com.gabiq.youbid;

import android.app.Application;
import android.os.Message;

import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Keyword;
import com.gabiq.youbid.model.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class AppApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "1pKjrKTD8PkqWMZNbwisMTbSlXnPw8eH16kn0JG2";
    public static final String YOUR_CLIENT_KEY = "lWoRPA81XQ7SW5DcwtIMbQe7nYLQMogGndk0AV7w";
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
    }
}
