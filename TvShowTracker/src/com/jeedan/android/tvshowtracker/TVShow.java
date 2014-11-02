package com.jeedan.android.tvshowtracker;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TVShow {

	private static final String TAG = "TVShow";
	
	// all show information
	
	// NEXT and LAST aired information
	private static final String JSON_EPISODE = "episode";
	private static final String JSON_SEASON = "season";
	private static final String JSON_RELEASE_DATE = "release_date";
	private static final String JSON_TITLE = "title";
	private static final String JSON_NUMBER = "number";
	private static final String JSON_SHOW = "show";
	private static final String JSON_EPGUIDE_NAME = "epguide_name";
	private static final String JSON_IMDB_ID = "imdb_id";
	
	
	private String mImdbId;
	private String mShowName;
	private String mSeason;
	
	private int mEpisodeNumber;
	private String mEpisodeTitle;
	
	private String mReleaseDate; // tells you when the episode airs
	
//	private ArrayList<TVSeason> mSeasons;
//	private ArrayList<TVSeason> mEpisodes;
	
	private boolean mTracked;
	
	public TVShow(String showName, String season, String episodeTitle, int epNumber, String releaseDate, String imdbId){
		mShowName = showName;
		mSeason = season;
		mEpisodeTitle = episodeTitle;
		mEpisodeNumber = epNumber;
		mReleaseDate = releaseDate;
		mImdbId = imdbId;
	}
	
	public void loadAll(JSONObject json) throws JSONException{
		JSONArray arr = json.getJSONArray("1");
		JSONObject sea = arr.getJSONObject(0);
		String season = sea.getString(JSON_SEASON);
		String release = sea.getString(JSON_RELEASE_DATE);
		String title = sea.getString(JSON_TITLE);
		int number = sea.getInt(JSON_NUMBER);

		mSeason = season; // number of season
		mReleaseDate = release; // release date / air date
		mEpisodeTitle = title; // episode title
		mEpisodeNumber = number; // episode number
		
		JSONObject show = sea.getJSONObject(JSON_SHOW);
		mShowName = show.getString(JSON_TITLE); // show name
	}
	
	public TVShow(JSONObject json) throws JSONException{
	//	JSONObject start = json.getJSONObject("1");
		JSONObject episode = null;
		String season = null ;
		String release = null;
		String title = null;
		int number = 0;
		String epGuideName = null;
		JSONObject show = null;
		
		if(json.has(JSON_SEASON)){
			season = json.getString(JSON_SEASON);
			release = json.getString(JSON_RELEASE_DATE);
			title = json.getString(JSON_TITLE);
			number = json.getInt(JSON_NUMBER);
			show = json.getJSONObject(JSON_SHOW);
		}else if(json.has(JSON_EPISODE)){
			episode = json.getJSONObject(JSON_EPISODE);	
			season = episode.getString(JSON_SEASON);
			release = episode.getString(JSON_RELEASE_DATE);
			title = episode.getString(JSON_TITLE);
			number = episode.getInt(JSON_NUMBER);
			show = episode.getJSONObject(JSON_SHOW);
		}

		mSeason = season; // number of season
		mReleaseDate = release; // release date / air date
		mEpisodeTitle = title; // episode title
		mEpisodeNumber = number; // episode number
		
		mShowName = show.getString(JSON_TITLE);
		epGuideName = show.getString(JSON_EPGUIDE_NAME);
		mImdbId = show.getString(JSON_IMDB_ID);
		Log.d(TAG, "epGuideName: " + epGuideName + " imdbId: " + mImdbId);
	}
		
	public JSONObject toJSON() throws JSONException{
		JSONObject json = new JSONObject();
		
		return json;
	}
	
	public boolean isTracked() {
		return mTracked;
	}

	public void setTracked(boolean tracked) {
		mTracked = tracked;
	}

	public String getShowName() {
		return mShowName;
	}

	public void setShowName(String mShowName) {
		this.mShowName = mShowName;
	}

	public String getSeason() {
		return mSeason;
	}

	public void setSeason(String mSeason) {
		this.mSeason = mSeason;
	}

	public int getEpisodeNumber() {
		return mEpisodeNumber;
	}

	public String getEpisodeTitle() {
		return mEpisodeTitle;
	}

	public void setEpisodeTitle(String mEpisodeTitle) {
		this.mEpisodeTitle = mEpisodeTitle;
	}

	public String getReleaseDate() {
		return mReleaseDate;
	}

	public void setAirDate(String airDate) {
		mReleaseDate = airDate;
	}
/*
	public ArrayList<TVSeason> getSeasons() {
		return mSeasons;
	}

	public void setSeasons(ArrayList<TVSeason> seasons) {
		mSeasons = seasons;
	}
*/
}
