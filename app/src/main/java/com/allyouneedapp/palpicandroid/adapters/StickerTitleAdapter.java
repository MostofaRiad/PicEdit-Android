package com.allyouneedapp.palpicandroid.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allyouneedapp.palpicandroid.R;
import com.allyouneedapp.palpicandroid.models.Sticker;

import java.util.ArrayList;

/**
 * Created by Mostofa on 10/30/2016.
 */

public class StickerTitleAdapter extends ArrayAdapter {
    Activity context;
    ArrayList<String> stickers;

    @SuppressWarnings("unchecked")
    public StickerTitleAdapter(Activity context, ArrayList<String> arraymodel) {
        super(context, R.layout.row_album_name, arraymodel);
        this.context = context;
        this.stickers = arraymodel;
    }

    @Override
    public int getCount() {
        return stickers.size();
    }

    /**
     * get view cell for command table
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.item_sticker_list, null);
        }
        TextView title = (TextView) row.findViewById(R.id.text_sticker_title);
        title.setText(stickers.get(position));
        return row;

    }
}
