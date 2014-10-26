package com.gabiq.youbid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
            convertView = View.inflate(getContext(), R.layout.entry_message, null);

            viewHolder = new ViewHolder();

            viewHolder.rlBubble = (RelativeLayout) convertView.findViewById(R.id.rlBubble);
            viewHolder.ivProfileImgMe = (ImageView) convertView
                    .findViewById(R.id.ivProfileImgMe);
            viewHolder.ivProfileImgOther = (ImageView) convertView
                    .findViewById(R.id.ivProfileImgOther);
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

        boolean isMe = sender.getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
        if (isMe) {
            viewHolder.ivProfileImgMe.setVisibility(View.VISIBLE);
            viewHolder.ivProfileImgOther.setVisibility(View.GONE);
        } else {
            viewHolder.ivProfileImgOther.setVisibility(View.VISIBLE);
            viewHolder.ivProfileImgMe.setVisibility(View.GONE);
        }
        final ImageView profileView = isMe ? viewHolder.ivProfileImgMe : viewHolder.ivProfileImgOther;

        if (photoFile != null) {
            Picasso.with(getContext())
                    .load(photoFile.getUrl())
                    .placeholder(getContext().getResources().getDrawable(R.drawable.ic_icon_profile))
                    .transform(new RoundTransform())
                    .into(profileView);
        } else {
            profileView.setImageResource(R.drawable.ic_icon_profile);
        }

        return convertView;
    }

    private static class ViewHolder {
        RelativeLayout rlBubble;
        ImageView ivProfileImgMe;
        ImageView ivProfileImgOther;
        TextView tvUserName;
        TextView tvBody;
        TextView tvTime;
    }

}
