package com.jeedan.android.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class CrimeListFragment extends ListFragment {
	private static final String TAG = "CrimeListFragment";
	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisible;
	
	private Button mAddNewCrimeButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		
		
		getActivity().setTitle(R.string.crimes_title);
		
		
		Log.d(TAG, "started onCreate()");
		// get crimes
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		Log.e(TAG, mCrimes.toString());
		
		// set up the adapter to display list view
		//ArrayAdapter<Crime> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mCrimes);
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter); // let the listfragment know that adapter will be the 1 we get from "getListAdapter()
		setRetainInstance(true);
		mSubtitleVisible = true;
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		//View v = super.onCreateView(inflater, parent, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_list_view_crimes, parent, false);

		if(getListAdapter().isEmpty()){
			//v = inflater.inflate(R.layout.fragment_list_empty, parent, false);
			mAddNewCrimeButton = (Button)v.findViewById(R.id.add_crime_button);
			mAddNewCrimeButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addNewCrime();
				}
			});
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if(mSubtitleVisible){
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}	
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
		// start a crime activity
		Intent crimeActivityIntent = new Intent(getActivity(), CrimePagerActivity.class);
		crimeActivityIntent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getID());
		startActivity(crimeActivityIntent);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if(mSubtitleVisible && showSubtitle != null){
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
	}
	
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.menu_item_new_crime:
			addNewCrime();
			return true;
		case R.id.menu_item_show_subtitle:
			if(getActivity().getActionBar().getSubtitle() == null){
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
				mSubtitleVisible = true;
				item.setTitle(R.string.hide_subtitle);
			}else {
				getActivity().getActionBar().setSubtitle(null);
				mSubtitleVisible = false;
				item.setTitle(R.string.show_subtitle);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void addNewCrime(){
		Crime crime = new Crime();
		CrimeLab.get(getActivity()).addCrime(crime);	
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getID());
		startActivityForResult(i,0);
	}
	
	private class CrimeAdapter extends ArrayAdapter<Crime>{
		public CrimeAdapter(ArrayList<Crime> crimes ){
			super(getActivity(), 0, crimes);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			// if we weren't given a view, inflate one
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
			
			// Configure the view for this Crime 
			Crime c = getItem(position);
			
			TextView titleTextView = (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
			titleTextView.setText(c.getTitle());

			TextView dateTextView = (TextView)convertView.findViewById(R.id.crime_list_item_titleDateTextView);

			DateFormat simpleFormat = new SimpleDateFormat("EEEE, MMM, dd, yyyy.", Locale.US);
			String formmattedDate = simpleFormat.format(c.getDate());  
			dateTextView.setText(formmattedDate);
			
			CheckBox solvedCheckBox = (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.isSolved());
			return convertView;
		}
	}
}
/*
	private class MyAsyncTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			Log.e(TAG, "do in background??");
			mCrimes = CrimeLab.get(getActivity()).getCrimes();
			
			return null;
		}
		
		protected void onPostExecuting(String result){
			Log.e(TAG, "Not sure what to do here: ");
			
		}
	}*/
