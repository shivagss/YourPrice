package com.gabiq.youbid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.utils.RoundTransform;
import com.gabiq.youbid.utils.Utils;
import com.parse.ParseFile;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class CommentsAdapter extends ParseQueryAdapter<Comment> {

    public CommentsAdapter(Context context, QueryFactory<Comment> parseQuery) {
        super(context, parseQuery);
    }

    @Override
    public View getItemView(Comment comment, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.comment_item, null);

            viewHolder = new ViewHolder();

            viewHolder.tvUser = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = (ParseUser) comment.get("createdBy");
        viewHolder.tvBody.setText(comment.getBody());
        viewHolder.tvUser.setText(user.getString("name"));
        viewHolder.tvTime.setText(Utils.getRelativeTimeAgo(comment.getUpdatedAt()));
        ParseFile photoFile = user.getParseFile("photo");

        if (photoFile != null) {
            Picasso.with(getContext())
                    .load(photoFile.getUrl())
                    .placeholder(getContext().getResources().getDrawable(R.drawable.ic_icon_profile))
                    .transform(new RoundTransform())
                    .into(viewHolder.ivProfileImg);
        } else {
            viewHolder.ivProfileImg.setImageResource(R.drawable.ic_icon_profile);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivProfileImg;
        TextView tvBody;
        TextView tvUser;
        TextView tvTime;
    }
}
