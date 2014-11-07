package com.gabiq.youbid.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.Toast;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.ImageActivity;
import com.gabiq.youbid.adapter.ItemTagsViewAdapter;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.ItemCache;
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
import uk.co.chrisjenx.paralloid.views.ParallaxScrollView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubmitOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SubmitOfferFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "itemId";
    private static final String ARG_PARAM2 = "itemCache";

    private String itemId;
    private ItemCache itemCache;
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
    private Bitmap original;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SubmitOfferFragment.
     */
    public static SubmitOfferFragment newInstance(String item_id, ItemCache itemCache) {
        SubmitOfferFragment fragment = new SubmitOfferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, item_id);
        args.putSerializable(ARG_PARAM2, itemCache);
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
            itemCache = (ItemCache) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null)
            itemId = getArguments().getString("itemId");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_submit_offer, container, false);

        ivItemPic = (ImageView) view.findViewById(R.id.ivItemPic);
        View ivPlaceHolderItemPic = view.findViewById(R.id.ivPlaceHolderItemPic);
        ivPlaceHolderItemPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item != null) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    ParseFile photoFile = item.getParseFile("photo");
                    intent.putExtra("media_url", photoFile.getUrl());
                    startActivity(intent);
                }
            }
        });
        tvCaption = (TextView)view.findViewById(R.id.tvCaption);

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

        FrameLayout topContent = (FrameLayout) view.findViewById(R.id.top_content);
        ParallaxScrollView scrollView = (ParallaxScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setListener(new ParallaxScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if(original == null) {
                    if(ivItemPic != null && ivItemPic.getDrawable() != null) {
                        original = ((BitmapDrawable) ivItemPic.getDrawable()).getBitmap();
                    }else {
                        return;
                    }
                }
                if(t > ivItemPic.getHeight()) return;
                if(t == 0 && oldt > 0){
                    ivItemPic.setImageBitmap(original);
                }else {
                    Bitmap blurBitmap = BlurImage(original, (t/100)*2);
                    ivItemPic.setImageBitmap(blurBitmap);
                }
            }
        });

        scrollView.parallaxViewBy(topContent, 0.5f);

        if (itemCache != null) {
            updateUIfromCache();
        }

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        retrieveItem(itemId);
        //retrievePreviousBid(itemId);

        return view;
    }

    @SuppressLint("NewApi")
    Bitmap BlurImage (Bitmap input, int radius)
    {
        try
        {
            RenderScript rsScript = RenderScript.create(getActivity().getApplicationContext());
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript,   Element.U8_4(rsScript));
            blur.setRadius(radius);
            blur.setInput(alloc);

            Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);

            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
            return result;
        }
        catch (Exception e) {
            return input;
        }

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
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etBidAmount.getWindowToken(), 0);
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
        query.include("createdBy");
        query.include("keywords");
        query.getFirstInBackground(new GetCallback<Item>() {
            public void done(Item i, ParseException e) {
                if(e == null && i != null){
                    item = i;
                    updateUI();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUIfromCache() {
// Disable preload thumbnail for now
//        String thumbnailUrl = itemCache.getThumbnailUrl();
//        if (thumbnailUrl != null) {
//            Picasso.with(getActivity())
//                    .load(thumbnailUrl)
//                    .into(ivItemPic);
//        }

        tvCaption.setText(itemCache.getCaption());

        TextView tvDesc = (TextView)view.findViewById(R.id.tvDescription);
        if(itemCache.getDescription() != null) {
            tvDesc.setText(itemCache.getDescription());
            tvDesc.setVisibility(View.VISIBLE);
        }

        boolean isSeller = itemCache.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
        if (isSeller) {
            bidSection.setVisibility(View.GONE);
            sellerSection.setVisibility(View.VISIBLE);
            cbItemSold.setChecked(itemCache.isHasSold());
        } else {
            bidSection.setVisibility(View.VISIBLE);
            sellerSection.setVisibility(View.GONE);
        }

        if (itemCache.isHasSold()) {
            ivItemSold.setVisibility(View.VISIBLE);
            bidSection.setVisibility(View.GONE);
        } else {
            ivItemSold.setVisibility(View.GONE);
        }

    }

    private void updateUI()
    {
        if (item == null || getActivity() == null) return;

//        ivItemPic.setImageResource(0);

        ParseFile photoFile =  item.getParseFile("photo");
        if (photoFile != null) {
            Picasso.with(getActivity())
                    .load(photoFile.getUrl())
                    .into(ivItemPic);
        }

        tvCaption.setText(item.getCaption());

        TextView tvDesc = (TextView)view.findViewById(R.id.tvDescription);
        if(item.getDescription() != null) {
            tvDesc.setText(item.getDescription());
            tvDesc.setVisibility(View.VISIBLE);
        }

        boolean isSeller = item.getUserFast().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
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
