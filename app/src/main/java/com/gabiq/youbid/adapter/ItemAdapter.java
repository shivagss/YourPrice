package com.gabiq.youbid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Item;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Random;

public class ItemAdapter extends ParseQueryAdapter<Item> {

    public ItemAdapter(Context context, ParseQueryAdapter.QueryFactory<Item> parseQuery) {
        super(context, parseQuery);
    }


    @Override
    public View getItemView(Item item, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.layout_item_cell, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.tvItemCellCaption);
        ParseImageView imageView = (ParseImageView) convertView.findViewById(R.id.ivItemCellImage);

        // randomize sizes for now...
        Random r = new Random();
        int i = 6; // r.nextInt(10);

        textView.setText(item.getString("caption"));
        ParseFile photoFile = item.getParseFile("photo");
        if (photoFile != null) {
            imageView.setParseFile(photoFile);
            imageView.setMinimumHeight(200+i*25);
            imageView.setMaxHeight(200+i*25);
            imageView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        return convertView;
    }
}
