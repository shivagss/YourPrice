package com.gabiq.youbid.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Notification;
import com.gabiq.youbid.model.User;
import com.gabiq.youbid.utils.Utils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = getItem(position);

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.comment_item, parent, false);

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

        viewHolder.tvBody.setText(notification.getMessage());
        viewHolder.tvTime.setText(Utils.getRelativeTimeAgo(notification.getCreatedAt()));

        final ViewHolder vh = viewHolder;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(notification.getSenderId(), new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null) {
                    User user = new User(parseUser);
                    vh.tvUserName.setText(user.getName());
                    // load image
                } else {
                    // something went wrong
                    Log.e("ERROR", "Error reading user in RecentActivityFragment");
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivProfileImg;
        TextView tvUserName;
        TextView tvBody;
        TextView tvTime;
    }
}
