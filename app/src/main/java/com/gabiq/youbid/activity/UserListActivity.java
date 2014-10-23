package com.gabiq.youbid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.EditProfileFragment;
import com.gabiq.youbid.fragment.FollowersUserFragment;
import com.gabiq.youbid.fragment.FollowingUserFragment;
import com.gabiq.youbid.fragment.ParseUsersListFragment;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserListActivity extends Activity implements ParseUsersListFragment.OnSelectUserListener {

    private ParseUser mUser;
    private ParseUsersListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            String type = getIntent().getStringExtra("type");
            if (type.equals("followers")) {
                fragment = new FollowersUserFragment();
            } else if (type.equals("following")) {
                fragment = new FollowingUserFragment();
            } else {
                fragment = new ParseUsersListFragment();
            }
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", getIntent().getStringExtra("userId"));
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        mUser = (ParseUser) objects.get(0);
                        fragment.updateUI();
                    }
                }
            });
        }
    }

    @Override
    public ParseUser getUser() {
        return mUser;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
