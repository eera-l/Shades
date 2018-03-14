package com.filters.shades;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

import static com.filters.shades.HomepageActivity.TAG;

/**
 * Created by Federica on 19/02/2018.
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Picture> mPictures;
    private Context mContext;

    public MainAdapter(PictureList mPicturePaths, Context context) {
        mPictures = new ArrayList<>();
        mPictures = mPicturePaths.getPictures();
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_picture, parent, false);

        ViewHolder viewHolder = new ViewHolder(v, mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(mPictures.get(position).getPictureUri().toString());
        } catch (Exception ioe) {
            Log.d(TAG, "Cannot open file: " + ioe.getMessage());
        }

        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("samsung")){
            bitmap = rotate(bitmap, 90);
        }
        holder.mImageView.setImageBitmap(scaleBitmapKeepingRatio(bitmap, 150, 150));
        holder.mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.mImageView.setCropToPadding(true);
        holder.mImageView.setAdjustViewBounds(true);
        holder.mPicture = mPictures.get(position);
    }

    private Bitmap scaleBitmapKeepingRatio(Bitmap bitmap, int destWidth, int destHeight) {
        Bitmap background = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();
        Canvas canvas = new Canvas(background);

        //Values (150 and 180) that can be tweaked to adjust padding
        //and zooming of picture thumbnails
        float scaleX = (float) 150 / originalWidth;
        float scaleY = (float) 180 / originalHeight;

        float xTranslation = 0.0f;
        float yTranslation = 0.0f;
        float scale;

        if (scaleX < scaleY) { // Scale on X, translate on Y
            scale = scaleX;
            yTranslation = (destHeight - originalHeight * scale) / 2.0f;
        } else { // Scale on Y, translate on X
            scale = scaleY;
            xTranslation = (destWidth - originalWidth * scale) / 2.0f;
        }

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, transformation, paint);
        return background;
    }


    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public Picture mPicture;
        private Context mContext;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            mImageView = (ImageView)itemView.findViewById(R.id.picture_thumbnail);
            mImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent =  HomepageActivity.newIntent(mContext, mPicture.getPictureUri().toString(), 2);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    private Bitmap rotate(Bitmap source, float degrees){
        float centerX = source.getWidth() / 2;
        float centerY = source.getHeight() / 2;

        Matrix matrix = new Matrix();
        matrix.postRotate((float) degrees, centerX, centerY);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
