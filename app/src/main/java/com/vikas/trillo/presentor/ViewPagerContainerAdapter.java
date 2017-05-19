package com.vikas.trillo.presentor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vikas.trillo.view.InfoFragment;
import com.vikas.trillo.view.RecordFragment;

/**
 * Created by OFFICE on 5/17/2017.
 */

public class ViewPagerContainerAdapter extends FragmentPagerAdapter {

    private final String[] titles = {"Record","Info"};

    public ViewPagerContainerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = RecordFragment.getInstance();
                break;
            case 1:
                fragment = InfoFragment.getInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
