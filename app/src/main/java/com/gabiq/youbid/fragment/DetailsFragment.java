package com.gabiq.youbid.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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

import java.lang.reflect.Field;

/**
 * Created by sreejumon on 10/14/14.
 */
public class DetailsFragment extends Fragment {

    private Item item;
    private String itemId;
    private TextView tvTimePosted;
    private TextView tvUserName;
    private TextView tvViewCount;
    private TextView tvLocation;

    private CommentsFragment commentFragment;
    private Menu detailsMenu;
    private ImageView ivProfile;

    private BootstrapButton btnDetails;
    private BootstrapButton btnComments;
    private BootstrapButton btnBids;
    private ViewType defaultTab = ViewType.Details;
    private DetailsFragmentAdapter mAdapter;
    private ViewPager mPager;
    private Field mScroller;
    private FixedSpeedScroller scroller;
    private static Interpolator sAnimator = new LinearInterpolator();




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

        showProgress("Loading...");

        ivProfile = (ImageView) rootView.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProfileActivity();
            }
        });

        btnDetails = (BootstrapButton) rootView.findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                updateView(ViewType.Details);
                resetButtons();
                btnDetails.setLeftIcon("fa-chevron-down");
                mPager.setCurrentItem(0);
            }
        });

        btnComments = (BootstrapButton) rootView.findViewById(R.id.btnComments);
        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                updateView(ViewType.Comments);
                resetButtons();
                btnComments.setLeftIcon("fa-chevron-down");
                mPager.setCurrentItem(1);
            }
        });

        btnBids = (BootstrapButton) rootView.findViewById(R.id.btnBids);
        btnBids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                updateView(ViewType.Bids);
                resetButtons();
                btnBids.setLeftIcon("fa-chevron-down");
                mPager.setCurrentItem(2);
            }
        });

        resetButtons();

        tvTimePosted = (TextView) rootView.findViewById(R.id.tvTimePosted);
        tvUserName = (TextView) rootView.findViewById(R.id.tvUserName);
        tvViewCount = (TextView) rootView.findViewById(R.id.tvViewsCount);
        tvLocation = (TextView) rootView.findViewById(R.id.tvItemLocation);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);

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
                resetButtons();
                if(position == 0){
                    btnDetails.setLeftIcon("fa-chevron-down");
                }if(position == 1){
                    btnComments.setLeftIcon("fa-chevron-down");
                }if(position == 2){
                    btnBids.setLeftIcon("fa-chevron-down");
                }
                super.onPageSelected(position);
            }
        });

        setHasOptionsMenu(true);

        retrieveItem(itemId);

//        updateView(defaultTab);

        return rootView;
    }

    private void startProfileActivity() {

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        if (item != null)
            intent.putExtra("userId", item.getUser().getObjectId());
        startActivity(intent);

    }

    public static DetailsFragment newInstance(String itemId, ViewType viewType) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("item_id", itemId);
        args.putSerializable("viewType", viewType);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    private void retrieveItem(String itemId) {
        ParseQuery<Item> query = ParseQuery.getQuery("Item");
        query.whereEqualTo("objectId", itemId);
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

    private void updateUI() {
        if (item == null) return;

        isSeller = item.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());

        mAdapter = new DetailsFragmentAdapter(itemId, isSeller, getActivity().getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);
        resetButtons();
        btnDetails.setLeftIcon("fa-chevron-down");
        mAdapter.notifyDataSetChanged();

        //Hide the delete & edit option if the user is not the owner
        MenuItem deleteMenu = detailsMenu.findItem(R.id.action_delete);
        MenuItem editIMenu = detailsMenu.findItem(R.id.action_edit);

        if (isSeller) {
            deleteMenu.setVisible(true);
            editIMenu.setVisible(true);
            btnBids.setText("Offers");
        } else {
            deleteMenu.setVisible(false);
            editIMenu.setVisible(false);
            btnBids.setText("My Offers");
        }

        tvTimePosted.setText(Utils.getRelativeTimeAgo(item.getCreatedAt()));
        tvUserName.setText(item.getUser().getName());

        int viewCount = item.getViewCount() + 1;
        item.setViewCount(viewCount);
        item.saveInBackground();
        tvViewCount.setText(viewCount + " views");
        ParseFile photoFile = item.getUser().getParseUser().getParseFile("photo");
        if (photoFile != null) {
            Picasso.with(getActivity())
                    .load(photoFile.getUrl())
                    .transform(new RoundTransform())
                    .into(ivProfile);
        }
        tvLocation.setText(item.getUser().getLocationText());
        dismissProgress();
    }

/*
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ivProfile.setImage(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {

        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
    };



    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(getActivity()).cancelRequest(target);
        super.onDestroy();
    }
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.details, menu);

        detailsMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
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
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
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


//    private void updateView(ViewType viewType) {
//
//        FragmentTransaction ft;
//        resetButtons();
//        switch (viewType) {
//            case Details:
//                ft = getFragmentManager().beginTransaction();
//                SubmitOfferFragment submitOfferFragment = SubmitOfferFragment.newInstance(itemId);
//                ft.replace(R.id.flCommentsContainer, submitOfferFragment);
//                ft.commit();
//                btnDetails.setLeftIcon("fa-chevron-down");
//                break;
//            case Comments:
//                ft = getFragmentManager().beginTransaction();
//                commentFragment = CommentsFragment.newInstance(itemId);
//                ft.replace(R.id.flCommentsContainer, commentFragment);
//                ft.commit();
//                btnComments.setLeftIcon("fa-chevron-down");
//                break;
//            case Bids:
//                ft = getFragmentManager().beginTransaction();
//                BidListFragment bidListFragment = BidListFragment.newInstance(itemId, isSeller);
//                ft.replace(R.id.flCommentsContainer, bidListFragment);
//                ft.commit();
//                btnBids.setLeftIcon("fa-chevron-down");
//                break;
//        }
//
//
//    }

    private void resetButtons() {
        btnBids.setLeftIcon("fa-chevron-right");
        btnComments.setLeftIcon("fa-chevron-right");
        btnDetails.setLeftIcon("fa-chevron-right");
    }

    public void submitOffer(double amount, String itemId) {
        SubmitOfferFragment offerFragment = (SubmitOfferFragment) mAdapter.getItem(0);
        if(offerFragment != null)
            offerFragment.submitOffer(amount, itemId);
    }

}
