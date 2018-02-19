package com.filters.shades;

import android.net.Uri;

/**
 * Created by Federica on 17/02/2018.
 */

public class Picture {

    private Uri mPictureUri;

    public Picture(Uri pictureUri) {
        mPictureUri = pictureUri;
    }

    public Uri getPictureUri() {
        return mPictureUri;
    }

    public void setPictureUri(Uri mPictureUri) {
        this.mPictureUri = mPictureUri;
    }
}
