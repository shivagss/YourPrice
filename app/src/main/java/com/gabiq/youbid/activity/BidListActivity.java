package com.gabiq.youbid.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.BidListFragment;

public class BidListActivity extends FragmentActivity implements BidListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_list);

        String itemId = getIntent().getStringExtra("itemId");
        boolean isSeller = getIntent().getBooleanExtra("isSeller", false);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            BidListFragment bidListFragment = BidListFragment.newInstance(itemId, isSeller);
            ft.replace(R.id.BidListContainer, bidListFragment);
            ft.commit();
        }
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bid_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction() {

    }

}
