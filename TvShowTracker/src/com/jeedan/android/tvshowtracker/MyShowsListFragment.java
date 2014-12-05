package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MyShowsListFragment extends ListFragment {

	private static final String TAG = "TrackedShowsFragment";
	private static final String EXTRA_TEST = "testString";

	private ArrayList<TVShow> mTrackedShows;
	private boolean mReturnedShows; // if we get shows back from searching, show

	ListView listView;
	
	private String testString;
	
	public static MyShowsListFragment newInstance(String test){
		MyShowsListFragment tsf = new MyShowsListFragment();
		
		// supply input
		Bundle args = new Bundle();
		args.putString(EXTRA_TEST, test);
		tsf.setArguments(args);
		return tsf;
	}
	
	@TargetApi(12)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.app_tracked_shows);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setHasOptionsMenu(true);
		}

		// get arguments passed by from starting a new fragment
		if(getArguments() != null){
			testString = getArguments().getString(EXTRA_TEST, "getArguments() did not work");	
			Log.d(TAG, "getArguments: " + testString);
		}

		mTrackedShows = ShowDatabase.getInstance(getActivity()).getTrackedShows(); // set the list to null so we do not crash at the start		
	
		if (!ShowDatabase.mHasLoadedTracked)
			fetchShowUpdate();
		

		setRetainInstance(true);// retain this fragment upon rotation
		setupAdapter();
	}

	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_tv_show_list, parent,
				false);
		
		
		listView = (ListView)v.findViewById(android.R.id.list);
		setupContextMenu(listView);
	
		((TrackedAdapter) getListAdapter()).notifyDataSetChanged();
		return v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		ShowDatabase.getInstance(getActivity()).saveTrackedShows(mTrackedShows);
		//Toast.makeText(getActivity(), R.string.saved_to_file_toast,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_tracked_shows);
		ShowDatabase.getInstance(getActivity()).loadTrackedShowsFromFile();
		mTrackedShows = ShowDatabase.getInstance(getActivity()).getTrackedShows();	
		setupAdapter();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		((TrackedAdapter) getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		getActivity().getMenuInflater().inflate(R.menu.shows_item_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		
		TrackedAdapter adapter = (TrackedAdapter)getListAdapter();
		TVShow show = adapter.getItem(position);
		switch(item.getItemId()){
		case R.id.menu_item_delete_show:
			mTrackedShows.remove(show);
			adapter.notifyDataSetChanged();
			return true;
		case R.id.menu_item_fetch_update_show:
			show.setUpdatedInformation(false);
			fetchShowUpdate();
			adapter.notifyDataSetChanged();
			return true;
		}

		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.myshow_menu_items, menu);
	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
		case R.id.action_settings:
			Toast.makeText(getActivity(), "One day this will open the settings window!", Toast.LENGTH_SHORT).show();
			return true;	
		case R.id.menu_item_sort_alphabetically:
			ShowDatabase.getInstance(getActivity()).sortShowsAlphabetically();
			((TrackedAdapter)getListAdapter()).notifyDataSetChanged();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		TVShow show = ((TrackedAdapter)getListAdapter()).getItem(position);
		
		if(!show.getUpdatedInformation()){
			fetchShowUpdate();
		}
		
		// TODO: send the user to a new activity which shows showinformation and a list of every show

		Log.d(TAG, "showName: " + show.getShowName() + " tv_Rage_id " +  show.getTVRage_id());
		Log.d(TAG, "showName: " + show.getShowName() + " next date " +  show.getNextReleaseDate());
		((TrackedAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	public void setupAdapter() {
		if (getActivity() == null && getListView() == null)
			return;
		if (mTrackedShows != null) {
			Log.d(TAG, "setupAdapter initialized");
			TrackedAdapter adapter = new TrackedAdapter(mTrackedShows);
			setListAdapter(adapter);

		} else {
			setListAdapter(null);
			Log.d(TAG, "setupAdapter failed");
		}

		((TrackedAdapter) getListAdapter()).notifyDataSetChanged();
	}
	// save the tracked show information in onPause, and only use the AsyncTask
	// if a new show has been added
	private void fetchShowUpdate() {
		// start an async task to update today's shows
		new FetchShowInfo(getActivity()).execute();
	}

	
	private void setupContextMenu(ListView listView){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			// use floating context menu
			registerForContextMenu(listView);
		}else{
			// use contextual action bar 			
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					// TODO Auto-generated method stub
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.shows_item_context, menu);					
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					// TODO Auto-generated method stub
					TrackedAdapter adapter = (TrackedAdapter)getListAdapter();
					switch(item.getItemId()){
					case R.id.menu_item_delete_show:
						for(int i = adapter.getCount() - 1; i >= 0; i--){
							if(getListView().isItemChecked(i)){
								mTrackedShows.remove(adapter.getItem(i));	
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					case R.id.menu_item_fetch_update_show:
						for(int i = adapter.getCount() - 1; i >= 0; i--){
							if(getListView().isItemChecked(i)){
								adapter.getItem(i).setUpdatedInformation(false);
								fetchShowUpdate();
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();

						return true;
					default:
						return false;
					}
				}
				
				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position,
						long id, boolean checked) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
	}
	
	private class FetchShowInfo extends
	AsyncTask<Void, Void, ArrayList<TVShow>> {

		private ProgressDialog dialog;

		public FetchShowInfo(Context context) {
			dialog = new ProgressDialog(context);
		}

		@Override
		protected ArrayList<TVShow> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// fetch either the latest episode or all episodes for a show
			ArrayList<TVShow> fetchedShow = new ArrayList<TVShow>();
			for (int i = 0; i < mTrackedShows.size(); i++) {
				String showName = mTrackedShows.get(i).getShowName().replaceAll("\\s", "");
				
				if (!mTrackedShows.get(i).getUpdatedInformation()) {
					if (showName.contains("The")) {

						String cutThe = showName.substring(3);
						TVShow ep = new XMLSerializer().fetchItem(cutThe);
						ep.setUpdatedInformation(true);
						fetchedShow.add(ep);

					} else {

						TVShow ep = new XMLSerializer().fetchItem(showName);
						ep.setUpdatedInformation(true);
						fetchedShow.add(ep);

					}
				}else {
					fetchedShow.add(mTrackedShows.get(i));
				}
			}
			mReturnedShows = fetchedShow.size() > 0;

			return fetchedShow;
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Loading show information");
			dialog.show();
		}

		@Override 
		protected void onPostExecute(ArrayList<TVShow> shows){
			super.onPostExecute(shows);
			if(dialog.isShowing()) dialog.dismiss(); // close the dialog
			
			mTrackedShows = shows;
			
			if(mReturnedShows)
			{
				Toast.makeText(getActivity(), R.string.fetched_info_toast, Toast.LENGTH_SHORT).show();	
				ShowDatabase.getInstance(getActivity()).saveTrackedShows(mTrackedShows);
				//ShowDatabase.mHasLoadedTracked = true;
			}else 
			{
				Toast.makeText(getActivity(), R.string.error_fetched_toast, Toast.LENGTH_SHORT).show();
			}
			setupAdapter();
		}
	}

	// the adapter required for the list view, this is where you initialize the
	// custom list item views
	private class TrackedAdapter extends ArrayAdapter<TVShow> {

		public TrackedAdapter(ArrayList<TVShow> tvShows) {
			super(getActivity(), 0, tvShows);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if we weren't given a view, inflate one
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_tracked_show, null);

			TVShow show = getItem(position);

			// show name
			TextView nameTextView = (TextView) convertView
					.findViewById(R.id.tracked_show_name_text_view);
			nameTextView.setText(show.getShowName());

			// season and episode number
			TextView SeasonEpisodeTextView = (TextView) convertView
					.findViewById(R.id.tracked_show_season_text_view);
			SeasonEpisodeTextView.setText(show.getSeason()
					+ show.getEpisodeNumber());
			// episode title
			TextView episodeTextView = (TextView) convertView
					.findViewById(R.id.tracked_show_episode_text_view);
			episodeTextView.setText(" - " + show.getEpisodeTitle());

			// release date
			TextView releaseDateTextView = (TextView) convertView
					.findViewById(R.id.tracked_show_release_date);
			releaseDateTextView
			.setText(" - Aired on: " + show.getReleaseDate());

			return convertView;
		}
	}
}

/*  Stuff that might become useful later on!

	private void compareDate(String airDate) {
		try {
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

			// day from calendar
			Date todaysDate = today.getTime();

			// episode date from string
			Date episodeDate = sdf.parse(airDate);

			if (todaysDate.compareTo(episodeDate) > 0) {
				Log.d(TAG, "today is after airdate");
				fetchShowUpdate();
			} else if (todaysDate.compareTo(episodeDate) < 0) {
				Log.d(TAG, "today is before airdate");
			}
			if (todaysDate.compareTo(episodeDate) == 0) {
				Log.d(TAG, "Dates match");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private String createShowName(String showName) {
		String numbers = showName.replaceAll("[a-zA-Z]+", "");// replaces all
		// letters with
		// empty char
		String str1 = showName.replaceAll("[0-9]+", "_"); // replaces all
		// numbers with an _
		String newName = str1 + numbers; // combine the 2 strings to create the
		// show name

		Log.d(TAG, "numbers: " + numbers);
		Log.d(TAG, "str1: " + str1);
		Log.d(TAG, "newName: " + newName);
		return newName;
	}
*/
