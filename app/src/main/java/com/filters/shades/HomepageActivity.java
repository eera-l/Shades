package com.filters.shades;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HomepageActivity extends AppCompatActivity {

    private ImageView mPictureView;

    public static final String EXTRA_PICTURE = "com.filters.shades.picture";
    public static final String TAG = "com.filters.shades";

    public static Intent newIntent(Context packageContext, String picturePath) {

        Intent intent = new Intent(packageContext, HomepageActivity.class);
        intent.putExtra(EXTRA_PICTURE, picturePath);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mPictureView = (ImageView)findViewById(R.id.image_view_picture);
        //flip ImageView horizontally to avoid mirroring picture
        mPictureView.setScaleX(-1);
        String pictureToShowPath = getIntent().getStringExtra(EXTRA_PICTURE);
        Uri selectedImage = Uri.parse(pictureToShowPath);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(selectedImage.getPath());
            mPictureView.setImageBitmap(bitmap);
        } /*catch (FileNotFoundException fe) {
            Log.d(TAG, "Error uploading the picture: " + fe.getMessage());
        }*/ catch (Exception ioe) {
            Log.d(TAG, "Error uploading the picture: " + ioe.getMessage());
        }
    }

}
