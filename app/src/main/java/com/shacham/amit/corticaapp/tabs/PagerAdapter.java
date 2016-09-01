package com.shacham.amit.corticaapp.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.shacham.amit.corticaapp.tabs.FacebookImagesFragment;
import com.shacham.amit.corticaapp.tabs.LocalImagesFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mNumberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LocalImagesFragment();
            case 1:
                return new FacebookImagesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}