package com.gabiq.youbid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.CommentsAdapter;
import com.gabiq.youbid.model.Comment;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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


    public static CommentsFragment newInstance(String item_id) {
        CommentsFragment fragmentComments = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString("itemId", item_id);
        fragmentComments.setArguments(args);
        return fragmentComments;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null)
            itemId = getArguments().getString("itemId");
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        lvComments = (ListView) view.findViewById(R.id.lvComments);

        aComments = new CommentsAdapter(getActivity(), getParseQuery());
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
        // Configure the refreshing colors
        swipeContainer.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }
}
