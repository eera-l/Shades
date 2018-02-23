package com.filters.shades;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Federica on 18/02/2018.
 */

public class PictureList {

    private List<Picture> mPictures;
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    public PictureList() {
        mPictures = new ArrayList<>();
    }

    public void initialize(Context context) {
        List<String> picturePaths = getCameraImages(context);
        int length = picturePaths.size();
        int end;
        if (length >= 15) {
            end = length - 15;
        } else {
            end = 0;
        }

        if (length > 0) {
            for (int i = picturePaths.size() - 1; i >= end; i--) {
                if (picturePaths.get(i) != null) {
                    Picture picture = new Picture(Uri.parse(picturePaths.get(i)));
                    mPictures.add(picture);
                }
            }
        }
    }
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public void setmPictures(List<Picture> mPictures) {
        this.mPictures = mPictures;
    }

    public static List<String> getCameraImages(Context context) {
        final String[] projection = { MediaStore.Images.Media.DATA };
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        ArrayList<String> result = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public List<Picture> getPictures() {
        return mPictures;
    }

}
