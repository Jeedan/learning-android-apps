package com.jeedan.android.tvshowtracker;

import android.support.v4.app.Fragment;

public class MyShowsActivity extends FragmentActivityWithNavDrawer {
	@Override
	protected Fragment createFragment() {
		return new MyShowsListFragment();
	}

}
