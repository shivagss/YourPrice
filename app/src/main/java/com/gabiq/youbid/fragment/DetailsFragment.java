package com.gabiq.youbid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Item;
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

    public DetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        itemId =  getArguments().getString("item_id");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
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

            }
        });

        tvCaption = (TextView)rootView.findViewById(R.id.tvCaption);
        tvCaption.setText(item.getCaption());


    }
}