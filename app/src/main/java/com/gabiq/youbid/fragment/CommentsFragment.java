package com.gabiq.youbid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.CommentsAdapter;
import com.gabiq.youbid.model.Comment;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by sreejumon on 10/14/14.
 */
public class CommentsFragment extends Fragment {

    String itemId;
    private CommentsAdapter aComments;
    private ListView lvComments;
    private ImageView ivSendComment;
    private TextView etComments;

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

        etComments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    //TODO: scrol the listview to end
                }
            }
        });

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

        return view;
    }

    private void sendComment(String commentText)
    {
        Comment comment = new Comment();
        comment.setBody(commentText);
        comment.setItemId(itemId);
        comment.setUser(ParseUser.getCurrentUser());
        comment.saveInBackground();
    }
    protected ParseQueryAdapter.QueryFactory<Comment> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Comment>() {
            public ParseQuery<Comment> create() {
                ParseQuery query = new ParseQuery("Comment");
                query.orderByAscending("updatedAt");
                query.whereEqualTo("itemId",itemId);
                return query;
            }
        };
    }
}
