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
import com.gabiq.youbid.utils.RoundTransform;
import com.gabiq.youbid.utils.Utils;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Notification notification = getItem(position);

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
//            viewHolder.ivProfileImg.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_icon_profile));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvBody.setText(notification.getMessage());
        viewHolder.tvTime.setText(Utils.getRelativeTimeAgo(notification.getCreatedAt()));
        String senderName = notification.getSenderName();
        if (senderName != null) {
            viewHolder.tvUserName.setText(senderName);
        } else {
            viewHolder.tvUserName.setText("anonymous");
        }
        String senderPhotoUrl = notification.getSenderPhotoUrl();
        if (senderPhotoUrl != null) {
            Picasso.with(getContext())
                    .load(senderPhotoUrl)
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
