package com.allyouneedapp.palpicandroid.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allyouneedapp.palpicandroid.R;
import com.allyouneedapp.palpicandroid.models.Album;

import java.util.ArrayList;

/**
 * Created by Mostofa on 10/26/2016.
 */

public class AlbumAdapter extends ArrayAdapter {
    Activity context;
    ArrayList<Album> albums;

    @SuppressWarnings("unchecked")
    public AlbumAdapter(Activity context, ArrayList<Album> arraymodel) {
        super(context, R.layout.row_album_name, arraymodel);
        this.context = context;
        this.albums = arraymodel;
    }

    @Override
    public int getCount() {
        return albums.size();
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
            row = inflater.inflate(R.layout.row_album_name, null);
        }
        Album album = this.albums.get(position);
        TextView albumName = (TextView) row.findViewById(R.id.text_row_album_name);
        TextView albumContentCout = (TextView) row.findViewById(R.id.text_row_album_count);
        albumName.setText(albums.get(position).name);
        albumContentCout.setText(String.valueOf(albums.get(position).count));
        return row;

    }
}
