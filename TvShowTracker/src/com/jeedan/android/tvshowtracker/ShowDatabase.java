package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

import android.content.Context;

public class ShowDatabase {
	private static ShowDatabase sShowDatabase;
	private Context mAppContext;
	private ArrayList<TVShow> mTVShows;
	
	private ShowDatabase(Context context){
		mAppContext = context;

		mTVShows = new ArrayList<TVShow>();
		
		// TVShow arrow = new TVShow("Arrow", 3, 50);
		TVShow arrow = new TVShow("Arrow", "Latest Season #", "Latest Episode #", false);
		TVShow flash = new TVShow("The Flash","Latest Season #", "Latest Episode #", false);
		TVShow constantine = new TVShow("Constantine", "Latest Season #", "Latest Episode #", false);
		TVShow doctorWho = new TVShow("Doctor Who", "Latest Season #", "Latest Episode #", false);
		
		mTVShows.add(arrow);
		mTVShows.add(flash);
		mTVShows.add(constantine);
		mTVShows.add(doctorWho);
		
		// load from JSON
		// create a Serializer
		// load from Serializer
		
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

}
