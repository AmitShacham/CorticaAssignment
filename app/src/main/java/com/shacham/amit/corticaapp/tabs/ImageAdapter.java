package com.shacham.amit.corticaapp.tabs;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.shacham.amit.corticaapp.R;
import com.shacham.amit.corticaapp.database.DatabaseImage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<DatabaseImage> mImagesUriList;

    public ImageAdapter(Context context, List<DatabaseImage> imageUriList) {
        mContext = context;
        mImagesUriList = imageUriList;
    }

    public int getCount() {
        return mImagesUriList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri uri = mImagesUriList.get(position).getUri();
        Picasso.with(mContext)
                .load(uri)
                .placeholder(R.mipmap.ic_launcher)
                .noFade()
                .resize(150, 150)
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}