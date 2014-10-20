package com.gabiq.youbid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Bid;
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

public class BidListAdapter extends ParseQueryAdapter<Bid> {

    public BidListAdapter(Context context, ParseQueryAdapter.QueryFactory<Bid> parseQuery) {
        super(context, parseQuery);
    }


    @Override
    public View getItemView(final Bid bid, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.entry_bid, null);

            viewHolder = new ViewHolder();
            viewHolder.pivEntryBidPhoto = (ParseImageView) convertView
                    .findViewById(R.id.pivEntryBidPhoto);
            viewHolder.tvEntryBidName = (TextView) convertView.findViewById(R.id.tvEntryBidName);
            viewHolder.tvEntryBidBody = (TextView) convertView.findViewById(R.id.tvEntryBidBody);
            viewHolder.tvEntryBidState = (TextView) convertView.findViewById(R.id.tvEntryBidState);
            viewHolder.tvEntryBidTime = (TextView) convertView.findViewById(R.id.tvEntryBidTime);
            viewHolder.pivEntryBidPhoto.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_icon_profile));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        super.getItemView(bid, convertView, parent);

        viewHolder.tvEntryBidBody.setText("Offer Amount: $" + String.valueOf(bid.getPrice()));
        viewHolder.tvEntryBidState.setText(bid.getState());
        viewHolder.tvEntryBidTime.setText(Utils.getRelativeTimeAgo(bid.getCreatedAt()));

        final ViewHolder vh = viewHolder;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        ParseUser bidder = (ParseUser) bid.get("createdBy");
        query.getInBackground(bidder.getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null) {
                    User user = new User(parseUser);
                    vh.tvEntryBidName.setText(user.getName());
                    // load image
                    ParseFile photoFile = parseUser.getParseFile("photo");
                    if (photoFile != null) {
                        viewHolder.pivEntryBidPhoto.setParseFile(photoFile);
                        viewHolder.pivEntryBidPhoto.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                // nothing to do
                            }
                        });
                    } else {
                        viewHolder.pivEntryBidPhoto.setParseFile(null);
                    }

                } else {
                    // something went wrong
                    Log.e("ERROR", "Error reading user in BidListAdapter");
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ParseImageView pivEntryBidPhoto;
        TextView tvEntryBidName;
        TextView tvEntryBidBody;
        TextView tvEntryBidState;
        TextView tvEntryBidTime;
    }


}
