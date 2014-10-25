package com.gabiq.youbid.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.EditProfileFragment;

public class EditProfileActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new EditProfileFragment())
                    .commit();
        }
    }
}
