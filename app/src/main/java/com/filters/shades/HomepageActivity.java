package com.filters.shades;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.media.FaceDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomepageActivity extends AppCompatActivity{

    public ImageView mPictureView;
    private ImageView mOverlayFilterView;
    private Bitmap tempBitmap;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    public static final String EXTRA_PICTURE = "com.filters.shades.picture";
    public static final String EXTRA_ORIGIN = "com.filters.shades.origin";
    public static final String EXTRA_POSITION = "com.filters.shades.position";
    public static final String TAG = "com.filters.shades";
    private static final int MAX_FACES = 1;
    private ImageBitmap imageBitmap;
    private Bitmap finalBitmap;
    public static DatabaseConnector databaseConnector;

    public static Intent newIntent(Context packageContext, String picturePath) {
        Intent intent = new Intent(packageContext, HomepageActivity.class);
        intent.putExtra(EXTRA_PICTURE, picturePath);
        return intent;
    }
    public void setImage(int position) {
        imageBitmap = ImageBitmap.getInstance();
        finalBitmap = imageBitmap.getBitmap();

        if (position!=0){
            List<Filter> filters = FilterPack.getFilterPack(getBaseContext());
            finalBitmap = filters.get(position-1).processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true));
            mPictureView.setImageBitmap(finalBitmap);
        }else {
            mPictureView.setImageBitmap(finalBitmap);
        }

        publishToFaceBook(finalBitmap);
        tempBitmap = finalBitmap;
    }

    public void setOverlayFilter(Bitmap bitmap) {
        imageBitmap = ImageBitmap.getInstance();
        finalBitmap = imageBitmap.getBitmap();

        if (mOverlayFilterView.getDrawable() == null) {
            Bitmap result = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(finalBitmap, 0f, 0f, null);
            canvas.drawBitmap(bitmap, mOverlayFilterView.getX(), mOverlayFilterView.getY(), null);
            finalBitmap = result;
            publishToFaceBook(finalBitmap);
            mOverlayFilterView.setImageBitmap(finalBitmap);
        }
        else {
            mOverlayFilterView.setImageDrawable(null);
            finalBitmap = tempBitmap;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        overridePendingTransition(0, 0);

        mPictureView = findViewById(R.id.image_view_filters);
        mOverlayFilterView = (ImageView)findViewById(R.id.image_view_overlay_filters);
        imageBitmap = ImageBitmap.getInstance();
        finalBitmap = imageBitmap.getBitmap();

        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);

        if (position!=0){
            List<Filter> filters = FilterPack.getFilterPack(getBaseContext());
            finalBitmap = filters.get(position-1).processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true));
            mPictureView.setImageBitmap(finalBitmap);
        }else {
            mPictureView.setImageBitmap(finalBitmap);
        }
        tempBitmap = finalBitmap;

        CardView mCardView = findViewById(R.id.card_view_filters);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mCardView.getBackground().setAlpha(0);
        }
        else {
            mCardView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
        mCardView.setCardElevation(0);

        SeekBar seekBarBrightness = findViewById(R.id.seekbar_brightness);
        seekBarBrightness.setMax(200);
        seekBarBrightness.setProgress(100);

        // keeping contrast value b/w 1.0 - 3.0
        SeekBar seekBarContrast = findViewById(R.id.seekbar_contrast);
        seekBarContrast.setMax(20);
        seekBarContrast.setProgress(0);

        // keeping saturation value b/w 0.0 - 3.0
        SeekBar seekBarSaturation = findViewById(R.id.seekbar_saturation);
        seekBarSaturation.setMax(30);
        seekBarSaturation.setProgress(10);

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onBrightnessChanged(progress -100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //onEditCompleted();
            }
        });

        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 10;
                float floatVal = .10f * progress;
                onContrastChanged(floatVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //onEditCompleted();
            }
        });

        seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float floatVal = .10f * progress;
                onSaturationChanged(floatVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //onEditCompleted();
            }
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmapToStorage(finalBitmap);
                Toast toast = Toast.makeText(getApplicationContext(), "Image saved to Shades folder", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        publishToFaceBook(finalBitmap);

        connectDatabase();
    }

    public void publishToFaceBook(Bitmap bitmap){
        FacebookSdk.sdkInitialize(getApplicationContext());
        final ShareButton fbShareButton = findViewById(R.id.share_btn);
        final Bitmap bitmapFinal = bitmap;

        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmapFinal).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();

        fbShareButton.setShareContent(content);
    }

    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        finalBitmap = myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true));
        mPictureView.setImageBitmap(finalBitmap);
    }

    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        finalBitmap = myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true));
        mPictureView.setImageBitmap(finalBitmap);
    }

    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        finalBitmap = myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true));
        mPictureView.setImageBitmap(finalBitmap);
    }

    public void onEditCompleted() {
        Bitmap bitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalBitmap = myFilter.processFilter(bitmap);
    }

    public void saveBitmapToStorage(Bitmap bitmap){
        File pictureFile = getOutputMediaFile(1);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions");
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException fe) {
            Log.d(TAG, "File not found: " + fe.getMessage());
        } catch (IOException ioe) {
            Log.d(TAG, "Error accessing file: " + ioe.getMessage());
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, pictureFile.getName());
        values.put(MediaStore.Images.Media.DESCRIPTION, R.string.pictures_description);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, pictureFile.toString().toLowerCase(Locale.US).hashCode());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, pictureFile.getName().toLowerCase(Locale.US));
        values.put("_data", pictureFile.getAbsolutePath());

        ContentResolver cr = getContentResolver();
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
        if (type == 1) {

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    private void connectDatabase() {
        databaseConnector = new DatabaseConnector(this, "FilterDB.sqlite", null, 1);

        databaseConnector.queryData("CREATE TABLE IF NOT EXISTS FILTER (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image INTEGER);");

        //Bitmap bitmapFlower = returnBitmapFromDrawable(getResources().getDrawable(R.drawable.crown_flowers));
        //Bitmap bitmapSparkles = returnBitmapFromDrawable(getResources().getDrawable(R.drawable.sparkle));


        if (databaseConnector.getData("SELECT * FROM FILTER").getCount() < 2) {
            databaseConnector.insertData("Primavera", R.drawable.crown_flowers);
            databaseConnector.insertData("Desir", R.drawable.sparkle);
            System.out.println(databaseConnector.getData("SELECT * FROM FILTER;").getCount());
            System.out.println("---------------------------------------------------------------------");
        }
    }
}
