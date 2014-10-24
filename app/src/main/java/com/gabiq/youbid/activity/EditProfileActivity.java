package com.gabiq.youbid.activity;


import android.app.Activity;
import android.os.Bundle;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.EditProfileFragment;

public class EditProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new EditProfileFragment())
                    .commit();
        }
    }
}
