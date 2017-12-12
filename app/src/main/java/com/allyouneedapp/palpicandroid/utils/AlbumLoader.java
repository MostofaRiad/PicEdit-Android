package com.allyouneedapp.palpicandroid.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.allyouneedapp.palpicandroid.models.AlbumData;

import java.util.ArrayList;

/**
 * Created by Mostofa on 11/20/2016.
 */

public class AlbumLoader {

    public interface AlbumLoadListener {
        public void loadFinished(ArrayList<AlbumData> albumContents);
    }

    private AlbumLoadListener listener;

    public void setListener(AlbumLoadListener listener) {
        this.listener = listener;
    }

    private Activity context;
    public AlbumLoader( Activity activity) {
        context = activity;
    }

    public void loadAlbums (String albumIdString) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.MINI_THUMB_MAGIC};
        String orderBy =  MediaStore.Images.Media.DATE_ADDED + " DESC";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, orderBy);

        ArrayList<AlbumData> paths = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                String albumId = cursor.getString(columnIndex);

                if (albumIdString.equals(albumId)) {
                    String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long imageId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
//                    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                            context.getContentResolver(), imageId,
//                            MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    paths.add(new AlbumData(filePath, imageId));
                }
            }
            cursor.close();
        }
        this.listener.loadFinished(paths);
    }
}
