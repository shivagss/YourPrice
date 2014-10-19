package com.gabiq.youbid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.math.BigInteger;
import java.util.Random;

public class ItemAdapter extends ParseQueryAdapter<Item> {

    public ItemAdapter(Context context, ParseQueryAdapter.QueryFactory<Item> parseQuery) {
        super(context, parseQuery);
    }


    @Override
    public View getItemView(final Item item, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.layout_item_cell, null);

            viewHolder = new ViewHolder();
            viewHolder.tvItemCellCaption = (TextView) convertView.findViewById(R.id.tvItemCellCaption);
            viewHolder.ivItemCellImage = (ParseImageView) convertView.findViewById(R.id.ivItemCellImage);
            viewHolder.btnItemCellFavorite = (Button) convertView.findViewById(R.id.btnItemCellFavorite);

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

//        Random r = new Random();
        // convert to big integer
        BigInteger bigInt = new BigInteger(item.getObjectId().getBytes());

        switch (bigInt.intValue() % 5) {
            case 0:
                viewHolder.ivItemCellImage.setPlaceholder(getContext().getResources().getDrawable(R.color.placeholder1));
                break;
            case 1:
                viewHolder.ivItemCellImage.setPlaceholder(getContext().getResources().getDrawable(R.color.placeholder2));
                break;
            case 2:
                viewHolder.ivItemCellImage.setPlaceholder(getContext().getResources().getDrawable(R.color.placeholder3));
                break;
            case 3:
                viewHolder.ivItemCellImage.setPlaceholder(getContext().getResources().getDrawable(R.color.placeholder4));
                break;
            case 4:
                viewHolder.ivItemCellImage.setPlaceholder(getContext().getResources().getDrawable(R.color.placeholder5));
                break;
        }

        int i = 6; // r.nextInt(10);

        viewHolder.tvItemCellCaption.setText(item.getString("caption"));
        ParseFile photoFile = item.getParseFile("thumbnail");
        if (photoFile != null) {
            viewHolder.ivItemCellImage.setParseFile(photoFile);
//            imageView.setMinimumHeight(200+i*25);
//            imageView.setMaxHeight(200+i*25);
            viewHolder.ivItemCellImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        } else {
            viewHolder.ivItemCellImage.setParseFile(null);
        }

        // set favorite
        viewHolder.btnItemCellFavorite.setPressed(false);
        ParseQuery<Favorite> query = ParseQuery.getQuery("Favorite");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("itemId", item.getObjectId());
        final ViewHolder vh = viewHolder;
        query.getFirstInBackground(new GetCallback<Favorite>() {
            public void done(Favorite favorite, ParseException e) {
                boolean isFavorite = (favorite != null);
                item.setFavorite(isFavorite);
                vh.btnItemCellFavorite.setPressed(isFavorite);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvItemCellCaption;
        ParseImageView ivItemCellImage;
        Button btnItemCellFavorite;
    }

}
