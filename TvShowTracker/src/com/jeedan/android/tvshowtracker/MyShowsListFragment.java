package com.jeedan.android.tvshowtracker;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MyShowsListFragment extends ListFragment {

	private static final String TAG = "TrackedShowsFragment";
	private static final String EXTRA_TEST = "testString";
	private static final String EXTRA_TODAYS_DATE = "today";
	
	private TrackedAdapter mAdapter;
	private ArrayList<TVShow> mTrackedShows;
	private boolean mReturnedShows; // if we get shows back from searching, show

	ListView listView;
	
	private String testString;

	Bitmap bm;
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
		Log.d(TAG, "onCreate");
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setHasOptionsMenu(true);
		}
		if(savedInstanceState != null){
			testString = savedInstanceState.getString(EXTRA_TODAYS_DATE);
			Log.d(TAG, "Retrieving savedInstanceState: " + testString);
		}
		
		// check todays date and if we loaded info once already do not do it again today.	
		try {
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

			// day from calendar
			Date todaysDate = today.getTime();
			testString = sdf.format(todaysDate);
			// episode date from string
			Date savedDate = sdf.parse(testString);
			String saveD = sdf.format(savedDate);

			if (testString.equals(saveD)) {
				Log.d(TAG, "Dates match");
			}else
			{
				Log.d(TAG, "Do not match");
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// get arguments passed by from starting a new fragment
		if(getArguments() != null){
			testString = getArguments().getString(EXTRA_TEST, "getArguments() did not work");	
			Log.d(TAG, "getArguments: " + testString);
		}

		mTrackedShows = ShowDatabase.getInstance(getActivity()).getTrackedShows(); // set the list to null so we do not crash at the start		
		
		// TODO: Check if we updated ALL SHOWS today if not.. do so
		
		// load all newly added and outofdate  shows
		if (!ShowDatabase.mHasLoadedTracked && mTrackedShows.size() > 0){
			for(int i = 0; i < mTrackedShows.size(); ++i){
				TVShow show = mTrackedShows.get(i);
				if(!show.getUpdatedInformation()){
					downloadShowUpdates(i);	
				}
			}
			//fetchShowUpdate();	
		}
		

	//	setRetainInstance(true);// retain this fragment upon rotation
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

		mAdapter.notifyDataSetChanged();
		return v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		ShowDatabase.getInstance(getActivity()).saveTrackedShows(mTrackedShows);
		Log.d(TAG, "onPause");
		//Toast.makeText(getActivity(), R.string.saved_to_file_toast,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_tracked_shows);
		Log.d(TAG, "onResume");
	}
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_TODAYS_DATE, testString);
		Log.d(TAG, "BEING SAVED savedInstanceState: " + testString);
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		getActivity().getMenuInflater().inflate(R.menu.shows_item_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		
		TVShow show = mAdapter.getItem(position);
		switch(item.getItemId()){
		case R.id.menu_item_delete_show:
			mTrackedShows.remove(show);
			mAdapter.notifyDataSetChanged();
			return true;
		case R.id.menu_item_fetch_update_show:
			show.setUpdatedInformation(false);
			downloadShowUpdates(position);	
			mAdapter.notifyDataSetChanged();
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
			mAdapter.notifyDataSetChanged();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		TVShow show = mAdapter.getItem(position);
		
		// TODO: send the user to a new activity which shows showinformation and a list of every show

		downloadShowUpdates(position);
		Log.d(TAG, "showName: " + show.getShowName() + " tv_Rage_id " +  show.getTVRage_id());
		Log.d(TAG, "showName: " + show.getShowName() + " next date " +  show.getNextReleaseDate());
		
		mAdapter.notifyDataSetChanged();
	}
	
	public void setupAdapter() {
		if (getActivity() == null && getListView() == null)
			return;
		if (mTrackedShows != null) {
			Log.d(TAG, "setupAdapter initialized");
			mAdapter = new TrackedAdapter(mTrackedShows);
			setListAdapter(mAdapter);

		} else {
			setListAdapter(null);
			Log.d(TAG, "setupAdapter failed");
		}

		mAdapter.notifyDataSetChanged();
	}
	// save the tracked show information in onPause, and only use the AsyncTask
	// if a new show has been added
	private void fetchShowUpdate() {
		// start an async task to update today's shows
		//new FetchShowInfo(getActivity()).execute();
	}

	private void downloadShowUpdates(int index){
		new DownloadShowInfo(getActivity(), index).execute();
	}
	
	// TODO TEMP STUFF
	ImageView showImage;
	private void downloadBitmap(String showName, TVShow show){
		try {
			byte[] data = null;
			if(showName.equals("Arrow")){
				data = new XMLSerializer().getURLBytes("http://www.thetvdb.com/banners/graphical/257655-g11.jpg");
				bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			}else if(showName.equals("Supernatural")){

				data = new XMLSerializer().getURLBytes("http://thetvdb.com/banners/graphical/78901-g38.jpg");
				bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			}
			else{
				bm = null;
				show.setBanner(bm);
			}					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.shows_item_context, menu);					
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch(item.getItemId()){
					case R.id.menu_item_delete_show:
						for(int i = mAdapter.getCount() - 1; i >= 0; i--){
							if(getListView().isItemChecked(i)){
								mTrackedShows.remove(mAdapter.getItem(i));	
							}
						}
						mode.finish();
						mAdapter.notifyDataSetChanged();
						return true;
					case R.id.menu_item_fetch_update_show:
						for(int i = mAdapter.getCount() - 1; i >= 0; i--){
							if(getListView().isItemChecked(i)){
								mAdapter.getItem(i).setUpdatedInformation(false);
								fetchShowUpdate();
							}
						}
						mode.finish();
						mAdapter.notifyDataSetChanged();

						return true;
					default:
						return false;
					}
				}
				
				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position,
						long id, boolean checked) {
				}
			});
		}
		
	}
	
	private class DownloadShowInfo extends AsyncTask<Void, Void, TVShow>{
		private ProgressDialog dialog;
		private int showIndex;
		public DownloadShowInfo(Context context, int index) {
			dialog = new ProgressDialog(context);
			showIndex = index;
		}
		
		@Override
		protected TVShow doInBackground(Void... params) {
			
			TVShow show = mTrackedShows.get(showIndex);
			String showName = show.getShowName().replaceAll("\\s", "");
		
			if (showName.contains("The")) {
				String cutThe = showName.substring(3);
				show = new XMLSerializer().fetchItem(cutThe);
			} else {
				show = new XMLSerializer().fetchItem(showName);
				// TODO 
				// download bitmap
				//downloadBitmap(showName, show);
			}
			
			if(show.getShowName() != null)
				return show;
			else
				return null;
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Loading show information");
			dialog.show();
		}

		@Override 
		protected void onPostExecute(TVShow show){
			super.onPostExecute(show);
			if(dialog.isShowing()) dialog.dismiss(); // close the dialog

			if(show != null)
			{
				show.setUpdatedInformation(true);
				show.setBanner(bm);
				mTrackedShows.set(showIndex, show);
				ShowDatabase.mHasLoadedTracked = true;
				Toast.makeText(getActivity(), R.string.fetched_info_toast, Toast.LENGTH_SHORT).show();	
				ShowDatabase.getInstance(getActivity()).saveTrackedShows(mTrackedShows);
				Log.d(TAG, show.getShowName() + " " + show.getEpisodeTitle() + "  got info");
			}else 
			{
				ShowDatabase.mHasLoadedTracked = false;
				Toast.makeText(getActivity(), R.string.error_fetched_toast, Toast.LENGTH_SHORT).show();
			}
			mAdapter.notifyDataSetChanged();
		}
	}


	// the adapter required for the list view, this is where you initialize the
	// custom list item views
	private class TrackedAdapter extends ArrayAdapter<TVShow> {

		public TrackedAdapter(ArrayList<TVShow> tvShows) {
			super(getActivity(), 0, tvShows);
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
			releaseDateTextView.setText("- Aired on: " + show.getReleaseDate());

			// TODO: IMAGE BANNER
//			showImage = (ImageView)convertView.findViewById(R.id.show_icon_imageView);
//			if(show.getBanner() != null)
//				showImage.setImageBitmap(show.getBanner());
//			else
//				showImage.setImageResource(R.drawable.prototype_tv_launcher_icon);
			
			//showImage.setImageResource(R.drawable.banner_arrow);
			//showImage.setImageBitmap(bm);
			
			return convertView;
		}
	}
}

