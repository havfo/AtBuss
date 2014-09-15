package net.fosstveit.atbuss.utils;

import net.fosstveit.atbuss.fragments.BusStopFragment;
import net.fosstveit.atbuss.fragments.BusStopInfoFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class BusStopViewPagerAdapter extends FragmentStatePagerAdapter {

	private final int PAGES = 2;
	
	public BusStopViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new BusStopFragment();
		case 1:
			return new BusStopInfoFragment();
		default:
			throw new IllegalArgumentException(
					"The item position should be less or equal to:" + PAGES);
		}
	}

	@Override
	public int getCount() {
		return PAGES;
	}
}
