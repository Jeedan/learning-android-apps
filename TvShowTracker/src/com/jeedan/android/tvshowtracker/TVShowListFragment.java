package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TVShowListFragment extends ListFragment {
	private static final String TAG = "TVShowListFragment";
	public  static final String EXTRA_SHOW_TRACKED = "com.jeedan.android.tvshowtracker.tracked";
	
	private ArrayList<TVShow> mTVShows;
	
	private boolean mSubtitleVisible;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.app_all_episodes);
		
		mTVShows = ShowDatabase.getInstance(getActivity()).getTVShows();
		
		// set up the adapter to display list view
		setupAdapter();
		setRetainInstance(true);// retain this fragment upon rotation
		mSubtitleVisible = true;
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_alltvshowslist, parent, false);
		
	//	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		//	if(mSubtitleVisible)
			//	getActivity().getActionBar().setSubtitle(R.string.new_show);
		
		return v; 
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		TVShow show = ((AllTvShowsAdapter)getListAdapter()).getItem(position);

		/*When you click on a tv show, add it to the "My tracked tv shows list" */
		boolean tracking = show.isTracked() ? false : true;

		show.setTracked(tracking);
		if(!ShowDatabase.getInstance(getActivity()).getTrackedShows().contains(show))
			ShowDatabase.getInstance(getActivity()).addTrackedShows(show);
		
		((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
		Log.d(TAG, "changed tracking of  " + show.getShowName());
	}
	
	@Override
	public void onPause(){
		super.onPause();
		ShowDatabase.getInstance(getActivity()).saveTVShowListTrackingInfo(mTVShows);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    
		((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.fragment_show_list, menu);
		
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_new_show);
		if( mSubtitleVisible && showSubtitle != null)
			showSubtitle.setTitle(R.string.new_show);
	}
	
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
		case R.id.menu_item_new_show:
			// add new show
			addShow();
			return true;
		case R.id.menu_item_tracked_shows:	
			Intent i = new Intent(getActivity(), TrackedShowsActivity.class);
			Log.d(TAG, "TVShows size: " + mTVShows.size());
			startActivity(i);
			return true;
		default:
				return super.onOptionsItemSelected(item);
				
		}
	}
	
	public void setupAdapter(){
		//ArrayAdapter<TVShow> adapter = new ArrayAdapter(getActivity(), android.R.layout.list_item_tvshow, mTVShows);
	    //AllTvShowsAdapter adapter = new AllTvShowsAdapter(mTVShows);	    			
	    //setListAdapter(adapter);
		if(getActivity() == null && getListView() == null)return;
		if(mTVShows != null){
			Log.d(TAG, "setupAdapter");
			AllTvShowsAdapter adapter = new AllTvShowsAdapter(mTVShows);
	    	setListAdapter(adapter);
		}else {
			setListAdapter(null);
			Log.d(TAG, "setupAdapter failed");
		}
	}
	
	private void addShow(){
		//if(mTVShows.size() <= 0) // only load if we haven't loaded already
		new FetchShowInfo(getActivity()).execute(); // create a task that loads show information from the api
	}
	
	private ArrayList<TVShow> fetchEpisodeLatest(String showName){
		Log.d(TAG, "testing in background " + ShowDatabase.getInstance(getActivity()).fetchLastAired(showName).get(0).getEpisodeTitle());
		return ShowDatabase.getInstance(getActivity()).fetchLastAired(showName);
	}
	
	private ArrayList<TVShow> fetchEpisodeAll(String showName){
		Log.d(TAG, "testing in background " + ShowDatabase.getInstance(getActivity()).fetchAllShowEpisodes(showName).get(0).getEpisodeTitle());
		return ShowDatabase.getInstance(getActivity()).fetchAllShowEpisodes(showName);
	}
	
	// ArrayAdapter for the listView
	private class AllTvShowsAdapter extends ArrayAdapter<TVShow>{

		public AllTvShowsAdapter(ArrayList<TVShow> tvShows) {
			super(getActivity(),0, tvShows);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			// if we weren't given a view, inflate one
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_tvshow, null);
			
			TVShow show = getItem(position);
			
			// show name
			TextView nameTextView = (TextView)convertView.findViewById(R.id.tv_show_name_text_view);
			nameTextView.setText(show.getShowName());
			
			// season and episode number
			TextView seasonTextView = (TextView)convertView.findViewById(R.id.tv_show_season_text_view);
			
			if(Integer.parseInt(show.getSeason()) < 10){
				seasonTextView.setText("s0" + show.getSeason() + "e0" + show.getEpisodeNumber());
			}
			else{
				seasonTextView.setText("s" + show.getSeason() + "e" + show.getEpisodeNumber());
			}
			
			// episode title
			TextView episodeTextView = (TextView)convertView.findViewById(R.id.tv_show_episode_text_view);
			episodeTextView.setText(" - " + show.getEpisodeTitle());
			
			// release date
			TextView releaseDateTextView = (TextView)convertView.findViewById(R.id.show_release_date);
			releaseDateTextView.setText(" - Aired on: " + show.getReleaseDate());
			
		//	TextView trackingTextView = (TextView)convertView.findViewById(R.id.tracking_text_View);
			
			CheckBox trackedCheckBox = (CheckBox)convertView.findViewById(R.id.tracked_tv_show_check_box);
			trackedCheckBox.setChecked(show.isTracked());
			
			return convertView;
		}
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
			ArrayList<TVShow> fetchedShow = fetchEpisodeLatest("arrow");
			//ArrayList<TVShow> shows = fetchEpisodeAll("arrow"); //;ShowDatabase.getInstance(getActivity()).fetchAllShowEpisodes("arrow");
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
			
			Log.d(TAG, "before adding episode " + mTVShows.size());
			for(TVShow s : shows){
				mTVShows.add(s);
			}
			//mTVShows = shows;
			Log.d(TAG, "after " + mTVShows.size());
			Toast.makeText(getActivity(), "fetched show information", Toast.LENGTH_SHORT).show();	
			setupAdapter();
		}
	}
}
