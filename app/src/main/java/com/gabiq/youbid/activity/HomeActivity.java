package com.gabiq.youbid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.FragmentNavigationDrawer;
import com.gabiq.youbid.fragment.GridFragment;
import com.gabiq.youbid.fragment.ProfileFragment;
import com.gabiq.youbid.fragment.UserItemsFragment;
import com.gabiq.youbid.fragment.UserStoreFragment;
import com.gabiq.youbid.model.Item;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


public class HomeActivity extends FragmentActivity implements GridFragment.OnFragmentInteractionListener,
        UserStoreFragment.OnUserStoreFragmentInteractionListener {
    private FragmentNavigationDrawer dlDrawer;

    private ParseQueryAdapter<Item> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupDrawer(savedInstanceState);
    }

    private void setupDrawer(Bundle savedInstanceState) {
        dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer),
                R.layout.drawer_nav_item, R.id.flContent);
        // Add nav items
        dlDrawer.addNavItem("My Profile", R.drawable.ic_icon_profile, "My Profile", ProfileFragment.class);
        dlDrawer.addNavItem("Activity Feed", R.drawable.ic_action_new, "Activity", GridFragment.class);
        dlDrawer.addNavItem("My Store", R.drawable.ic_action_photo, "My Store", UserStoreFragment.class);
        dlDrawer.addNavItem("Favorites", R.drawable.ic_action_new, "Favorites", GridFragment.class);
        dlDrawer.addNavItem("Settings", R.drawable.ic_action_photo, "Settings", GridFragment.class);
        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(1);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dlDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dlDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_search: {
                //TODO: Invoke search
                break;
            }
            case R.id.action_new: {
                postItem();
                break;
            }
            case R.id.action_logout:{
                logout();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        if(ParseFacebookUtils.getSession() != null)
            ParseFacebookUtils.getSession().closeAndClearTokenInformation();
        ParseUser.logOut();
        Intent intent = new Intent(HomeActivity.this,
                LoginDispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void postItem() {
        Intent i = new Intent(this, NewItemActivity.class);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        //Hijack accidental back button press to avoid finishing activity.
        //User should logout from action menu
    }

    public void onFragmentMessage() {
        // placeholder
    }

    public void OnUserStoreFragmentInteraction() {

    }


}
