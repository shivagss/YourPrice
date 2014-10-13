package com.gabiq.youbid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Item;
import com.parse.ParseQueryAdapter;


public class HomeActivity extends Activity {

    private ParseQueryAdapter<Item> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);


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
        }
        return super.onOptionsItemSelected(item);
    }

    private void postItem() {
        Intent i = new Intent(this, NewItemActivity.class);
        startActivity(i);
    }

    public void onDetailsClick(View v)
    {
        Intent i = new Intent(this, DetailsActivity.class);
        startActivity(i);
    }
}
