package com.gabiq.youbid.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.DetailsActivity;
import com.gabiq.youbid.activity.NewItemActivity;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.Utils;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

/**
 * Created by sreejumon on 10/14/14.
 */
public class DetailsFragment extends Fragment {

    private Item item;
    private View rootView;
    private ParseImageView ivItemPic;
    private TextView tvCaption;
    private String itemId;
    private ProgressBar progressBar;
    private TextView tvTimePosted;
    private TextView tvUserName;
    private TextView tvViewCount;

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
        setHasOptionsMenu(true);
        retrieveItem(itemId);
        return rootView;
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

        ivItemPic = (ParseImageView) rootView.findViewById(R.id.ivItemPic);
        ivItemPic.setImageResource(0);

        ivItemPic.setParseFile(item.getParseFile("photo"));
        ivItemPic.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        tvCaption = (TextView)rootView.findViewById(R.id.tvCaption);
        tvCaption.setText(item.getCaption());

        tvTimePosted = (TextView) rootView.findViewById(R.id.tvTimePosted);
        tvTimePosted.setText(Utils.getRelativeTimeAgo(item.getUpdatedAt()));

        tvUserName = (TextView)rootView.findViewById(R.id.tvUserName);
        tvUserName.setText(item.getUser().getName());

        tvViewCount = (TextView)rootView.findViewById(R.id.tvViewsCount);
        int viewCount = item.getViewCount() + 1;
        item.setViewCount(viewCount);
        item.saveInBackground();
        tvViewCount.setText(viewCount + " views");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.details, menu);
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
        return super.onOptionsItemSelected(menuItem);
    }
}
