package com.jeedan.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class CrimePagerActivity extends FragmentActivity {
	
	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);
		
		FragmentManager fm = getSupportFragmentManager();
		mCrimes = CrimeLab.get(this).getCrimes();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mCrimes.size();
			}
			
			@Override
			public Fragment getItem(int pos) {
				// TODO Auto-generated method stub
				Crime crime = mCrimes.get(pos);
				
				return CrimeFragment.newInstance(crime.getID());
			}
		});
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				Crime crime = mCrimes.get(pos);
				if(crime.getTitle() != null){
					setTitle(crime.getTitle());
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		for(int i = 0; i < mCrimes.size(); i++){
			if(mCrimes.get(i).getID().equals(crimeId)){
				mViewPager.setCurrentItem(i);
			break;
			}
		}
	}
}