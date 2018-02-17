package com.filters.shades;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.filters.shades.CameraPreview.getCameraInstance;

public class CameraActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Button mButton;
    public static final int MEDIA_TYPE_IMAGE = 1;
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
                fileOutputStream.write(data);
                fileOutputStream.close();
                Intent intent = HomepageActivity.newIntent(CameraActivity.this, pictureFile.getPath());

                startActivity(intent);
            } catch (FileNotFoundException fe) {
                Log.d(TAG, "File not found: " + fe.getMessage());
            } catch (IOException ioe) {
                Log.d(TAG, "Error accessing file: " + ioe.getMessage());
            }
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
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


        mButton = (Button)findViewById(R.id.button_capture);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    private static File getOutputMediaFile(int type) {

        //Get folder on phone's gallery
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
}
