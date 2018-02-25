package com.filters.shades;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;


public class HomepageActivity extends AppCompatActivity {

    private ImageView mPictureView;
    private CardView mCardView;

    public static final String EXTRA_PICTURE = "com.filters.shades.picture";
    public static final String EXTRA_ORIGIN = "com.filters.shades.origin";
    public static final String TAG = "com.filters.shades";
    private static final int MAX_FACES = 1;

    public static Intent newIntent(Context packageContext, String picturePath, int uploaded) {

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
        int up = getIntent().getIntExtra(EXTRA_ORIGIN, 0);
        Bitmap bitmap;
        try {
            if (up == 0) {
                bitmap = BitmapFactory.decodeFile(selectedImage.getPath());
                bitmap = flipBitmapHorizontally(bitmap);
            } else if (up == 1) {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } else {
                bitmap = BitmapFactory.decodeFile(selectedImage.getPath());
            }
            mPictureView.setImageBitmap(bitmap);
        } catch (Exception ioe) {
            Log.d(TAG, "Error uploading the picture: " + ioe.getMessage());
        }

        mCardView = (CardView)findViewById(R.id.card_view_filters);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mCardView.getBackground().setAlpha(0);
        }
        else {
            mCardView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
        mCardView.setCardElevation(0);

    }

    private Bitmap flipBitmapHorizontally(Bitmap source) {
        float centerX = source.getWidth() / 2;
        float centerY = source.getHeight() / 2;

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, centerX, centerY);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void prepareBitmapForRecognition(Bitmap bitmap) {
        bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        int faceWidth = bitmap.getWidth();
        int faceHeight = bitmap.getHeight();

        setFace(bitmap, faceWidth, faceHeight);
    }

    private void setFace(Bitmap bitmap, int faceWidth, int faceHeight) {
            FaceDetector fd;
            FaceDetector.Face [] faces = new FaceDetector.Face[MAX_FACES];
            PointF midpoint = new PointF();
            int [] fpx = null;
            int [] fpy = null;
            int count = 0;

            try {
                fd = new FaceDetector(faceWidth, faceHeight, MAX_FACES);
                count = fd.findFaces(bitmap, faces);
            } catch (Exception e) {
                Log.e(TAG, "setFace(): " + e.toString());
                return;
            }

            // check if we detect any faces
            if (count > 0) {
                fpx = new int[count];
                fpy = new int[count];

                for (int i = 0; i < count; i++) {
                    try {
                        faces[i].getMidPoint(midpoint);

                        fpx[i] = (int)midpoint.x;
                        fpy[i] = (int)midpoint.y;
                    } catch (Exception e) {
                        Log.e(TAG, "setFace(): face " + i + ": " + e.toString());
                    }
                }
            }

            //mPictureView.setDisplayPoints(fpx, fpy, count, 0);
        }
}
