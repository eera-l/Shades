package com.filters.shades;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Federica on 21/02/2018.
 */

public class FilterListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Picture mPicture;
    private PictureList mPicturePaths;
    private int mMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_list, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.pictures_list_view);
        mPicture = new Picture(Uri.parse(getActivity().getIntent().getStringExtra(HomepageActivity.EXTRA_PICTURE)));
        mMode = getActivity().getIntent().getIntExtra(HomepageActivity.EXTRA_ORIGIN, 0);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        FragmentAsyncTask fragmentAsyncTask = new FragmentAsyncTask();
        fragmentAsyncTask.doInBackground(new Object[] {null});
        fragmentAsyncTask.onPostExecute(null);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class FragmentAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            mPicturePaths = new PictureList();
            List<Picture> pictures = new ArrayList<>();
            for (int i = 0; i < 21; i++) {
                pictures.add(mPicture);
            }
            mPicturePaths.setmPictures(pictures);
            mAdapter = new FilterAdapter(mPicturePaths, getActivity(), mMode);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
