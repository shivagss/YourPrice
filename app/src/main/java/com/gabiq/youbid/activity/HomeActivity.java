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
import com.gabiq.youbid.fragment.DetailsFragment;
import com.gabiq.youbid.fragment.FollowingItemsFragment;
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
            dispatchNotification(extras);
        }
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
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
                        intent.putExtra("item_id", itemId);

                        DetailsFragment.ViewType viewType = DetailsFragment.ViewType.Details;
                        String type = json.getString("type");
                        if (type.equals("bid")) {
                            viewType = DetailsFragment.ViewType.Bids;
                        } else if (type.equals("comment")) {
                            viewType = DetailsFragment.ViewType.Comments;
                        } else if (type.equals("message")) {
                            viewType = DetailsFragment.ViewType.Bids;
                        }
                        intent.putExtra("viewType", viewType);

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
        dlDrawer.addNavItem("Shop", R.drawable.ic_store_white, "YourPrice", SearchItemFragment.class);
        dlDrawer.addNavItem("Activity", R.drawable.ic_gift_white, "Activity Feed", FollowingItemsFragment.class);
        dlDrawer.addNavItem("Favorites", R.drawable.ic_heart_white, "Favorites", FavoriteItemsFragment.class);
        dlDrawer.addNavItem("Offers", R.drawable.ic_dollar_white, "Offers", MyBidsFragment.class);
        dlDrawer.addNavItem("Notifications", R.drawable.ic_alert_white, "Notifications", RecentActivityFragment.class);
        dlDrawer.addNavItem("Profile", R.drawable.ic_card_white, "Profile", ProfileFragment.class);
        dlDrawer.addNavItem("Logout", R.drawable.ic_logout_white, "Logout", LogoutFragment.class);

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
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
    }


    @Override
    public void onBackPressed() {
        //Hijack accidental back button press to avoid finishing activity.
        //User should logout from action menu
        dlDrawer.selectDrawerItem(0);
    }

    public void onFragmentMessage() {
        // placeholder
    }

    public void OnUserStoreFragmentInteraction() {

    }


}
