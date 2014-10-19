package com.gabiq.youbid.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
        if (savedInstanceState == null) {

            ProfileFragment fragment = ProfileFragment.newInstance(userId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void OnUserStoreFragmentInteraction() {

    }

    @Override
    public void onFragmentMessage() {

    }
}
