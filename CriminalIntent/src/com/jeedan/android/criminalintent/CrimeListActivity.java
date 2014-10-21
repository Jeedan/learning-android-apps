package com.jeedan.android.criminalintent;

import android.support.v4.app.Fragment;


public class CrimeListActivity extends SimpleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new CrimeListFragment();
	}
}
