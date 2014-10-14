package com.gabiq.youbid;

import android.app.Application;
import android.os.Message;

import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Keyword;
import com.gabiq.youbid.model.User;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        // Register your parse models here
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Keyword.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Bid.class);
        Parse.initialize(this, getString(R.string.parse_app_id),
                getString(R.string.parse_client_key));

        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
    }
}
