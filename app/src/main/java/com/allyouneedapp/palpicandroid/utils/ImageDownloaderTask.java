package com.allyouneedapp.palpicandroid.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.allyouneedapp.palpicandroid.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import static com.allyouneedapp.palpicandroid.utils.Constants.THUMBNAIL_SIZE_BIG;

/**
 * Created by Soul on 12/17/2016.
 */

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Activity context;
    private long imageId;

    public ImageDownloaderTask(ImageView imageView, Activity context,long imageId) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
        this.imageId = imageId;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return loadBitmap(params[0],params[1]);
    }

    private Bitmap loadBitmap(String param, String path) {
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
        context.getContentResolver(), imageId,
        MediaStore.Images.Thumbnails.MICRO_KIND, null);

        if (AppUtils.getExifRotation(path) == 90) {
            bitmap = AppUtils.rotateBitmap(bitmap,90);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.caution);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }


}