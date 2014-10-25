package com.gabiq.youbid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.Page1Fragment;
import com.gabiq.youbid.fragment.Page2Fragment;
import com.gabiq.youbid.listener.OnNewItemFragmentInteractionListener;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Keyword;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

public class CreateItemActivity extends FragmentActivity implements OnNewItemFragmentInteractionListener {

    private ViewPager mPager;
    private CirclePageIndicator mIndicator;
    private CreateItemFragmentAdapter mAdapter;
    private Item mItem;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String item_id = intent.getStringExtra("item_id");
        if(!TextUtils.isEmpty(item_id)){
            retrieveItem(item_id);
        }

        mAdapter = new CreateItemFragmentAdapter(this
                .getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageTransformer(true, new ParallaxPageTransformer());

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        ((CirclePageIndicator) mIndicator).setSnap(true);
        ((CirclePageIndicator) mIndicator).setFillColor(getResources().getColor(R.color.com_facebook_blue));
        ((CirclePageIndicator) mIndicator).setStrokeColor(getResources().getColor(R.color.primary_color));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveItem();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Item getItem() {
        return mItem;
    }

    @Override
    public void saveItem() {

        Page1Fragment fragment = (Page1Fragment) mAdapter.getRegisteredFragment(0);
        Page2Fragment fragment2 = (Page2Fragment) mAdapter.getRegisteredFragment(1);

        showProgress(getString(R.string.saving_item));
        Item item = mItem;
        if (item == null) {
            item = new Item();
        }

        boolean success = fragment.saveRequiredFields(item);
        if(!success){
            dismissProgress();
            return;
        }

        fragment2.saveOptionalFields(item);

        // Associate the mItem with the current user
        item.setUser(ParseUser.getCurrentUser());
        item.setHasSold(false);

        item.setLocation(ParseUser.getCurrentUser().getParseGeoPoint("location"));

        // Save the mItem and return
        final Item finalItem = item;
        item.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                dismissProgress();
                if (e == null) {
                    setResult(Activity.RESULT_OK);
                    String objectId = finalItem.getObjectId();
                    List<Keyword> list = finalItem.getKeywords();
                    for(Keyword key : list){
                        key.setItemId(objectId);
                    }
                    ParseObject.saveAllInBackground(list);
                    finish();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void retrieveItem(String itemId) {
        getActionBar().setTitle(getString(R.string.title_edit_item));
        showProgress("Retrieving Item");
        ParseQuery<Item> query = ParseQuery.getQuery("Item");
        query.whereEqualTo("objectId", itemId);
        query.include("keywords");
        query.getFirstInBackground(new GetCallback<Item>() {
            public void done(Item i, ParseException e) {
                if (e == null) {
                    mItem = i;
                    updateUI();
                } else {
                    e.printStackTrace();
                    Toast.makeText(CreateItemActivity.this,
                            "Failed to get item. Please try again later",
                            Toast.LENGTH_LONG).show();
                }
                dismissProgress();
            }
        });
    }

    private void updateUI() {
        Page1Fragment fragment = (Page1Fragment) mAdapter.getRegisteredFragment(0);
        Page2Fragment fragment2 = (Page2Fragment) mAdapter.getRegisteredFragment(1);
        fragment.updateUI(mItem);
        fragment2.updateUI(mItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgress();
    }

    @Override
    public void showProgress(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public class ParallaxPageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) {
                view.setAlpha(1);

            } else if (position <= 1) {
                EditText editText = (EditText) view.findViewById(R.id.etCaption);
                if(editText != null) {
                    editText.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed
                }
                ParseImageView imageView = (ParseImageView) view.findViewById(R.id.ivImage1);
                if(imageView != null) {
                    imageView.setTranslationX(-position * (pageWidth / 3)); //Half the normal speed
                }
                EditText editText1 = (EditText) view.findViewById(R.id.etDescription);
                if(editText1 != null) {
                    editText1.setTranslationX(position * (pageWidth / 4)); //Half the normal speed
                }
                EditText editText2 = (EditText) view.findViewById(R.id.etMinPrice);
                if(editText2 != null) {
                    editText2.setTranslationX(position * (pageWidth / 2)); //Half the normal speed
                }
                EditText editText3 = (EditText) view.findViewById(R.id.etTags);
                if(editText3 != null) {
                    editText3.setTranslationX(position * (pageWidth)); //Half the normal speed
                }
                GridView gridView = (GridView) view.findViewById(R.id.gvTags);
                if(gridView != null) {
                    gridView.setTranslationX(position * (pageWidth * 2)); //Half the normal speed
                }
            } else {
                view.setAlpha(1);
            }

        }
    }

}
