package com.giljam.daniel.averageandstatisticaldispersion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Adds a fragment to the list of fragments for the adapter to adapt
     * for the ViewPager to display.
     * @param fragment The fragment to be added.
     */
    void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    /**
     * Return the fragment associated with a specified position.
     * @param position Integer representing fragment position.
     */
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return mFragmentList.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        // Show the total of as many pages as the mFragmentList contains fragments.
        return mFragmentList.size();
    }
}
