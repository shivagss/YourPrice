package com.gabiq.youbid.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.NewItemActivity;
import com.gabiq.youbid.activity.ProfileActivity;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.Utils;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by sreejumon on 10/14/14.
 */
public class DetailsFragment extends Fragment {

    private Item item;
    private View rootView;
    private String itemId;
    private TextView tvTimePosted;
    private TextView tvUserName;
    private TextView tvViewCount;

    private CommentsFragment commentFragment;
    private Menu detailsMenu;
    private ParseImageView ivProfile;

    private Button btnDetails ;
    private Button btnComments ;
    private Button btnBids ;
    private Button btnMessages ;


    private enum ViewType{
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
        itemId =  getArguments().getString("item_id");

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(itemId)){
            retrieveItem(itemId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);


        ivProfile = (ParseImageView) rootView.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProfileActivity();
            }
        });

        btnDetails = (Button)rootView.findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateView(ViewType.Details);
            }
        });

        btnComments = (Button)rootView.findViewById(R.id.btnComments);
        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateView(ViewType.Comments);
            }
        });

        btnBids = (Button)rootView.findViewById(R.id.btnBids);
        btnBids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateView(ViewType.Bids);
            }
        });
        btnMessages = (Button)rootView.findViewById(R.id.btnMessages);
        btnMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/* removed in merge?
                try {
                    double bidAmount = Double.parseDouble(etBidAmount.getText().toString());
                    submitBid(bidAmount);
                }
                catch(Exception e)
                {
                  e.printStackTrace();
                }
*/
                updateView(ViewType.Messages);
            }
        });


        setHasOptionsMenu(true);

        retrieveItem(itemId);


        updateView(ViewType.Details);


        return rootView;
    }

    private void startProfileActivity() {

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        if(item != null)
            intent.putExtra("userId", item.getUser().getObjectId());
        startActivity(intent);

    }

    public static DetailsFragment newInstance(String itemId) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("item_id", itemId);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    private void retrieveItem(String itemId){
        ParseQuery<Item> query = ParseQuery.getQuery("Item");
        query.whereEqualTo("objectId", itemId);
        query.getFirstInBackground(new GetCallback<Item>() {
            public void done(Item i, ParseException e) {
                if(e == null){
                    item = i;
                    updateUI();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI()
    {
        if(item == null) return;

        isSeller = item.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
/*
        if (isSeller) {
            btnBid.setText(R.string.btn_bid_list);
        } else {
            etBidAmount.setVisibility(View.VISIBLE);
            btnBid.setVisibility(View.VISIBLE);
            btnBidList.setText("MY BIDS");
        }

        btnBidList.setVisibility(View.VISIBLE);
        btnBid.setVisibility(View.VISIBLE);
*/

        //Hide the delete & edit option if the user is not the owner
        MenuItem deleteMenu = detailsMenu.findItem(R.id.action_delete);
        MenuItem editIMenu = detailsMenu.findItem(R.id.action_edit);

        if (isSeller) {
            deleteMenu.setVisible(true);
            editIMenu.setVisible(true);
            btnBids.setVisibility(View.VISIBLE);
        }
        else{
            deleteMenu.setVisible(false);
            editIMenu.setVisible(false);
            btnBids.setVisibility(View.GONE);
        }

        tvTimePosted = (TextView) rootView.findViewById(R.id.tvTimePosted);
        tvTimePosted.setText(Utils.getRelativeTimeAgo(item.getCreatedAt()));

        tvUserName = (TextView)rootView.findViewById(R.id.tvUserName);
        tvUserName.setText(item.getUser().getName());

        tvViewCount = (TextView)rootView.findViewById(R.id.tvViewsCount);
        int viewCount = item.getViewCount() + 1;
        item.setViewCount(viewCount);
        item.saveInBackground();
        tvViewCount.setText(viewCount + " views");
        ParseFile photoFile = item.getUser().getParseUser().getParseFile("photo");
        if (photoFile != null) {
            ivProfile.setParseFile(photoFile);
            ivProfile.loadInBackground();
        }
  }

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
            Intent intent = new Intent(getActivity(), NewItemActivity.class);
            intent.putExtra("item_id", item.getObjectId());
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_delete) {
            if(item != null){
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

    private ProgressDialog mProgressDialog;

    public void showProgress(String message){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getActivity());
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void dismissProgress(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgress();
    }


    private void updateView(ViewType viewType){

        FragmentTransaction ft;
        resetButtons();
        switch (viewType)
        {
            case Details:
                ft = getFragmentManager().beginTransaction();
                SubmitOfferFragment submitOfferFragment = SubmitOfferFragment.newInstance(itemId);
                ft.replace(R.id.flCommentsContainer, submitOfferFragment);
                ft.commit();
                btnDetails.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                break;
            case Comments:
                ft = getFragmentManager().beginTransaction();
                commentFragment = CommentsFragment.newInstance(itemId);
                ft.replace(R.id.flCommentsContainer, commentFragment);
                ft.commit();
                btnComments.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                break;
            case Bids:
                //TODO: copy the similar code from above section
                btnBids.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                break;
            case Messages:
                //TODO: Copy the similar code from above section
                btnMessages.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                break;

        }


    }

    private void resetButtons()
    {
        btnBids.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        btnComments.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        btnMessages.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
        btnDetails.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
    }


}
