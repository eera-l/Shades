package com.filters.shades;

import android.graphics.Bitmap;

/**
 * Created by Federica on 15/03/2018.
 */

public class OverFilter {

    private String mName;
    private Bitmap mImage;
    private int mId;

    public OverFilter(int mId, String mName, Bitmap mImage) {
        this.mName = mName;
        this.mImage = mImage;
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap mImage) {
        this.mImage = mImage;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }
}
