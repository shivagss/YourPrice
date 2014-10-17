package com.gabiq.youbid.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.NewItemActivity;
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


        etComments = (EditText)rootView.findViewById(R.id.etComments);
        ivSendComment = (ImageView)rootView.findViewById(R.id.ivSendComment);
        ivSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = etComments.getText().toString();
                if(comments !=null  &&  !comments.isEmpty()) {
                    sendComment(comments);
                    etComments.setText(null);
                    commentFragment.refresh();
                }
            }
        });


        setHasOptionsMenu(true);
        retrieveItem(itemId);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        commentFragment = CommentsFragment.newInstance(itemId);
        ft.replace(R.id.flCommentsContainer, commentFragment);
        ft.commit();

        return rootView;
    }

    private void sendComment(String commentText)
    {
        Comment comment = new Comment();
        comment.setBody(commentText);
        comment.setItemId(itemId);
        comment.setUser(ParseUser.getCurrentUser());
        comment.saveInBackground();
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
