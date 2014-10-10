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
    public static final String YOUR_APPLICATION_ID = "QY1YDlP7oSnz9l5FuRChPYCHrAzZcE4tc12uiCNW";
    public static final String YOUR_CLIENT_KEY = "xSrwaefRGYgN1Y4Dn7PvVhVUYkpxOrvOgYcagGxG";
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
