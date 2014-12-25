package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
	private static final String TAG = "MyShowsListFragment";

	private static final String PREFS_NAME = "MyPrefsFile";

	private static final String EXTRA_TEST = "testString";
	private static final String EXTRA_TODAYS_DATE = "today";

	private TrackedAdapter mAdapter;
	private ArrayList<TVShow> mTrackedShows;
	private ShowDatabase mShowDB;

	private ListView listView;
	private ImageView showImage;

	private int mTimeToUpdate;
	private int hour;
	private int mDayOfMonth;

	private static boolean mShowingDialog;

	public static MyShowsListFragment newInstance(String test) {
		MyShowsListFragment tsf = new MyShowsListFragment();

		// supply input
		Bundle args = new Bundle();
		args.putString(EXTRA_TEST, test);
		tsf.setArguments(args);
		return tsf;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.app_tracked_shows);
		Log.d(TAG, "onCreate");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setHasOptionsMenu(true);
		}

		mShowDB = ShowDatabase.getInstance(getActivity());

		// TODO maybe remove?
		// get arguments passed by from starting a new fragment
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (getArguments() != null) {
				// testString =
				// getArguments().getString(EXTRA_TEST,"getArguments() did not work");
			}
		}

		if (savedInstanceState != null) {
			// testString = savedInstanceState.getString(EXTRA_TODAYS_DATE);

			// calendarTimeTest = savedInstanceState.getInt(EXTRA_TODAYS_DATE);
			// Log.d(TAG, "Retrieving savedInstanceState: " + calendarTimeTest);
		}

		// get our settings and retrieve our last updated date;
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		mTimeToUpdate = settings.getInt(EXTRA_TODAYS_DATE, 0);

		// TODO
		// move code to UtilsDate
		Calendar today = Calendar.getInstance();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		hour = today.get(Calendar.HOUR);
		mDayOfMonth = today.get(Calendar.DAY_OF_MONTH);

		// get shows from showdatabase
		mTrackedShows = mShowDB.getTrackedShows(); // set the list to null so we
		// do not crash

		// updateShows();
		setupAdapter();
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_tv_show_list, parent, false);

		listView = (ListView) v.findViewById(android.R.id.list);
		setupContextMenu(listView);

		mAdapter.notifyDataSetChanged();
		return v;
	}

	@Override
	public void onPause() {
		super.onPause();
		mShowDB.saveTrackedShows(mTrackedShows);
		Log.d(TAG, "onPause");
		// Toast.makeText(getActivity(),
		// R.string.saved_to_file_toast,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_tracked_shows);

		updateShows();
		Log.d(TAG, "onResume");
	}

	@Override
	public void onStop() {
		super.onStop();
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		// mTimeToUpdate = 2; // change this number to check if updating works
		editor.putInt(EXTRA_TODAYS_DATE, mTimeToUpdate);
		// Commit edits or they won't matter
		editor.commit();

		Log.d(TAG, "BEING SAVED onStop: " + mTimeToUpdate);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putInt(EXTRA_TODAYS_DATE, calendarTimeTest);
		// Log.d(TAG, "BEING SAVED savedInstanceState: " + calendarTimeTest);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.shows_item_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;

		TVShow show = mAdapter.getItem(position);
		switch (item.getItemId()) {
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
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(getActivity(), "One day this will open the settings window!", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_item_sort_alphabetically:
			mShowDB.sortShowsAlphabetically();
			mAdapter.notifyDataSetChanged();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TVShow show = mAdapter.getItem(position);

		// TODO:
		// send the user to a new activity which shows showinformation and
		// a list of every show

		// downloadShowUpdates(position);
		Log.d(TAG, "showName: " + show.getShowName() + " TVDB_ID " + show.getTvDb_id());
		Log.d(TAG, "showName: " + show.getShowName() + " tv_Rage_id " + show.getTVRage_id());
		Log.d(TAG, "showName: " + show.getShowName() + " next date " + show.getNextReleaseDate());
		Log.d(TAG, "showName: " + show.getShowName() + " BANNER URL " + show.getBannerURL());
		Log.d(TAG, "showName: " + show.getShowName() + " POSTER URL " + show.getPosterURL());
		Log.d(TAG, "showName: " + show.getShowName() + " updated: " + show.getUpdatedInformation());

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

	private void downloadShowUpdates(int index) {
		new DownloadShowInfo(getActivity(), index).execute();
	}

	private void setupContextMenu(ListView listView) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			// use floating context menu
			registerForContextMenu(listView);
		} else {
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
					switch (item.getItemId()) {
					case R.id.menu_item_delete_show:
						for (int i = mAdapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								mTrackedShows.remove(mAdapter.getItem(i));
							}
						}
						mode.finish();
						mAdapter.notifyDataSetChanged();
						return true;
					case R.id.menu_item_fetch_update_show:
						for (int i = mAdapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								mAdapter.getItem(i).setUpdatedInformation(false);
								downloadShowUpdates(i);
								// fetchShowUpdate();
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
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				}
			});
		}

	}

	private String checkEmptyString(String stringToCheck) {
		if (stringToCheck == null) {
			stringToCheck = "TBA";
		}
		return stringToCheck;
	}

	private void updateShows() {
		// Check if we updated ALL SHOWS today if not.. do so
		// load all newly added and outofdate shows
		if (mTrackedShows.size() > 0) {
			for (int i = 0; i < mTrackedShows.size(); ++i) {
				TVShow show = mTrackedShows.get(i);
				// update only the ones that we have not updated yet
				if (!show.getUpdatedInformation() || (mTimeToUpdate != mDayOfMonth)) {
					downloadShowUpdates(i);
					// set the updateDate to today so we do not keep updating
					// unneccessarily
					if (mTimeToUpdate != mDayOfMonth) {
						Log.d(TAG, "Day does not match: " + mTimeToUpdate);
						mTimeToUpdate = mDayOfMonth;
					}
				} else {
					new DownloadBitmap(getActivity(), show).execute();
				}
			}
		}
	}

	private void updateShowInfo(TVShow show, TVShow show2) {

		show.setEpisodeNumber(checkEmptyString(show2.getEpisodeNumber()));
		show.setEpisodeTitle(checkEmptyString(show2.getEpisodeTitle()));
		show.setSeason(checkEmptyString(show2.getSeason()));
		show.setReleaseDate(checkEmptyString(show2.getReleaseDate()));

		show.setNextEpisodeTitle(checkEmptyString(show2.getNextEpisodeTitle()));
		show.setNextEpisodeNumber(checkEmptyString(show2.getNextEpisodeNumber()));
		show.setNextReleaseDate(checkEmptyString(show2.getNextReleaseDate()));
		show.setNextSeason(checkEmptyString(show2.getNextSeason()));
		show.setTVRage_id(checkEmptyString(show2.getTVRage_id()));
	}

	private class DownloadShowInfo extends AsyncTask<Void, Void, TVShow> {
		private ProgressDialog dialog;
		private int showIndex;

		public DownloadShowInfo(Context context, int index) {
			dialog = new ProgressDialog(context);
			showIndex = index;
		}

		@Override
		protected TVShow doInBackground(Void... params) {

			TVShow show = mTrackedShows.get(showIndex);
			String showName = show.getShowName().replaceAll("\\s", ""); // \s
			// removes
			// all
			// whitespace
			if (showName.contains("The")) {
				// String noBracers = showName.replaceAll("[^a-zA-Z0-9]", "");
				String cutThe = showName.substring(3);
				// show = new XMLSerializer().fetchItemTVRage(cutThe);
				TVShow show2 = new XMLSerializer().fetchItemTVRage(cutThe);
				updateShowInfo(show, show2);
				Log.d(TAG, show.getShowName() + " " + cutThe);

				TVShow tvDBshowInfo = new XMLSerializer().fetchItemTVdb(show.getTvDb_id());
				show.setPosterURL(tvDBshowInfo.getPosterURL());
				show.setAirStatus(tvDBshowInfo.getAirStatus());
				show.setAirTime(tvDBshowInfo.getAirTime());
			} else {
				// show = new XMLSerializer().fetchItemTVRage(showName);

				String noBracers = showName.replaceAll("[^a-zA-Z0-9]", "");
				TVShow show2 = new XMLSerializer().fetchItemTVRage(noBracers);
				updateShowInfo(show, show2);

				TVShow tvDBshowInfo = new XMLSerializer().fetchItemTVdb(show.getTvDb_id());
				show.setPosterURL(tvDBshowInfo.getPosterURL());
				show.setAirStatus(tvDBshowInfo.getAirStatus());
				show.setAirTime(tvDBshowInfo.getAirTime());
			}

			if ((show.getEpisodeNumber() != null) || (show.getReleaseDate() != null) 
					|| (show.getEpisodeTitle() != null) || (show.getSeason() != null)) {

				new DownloadBitmap(getActivity(), show).execute();
				return show;
			} else{
				show.setUpdatedInformation(false);
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			if(!mShowingDialog){
				dialog.setMessage("Loading show information");
				dialog.show();
				mShowingDialog = true;
			}
		}

		@Override
		protected void onPostExecute(TVShow show) {
			super.onPostExecute(show);

			if (dialog.isShowing()){ 
				dialog.dismiss(); // close the dialog 
			}

			if (show != null) {
				show.setUpdatedInformation(true);
				// show.setBanner(bm);
				mTrackedShows.set(showIndex, show);
				mShowDB.saveTrackedShows(mTrackedShows);
				mShowingDialog = false;
			} else {
				Toast.makeText(getActivity(), R.string.error_fetched_toast, Toast.LENGTH_SHORT).show();
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	private class DownloadBitmap extends AsyncTask<Void, Void, Void> {

		private TVShow show;

		private Bitmap poster;
		private boolean gotImage;
		public DownloadBitmap(Context context, TVShow show) {
			// dialog = new ProgressDialog(context);
			this.show = show;
			gotImage = false;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// if we do not download it
			// else use the 1 from file
			if (show.getPosterURL() != null) {
				poster = new XMLSerializer().readBitmapFromFile(show.getPosterURL());
				if (poster != null) {
					show.setBanner(poster);
					Log.d(TAG, "readBitmap doInBackground: " + show.getShowName() + " " + show.getPosterURL());
				} else {
					gotImage = new XMLSerializer().downloadPosterandSave(show.getPosterURL(), show, poster);
					Log.d(TAG, "DownloadBitmap doInBackground: " + show.getShowName() + " " + show.getPosterURL());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			super.onPostExecute(params);
			if(show.getBanner() != null){
				showImage.setImageBitmap(show.getBanner());
			}
			
			new XMLSerializer().saveBitmapToFile(show.getBanner(),show.getPosterURL());
			
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
			TextView nameTextView = (TextView) convertView.findViewById(R.id.tracked_show_name_text_view);
			nameTextView.setText(show.getShowName());

			// NETWORK
			TextView netWorkTextView = (TextView) convertView.findViewById(R.id.network_info_text_view);
			netWorkTextView.setText(show.getNetwork());

			// Air day and time
			TextView airTimeTextView = (TextView) convertView.findViewById(R.id.air_day_time_info_text_view);
			airTimeTextView.setText(show.getAirTime());

			// Air Status
			TextView statusTextView = (TextView) convertView.findViewById(R.id.show_status_text_view);
			statusTextView.setText("Status: " + show.getAirStatus());

			// season and episode number
			TextView seasonEpisodeTextView = (TextView) convertView.findViewById(R.id.tracked_show_season_text_view);
			seasonEpisodeTextView.setText("Latest ep: " + show.getSeason() + show.getEpisodeNumber());

			// episode title
			TextView episodeTextView = (TextView) convertView.findViewById(R.id.tracked_show_episode_text_view);
			episodeTextView.setText(" - " + show.getEpisodeTitle() + " - ");

			// release date
			TextView releaseDateTextView = (TextView) convertView.findViewById(R.id.tracked_show_release_date);
			releaseDateTextView.setText("Aired on: " + show.getReleaseDate());

			showImage = (ImageView) convertView.findViewById(R.id.show_icon_imageView);
			if (show.getBanner() != null) {
				showImage.setImageBitmap(show.getBanner());
			} else {
				showImage.setImageResource(R.drawable.no_img_found);
				show.setBanner(null);
			}

			// TODO
			// show number of watched episodes
			// ex: watched 10/50 episodes
			return convertView;
		}
	}
}
