package com.filters.shades;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

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

    public PictureList(Context context) {
        /*
        //Array of metadata to check on pictures
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        try {
            System.out.println(cursor.getString(1));
            String pictureLocation = cursor.getString(1);
            Picture picture = new Picture(Uri.parse(pictureLocation));
            mPictures.add(picture);
        } catch (NullPointerException ne) {

            Log.d(TAG, "Cannot find picture: " + ne.getMessage());
        }*/

        mPictures = new ArrayList<>();
        List<String> picturePaths = getCameraImages(context);

        for (int i = picturePaths.size() - 1; i > picturePaths.size() - 16; i--) {

            if (picturePaths.get(i) != null) {
                Picture picture = new Picture(Uri.parse(picturePaths.get(i)));
                mPictures.add(picture);
            }

        }
    }
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
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
