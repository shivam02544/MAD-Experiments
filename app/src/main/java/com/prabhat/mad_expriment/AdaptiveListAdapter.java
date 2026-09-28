package com.prabhat.mad_expriment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AdaptiveListAdapter extends ArrayAdapter<AdaptiveItem> {

    private final Context context;
    private final int resource;

    public AdaptiveListAdapter(@NonNull Context context, int resource, @NonNull List<AdaptiveItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View itemView;

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.iconContainer = itemView.findViewById(R.id.iconContainer);
            holder.imgIcon = itemView.findViewById(R.id.imgIcon);
            holder.tvTitle = itemView.findViewById(R.id.tvItemTitle);
            holder.tvCategory = itemView.findViewById(R.id.tvItemCategory);
            holder.tvPrice = itemView.findViewById(R.id.tvItemPrice);
            itemView.setTag(holder);
        } else {
            itemView = convertView;
            holder = (ViewHolder) itemView.getTag();
        }

        AdaptiveItem item = getItem(position);
        if (item != null) {
            holder.tvTitle.setText(item.getTitle());
            holder.tvCategory.setText(item.getCategory());
            holder.tvPrice.setText(item.getPrice());
            holder.imgIcon.setImageResource(item.getImageResId());
            if (item.getBgDrawableResId() != 0) {
                holder.iconContainer.setBackgroundResource(item.getBgDrawableResId());
            }
        }

        return itemView;
    }

    private static class ViewHolder {
        FrameLayout iconContainer;
        ImageView imgIcon;
        TextView tvTitle;
        TextView tvCategory;
        TextView tvPrice;
    }
}