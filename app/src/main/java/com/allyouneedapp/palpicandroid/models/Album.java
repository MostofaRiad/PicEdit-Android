package com.allyouneedapp.palpicandroid.models;

/**
 * Created by Mostofa on 10/26/2016.
 */

public class Album {
    public String id;
    public String name;
    public String filePaht;
    public long coverID;
    public int count;
    public String thumbFilePath;

    public Album(){
        this.id = "";
        this.name = "";
        this.filePaht = "";
        this.coverID = 0;
        this.count = 1;
        this.thumbFilePath = "";
    }
}
