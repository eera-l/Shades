package com.filters.shades;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


public class HomepageActivity extends AppCompatActivity {

    private ImageView mPictureView;

    public static final String EXTRA_PICTURE = "com.filters.shades.picture";
    public static final String EXTRA_ORIGIN = "com.filters.shades.origin";
    public static final String TAG = "com.filters.shades";

    public static Intent newIntent(Context packageContext, String picturePath, boolean uploaded) {

        Intent intent = new Intent(packageContext, HomepageActivity.class);
        intent.putExtra(EXTRA_PICTURE, picturePath);
        intent.putExtra(EXTRA_ORIGIN, uploaded);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mPictureView = (ImageView)findViewById(R.id.image_view_picture);
        String pictureToShowPath = getIntent().getStringExtra(EXTRA_PICTURE);
        Uri selectedImage = Uri.parse(pictureToShowPath);
        boolean up = getIntent().getBooleanExtra(EXTRA_ORIGIN, false);
        Bitmap bitmap;
        try {
            if (!up) {
                bitmap = BitmapFactory.decodeFile(selectedImage.getPath());
                bitmap = flipBitmapHorizontally(bitmap);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            }
            mPictureView.setImageBitmap(bitmap);
        } catch (Exception ioe) {
            Log.d(TAG, "Error uploading the picture: " + ioe.getMessage());
        }
    }

    private Bitmap flipBitmapHorizontally(Bitmap source) {
        float centerX = source.getWidth() / 2;
        float centerY = source.getHeight() / 2;

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, centerX, centerY);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
