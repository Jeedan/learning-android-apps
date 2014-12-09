package com.jeedan.android.tvshowtracker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

public class ShowDatabase {
	private static final String TAG = "ShowDatabase";
	private static final String FILENAME_SAVE_TRACKED = "myShows.json";  // use to store our tracking information allshows
	
	// these Strings will be changed to become user input instead
	private static final String EPGUIDE_URL = "http://epguides.frecar.no/show/";
	private static final String TVRAGE_URL = "http://epguides.frecar.no/show/";
	
	private ArrayList<TVShow> mTVShows;  // shows that are found by searching
	private ArrayList<TVShow> mTrackedTVShows; // shows that are being tracked
	private TVShowJSONSerializer mSerializer;  // save tracked shows
	
	public static boolean mHasLoadedTracked;
	private static ShowDatabase sShowDatabase;
	private Context mAppContext;

	public static ShowDatabase getInstance(Context context){
		if(sShowDatabase == null){
			sShowDatabase = new ShowDatabase(context.getApplicationContext());
		}
		return sShowDatabase;
	}

	private ShowDatabase(Context context){
		mAppContext = context;
		mSerializer = new TVShowJSONSerializer(mAppContext, FILENAME_SAVE_TRACKED);
		
		mTVShows = new ArrayList<TVShow>();
	//	mTrackedTVShows = new ArrayList<TVShow>();
		loadTrackedShowsFromFile();
		sortShowsAlphabetically();
	}

	public void addShow(TVShow show){
		mTVShows.add(show);
	}
	
	public void sortShowsAlphabetically(){
		// sort shows alphabetically
		Collections.sort(mTrackedTVShows, new Comparator<TVShow>(){

			@Override
			public int compare(TVShow lhs, TVShow rhs) {
				// TODO Auto-generated method stub

				return lhs.getShowName().compareTo(rhs.getShowName());
			}

		});
	}
	
	public boolean containsShow(String name){
		for(int i =0; i < mTrackedTVShows.size(); i++){
			String trackedShowName = mTrackedTVShows.get(i).getShowName().replaceAll(" ", "_"); 
			if(trackedShowName.equals(name)){
				Log.d(TAG, "contains " + trackedShowName);
				return true; // the show is in our arraylist
			}
		}
		return false;
	}
	
	public void addTrackedShows(TVShow show){
		mTrackedTVShows.add(show);
	}
	
	public void removeAllTrackedShows(ArrayList<TVShow> toRemove){
		mTrackedTVShows.removeAll(toRemove);
	}
	
	public void removeTrackedShows(TVShow show){
		mTrackedTVShows.remove(show);
	}
	
	public ArrayList<TVShow> getTrackedShows() {
		return mTrackedTVShows;
	}
	
	public void setTrackedShows(ArrayList<TVShow> shows) {
		mTrackedTVShows = shows;
	}
	
	public ArrayList<TVShow> getTVShows() {
		return mTVShows;
	}
	
	public void loadTrackedShowsFromFile(){
		try{
			// load here
			mTrackedTVShows = mSerializer.loadShowsFromFile();
			mHasLoadedTracked = true;
		}
		catch(Exception e){
			// error here
			mTrackedTVShows = new ArrayList<TVShow>();
			mHasLoadedTracked = false;
			Log.d(TAG, "Error loading from serializer loadTrackedShowsFromFile \n" + e);
		}
	}
	
	public void loadJSONShowsFromFile(){
		try{

			// load here
		//	mTVShows = mTvShowSerializer.loadShowsFromFile();
			mTVShows = new ArrayList<TVShow>();
		}
		catch(Exception e){
			// error here
			mTVShows = new ArrayList<TVShow>();
			Log.d(TAG, "Error loading from serializer loadJSONShowsFromFile \n" + e);
		}
	}
	
	public void loadShowsFromTxtFile(){
		try{
			// load here
			mTVShows = mSerializer.loadAllTVShowsFromFile();
		}
		catch(Exception e){
			// error here
			mTVShows = new ArrayList<TVShow>();
			Log.d(TAG, "Error loading from serializer \n" + e);
		}
	}

	// save all tracked shows to json file
	public boolean saveTrackedShows(ArrayList<TVShow> shows){
		//TODO
		Log.d(TAG, "size " + shows.size());
		//Log.d(TAG, "testing " + shows.get(0).toJSON().toString());
		try {
			mSerializer.saveShowsToFile(shows);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
		return true;
	}
	
	public ArrayList<TVShow> fetchAllShowEpisodes(String showName){
		try{
			// load here
			String url = EPGUIDE_URL + showName;
			Log.d(TAG, url);
			mTVShows = mSerializer.fetchShowAllEpisodes(url);
		}
		catch(Exception e){
			// error here
			mTVShows = new ArrayList<TVShow>();
			Log.d(TAG, "Error loading from serializer \n" + e);
		}
		return mTVShows;
	}
	
	public TVShow fetchLast(String showName){
		TVShow ep = null;
		try{
			// load here
			String url = EPGUIDE_URL + showName + "/last/";
			Log.d(TAG, url);
			
			ep = mSerializer.fetchEpisode(url);
		}
		catch(Exception e){
			// error here
			ep = new TVShow("Show not found, verify spelling by visiting epguides.com show database", "1", "title", "0", "date");
			Log.d(TAG, "Error loading from serializer \n" + e);
		}
		return ep;
	}
	
	// this is working do not delete
	public ArrayList<TVShow> fetchLastAired(String showName) {
		ArrayList<TVShow> lastAiredEpisodes = new ArrayList<TVShow>();
		try{
			// load here
			String url = EPGUIDE_URL + showName + "/last/";
			Log.d(TAG, url);
			lastAiredEpisodes = mSerializer.fetchLastAiredEpisode(url);
		}
		catch(Exception e){
			// error here
			lastAiredEpisodes = new ArrayList<TVShow>();
			Log.d(TAG, "Error loading from serializer \n" + e);
		}
		return lastAiredEpisodes;
	}
	
	public void createTVSHOWbyHand(){
		mTVShows = new ArrayList<TVShow>();
		
		// TVShow arrow = new TVShow("Arrow", 3, 50);
		TVShow personOfInterest = new TVShow("Person of Interest", "1" , "Title ","0" , "Release date");
		TVShow flash =  new TVShow("The Flash 2014", "1", "Title ", "0", "Release date");
		TVShow constantine =  new TVShow("Constantine", "1", "Title ", "0", "Release date");
		TVShow doctorWho =  new TVShow("Doctor Who 2005", "1", "Title ", "0", "Release date");
		
		mTVShows.add(personOfInterest);
		mTVShows.add(flash);
		mTVShows.add(constantine);
		mTVShows.add(doctorWho);
	}
}
