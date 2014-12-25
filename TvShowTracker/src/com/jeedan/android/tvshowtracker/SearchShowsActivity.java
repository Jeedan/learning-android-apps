package com.jeedan.android.tvshowtracker;

import android.support.v4.app.Fragment;

public class SearchShowsActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new SearchShowsListFragment();
	}

}
