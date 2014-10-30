package com.gabiq.youbid.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.MessageListFragment;

public class MessageListActivity extends FragmentActivity implements MessageListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        String bidId = getIntent().getStringExtra("bidId");
        String itemId = getIntent().getStringExtra("itemId");
        boolean isSeller = getIntent().getBooleanExtra("isSeller", false);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            MessageListFragment messageListFragment = MessageListFragment.newInstance(bidId, itemId, isSeller);
            ft.replace(R.id.MessageListContainer, messageListFragment);
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
        getMenuInflater().inflate(R.menu.message_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction() {

    }
}
