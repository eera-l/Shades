package com.filters.shades;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_list, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.pictures_list_view);
        mPicture = new Picture(Uri.parse(getActivity().getIntent().getStringExtra(HomepageActivity.EXTRA_PICTURE)));
        int mode = getActivity().getIntent().getIntExtra(HomepageActivity.EXTRA_ORIGIN, 0);
        mPicturePaths = new PictureList();
        List<Picture> pictures = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            pictures.add(mPicture);
        }
        mPicturePaths.setmPictures(pictures);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FilterAdapter(mPicturePaths, getActivity(), mode);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        //getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }
}
