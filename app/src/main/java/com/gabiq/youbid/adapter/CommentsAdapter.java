package com.gabiq.youbid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.utils.Utils;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter;

public class CommentsAdapter extends ParseQueryAdapter<Comment> {

    public CommentsAdapter(Context context, QueryFactory<Comment> parseQuery) {
        super(context, parseQuery);
    }

    @Override
    public View getItemView(Comment comment, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.comment_item, null);
        }

        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        tvBody.setText(comment.getBody());

        TextView tvUser = (TextView) convertView.findViewById(R.id.tvUserName);
        tvUser.setText(comment.getUser().getName());

        TextView tvTime = (TextView)convertView.findViewById(R.id.tvTime);
        tvTime.setText(Utils.getRelativeTimeAgo(comment.getUpdatedAt()));

        ParseImageView ivProfileImg = (ParseImageView)convertView.findViewById(R.id.ivProfileImg);
        ParseFile photoFile = comment.getUser().getParseUser().getParseFile("photo");
        if (photoFile != null) {
            ivProfileImg.setParseFile(photoFile);
            ivProfileImg.loadInBackground();
        }

        return convertView;
    }
}
