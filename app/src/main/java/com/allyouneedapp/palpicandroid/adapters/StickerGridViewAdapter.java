package com.allyouneedapp.palpicandroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.allyouneedapp.palpicandroid.R;
import com.allyouneedapp.palpicandroid.models.Sticker;
import com.allyouneedapp.palpicandroid.utils.StickerUtil;

import java.util.ArrayList;

/**
 * Created by Mostofa on 10/30/2016.
 */
    public class StickerGridViewAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<Sticker> stickers;
        private Context context;

        public StickerGridViewAdapter(Context context, ArrayList<Sticker> stickers) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.stickers = stickers;
            this.context = context;
        }

        public int getCount() {
            return stickers.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_grid_sticker, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.image_grid_main_item);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
                Bitmap bitmap = StickerUtil.getBitmapFromAsset(context, stickers.get(position).filePath);
                holder.imageview.setImageBitmap(bitmap);
            if (bitmap == null) {
                Bitmap bitmap1 = BitmapFactory.decodeFile(stickers.get(position).filePath);
                holder.imageview.setImageBitmap(bitmap1);
            }
            return convertView;
        }

    }
    class ViewHolder {
        ImageView imageview;
    }

