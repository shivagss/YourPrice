package com.gabiq.youbid.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.DetailsFragment;

public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

    /* if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsFragment())
                    .commit();
        }
     */
        String itemId = getIntent().getStringExtra("item_id");
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DetailsFragment detailsFragment = DetailsFragment.newInstance(itemId);
            ft.replace(R.id.container, detailsFragment);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
