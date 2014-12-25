package com.jeedan.android.tvshowtracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarShowsListFragment extends ListFragment {
	private static final String TAG = "CalendarShowsListFragment";
	private static final String EXTRA_TEST = "testString";

	private ArrayList<TVShow> mTrackedShows;

	private ArrayList<TVShow> mTodaysShows;
	private String testString;
	
	public static CalendarShowsListFragment newInstance(String test){
		CalendarShowsListFragment calF = new CalendarShowsListFragment();
		
		// supply input
		Bundle args = new Bundle();
		args.putString(EXTRA_TEST, test);
		calF.setArguments(args);
		return calF;
	}
	
	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.app_calendar_title);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setHasOptionsMenu(true);
		}
		// get arguments passed by from starting a new fragment
		if(getArguments() != null){
			testString = getArguments().getString(EXTRA_TEST, "getArguments() did not work");	
			Log.d(TAG, "getArguments: " + testString);
		}

		mTodaysShows = new ArrayList<TVShow>();
		
		mTrackedShows = ShowDatabase.getInstance(getActivity()).getTrackedShows(); // set the list to null so we do not crash at the start		
		

		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true); // display the home icon
		getActivity().getActionBar().setHomeButtonEnabled(true);
		setRetainInstance(true);// retain this fragment upon rotation
		setupAdapter();
	}

	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_tv_show_list, parent,
				false);

		for(int i = 0; i < mTrackedShows.size(); i++){
			if(mTrackedShows.get(i).getNextReleaseDate() != null){
				compareDate(mTrackedShows.get(i), mTrackedShows.get(i).getNextReleaseDate());	
			}
		}
		
		((CalendarShowAddapter) getListAdapter()).notifyDataSetChanged();
		return v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		ShowDatabase.getInstance(getActivity()).saveTrackedShows(mTrackedShows);
//		((CalendarShowAddapter) getListAdapter()).notifyDataSetChanged();
		//Toast.makeText(getActivity(), R.string.saved_to_file_toast,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_calendar_title);
//		ShowDatabase.getInstance(getActivity()).loadTrackedShowsFromFile();
//		mTrackedShows = ShowDatabase.getInstance(getActivity()).getTrackedShows();	
//		setupAdapter();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		String test = data.getStringExtra(EXTRA_TEST);
		Log.d(TAG, "received: " + test);
		((CalendarShowAddapter) getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.fragment_show_list, menu);
	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
		case android.R.id.home:
			if(NavUtils.getParentActivityIntent(getActivity()) != null){
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		case R.id.action_settings:
			Toast.makeText(getActivity(), "One day this will open the settings window!", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		TVShow show = ((CalendarShowAddapter)getListAdapter()).getItem(position);
	
		Log.d(TAG, "showName: " + show.getShowName() + " updated " + show.getReleaseDate());
		((CalendarShowAddapter)getListAdapter()).notifyDataSetChanged();
	}
	
	public void setupAdapter() {
		if (getActivity() == null && getListView() == null)
			return;
		if (mTodaysShows != null) {
			Log.d(TAG, "setupAdapter initialized");
			CalendarShowAddapter adapter = new CalendarShowAddapter(mTodaysShows);
			setListAdapter(adapter);

		} else {
			setListAdapter(null);
			Log.d(TAG, "setupAdapter failed");
		}

		((CalendarShowAddapter) getListAdapter()).notifyDataSetChanged();
	}

	private void compareDate(TVShow show, String airDate) {
		try {
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

			// day from calendar
			Date todaysDate = today.getTime();

			// episode date from string
			Date episodeDate = sdf.parse(airDate);
			sdf.applyPattern("yyyy-MM-dd");
			
			// format the dates 
			String epDate = sdf.format(episodeDate);
			String toDate = sdf.format(todaysDate);
			
			if (toDate.equals(epDate)) {
				Log.d(TAG, "Dates match ");
				Log.d(TAG, "episodeDate is " + epDate);
				Log.d(TAG, "today is "+ toDate);
				mTodaysShows.add(show);
			} 
			else {
				Log.d(TAG, "Dates do not match");
			}
			
			((CalendarShowAddapter) getListAdapter()).notifyDataSetChanged();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	// the adapter required for the list view, this is where you initialize the
	// custom list item views
	private class CalendarShowAddapter extends ArrayAdapter<TVShow> {

		public CalendarShowAddapter(ArrayList<TVShow> tvShows) {
			super(getActivity(), 0, tvShows);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if we weren't given a view, inflate one
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_calendar_today, null);

			TVShow show = getItem(position);

			// show name
			TextView nameTextView = (TextView) convertView
					.findViewById(R.id.tracked_show_name_text_view);
			nameTextView.setText(show.getShowName());

			// season and episode number
			TextView SeasonEpisodeTextView = (TextView) convertView
					.findViewById(R.id.tracked_show_season_text_view);
			SeasonEpisodeTextView.setText(show.getNextSeason()
					+ show.getNextEpisodeNumber());
			// episode title
			TextView episodeTextView = (TextView) convertView
					.findViewById(R.id.tracked_show_episode_text_view);
			episodeTextView.setText("- " + show.getNextEpisodeTitle());
			
			return convertView;
		}
	}
}
