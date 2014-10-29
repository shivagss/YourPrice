package com.gabiq.youbid.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.CreateItemActivity;
import com.gabiq.youbid.activity.ProfileActivity;
import com.gabiq.youbid.adapter.DetailsFragmentAdapter;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.ItemCache;
import com.gabiq.youbid.utils.FixedSpeedScroller;
import com.gabiq.youbid.utils.RoundTransform;
import com.gabiq.youbid.utils.Utils;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.TabPageIndicator;

import java.lang.reflect.Field;

public class DetailsFragment extends Fragment {

    private Item item;
    private ItemCache itemCache;
    private String itemId;
    private TextView tvTimePosted;
    private TextView tvUserName;
    private TextView tvViewCount;
    private TextView tvLocation;

    private Menu detailsMenu;
    private ImageView ivProfile;

    private ViewType defaultTab = ViewType.Details;
    private DetailsFragmentAdapter mAdapter;
    private ViewPager mPager;
    private Field mScroller;
    private FixedSpeedScroller scroller;
    private static Interpolator sAnimator = new LinearInterpolator();
    private TabPageIndicator indicator;


    public enum ViewType {
        Details,
        Comments,
        Bids,
        Messages
    }

    private boolean isSeller = false;


    public DetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        itemId = getArguments().getString("item_id");
        itemCache = (ItemCache) getArguments().getSerializable("item_cache");
        defaultTab = (ViewType) getArguments().getSerializable("viewType");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(itemId)) {
            retrieveItem(itemId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        item = null;

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        ivProfile = (ImageView) rootView.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProfileActivity();
            }
        });

        tvTimePosted = (TextView) rootView.findViewById(R.id.tvTimePosted);
        tvUserName = (TextView) rootView.findViewById(R.id.tvUserName);
        tvViewCount = (TextView) rootView.findViewById(R.id.tvViewsCount);
        tvLocation = (TextView) rootView.findViewById(R.id.tvItemLocation);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        indicator = (TabPageIndicator)rootView.findViewById(R.id.indicator);

        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new FixedSpeedScroller(mPager.getContext(), sAnimator);
            scroller.setDuration(500);
            mScroller.set(mPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e1) {
        }

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        setHasOptionsMenu(true);

        updateUI();
//        if (itemCache == null) {
//            showProgress("Loading...");
//            retrieveItem(itemId);
//        }

        setTabTo(defaultTab);

        return rootView;
    }

    private void setTabTo(ViewType viewType) {
        mPager.setCurrentItem(viewType.ordinal());
    }

    private void startProfileActivity() {

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        if (item != null)
            intent.putExtra("userId", item.getUserFast().getObjectId());
        startActivity(intent);

    }

    public static DetailsFragment newInstance(String itemId, ItemCache itemCache, ViewType viewType) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("item_id", itemId);
        args.putSerializable("viewType", viewType);
        args.putSerializable("item_cache", itemCache);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    private void retrieveItem(String itemId) {
        ParseQuery<Item> query = ParseQuery.getQuery("Item");
        query.whereEqualTo("objectId", itemId);
        query.include("createdBy");
        query.getFirstInBackground(new GetCallback<Item>() {
            public void done(Item i, ParseException e) {
                if (e == null) {
                    item = i;
                    updateUI();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateMenus() {
        if (detailsMenu != null) {
            //Hide the delete & edit option if the user is not the owner
            MenuItem deleteMenu = detailsMenu.findItem(R.id.action_delete);
            MenuItem editIMenu = detailsMenu.findItem(R.id.action_edit);

            if (isSeller) {
                deleteMenu.setVisible(true);
                editIMenu.setVisible(true);
            } else {
                deleteMenu.setVisible(false);
                editIMenu.setVisible(false);
            }
        }
    }

    private void updateUI() {
        if (item != null) {
            Log.d("INFO", "******************** updateUI called");
            isSeller = item.getUserFast().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
        } else if (itemCache != null) {
            Log.d("INFO", "******************** updateUI called (cached)");
            isSeller = itemCache.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
        }

        int lastItem = 0;
        if (mPager != null) {
            lastItem = mPager.getCurrentItem();
        }
        mAdapter = new DetailsFragmentAdapter(itemId, itemCache, isSeller, getActivity().getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        indicator.setViewPager(mPager);
        mPager.setCurrentItem(lastItem);
        mAdapter.notifyDataSetChanged();
        indicator.setVisibility(View.VISIBLE);

        if (item != null) {
            updateMenus();

            tvTimePosted.setText(Utils.getRelativeTimeAgo(item.getCreatedAt()));
            tvUserName.setText(item.getUserFast().getName());

            int viewCount = item.getViewCount() + 1;
            item.setViewCount(viewCount);
            item.saveInBackground();
            tvViewCount.setText(viewCount + " views");
            ParseFile photoFile = item.getUserFast().getParseUser().getParseFile("photo");
            if (photoFile != null) {
                Picasso.with(getActivity())
                        .load(photoFile.getUrl())
                        .transform(new RoundTransform())
                        .into(ivProfile);
            }
            tvLocation.setText(item.getUserFast().getLocationText());
//            dismissProgress();
        } else if (itemCache != null) {
            updateMenus();

            tvTimePosted.setText(Utils.getRelativeTimeAgo(itemCache.getCreatedAt()));
            tvUserName.setText(itemCache.getUser().getName());

            int viewCount = itemCache.getViewCount() + 1;
// TODO: launch in background
//            item.setViewCount(viewCount);
//            item.saveInBackground();
            tvViewCount.setText(viewCount + " views");
            String photoUrl = itemCache.getUser().getPhotoUrl();
            if (photoUrl != null) {
                Picasso.with(getActivity())
                        .load(photoUrl)
                        .transform(new RoundTransform())
                        .into(ivProfile);
            }
            tvLocation.setText(itemCache.getUser().getLocationText());
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.details, menu);

        detailsMenu = menu;
        updateMenus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == android.R.id.home){
            getActivity().finish();
            return true;
        }
        if (id == R.id.action_edit) {
            Intent intent = new Intent(getActivity(), CreateItemActivity.class);
            intent.putExtra("item_id", item.getObjectId());
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_share) {
            onShareItem(item);
        }
        if (id == R.id.action_delete) {
            if (item != null) {
                showProgress("Deleting item...");
                item.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        dismissProgress();
                        if (e == null) {
                            getActivity().finish();
                        } else {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error deleting item. Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Can be triggered by a view event such as a button press
    public void onShareItem(Item item) {
        showProgress("Loading...");
        if (item != null) {
            String text = "Check out this " + item.getCaption() + ": http://yourprice.com/viewitem?item_id=" + item.getObjectId() + "";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        }
        dismissProgress();
    }

    private ProgressDialog mProgressDialog;

    public void showProgress(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        }
//        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.app_progress);
    }

    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgress();
    }


    public void submitOffer(double amount, String itemId) {
        SubmitOfferFragment offerFragment = (SubmitOfferFragment) mAdapter.getItem(0);
        if(offerFragment != null)
            offerFragment.submitOffer(amount, itemId);
    }

}
