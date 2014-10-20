package com.gabiq.youbid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.Keyword;

import java.util.List;

public class ItemTagsAdapter extends ArrayAdapter<Keyword> {
    public ItemTagsAdapter(Context context, List<Keyword> objects) {
        super(context, R.layout.tag_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_item, null);

            holder = new ViewHolder();
            holder.tvTagItem = (TextView) convertView.findViewById(R.id.tvTagItem);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTagItem.setText(getItem(position).getKeyword());

        return convertView;
    }

    private static class ViewHolder{
        TextView tvTagItem;
    }
}
