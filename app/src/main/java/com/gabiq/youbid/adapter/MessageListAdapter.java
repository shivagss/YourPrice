package com.gabiq.youbid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Message;
import com.gabiq.youbid.model.User;
import com.gabiq.youbid.utils.RoundTransform;
import com.gabiq.youbid.utils.Utils;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class MessageListAdapter extends ParseQueryAdapter<Message> {
    public MessageListAdapter(Context context, ParseQueryAdapter.QueryFactory<Message> parseQuery) {
        super(context, parseQuery);
    }

    @Override
    public View getItemView(final Message message, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.comment_item, null);

            viewHolder = new ViewHolder();

            viewHolder.ivProfileImg = (ImageView) convertView
                    .findViewById(R.id.ivProfileImg);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        super.getItemView(message, convertView, parent);

        viewHolder.tvBody.setText(message.getBody());
        viewHolder.tvTime.setText(Utils.getRelativeTimeAgo(message.getCreatedAt()));

        ParseUser sender = message.getSender();
        viewHolder.tvUserName.setText(sender.getString("name"));
        ParseFile photoFile = sender.getParseFile("photo");

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
        TextView tvUserName;
        TextView tvBody;
        TextView tvTime;
    }

}
