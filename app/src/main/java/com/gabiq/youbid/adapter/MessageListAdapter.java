package com.gabiq.youbid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Message;
import com.gabiq.youbid.model.User;
import com.gabiq.youbid.utils.Utils;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class MessageListAdapter extends ParseQueryAdapter<Message> {
    public MessageListAdapter(Context context, ParseQueryAdapter.QueryFactory<Message> parseQuery) {
        super(context, parseQuery);
    }

    @Override
    public View getItemView(final Message message, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.entry_bid, null);

            viewHolder = new ViewHolder();

            viewHolder.ivProfileImg = (ParseImageView) convertView
                    .findViewById(R.id.ivProfileImg);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.ivProfileImg.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_icon_profile));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        super.getItemView(message, convertView, parent);

        viewHolder.tvBody.setText(message.getBody());
        viewHolder.tvTime.setText(Utils.getRelativeTimeAgo(message.getCreatedAt()));

        final ViewHolder vh = viewHolder;

        // change to fetch
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(message.getSender().getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null) {
                    User user = new User(parseUser);
                    vh.tvUserName.setText(user.getName());
                    // load image
                    ParseFile photoFile = parseUser.getParseFile("photo");
                    if (photoFile != null) {
                        viewHolder.ivProfileImg.setParseFile(photoFile);
                        viewHolder.ivProfileImg.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                // nothing to do
                            }
                        });
                    } else {
                        viewHolder.ivProfileImg.setParseFile(null);
                    }

                } else {
                    // something went wrong
                    Log.e("ERROR", "Error reading user in RecentActivityFragment");
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ParseImageView ivProfileImg;
        TextView tvUserName;
        TextView tvBody;
        TextView tvTime;
    }

}
