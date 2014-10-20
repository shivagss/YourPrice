package com.gabiq.youbid.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.RecentActivityFragment;
import com.gabiq.youbid.fragment.FavoriteItemsFragment;
import com.gabiq.youbid.fragment.FragmentNavigationDrawer;
import com.gabiq.youbid.fragment.GridFragment;
import com.gabiq.youbid.fragment.LogoutFragment;
import com.gabiq.youbid.fragment.MyBidsFragment;
import com.gabiq.youbid.fragment.ProfileFragment;
import com.gabiq.youbid.fragment.SearchItemFragment;
import com.gabiq.youbid.fragment.UserStoreFragment;
import com.gabiq.youbid.model.Item;
import com.parse.ParseQueryAdapter;

import org.json.JSONException;
import org.json.JSONObject;


public class HomeActivity extends FragmentActivity implements GridFragment.OnFragmentInteractionListener,
        UserStoreFragment.OnUserStoreFragmentInteractionListener {
    private FragmentNavigationDrawer dlDrawer;

    private ParseQueryAdapter<Item> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupDrawer(savedInstanceState);

        // handle intent
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        if((extras != null)){
            dispatchNotification(extras);        }
    }

    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            dispatchNotification(extras);
        }
    }

    private void dispatchNotification(Bundle extra) {
        String jsonString = extra.getString("com.parse.Data");
        if (jsonString != null) {
            try {
            JSONObject json = new JSONObject(jsonString);
                if (json != null) {
                    String itemId = json.getString("itemId");
                    if (itemId != null) {
                        // Launch Item Detail activity
                        Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                        intent.putExtra("item_id",itemId);
                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                Log.e("Error", "Error parsing json in push notification " + e.toString());
            }
        }
    }



    private void setupDrawer(Bundle savedInstanceState) {
        dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer),
                R.layout.drawer_nav_item, R.id.flContent);
        // Add nav items
        dlDrawer.addNavItem("Items for Sale", R.drawable.ic_action_new, "Items for Sale", SearchItemFragment.class);
        dlDrawer.addNavItem("My Profile", R.drawable.ic_icon_profile, "My Profile", ProfileFragment.class);
        dlDrawer.addNavItem("My Favorites", R.drawable.ic_action_new, "My Favorites", FavoriteItemsFragment.class);
        dlDrawer.addNavItem("My Bids", R.drawable.ic_action_photo, "My Bids", MyBidsFragment.class);
        dlDrawer.addNavItem("Notifications", R.drawable.ic_action_photo, "Notifications", RecentActivityFragment.class);
        dlDrawer.addNavItem("Logout", R.drawable.ic_action_photo, "Logout", LogoutFragment.class);

        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(0);
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
//            case R.id.action_logout:{
//                logout();
//            }
        }
        return super.onOptionsItemSelected(item);
    }

//    private void logout() {
//        if(ParseFacebookUtils.getSession() != null)
//            ParseFacebookUtils.getSession().closeAndClearTokenInformation();
//        ParseUser.logOut();
//        Intent intent = new Intent(HomeActivity.this,
//                LoginDispatchActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//                | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    private void postItem() {
        Intent i = new Intent(this, CreateItemActivity.class);
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
