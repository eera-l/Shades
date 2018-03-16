package com.filters.shades;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.filters.shades.HomepageActivity.TAG;

/**
 * Created by Federica on 19/02/2018.
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Picture> mPictures;
    private Context mContext;
    private Bitmap bitmap;

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
        bitmap = null;
        int imgPosition = position;

        Glide.with(mContext)
                .load(mPictures.get(position).getPictureUri().toString())
                .override(150, 150)
                .into(holder.mImageView);


        holder.mPicture = mPictures.get(position);
    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public Picture mPicture;
        private Context mContext;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            mImageView = itemView.findViewById(R.id.picture_thumbnail);
            mImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    bitmap = BitmapFactory.decodeFile(mPicture.getPictureUri().toString());
                    ImageBitmap imageBitmap = ImageBitmap.getInstance();
                    String manufacturer = Build.MANUFACTURER;
                    if (manufacturer.equalsIgnoreCase("samsung")) {
                        imageBitmap.rotate(bitmap, 90);
                    } else {
                        imageBitmap.setBitmap(bitmap);
                    }
                    Intent intent = HomepageActivity.newIntent(mContext, mPicture.getPictureUri().toString());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
