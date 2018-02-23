package com.filters.shades;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Federica on 19/02/2018.
 */

public class PictureListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private PictureList mPicturePaths;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_list, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.pictures_list_view);
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
            mPicturePaths.initialize(getActivity());
            mAdapter = new MainAdapter(mPicturePaths, getActivity());

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
