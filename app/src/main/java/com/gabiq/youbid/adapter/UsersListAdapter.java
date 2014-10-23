package com.gabiq.youbid.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.ProfileActivity;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersListAdapter extends ArrayAdapter<ParseUser> {

    private void startProfileActivity(String userID) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("userId", userID);
        getContext().startActivity(intent);

    }

    private class ViewHolder {
        ImageView ivProfilePic;
        TextView tvUserName;
        TextView tvBodyText;
        ImageView btnFollowing;

    }

    protected ArrayList<ParseUser> dataList;

    public UsersListAdapter(Context context, ArrayList<ParseUser> objects) {
        super(context, R.layout.item_users, objects);
        dataList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ParseUser user = getItem(position);

        return getTweetItemView(convertView, parent, user);
    }

    public View getTweetItemView(View convertView, ViewGroup parent, final ParseUser user) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.item_users, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBodyText = (TextView) convertView.findViewById(R.id.tvBodyText);
            viewHolder.btnFollowing = (ImageView) convertView.findViewById(R.id.ivFollowing);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUserName.setText(user.getString("name"));
        viewHolder.tvBodyText.setText((user.getString("about")));

        viewHolder.ivProfilePic.setImageResource(0);
        ParseFile photo = user.getParseFile("photo");
        if(photo != null) {
            Picasso.with(getContext()).load(photo.getUrl()).into(viewHolder.ivProfilePic);
        }

        viewHolder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(user.getObjectId());
            }
        });

        return convertView;
    }
}
