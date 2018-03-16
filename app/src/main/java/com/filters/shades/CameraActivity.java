package com.filters.shades;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static android.content.ContentValues.TAG;
import static com.filters.shades.CameraPreview.getCameraInstance;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity {

    private Camera mCamera;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int GET_FROM_GALLERY = 2;
    public static final String DIALOG_NAME = "com.filters.shades.URLDialogFragment";
    private ImageBitmap imageBitmap = ImageBitmap.getInstance();
    private Bitmap bitmap;
    private String manufacturer = Build.MANUFACTURER;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);

                imageBitmap.createBitmap(data);
                bitmap = imageBitmap.getBitmap();

                if (manufacturer.equalsIgnoreCase("samsung")) {
                    ExifInterface exif = new ExifInterface(pictureFile.toString());
                    if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                        bitmap = imageBitmap.rotate(bitmap, 90);
                    } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                        bitmap = imageBitmap.rotate(bitmap, 270);
                    } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                        bitmap = imageBitmap.rotate(bitmap, 180);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")) {
                        bitmap = imageBitmap.rotate(bitmap, 90);
                        bitmap = imageBitmap.flipBitmapVertically(bitmap);
                    }
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();

            } catch (FileNotFoundException fe) {
                Log.d(TAG, "File not found: " + fe.getMessage());
            } catch (IOException ioe) {
                Log.d(TAG, "Error accessing file: " + ioe.getMessage());
            }

            //add metadata to pictures so that they are automatically displayed
            //on the phone's gallery
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, pictureFile.getName());
            values.put(MediaStore.Images.Media.DESCRIPTION, R.string.pictures_description);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, pictureFile.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, pictureFile.getName().toLowerCase(Locale.US));
            values.put("_data", pictureFile.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = HomepageActivity.newIntent(CameraActivity.this, pictureFile.getAbsolutePath());
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance(this);

        //Set camera's parameters: orientation and size of pictures
        mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> imageSizes = mCamera.getParameters().getSupportedPictureSizes();
        //biggest size
        Camera.Size pictureSize = imageSizes.get(0);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        mCamera.setParameters(parameters);

        // Create our Preview view and set it as the content of our activity.
        CameraPreview mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button mButtonCapture = findViewById(R.id.button_capture);
        mButtonCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        Button mButtonUpload = findViewById(R.id.button_upload);
        mButtonUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        Button mButtonFromURL = findViewById(R.id.button_from_url);
        mButtonFromURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URLDialogFragment urlDialogFragment = new URLDialogFragment();
                urlDialogFragment.show(getFragmentManager(), DIALOG_NAME);
            }
        });

        CardView mCardView = findViewById(R.id.card_view_pictures);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mCardView.getBackground().setAlpha(0);
        }
        else {
            mCardView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
        mCardView.setCardElevation(0);
    }

    private static File getOutputMediaFile(int type) {

        //Get folder on phone's storage
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Shades");

        //Create folder if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Shades", "Failed to create directory");
                return null;
            }
        }

        //Save picture to file in the chosen folder
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri pictureFile = data.getData();
            String pictureToShowPath = pictureFile.toString();
            Uri selectedImage = Uri.parse(pictureToShowPath);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (manufacturer.equalsIgnoreCase("samsung")){
                imageBitmap.rotate(bitmap,90);
            }else {
                imageBitmap.setBitmap(bitmap);
            }
            Intent intent = HomepageActivity.newIntent(CameraActivity.this, pictureToShowPath);
            startActivity(intent);
        }
    }

    public void onUserSelectValue(String value) {
        Glide
                .with(this)
                .load(value)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(800,800) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        //Get folder on phone's storage
                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Shades");

                        //Create folder if it does not exist
                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                Log.d("Shades", "Failed to create directory");
                            }
                        }
                        //Save picture to file in the chosen folder
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        File mediaFile;
                        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(mediaFile);
                            resource.compress(Bitmap.CompressFormat.PNG, 70, fileOutputStream);
                            fileOutputStream.close();

                                //add metadata to pictures so that they are automatically displayed
                                //on the phone's gallery
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, mediaFile.getName());
                                values.put(MediaStore.Images.Media.DESCRIPTION, R.string.pictures_description);
                                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
                                values.put(MediaStore.Images.ImageColumns.BUCKET_ID, mediaFile.toString().toLowerCase(Locale.US).hashCode());
                                values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, mediaFile.getName().toLowerCase(Locale.US));
                                values.put("_data", mediaFile.getAbsolutePath());

                                ContentResolver cr = getContentResolver();
                                cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intent = HomepageActivity.newIntent(getApplicationContext(), mediaFile.getPath());
                                startActivity(intent);
                        } catch (IOException ioe) {

                        }
                    }
                });
    }
}
