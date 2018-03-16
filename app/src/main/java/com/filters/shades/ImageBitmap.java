package com.filters.shades;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;

/**
 * Created by felic on 14/03/2018.
 */

public class ImageBitmap {

    private static ImageBitmap INSTANCE = null;

    private Bitmap bitmap;
    private String filePath;

    private ImageBitmap(){};

    public static ImageBitmap getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ImageBitmap();
        }
        return INSTANCE;
    }

    public ImageBitmap(String filePath) {
        this.filePath = filePath;
    }

    public void setBitmap(Bitmap newBitmap){
        bitmap = newBitmap;
    }

    public Bitmap createBitmapFromFile(String filePath){
        bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;
    }


    public Bitmap createBitmap(byte[] data){
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Bitmap rotate(Bitmap source, float degrees){
        float centerX = source.getWidth() / 2;
        float centerY = source.getHeight() / 2;

        Matrix matrix = new Matrix();
        matrix.postRotate((float) degrees, centerX, centerY);
        bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return bitmap;
    }

    public Bitmap flipBitmapVertically(Bitmap source) {
        float centerX = source.getWidth() / 2;
        float centerY = source.getHeight() / 2;

        Matrix matrix = new Matrix();
        matrix.postScale(1, -1, centerX, centerY);

        bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return bitmap;
    }
}
