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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.BidListActivity;
import com.gabiq.youbid.activity.DetailsActivity;
import com.gabiq.youbid.activity.NewItemActivity;
import com.gabiq.youbid.activity.ProfileActivity;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.Utils;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    private ImageView ivSendComment;
    private TextView etComments;
    private CommentsFragment commentFragment;
    private Menu detailsMenu;
    private EditText etBidAmount;
    private Button btnBid;
    private TextView tvBidStatus;
    private ImageView ivProfile;
    private RelativeLayout commentBox;
    private ScrollView scrollView;
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


        ivProfile = (ImageView) rootView.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProfileActivity();
            }
        });
        etComments = (EditText)rootView.findViewById(R.id.etComments);

        etComments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.scrollTo(0, Integer.MAX_VALUE);
                           }
                    });
            }
        });

        ivSendComment = (ImageView)rootView.findViewById(R.id.ivSendComment);
        ivSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = etComments.getText().toString();
                if(comments !=null  &&  !comments.isEmpty()) {
                    sendComment(comments);
                    etComments.setText(null);

                }
            }
        });
        commentBox = (RelativeLayout)rootView.findViewById(R.id.commentBox);
        etBidAmount = (EditText)rootView.findViewById(R.id.etBidAmount);
        etBidAmount.setVisibility(View.INVISIBLE);

        etBidAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentBox.setVisibility(View.INVISIBLE);
            }
        });


        btnBid = (Button)rootView.findViewById(R.id.btnBid);
        btnBid.setVisibility(View.INVISIBLE);
        tvBidStatus = (TextView)rootView.findViewById(R.id.tvBidStatus);
        btnBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isSeller) {
                        Intent i = new Intent(getActivity(), BidListActivity.class);
                        i.putExtra("itemId",itemId);
                        startActivity(i);
                    } else {
                        double bidAmount = Double.parseDouble(etBidAmount.getText().toString());
                        submitBid(bidAmount);
                    }
                }
                catch(Exception e)
                {
                  e.printStackTrace();
                }
            }
        });

        setHasOptionsMenu(true);

        retrieveItem(itemId);

        scrollView = (ScrollView)rootView.findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewTreeObserver observer = scrollView.getViewTreeObserver();
                observer.addOnScrollChangedListener(onScrollChangedListener);

                return false;
            }
        });


       FragmentTransaction ft = getFragmentManager().beginTransaction();
        commentFragment = CommentsFragment.newInstance(itemId);
        ft.replace(R.id.flCommentsContainer, commentFragment);
        ft.commit();
        return rootView;
    }
    final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
            ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            commentBox.setVisibility(View.VISIBLE);
        }
    };


    private void startProfileActivity() {

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        if(item != null)
            intent.putExtra("userId", item.getUser().getObjectId());
        startActivity(intent);

    }

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
        else if(amount > 5 * item.getMinPrice()) //Very high if bid amount is more than 5 X
            return 1;
        return 0;
    }

    private void sendComment(String commentText)
    {
        Comment comment = new Comment();
        comment.setBody(commentText);
        comment.setItemId(itemId);
        comment.setUser(ParseUser.getCurrentUser());
        comment.saveInBackground();
        commentFragment.addComment(comment);
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
        if (isSeller) {
            btnBid.setText(R.string.btn_bid_list);
        } else {
            etBidAmount.setVisibility(View.VISIBLE);
        }
        btnBid.setVisibility(View.VISIBLE);

        //Hide the delete & edit option if the user is not the owner
        MenuItem deleteMenu = detailsMenu.findItem(R.id.action_delete);
        MenuItem editIMenu = detailsMenu.findItem(R.id.action_edit);
        if(item.getUser().getObjectId().equals( ParseUser.getCurrentUser().getObjectId())){
            deleteMenu.setVisible(true);
            editIMenu.setVisible(true);
        }
        else{
            deleteMenu.setVisible(false);
            editIMenu.setVisible(false);
        }



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
        tvTimePosted.setText(Utils.getRelativeTimeAgo(item.getCreatedAt()));

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
}
