package com.gabiq.youbid.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.ItemTagsViewAdapter;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Keyword;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.paralloid.Parallaxor;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubmitOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SubmitOfferFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "itemId";

    private String itemId;
    private EditText etBidAmount;
    private Button btnBid;
    private TextView tvBidStatus;
    private Item item;
    private ImageView ivItemPic;
    private TextView tvCaption;
    private CheckBox cbItemSold;
    private ImageView ivItemSold;
    private ProgressBar progressBar;
    private View view;
    private RelativeLayout bidSection;
    private RelativeLayout sellerSection;
    AlertDialog dialog;
    private GridView gvTags;
    private ArrayList<Keyword> mTagsList;
    private ArrayAdapter<Keyword> mTagsAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SubmitOfferFragment.
     */
    public static SubmitOfferFragment newInstance(String item_id) {
        SubmitOfferFragment fragment = new SubmitOfferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, item_id);
        fragment.setArguments(args);
        return fragment;
    }

    public SubmitOfferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null)
            itemId = getArguments().getString("itemId");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_submit_offer, container, false);


        etBidAmount = (EditText) view.findViewById(R.id.etBidAmount);

        btnBid = (Button)view.findViewById(R.id.btnBid);
        tvBidStatus = (TextView)view.findViewById(R.id.tvBidStatus);
        btnBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //double bidAmount = Double.parseDouble(etBidAmount.getText().toString());
                    //submitBid(bidAmount);
                    showAlert();
                    etBidAmount.clearFocus();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        bidSection = (RelativeLayout)view.findViewById(R.id.bidSection);
        retrieveItem(itemId);

        sellerSection = (RelativeLayout)view.findViewById(R.id.layoutSeller);

        cbItemSold = (CheckBox) view.findViewById(R.id.cbItemSold);
        cbItemSold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (item != null) {
                    item.setHasSold(b);
                    item.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (b) {
                                ivItemSold.setVisibility(View.VISIBLE);
                            } else {
                                ivItemSold.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        ivItemSold = (ImageView) view.findViewById(R.id.ivItemSold);

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        FrameLayout topContent = (FrameLayout) view.findViewById(R.id.top_content);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        if (scrollView instanceof Parallaxor) {
            ((Parallaxor) scrollView).parallaxViewBy(topContent, 0.5f);
        }

        //retrievePreviousBid(itemId);

        return view;
    }

    private void retrievePreviousBid(String itemId) {
        ParseQuery<Bid> query = ParseQuery.getQuery("Bid");
        query.whereEqualTo("itemId", itemId);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Bid>() {
            public void done(Bid bid, ParseException e) {
                if(e == null){
                    if(bid != null)
                    {
                        tvBidStatus = (TextView)view.findViewById(R.id.tvBidStatus);
                        tvBidStatus.setText(getResources().getString(R.string.bid_amount_last) + " " + String.valueOf(bid.getPrice()));
                        tvBidStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert()
    {
        OfferConfirmation dialog = new OfferConfirmation();
        dialog.setAmount(Double.parseDouble(etBidAmount.getText().toString()));
        dialog.setItemId(itemId);
        dialog.show(getFragmentManager(), "OfferConfirmation");
        etBidAmount.getText().clear();
    }


    public void submitOffer(double amount, String itemId) {
        Bid bid = new Bid();
        bid.setItemId(itemId);
        bid.setBuyer(ParseUser.getCurrentUser());
        bid.setPrice(amount);
        bid.setState("pending"); //Pending, accepted, rejected states
        bid.saveInBackground();
        //retrievePreviousBid(itemId);
    }

/*
    private void submitBid(double amount) {
        int validity = validBid(amount);
        if( validity == 0) {
            Bid bid = new Bid();
            bid.setItemId(itemId);
            bid.setBuyer(ParseUser.getCurrentUser());
            bid.setPrice(amount);
            bid.setState("pending"); //Pending, accepted, rejected states
            bid.saveInBackground();
            tvBidStatus.setText(getResources().getString(R.string.bid_amount_submitted) );
            tvBidStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        else if(validity < 0) //very low bid amount
        {
            tvBidStatus.setText(getResources().getString(R.string.bid_amount_low) );
            tvBidStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        else //very high bid amount
        {
            tvBidStatus.setText(getResources().getString(R.string.bid_amount_high) );
            tvBidStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        tvBidStatus.setVisibility(View.VISIBLE);


    }


    private int validBid(double amount) {
        //Simple rule of basic validation
        if(amount < item.getMinPrice() / 2)  //Very low if bid amount is less than half of min price
            return -1;
        //else if(amount > 5 * item.getMinPrice()) //Very high if bid amount is more than 5 X
        //    return 1;
        return 0;
    }
  */
    private void retrieveItem(String itemId){
        ParseQuery<Item> query = ParseQuery.getQuery("Item");
        query.whereEqualTo("objectId", itemId);
        query.getFirstInBackground(new GetCallback<Item>() {
            public void done(Item i, ParseException e) {
                if(e == null){
                    item = i;
                    updateUI();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI()
    {
        ivItemPic = (ImageView) view.findViewById(R.id.ivItemPic);
        ivItemPic.setImageResource(0);

        ParseFile photoFile =  item.getParseFile("photo");

        if (photoFile != null) {
            Picasso.with(getActivity())
                    .load(photoFile.getUrl())
                    .into(ivItemPic);
        }

        tvCaption = (TextView)view.findViewById(R.id.tvCaption);
        tvCaption.setText(item.getCaption());

        TextView tvDesc = (TextView)view.findViewById(R.id.tvDescription);
        if(item.getDescription() != null) {
            tvDesc.setText(item.getDescription());
            tvDesc.setVisibility(View.VISIBLE);
        }

        boolean isSeller = item.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
        if (isSeller) {
            bidSection.setVisibility(View.GONE);
            sellerSection.setVisibility(View.VISIBLE);
            cbItemSold.setChecked(item.getHasSold());
        } else {
            bidSection.setVisibility(View.VISIBLE);
            sellerSection.setVisibility(View.GONE);
        }

        if (item.getHasSold()) {
            ivItemSold.setVisibility(View.VISIBLE);
            bidSection.setVisibility(View.GONE);
        } else {
            ivItemSold.setVisibility(View.GONE);
        }

        gvTags = (GridView) view.findViewById(R.id.gvTagsDetails);
        mTagsList = new ArrayList<Keyword>();
        mTagsAdapter = new ItemTagsViewAdapter(getActivity(), mTagsList);
        mTagsAdapter.addAll(item.getKeywords());
        gvTags.setAdapter(mTagsAdapter);
        if (mTagsList.size() == 0) {
            gvTags.setVisibility(View.GONE);
        } else {
            gvTags.setVisibility(View.VISIBLE);
        }

    }


}
