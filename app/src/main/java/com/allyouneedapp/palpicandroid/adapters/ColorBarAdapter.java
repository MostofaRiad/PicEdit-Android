package com.allyouneedapp.palpicandroid.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.allyouneedapp.palpicandroid.R;

/**
 * Created by Mostofa on 10/30/2016.
 */

public class ColorBarAdapter extends RecyclerView.Adapter<ColorBarAdapter.ColorItemHolder> {

    Context mContext;
    int[] itemData = {};

    public ColorBarAdapter(Context mContext, int[] arrayData) {
        super();
        this.mContext = mContext;
        this.itemData = arrayData;
    }
    @Override
    public ColorItemHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_color, parent, false);
        ColorItemHolder viewHolder = new ColorItemHolder(itemView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ColorItemHolder holder, int position) {
        holder.imLayer.setBackgroundColor(mContext.getResources().getColor(itemData[position]));
    }


    @Override
    public int getItemCount() {
        return itemData.length;
    }

    public class ColorItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout imLayer;

        public ColorItemHolder(View itemView) {
            super(itemView);
            imLayer = (LinearLayout) itemView.findViewById(R.id.item_color_layer);
        }
    }

}
