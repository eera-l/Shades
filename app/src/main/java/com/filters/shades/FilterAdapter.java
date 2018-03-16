package com.filters.shades;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Federica on 20/02/2018.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private List<Picture> mPictures;
    private List<OverFilter> mFilters;
    private Context mContext;
    private Activity mActivity;
    private ImageBitmap imageBitmap;
    Bitmap bitmap;

    public FilterAdapter(PictureList mPicturePaths, Activity context, int mode) {
        mPictures = new ArrayList<>();
        mFilters = new ArrayList<>();
        mPictures = mPicturePaths.getPictures();
        mContext = context;
        int mMode = mode;
        mActivity = context;
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter, parent, false);

        FilterAdapter.ViewHolder viewHolder = new FilterAdapter.ViewHolder(v, mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterAdapter.ViewHolder holder, int position) {
        imageBitmap = ImageBitmap.getInstance();
        bitmap = imageBitmap.getBitmap();

        List<Filter> filters = FilterPack.getFilterPack(mContext);
        Cursor cursor = ((HomepageActivity)mActivity).databaseConnector.getData("SELECT * FROM FILTER");

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int imgId = (int)cursor.getLong(2);

                mFilters.add(new OverFilter(id, name, imgId));

            } while (cursor.moveToNext());
        }


        if (bitmap.getWidth() >= bitmap.getHeight()) {
            bitmap = scaleBitmapKeepingRatio(bitmap, 200, 150);
        } else {
            bitmap = scaleBitmapKeepingRatio(bitmap, 150, 200);
        }

        if (position == 0){
            holder.mFilterThumbnail.setImageBitmap(bitmap);
            holder.mText.setText(R.string.original_image);
        } else if (position == 17) {
            bitmap = returnBitmapFromDrawable(mActivity.getResources().getDrawable(mFilters.get(0).getImage()));
            holder.mFilterThumbnail.setImageBitmap(bitmap);
            holder.mText.setText(mFilters.get(0).getName());
        } else if (position == 18) {
            bitmap = returnBitmapFromDrawable(mActivity.getResources().getDrawable(mFilters.get(1).getImage()));
            holder.mFilterThumbnail.setImageBitmap(bitmap);
            holder.mText.setText(mFilters.get(1).getName());
        } else{
            holder.mFilterThumbnail.setImageBitmap(filters.get(position-1).processFilter(bitmap.copy(Bitmap.Config.ARGB_8888, true)));
            holder.mText.setText(filters.get(position-1).getName());
        }

        holder.mFilterThumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.mFilterThumbnail.setCropToPadding(true);
        holder.mFilterThumbnail.setAdjustViewBounds(true);
        holder.mPicture = mPictures.get(position);

        if (position == 17)
            holder.mOverFilter = mFilters.get(0);
        else if (position == 18) {
            holder.mOverFilter = mFilters.get(1);
        }
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
        public OverFilter mOverFilter;
        private TextView mText;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            mFilterThumbnail = itemView.findViewById(R.id.filter_thumbnail);
            mText = itemView.findViewById(R.id.filter_text);

            mFilterThumbnail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (getPosition() == 17 || getPosition() == 18) {
                        ((HomepageActivity)mActivity).setOverlayFilter(returnBitmapFromDrawable(mActivity.getResources().getDrawable(mOverFilter.getImage())));
                    }else {
                        ((HomepageActivity) mActivity).setImage(getPosition());
                    }
                }
            });
        }
    }

    //Create a Bitmap from Drawable
    private Bitmap returnBitmapFromDrawable(Drawable drawable)  {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final int width = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().height() : drawable.getIntrinsicHeight();

        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width,
                height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
