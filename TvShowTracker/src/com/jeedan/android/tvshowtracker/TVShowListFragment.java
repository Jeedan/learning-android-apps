package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

import android.content.Intent;
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

public class TVShowListFragment extends ListFragment {
	private static final String TAG = "TVShowListFragment";
	private ArrayList<TVShow> mTVShows;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.app_all_shows);
		
		mTVShows = ShowDatabase.getInstance(getActivity()).getTVShows();

		// set up the adapter to display list view
		//ArrayAdapter<Crime> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mCrimes);
	    AllTvShowsAdapter adapter = new AllTvShowsAdapter(mTVShows);
	    		
	   setListAdapter(adapter);
	   setRetainInstance(true);// retain this fragment upon rotation
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_alltvshowslist, parent, false);
		
		return v; 
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		TVShow show = ((AllTvShowsAdapter)getListAdapter()).getItem(position);
		
		boolean tracking = show.isTracked() ? false : true;
		
		show.setTracked(tracking);
		Log.d(TAG, "changed tracking of  " + show.getShowName());
		((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
		/*When you click on a tv show, add it to the "My tracked tv shows list" */
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
		inflater.inflate(R.menu.tvshow_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
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
			
			TextView nameTextView = (TextView)convertView.findViewById(R.id.tv_Show_Name_Text_View);
			nameTextView.setText(show.getShowName());
			
			TextView seasonTextView = (TextView)convertView.findViewById(R.id.tv_Show_Season_Text_View);
			seasonTextView.setText(show.getSeason());
			
			TextView episodeTextView = (TextView)convertView.findViewById(R.id.tv_Show_Episode_Text_View);
			episodeTextView.setText(show.getEpisodeTitle());
			

			TextView trackingTextView = (TextView)convertView.findViewById(R.id.tracking_text_View);
			
			CheckBox trackedCheckBox = (CheckBox)convertView.findViewById(R.id.tracked_tv_show_check_box);
			trackedCheckBox.setChecked(show.isTracked());
			return convertView;
		}
	}
}
