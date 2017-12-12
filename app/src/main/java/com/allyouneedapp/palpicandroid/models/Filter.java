package com.allyouneedapp.palpicandroid.models;

import com.allyouneedapp.palpicandroid.FilterActivity;
import com.allyouneedapp.palpicandroid.utils.FilterUtil;

/**
 * Created by Mostofa on 11/15/2016.
 */

public class Filter {
    String title;
    FilterUtil.FilterType type;

    public Filter () {

    }
    public Filter (String title, FilterUtil.FilterType type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public FilterUtil.FilterType getType() {
        return type;
    }
}
