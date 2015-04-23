package com.project.furnishyourhome.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.project.furnishyourhome.R;
import com.project.furnishyourhome.fragments.ContainerMapFragment;
import com.project.furnishyourhome.fragments.ContainerMyFurnitureFragment;
import com.project.furnishyourhome.fragments.ContainerMyRoomFragment;
import com.project.furnishyourhome.fragments.MapFragment;
import com.project.furnishyourhome.fragments.MyRoomFragment;

//TODO: check FragmentStatePagerAdapter vs. FragmentPagerAdapter
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    Context context;
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int numbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    FragmentManager fragmentManager;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(Context context, FragmentManager fm,CharSequence mTitles[], int mNumbOfTabs) {
        super(fm);

        this.context = context;
        this.fragmentManager = fm;
        this.Titles = mTitles;
        this.numbOfTabs = mNumbOfTabs;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Log.d(TAG, "new ContainerMyRoomFragment()");
                return new ContainerMyRoomFragment();
            case 1:
                if(this.numbOfTabs == 3) {
                    Log.d(TAG, "new ContainerMyFurnitureFragment()");
                    return new ContainerMyFurnitureFragment();
                } else {
                    Log.d(TAG, "new ContainerMapFragment()");
                    return new ContainerMapFragment();
                }
            default:
                Log.d(TAG, "new ContainerMapFragment()");
                return new ContainerMapFragment();
        }
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return numbOfTabs;
    }
}