package com.allyouneedapp.palpicandroid.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.allyouneedapp.palpicandroid.utils.AppUtils;

import static com.allyouneedapp.palpicandroid.utils.Constants.THUMBNAIL_HEIGHT;
import static com.allyouneedapp.palpicandroid.utils.Constants.THUMBNAIL_SIZE;

/**
 * Created by Mostofa on 11/14/2016.
 */

public class AlbumData {
    String path;
    long id;
    Bitmap bitmap;

    public AlbumData(String path, long id) {
        this.path = path;
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getPath() {
        return path;
    }

    public long getId() {
        return id;
    }

    public void rotateImage() {
        if (AppUtils.getExifRotation(path) == 90) {
            this.bitmap = AppUtils.rotateBitmap(bitmap,90);
        } else {
            this.bitmap = bitmap;
        }
    }
}
