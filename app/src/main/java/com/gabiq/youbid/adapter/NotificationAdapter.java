package com.gabiq.youbid.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Notification;
import com.gabiq.youbid.utils.Utils;

import org.w3c.dom.Text;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.comment_item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.ivProfileImg = (ImageView) convertView
                    .findViewById(R.id.ivProfileImg);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserStoreName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvBody.setText(notification.getMessage());
        viewHolder.tvTime.setText(Utils.getRelativeTimeAgo(notification.getCreatedAt()));

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivProfileImg;
        TextView tvUserName;
        TextView tvBody;
        TextView tvTime;
    }
}
