package com.gabiq.youbid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Favorite;
import com.gabiq.youbid.model.Item;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.Random;

public class ItemAdapter extends ParseQueryAdapter<Item> {

    public ItemAdapter(Context context, ParseQueryAdapter.QueryFactory<Item> parseQuery) {
        super(context, parseQuery);
    }

//    @Override
//    protected void setPageOnQuery(int page, ParseQuery<Item> query) {
//        super.setPageOnQuery(page, query);
//    }

    @Override
    public View getItemView(final Item item, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.layout_item_cell, null);

            viewHolder = new ViewHolder();
            viewHolder.tvItemCellCaption = (TextView) convertView.findViewById(R.id.tvItemCellCaption);
            viewHolder.ivItemCellImage = (ImageView) convertView.findViewById(R.id.ivItemCellImage);
            viewHolder.btnItemCellFavorite = (Button) convertView.findViewById(R.id.btnItemCellFavorite);
            viewHolder.ivItemCellSold = (ImageView) convertView.findViewById(R.id.ivItemCellSold);
            viewHolder.tvViewsCount = (TextView) convertView.findViewById(R.id.tvViewsCount);
            viewHolder.tvLikesCount = (TextView) convertView.findViewById(R.id.tvLikesCount);
            viewHolder.ivViewsIcon = (ImageView) convertView.findViewById(R.id.ivViewsIcon);
            viewHolder.ivLikesIcon = (ImageView) convertView.findViewById(R.id.ivLikesIcon);
            viewHolder.rlItemCellStatus = (RelativeLayout) convertView.findViewById(R.id.rlItemCellStatus);

            viewHolder.btnItemCellFavorite.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean isFavorite = !item.isFavorite();
                    Favorite.setFavorite(item, isFavorite);
                    item.setFavorite(isFavorite);
                    viewHolder.btnItemCellFavorite.setPressed(isFavorite);

                    return true;
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        super.getItemView(item, convertView, parent);

        BigInteger bigInt = new BigInteger(item.getObjectId().getBytes());

        int imageResource = 0;
        switch (bigInt.intValue() % 5) {
            case 0:
                imageResource = R.color.placeholder1;
                break;
            case 1:
                imageResource = R.color.placeholder2;
                break;
            case 2:
                imageResource = R.color.placeholder3;
                break;
            case 3:
                imageResource = R.color.placeholder4;
                break;
            case 4:
                imageResource = R.color.placeholder5;
                break;
        }

        viewHolder.itemId = item.getObjectId();
        viewHolder.tvItemCellCaption.setText(item.getString("caption"));
        viewHolder.ivItemCellImage.setImageResource(imageResource);
        if (item.getHasSold()) {
            viewHolder.ivItemCellSold.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivItemCellSold.setVisibility(View.GONE);
        }

        int viewCount = item.getViewCount();
        viewHolder.tvViewsCount.setText(String.valueOf(viewCount));

        int likeCount = item.getLikeCount();
        viewHolder.tvLikesCount.setText(String.valueOf(likeCount));

        if (viewCount == 0 && likeCount == 0) {
            viewHolder.rlItemCellStatus.setVisibility(View.GONE);
        } else {
            viewHolder.rlItemCellStatus.setVisibility(View.VISIBLE);
            if (viewCount == 0) {
                viewHolder.ivViewsIcon.setVisibility(View.GONE);
                viewHolder.tvViewsCount.setVisibility(View.GONE);
            } else {
                viewHolder.ivViewsIcon.setVisibility(View.VISIBLE);
                viewHolder.tvViewsCount.setVisibility(View.VISIBLE);
            }
            if (false && likeCount == 0) {
                viewHolder.ivLikesIcon.setVisibility(View.GONE);
                viewHolder.tvLikesCount.setVisibility(View.GONE);
            } else {
                viewHolder.ivLikesIcon.setVisibility(View.VISIBLE);
                viewHolder.tvLikesCount.setVisibility(View.VISIBLE);
            }
        }

        ParseFile photoFile = item.getParseFile("thumbnail");
        if (photoFile != null) {
            Picasso.with(getContext())
                    .load(photoFile.getUrl())
                    .placeholder(getContext().getResources().getDrawable(imageResource))
                    .into(viewHolder.ivItemCellImage);
        }

        // set favorite
//        viewHolder.btnItemCellFavorite.setPressed(false);
//        ParseQuery<Favorite> query = ParseQuery.getQuery("Favorite");
//        query.whereEqualTo("user", ParseUser.getCurrentUser());
//        final String itemId = item.getObjectId();
//        query.whereEqualTo("itemId", itemId);
//        final ViewHolder vh = viewHolder;
//        query.getFirstInBackground(new GetCallback<Favorite>() {
//            public void done(Favorite favorite, ParseException e) {
//                if (!vh.itemId.equals(itemId)) return;
//                boolean isFavorite = (favorite != null);
//                item.setFavorite(isFavorite);
//                vh.btnItemCellFavorite.setPressed(isFavorite);
//            }
//        });

        return convertView;
    }

    private static class ViewHolder {
        String itemId;
        ImageView ivItemCellImage;
        ImageView ivItemCellSold;
        TextView tvItemCellCaption;
        Button btnItemCellFavorite;
        TextView tvViewsCount;
        TextView tvLikesCount;
        ImageView ivViewsIcon;
        ImageView ivLikesIcon;
        RelativeLayout rlItemCellStatus;
    }

}
