package com.gabiq.youbid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.CommentsAdapter;
import com.gabiq.youbid.model.Comment;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * Created by sreejumon on 10/14/14.
 */
public class CommentsFragment extends Fragment {

    String itemId;
    private CommentsAdapter aComments;
    private ListView lvComments;

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

        return view;

    }

    public void refresh()
    {
       // lvComments.setAdapter(null);
        aComments = new CommentsAdapter(getActivity(), getParseQuery());
        lvComments.setAdapter(aComments);
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
