package com.jeedan.android.tvshowtracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TrackedShowsFragment extends ListFragment {
	

	private static final String TAG = "TrackedShowsFragment";
	
	private ArrayList<TVShow> mTrackedShows;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.app_tracked_shows);
		mTrackedShows = new ArrayList<TVShow>();
		mTrackedShows = ShowDatabase.getInstance(getActivity()).getTrackedShows(); // set the list to null so we do not crash at the start

		
		fetchShowUpdate();
		Log.d(TAG, "TVShows size: " + mTrackedShows.size());
		
		setRetainInstance(true);// retain this fragment upon rotation
		setupAdapter();
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_alltvshowslist, parent, false);
		
		return v; 
	}
	
	@Override
	public void onPause(){
		super.onPause();
		ShowDatabase.getInstance(getActivity()).saveTrackedShows(mTrackedShows);
		Toast.makeText(getActivity(), "saved shows to file", Toast.LENGTH_SHORT).show();
	}
	
	@Override 
	public void onResume(){
		super.onResume();
		((TrackedAdapter)getListAdapter()).notifyDataSetChanged();
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    
		((TrackedAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
	public void setupAdapter(){
		if(getActivity() == null && getListView() == null)return;
		if(mTrackedShows != null){
			Log.d(TAG, "setupAdapter");
			TrackedAdapter adapter = new TrackedAdapter(mTrackedShows);
	    	setListAdapter(adapter);
		}else {
			setListAdapter(null);
			Log.d(TAG, "setupAdapter failed");
		}
	}
	private void compareDate(String airDate){
		try {
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			
			// day from calendar
			Date todaysDate = today.getTime();
			
			// episode date from string
			Date episodeDate = sdf.parse(airDate);	
			
			if(todaysDate.compareTo(episodeDate) > 0){
				Log.d(TAG, "today is after airdate");
				fetchShowUpdate();
			}
			else if(todaysDate.compareTo(episodeDate) < 0){
				Log.d(TAG, "today is before airdate");
			}
			if(todaysDate.compareTo(episodeDate) == 0){
				Log.d(TAG, "Dates match");
			}
		}catch(ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// save the tracked show information in onPause, and only use the AsyncTask if a new show has been added
	private void fetchShowUpdate(){
		// start an async task to update today's shows
		new FetchShowInfo(getActivity()).execute();
	}
	
	private ArrayList<TVShow> fetchEpisodeLatest(String showName){
		return ShowDatabase.getInstance(getActivity()).fetchLastAired(showName);
	}
	
	private TVShow fetchEpisode(String showName){

		return ShowDatabase.getInstance(getActivity()).fetchLast(showName);
	}
	
	private String createShowName(String showName){
		String numbers = showName.replaceAll("[a-zA-Z]+", "");// replaces all letters with empty char
		String str1 = showName.replaceAll("[0-9]+", "_"); // replaces all numbers with an _
		String newName = str1+numbers; // combine the 2 strings to create the show name

		Log.d(TAG, "numbers: " + numbers);
		Log.d(TAG, "str1: " + str1);
		Log.d(TAG, "newName: " + newName);
		return newName;
	}
	
	private class FetchShowInfo extends AsyncTask<Void, Void, ArrayList<TVShow>> {
		
		private ProgressDialog dialog;
		public FetchShowInfo(Context context){
			dialog = new ProgressDialog(context);
		}
		
		@Override
		protected ArrayList<TVShow> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// fetch either the latest episode or all episodes for a show
			ArrayList<TVShow> fetchedShow =  new ArrayList<TVShow>();
			for(int i = 0; i< mTrackedShows.size(); i++){
				String showName = mTrackedShows.get(i).getShowName().replaceAll("\\s", "");
				//mTrackedShows.get(i).setEpGuideName(showName);
				TVShow ep = fetchEpisode(showName);
				fetchedShow.add(ep);

				Log.d(TAG, "showName: " + showName);
			}
			
			return fetchedShow;
		}
		
		@Override 
		protected void onPreExecute(){
			dialog.setMessage("Loading show information");
			dialog.show();
		}
		
		@Override 
		protected void onPostExecute(ArrayList<TVShow> shows){
			super.onPostExecute(shows);
			if(dialog.isShowing()) dialog.dismiss(); // close the dialog
			
			mTrackedShows = shows;
			Toast.makeText(getActivity(), "fetched show information", Toast.LENGTH_SHORT).show();	
			setupAdapter();
		}
	}
	// the adapter required for the list view, this is where you initialize the list item views
	private class TrackedAdapter extends ArrayAdapter<TVShow>{

		public TrackedAdapter(ArrayList<TVShow> tvShows) {
			super(getActivity(),0, tvShows);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			// if we weren't given a view, inflate one
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_tracked_show, null);
			
			TVShow show = getItem(position);
			
			// show name
			TextView nameTextView = (TextView)convertView.findViewById(R.id.tracked_show_name_text_view);
			nameTextView.setText(show.getShowName());
			
			// season and episode number
			TextView seasonTextView = (TextView)convertView.findViewById(R.id.tracked_show_season_text_view);
			
			if(Integer.parseInt(show.getSeason()) < 10){
				seasonTextView.setText("s0" + show.getSeason() + "e0" + show.getEpisodeNumber());
			}
			else{
				seasonTextView.setText("s" + show.getSeason() + "e" + show.getEpisodeNumber());
			}
			
			// episode title
			TextView episodeTextView = (TextView)convertView.findViewById(R.id.tracked_show_episode_text_view);
			episodeTextView.setText(" - " + show.getEpisodeTitle());
			
			// release date
			TextView releaseDateTextView = (TextView)convertView.findViewById(R.id.tracked_show_release_date);
			releaseDateTextView.setText(" - Aired on: " + show.getReleaseDate());
			
			return convertView;
		}
	}
}
