package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class ShowDatabase {
	private static final String TAG = "ShowDatabase";
	private static final String FILENAME = "arrow.json";  // use to store our tracking information
	private static final String FILENAME_ALL_EPISODES = "arrow_ALL.json";
	private static final String FILENAME_LAST_AIRED = "shows_Last.json";
	private static final String FILENAME_NEXT_AIRED = "showsNEXT.json";

	private ArrayList<TVShow> mTVShows;
	
	private TVShowJSONSerializer mSerializer;
	
	private static ShowDatabase sShowDatabase;
	private Context mAppContext;
	
	private ShowDatabase(Context context){
		mAppContext = context;
		mSerializer = new TVShowJSONSerializer(mAppContext, FILENAME_ALL_EPISODES);


	//	createTVSHOWbyHand();
		
		// load from JSON
		loadShowsFromFile();
	}
	
	public void createTVSHOWbyHand(){
		mTVShows = new ArrayList<TVShow>();
		
		// TVShow arrow = new TVShow("Arrow", 3, 50);
		TVShow arrow = new TVShow("Arrow", "Latest Season #", "Title ",0 , "Release date", "imdbID");
		TVShow flash =  new TVShow("The Flash 2014", "Latest Season #", "Title ", 0, "Release date", "imdbID");
		TVShow constantine =  new TVShow("Constantine", "Latest Season #", "Title ", 0, "Release date", "imdbID");
		TVShow doctorWho =  new TVShow("Doctor Who", "Latest Season #", "Title ", 0, "Release date", "imdbID");
		
		mTVShows.add(arrow);
		mTVShows.add(flash);
		mTVShows.add(constantine);
		mTVShows.add(doctorWho);
	}
	
	public static ShowDatabase getInstance(Context context){
		if(sShowDatabase == null){
			sShowDatabase = new ShowDatabase(context.getApplicationContext());
		}
		return sShowDatabase;
	}
	
	public ArrayList<TVShow> getTVShows() {
		return mTVShows;
	}
	
	public void loadShowsFromURL(){
		//TODO:
	}
	
	public void loadMyTrackedShows(){
		//TODO:
	}
	
	public void loadShowsFromFile(){
		try{
			// load here
			mTVShows = mSerializer.loadShowsFromFile();
		}
		catch(Exception e){
			// error here
			mTVShows = new ArrayList<TVShow>();
			Log.d(TAG, "Error loading from serializer \n" + e);
		}
	}
}
