package com.shacham.amit.corticaapp.tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.shacham.amit.corticaapp.FullscreenImageActivity;
import com.shacham.amit.corticaapp.R;
import com.shacham.amit.corticaapp.database.DatabaseImage;

import java.util.ArrayList;
import java.util.List;

public class TabFragment extends Fragment implements
        AdapterView.OnItemClickListener {

    protected SharedPreferences mSharedPrefs;

    protected GridView mGridView;
    protected TextView mNoImagesError;
    protected List<DatabaseImage> mImageList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_grid_layout, container, false);
        initViews(view);
        initListeners();

        mImageList = new ArrayList<>();

        return view;
    }

    private void initViews(View view) {
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mNoImagesError = (TextView) view.findViewById(R.id.no_images_error);
    }

    private void initListeners() {
        mGridView.setOnItemClickListener(this);
    }

    protected void insertImagesIntoGridView() {
        ImageAdapter adapter = new ImageAdapter(getActivity(), mImageList);
        mGridView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
        intent.putExtra(FullscreenImageActivity.KEY_IMAGE_DATE, mImageList.get(position).getImageDate());
        intent.putExtra(FullscreenImageActivity.KEY_IMAGE_URI, mImageList.get(position).getUri().toString());
        getActivity().startActivity(intent);
    }

    protected void showNoImagesError() {
        mNoImagesError.setVisibility(View.VISIBLE);
    }
}
