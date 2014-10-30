package com.gabiq.youbid.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.GridFragment;
import com.gabiq.youbid.fragment.ProfileFragment;
import com.gabiq.youbid.fragment.UserStoreFragment;

public class ProfileActivity extends FragmentActivity implements GridFragment.OnFragmentInteractionListener, UserStoreFragment.OnUserStoreFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String userId = getIntent().getStringExtra("userId");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {

            ProfileFragment fragment = ProfileFragment.newInstance(userId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    @Override
    public void OnUserStoreFragmentInteraction() {

    }

    @Override
    public void onFragmentMessage() {

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
