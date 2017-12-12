package com.allyouneedapp.palpicandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.allyouneedapp.palpicandroid.models.Sticker;

import java.util.ArrayList;

/**
 * Created by Mostofa on 11/1/2016.
 */

public class PalPicDBHandler {
    private DBHelper dbHelper;
    public static final String RECENT_TABLE = "recent";
    public static final String TIME_STUMP = "time";
    SQLiteDatabase db_write;
    public PalPicDBHandler (Context context) {
        dbHelper = new DBHelper(context);
        db_write = dbHelper.getWritableDatabase();
    }

    public void inSertAll(ArrayList<Sticker> stickers) {
        db_write.beginTransaction();
        for (Sticker sticker: stickers){
            ContentValues values = new ContentValues();
            values.put(Sticker.KEY_ID, sticker.id);
            values.put(Sticker.KEY_TITLE, sticker.title);
            values.put(Sticker.KEY_PATH, sticker.filePath);
            // Inserting Row
            db_write.insert(Sticker.TABLE, null, values);
        }
        db_write.setTransactionSuccessful();
        db_write.endTransaction();
    }

    public void insert(Sticker sticker) {
        // TODO Auto-generated method stub
        if (getStickersById(sticker.id).id != null){
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Sticker.KEY_ID, sticker.id);
        values.put(Sticker.KEY_TITLE, sticker.title);
        values.put(Sticker.KEY_PATH, sticker.filePath);

        // Inserting Row
        db_write.insert(Sticker.TABLE, null, values);
    }

    public void delete(String id) {
        //int student_Id = getFirstStudent();
        db_write.delete(Sticker.TABLE, Sticker.KEY_ID + "='" + id + "'", null);
    }

    public void update(Sticker sticker) {
        ContentValues values = new ContentValues();

        values.put(Sticker.KEY_ID, sticker.id);
        values.put(Sticker.KEY_TITLE,sticker.title);
        values.put(Sticker.KEY_PATH, sticker.filePath);

        db_write.update(Sticker.TABLE, values, Sticker.KEY_ID + "='" + sticker.id + "'", null);
    }

    public ArrayList<Sticker> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Sticker.KEY_ID + "," +
                Sticker.KEY_TITLE + "," +
                Sticker.KEY_PATH +
                " FROM " + Sticker.TABLE + " ORDER BY "+ Sticker.KEY_TITLE +" COLLATE NOCASE";
        ArrayList<Sticker> stickers =new ArrayList();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Sticker item = new Sticker(cursor.getString(cursor.getColumnIndex(Sticker.KEY_ID)),
                                            cursor.getString(cursor.getColumnIndex(Sticker.KEY_TITLE)),
                                            cursor.getString(cursor.getColumnIndex(Sticker.KEY_PATH)));

                stickers.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return stickers;
    }

    public Sticker getStickersById(String Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Sticker.KEY_ID + "," +
                Sticker.KEY_TITLE + "," +
                Sticker.KEY_PATH  +
                " FROM " + Sticker.TABLE
                + " WHERE " +
                Sticker.KEY_ID + "='" + Id +"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        Sticker sticker = new Sticker();
        if (cursor.moveToFirst()) {
            do {
                sticker = new Sticker();
                sticker.id =cursor.getString(cursor.getColumnIndex(Sticker.KEY_ID));
                sticker.title =cursor.getString(cursor.getColumnIndex(Sticker.KEY_TITLE));
                sticker.filePath  =cursor.getString(cursor.getColumnIndex(Sticker.KEY_PATH));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return sticker;
    }

    public ArrayList<Sticker> getStickersWithTile(String title) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Sticker.KEY_ID + "," +
                Sticker.KEY_TITLE + "," +
                Sticker.KEY_PATH +
                " FROM " + Sticker.TABLE + " WHERE " +
                Sticker.KEY_TITLE + "=\"" + title + "\" ORDER BY " + Sticker.KEY_TITLE +" COLLATE NOCASE";

        ArrayList<Sticker> stickers =new ArrayList();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Sticker item = new Sticker(cursor.getString(cursor.getColumnIndex(Sticker.KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(Sticker.KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(Sticker.KEY_PATH)));

                stickers.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return stickers;
    }

    public ArrayList<String> searchStickersWithTile(String title) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Sticker.KEY_ID + "," +
                Sticker.KEY_TITLE + "," +
                Sticker.KEY_PATH +
                " FROM " + Sticker.TABLE + " WHERE " +
                Sticker.KEY_TITLE + " LIKE \"%" + title + "%\" ORDER BY " + Sticker.KEY_TITLE +" COLLATE NOCASE";

        ArrayList<String> stickers =new ArrayList();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                if (!stickers.contains(cursor.getString(cursor.getColumnIndex(Sticker.KEY_TITLE))))
                stickers.add(cursor.getString(cursor.getColumnIndex(Sticker.KEY_TITLE)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return stickers;
    }

    /**Table for Recent Sticker**/
    public void insertToRecent(Sticker sticker) {
        // TODO Auto-generated method stub

        if (null != getRecentStickersById(sticker.id).id){
            deleteRecent(sticker.id);
        }
        ArrayList<Sticker> arrayList = new ArrayList<>();
        arrayList = getAllRecent();
        if (arrayList.size() > 20){
            deleteRecent(arrayList.get(20).id);
        }

        ContentValues values = new ContentValues();
        values.put(Sticker.KEY_ID, sticker.id);
        values.put(Sticker.KEY_TITLE, sticker.title);
        values.put(Sticker.KEY_PATH, sticker.filePath);
        Long tsLong = System.currentTimeMillis()/1000;
        values.put(TIME_STUMP, tsLong);

        // Inserting Row
        db_write.insert(RECENT_TABLE, null, values);
    }

    public Sticker getRecentStickersById(String Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Sticker.KEY_ID + "," +
                Sticker.KEY_TITLE + "," +
                Sticker.KEY_PATH + "," +
                TIME_STUMP +
                " FROM " + RECENT_TABLE
                + " WHERE " +
                Sticker.KEY_ID + "='" + Id + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        Sticker sticker = new Sticker();
        if (cursor.moveToFirst()) {
                sticker.id =cursor.getString(cursor.getColumnIndex(Sticker.KEY_ID));
                sticker.title =cursor.getString(cursor.getColumnIndex(Sticker.KEY_TITLE));
                sticker.filePath  =cursor.getString(cursor.getColumnIndex(Sticker.KEY_PATH));
        }

        cursor.close();
        return sticker;
    }

    public void deleteRecent(String id) {

        db_write.delete(RECENT_TABLE, Sticker.KEY_ID + "='" + id + "'", null);
    }

    public ArrayList<Sticker> getAllRecent() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Sticker.KEY_ID + "," +
                Sticker.KEY_TITLE + "," +
                Sticker.KEY_PATH + "," +
                TIME_STUMP +
                " FROM " + RECENT_TABLE
                + " ORDER BY " +
                TIME_STUMP + " DESC ";

        ArrayList<Sticker> stickers =new ArrayList();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Sticker item = new Sticker(cursor.getString(cursor.getColumnIndex(Sticker.KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(Sticker.KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(Sticker.KEY_PATH)));

                stickers.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return stickers;

    }

    public void closeDB(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.close();
    }


}
