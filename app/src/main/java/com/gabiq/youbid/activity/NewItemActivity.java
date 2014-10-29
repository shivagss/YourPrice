package com.gabiq.youbid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.NewItemFragment;

public class NewItemActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_item);

        Intent intent = new Intent(this, CreateItemActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();

        String itemId = getIntent().getStringExtra("item_id");
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            NewItemFragment newItemFragment = NewItemFragment.newInstance(itemId);
            ft.replace(R.id.fragmentContainer, newItemFragment);
            ft.commit();
        }
    }
}
