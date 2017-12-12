package com.allyouneedapp.palpicandroid.models;

/**
 * Created by Mostofa on 10/28/2016.
 */

public class FilterData {
    public int filterType;
    public String filterTitle;
    public FilterData(int type, String title) {
        this.filterTitle = title;
        this.filterType = type;
    }
}
