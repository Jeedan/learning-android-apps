package com.jeedan.android.tvshowtracker;

import android.support.v4.app.Fragment;

public class TrackedShowsActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new TrackedShowsFragment();
	}

}
