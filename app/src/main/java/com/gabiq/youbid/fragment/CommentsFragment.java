package com.gabiq.youbid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

/**
 * Created by sreejumon on 10/14/14.
 */
public class CommentsFragment extends Fragment {

    String itemId;
    private LinearLayout listComments;
    private static LayoutInflater inflater;
    private static ViewGroup container;

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
        CommentsFragment.inflater = inflater;
        if(getArguments() != null)
            itemId = getArguments().getString("itemId");
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        listComments = (LinearLayout) view.findViewById(R.id.listComments);

        ParseQuery query = new ParseQuery("Comment");
        query.orderByAscending("updatedAt");
        query.whereEqualTo("itemId",itemId);

        query.findInBackground(new FindCallback() {
            @Override
            public void done(List comments, ParseException e) {
                if((comments!= null) && !comments.isEmpty()){
                    for (int i=0; i < comments.size(); i++){
                        Comment comm = (Comment)comments.get(i);
                        addComment(comm);

                    }
                }
            }
        });

     return view;

    }

    //Add comment to already existing comments list
    public void addComment(Comment comm)
    {
        View commentView = CommentsFragment.inflater.inflate(R.layout.comment_item,
                CommentsFragment.container, false);
        TextView tvComment = (TextView)commentView.findViewById(R.id.tvBody);
        tvComment.setText(comm.getBody());

        TextView tvUser = (TextView) commentView.findViewById(R.id.tvUserName);
        tvUser.setText(comm.getUser().getName());

        TextView tvTime = (TextView)commentView.findViewById(R.id.tvTime);
        if(comm.getUpdatedAt()!= null)
            tvTime.setText(Utils.getRelativeTimeAgo(comm.getUpdatedAt()));
        else
            tvTime.setText("Just now");

        listComments.addView(commentView);
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
