package net.fosstveit.atbuss.utils;

import net.fosstveit.atbuss.AtBussFragment;
import net.fosstveit.atbuss.MostUsedFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	private final int PAGES = 2;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new AtBussFragment();
		case 1:
			return new MostUsedFragment();
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