/* 
 * 
 *  
 * 
 * old //download metthod
	private class FetchShowInfo extends
	AsyncTask<Void, Void, ArrayList<TVShow>> {

		private ProgressDialog dialog;

		public FetchShowInfo(Context context) {
			dialog = new ProgressDialog(context);
		}

		@Override
		protected ArrayList<TVShow> doInBackground(Void... params) {
			// fetch either the latest episode or all episodes for a show	
			ArrayList<TVShow> fetchedShow = new ArrayList<TVShow>();
			for (int i = 0; i < mTrackedShows.size(); i++) {
				String showName = mTrackedShows.get(i).getShowName().replaceAll("\\s", "");
				
				if (!(mTrackedShows.get(i).getUpdatedInformation())) {
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
			
			if(mReturnedShows)
			{
				mTrackedShows = shows;
				Toast.makeText(getActivity(), R.string.fetched_info_toast, Toast.LENGTH_SHORT).show();	
				ShowDatabase.getInstance(getActivity()).saveTrackedShows(mTrackedShows);
				ShowDatabase.getInstance(getActivity()).setTrackedShows(mTrackedShows);
				ShowDatabase.mHasLoadedTracked = true;
			}else 
			{
				ShowDatabase.mHasLoadedTracked = false;
				Toast.makeText(getActivity(), R.string.error_fetched_toast, Toast.LENGTH_SHORT).show();
			}
			setupAdapter();
		}
	}
 *  
 *  
 *  
 *  Stuff that might become useful later on!

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
