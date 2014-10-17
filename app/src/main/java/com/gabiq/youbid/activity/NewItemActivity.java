package com.gabiq.youbid.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.DetailsFragment;
import com.gabiq.youbid.fragment.NewItemFragment;
import com.gabiq.youbid.model.Item;

public class NewItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_item);

        String itemId = getIntent().getStringExtra("item_id");
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            NewItemFragment newItemFragment = NewItemFragment.newInstance(itemId);
            ft.replace(R.id.fragmentContainer, newItemFragment);
            ft.commit();
        }
    }
}
