package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class TVShowListFragment extends ListFragment {
	private static final String TAG = "TVShowListFragment";
	public  static final String EXTRA_SHOW_TRACKED = "com.jeedan.android.tvshowtracker.tracked";
	
	private ArrayList<TVShow> mTVShows;
	
	private boolean mSubtitleVisible;
	
	private String mShowURL;  // the edit text string we type will be stored so we can search tvrage with this string.
	private boolean mReturnedShows; // if we get shows back from searching, show a toast
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		// get the tvshow list from the singleton ShowDatabase 
		// the tvshows list will be empty
		// in this activity we will search tvrage.com for the shows we want and they will be added into this arraylist.
		mTVShows = ShowDatabase.getInstance(getActivity()).getTVShows();

		// set up the adapter to display list view
		setupAdapter();
		setRetainInstance(true);// retain this fragment upon rotation
		mSubtitleVisible = true; // the text underneath the app name
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_alltvshowslist, parent, false);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			if(mSubtitleVisible)
				getActivity().getActionBar().setSubtitle("");

		// the edit text is where we type in the show we are looking for
		// we can search for more than 1 show at a time thanks to tvRage database's search functionality
		EditText searchEditText = (EditText)v.findViewById(R.id.search_show_edit_text);
		searchEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String str1 = s.toString().replaceAll(" ", "_"); // replaces all numbers with an _
				mShowURL = str1;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	addShow();
		            return true;
		        }
		        return false;
		    }
		});

		Button searchButton = (Button)v.findViewById(R.id.search_show_button);
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addShow();
			}
		});
		return v; 
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		TVShow show = ((AllTvShowsAdapter)getListAdapter()).getItem(position);
		boolean tracking = !show.isTracked() ? true : false;
		show.setTracked(tracking);
		/*When you click on a tv show, add it to the "My tracked tv shows list" */
		String showName = show.getShowName().replaceAll(" ", "_"); // replaces all spaces with an _

		if(ShowDatabase.getInstance(getActivity()).containsShow(showName)){
			Toast.makeText(getActivity(), R.string.already_tracking_toast, Toast.LENGTH_SHORT).show();
		}else
		{
			ShowDatabase.getInstance(getActivity()).addTrackedShows(show); // add show only if we don't already track it
			Toast.makeText(getActivity(), R.string.added_show_toast, Toast.LENGTH_SHORT).show();	
			ShowDatabase.mHasLoadedTracked = false;
		}		
		((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onPause(){
		super.onPause();
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
	}
	
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
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
		if(mShowURL != null)
			new FetchShowInfo(getActivity()).execute(); // create a task that loads show information from the api
		else
			Toast.makeText(getActivity(), R.string.enter_show_search_toast, Toast.LENGTH_SHORT).show();
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
			
			String seasonPrefix = "Number of seasons: ";
			
			seasonTextView.setText(seasonPrefix + show.getTotalSeasons());
			// episode title
			TextView runningStatusTextView = (TextView)convertView.findViewById(R.id.show_status_text_view);
			runningStatusTextView.setText("Status: " + show.getAirStatus());
					
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
			ArrayList<TVShow> fetchedShow =  new XMLSerializer().fetchItems(mShowURL); // use the edit text here

			mReturnedShows = fetchedShow.size() > 0;

			return fetchedShow;
		}
		
		@Override 
		protected void onPreExecute(){
			dialog.setMessage("Searching for Shows");
			dialog.show();
		}
		
		@Override 
		protected void onPostExecute(ArrayList<TVShow> shows){
			super.onPostExecute(shows);
			if(dialog.isShowing()) dialog.dismiss(); // close the dialog

			Log.d(TAG, "before adding episode " + mTVShows.size());
			mTVShows = shows;
			
			Log.d(TAG, "after " + mTVShows.size());
			
			if(mReturnedShows)
				Toast.makeText(getActivity(), R.string.fetched_info_toast, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getActivity(), R.string.error_fetched_toast, Toast.LENGTH_LONG).show();
				
			setupAdapter();
		}
	}
}
