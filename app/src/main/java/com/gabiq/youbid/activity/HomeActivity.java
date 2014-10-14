package com.gabiq.youbid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.GridFragment;
import com.gabiq.youbid.model.Item;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


public class HomeActivity extends Activity implements GridFragment.OnFragmentInteractionListener {

    private ParseQueryAdapter<Item> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

}
