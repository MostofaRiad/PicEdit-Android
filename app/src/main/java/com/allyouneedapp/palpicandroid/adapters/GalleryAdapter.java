package com.allyouneedapp.palpicandroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.allyouneedapp.palpicandroid.R;
import com.allyouneedapp.palpicandroid.models.AlbumData;
import com.allyouneedapp.palpicandroid.utils.ImageDownloaderTask;
import com.facebook.internal.ImageDownloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mostofa on 11/14/2016.
 */

public class GalleryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Activity activity;
    private ArrayList<AlbumData> arrayList;
    public GalleryAdapter(Activity context, ArrayList<AlbumData> array) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = context;
        this.arrayList = array;
    }

    public int getCount() {
        return arrayList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final GalleryAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new GalleryAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grid_main, null);
            holder.imageview = (ImageView) convertView.findViewById(R.id.image_grid_main_item);

            convertView.setTag(holder);
        } else {
            holder = (GalleryAdapter.ViewHolder) convertView.getTag();
        }

        Uri uri = Uri.fromFile(new File(this.arrayList.get(position).getPath()));
        if (uri == null) {
            holder.imageview.setImageResource(R.drawable.question_mark);
        }
        Picasso.with(activity).load(uri).resize(96, 96).centerCrop().into(holder.imageview);

        return convertView;
    }

    class ViewHolder {
        ImageView imageview;
    }
}
