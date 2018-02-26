package com.filters.shades;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.filters.shades.HomepageActivity.TAG;

/**
 * Created by Federica on 20/02/2018.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private List<Picture> mPictures;
    private Context mContext;
    private int mMode;

    public FilterAdapter(PictureList mPicturePaths, Context context, int mode) {
        mPictures = new ArrayList<>();
        mPictures = mPicturePaths.getPictures();
        mContext = context;
        mMode = mode;
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter, parent, false);

        FilterAdapter.ViewHolder viewHolder = new FilterAdapter.ViewHolder(v, mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterAdapter.ViewHolder holder, int position) {
        Bitmap bitmap = null;
        try {
            if (mMode == 0) {
                bitmap = BitmapFactory.decodeFile(mPictures.get(position).getPictureUri().toString());
                bitmap = flipBitmapHorizontally(bitmap);
            } else if (mMode == 1) {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mPictures.get(position).getPictureUri());
            } else if (mMode == 2) {
                bitmap = BitmapFactory.decodeFile(mPictures.get(position).getPictureUri().toString());
            } else {
                DownloaderTask downloaderTask = new DownloaderTask();
                bitmap = (Bitmap)downloaderTask.doInBackground(new Object[] {position});
            }
        } catch (Exception ioe) {
            Log.d(TAG, "Cannot open file: " + ioe.getMessage());
        }

        List<Filter> filters = FilterPack.getFilterPack(mContext);

        if (bitmap.getWidth() >= bitmap.getHeight()) {
            bitmap = scaleBitmapKeepingRatio(bitmap, 200, 150);
        } else {
            bitmap = scaleBitmapKeepingRatio(bitmap, 150, 200);
        }

        if (position==0){
            holder.mFilterThumbnail.setImageBitmap(bitmap);
            holder.mText.setText(R.string.original_image);
        }else{
            holder.mFilterThumbnail.setImageBitmap(filters.get(position-1).processFilter(bitmap.copy(Bitmap.Config.ARGB_8888, true)));
            holder.mText.setText(filters.get(position-1).getName());
        }

        holder.mFilterThumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.mFilterThumbnail.setCropToPadding(true);
        holder.mFilterThumbnail.setAdjustViewBounds(true);
        holder.mPicture = mPictures.get(position);
    }

    private Bitmap scaleBitmapKeepingRatio(Bitmap bitmap, int destWidth, int destHeight) {
        Bitmap background = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();
        Canvas canvas = new Canvas(background);

        //Values (150 and 180) that can be tweaked to adjust padding
        //and zooming of picture thumbnails
        float scaleX = (float) destWidth / originalWidth;
        float scaleY = (float) destHeight / originalHeight;

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

        public ImageView mFilterThumbnail;
        public Picture mPicture;
        private Context mContext;
        private TextView mText;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            mFilterThumbnail = itemView.findViewById(R.id.filter_thumbnail);
            mText = itemView.findViewById(R.id.filter_text);
            ImageView mImageView = itemView.findViewById(R.id.image_view_filters);
            mFilterThumbnail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    Intent intent =  HomepageActivity.filterIntent(mContext, mPicture.getPictureUri().toString(), 2, position);
                    mContext.startActivity(intent);
                }
            });
        }

    }
    private Bitmap flipBitmapHorizontally(Bitmap source) {
        float centerX = source.getWidth() / 2;
        float centerY = source.getHeight() / 2;

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, centerX, centerY);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private class DownloaderTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(mPictures.get((int) params[0]).getPictureUri().toString());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException me) {
                Log.e(TAG, "Malformed URL exception: " + me.getMessage());
            } catch (IOException ioe) {
                Log.e(TAG, "IOException: " + ioe.getMessage());
            }
            return bitmap;
        }
    }
}
