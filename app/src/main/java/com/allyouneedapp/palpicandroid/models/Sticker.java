package com.allyouneedapp.palpicandroid.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Mostofa on 10/29/2016.
 */

public class Sticker {

    public static final String TABLE = "sticker";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PATH = "path";

    public String id;
    public String title;
    public String filePath;

    public Sticker(String id, String title, String filePath){
        this.id = id;
        this.title = title;
        this.filePath = filePath;
    }

    public Sticker(){

    }

    public Bitmap getBitmap(){
        Bitmap bitmap = BitmapFactory.decodeFile(this.filePath);
        return  bitmap;
    }
}
