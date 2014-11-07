package com.gabiq.youbid.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.CommentsAdapter;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sreejumon on 10/14/14.
 */
public class CommentsFragment extends Fragment {

    String itemId;
    private CommentsAdapter aComments;
    private ListView lvComments;
    private ImageView ivSendComment;
    private TextView etComments;

    private SwipeRefreshLayout swipeContainer;


    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra("ordered")) {

                String jsonString = intent.getStringExtra("com.parse.Data");
                if (jsonString != null) {
                    try {
                        JSONObject json = new JSONObject(jsonString);
                        if (json != null) {
                            if (json.has("type")) {
                                String type = json.getString("type");
                                if (type.equals("comment")) {
                                    if (json.has("itemId")) {
                                        String itemId = json.getString("itemId");
                                        if (CommentsFragment.this.itemId != null && itemId.equals(CommentsFragment.this.itemId)) {
                                            if (aComments != null) {
                                                abortBroadcast();
                                                aComments.loadObjects();
                                                Utils.tryPlayRingtone(getActivity());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("Error", "Error parsing json in push notification in BidListFragment" + e.toString());
                    }
                }
            }
        }
    };

    public static CommentsFragment newInstance(String item_id) {
        CommentsFragment fragmentComments = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString("itemId", item_id);
        fragmentComments.setArguments(args);
        return fragmentComments;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter("com.parse.push.intent.RECEIVE");
        filter.setPriority(1);

        getActivity().registerReceiver(mNotificationReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mNotificationReceiver);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null)
            itemId = getArguments().getString("itemId");
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        lvComments = (ListView) view.findViewById(R.id.lvComments);
        final RelativeLayout emptySection = (RelativeLayout) view.findViewById(R.id.emptySection);
        ((TextView) view.findViewById(R.id.empty_label)).setText(getActivity().getString(R.string.comments_empty_label));
        lvComments.setEmptyView(emptySection);

        aComments = new CommentsAdapter(getActivity(), getParseQuery());

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        aComments.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Comment>() {
            @Override
            public void onLoading() {
//                progressBar.setVisibility(View.VISIBLE);
                emptySection.setVisibility(View.GONE);
            }

            @Override
            public void onLoaded(List<Comment> items, Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });



        lvComments.setAdapter(aComments);
        etComments = (EditText)view.findViewById(R.id.etComments);
        etComments.clearFocus();

        ivSendComment = (ImageView)view.findViewById(R.id.ivSendComment);
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
        setupSwipeContainer(view);
        return view;
    }

    private void sendComment(String commentText)
    {
        Comment comment = new Comment();
        comment.setBody(commentText);
        comment.setItemId(itemId);
        comment.setUser(ParseUser.getCurrentUser());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                aComments.loadObjects();
                closeKeyboard();
            }
        });
    }
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etComments.getWindowToken(), 0);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction();
    }
    protected ParseQueryAdapter.QueryFactory<Comment> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Comment>() {
            public ParseQuery<Comment> create() {
                ParseQuery query = new ParseQuery("Comment");
                query.orderByAscending("updatedAt");
                query.whereEqualTo("itemId",itemId);
                query.include("createdBy");
                return query;
            }
        };
    }





    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.commentListSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                aComments.loadObjects();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorScheme(R.color.refreshColor1,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }
}
