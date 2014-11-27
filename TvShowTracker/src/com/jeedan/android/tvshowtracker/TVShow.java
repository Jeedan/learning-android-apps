package com.jeedan.android.tvshowtracker;


import org.json.JSONException;
import org.json.JSONObject;

public class TVShow {
	// all show information
	
	// NEXT and LAST aired information
	private static final String JSON_EPISODE = "episode";
	private static final String JSON_SEASON = "season";
	private static final String JSON_RELEASE_DATE = "release_date";
	private static final String JSON_TITLE = "title";
	private static final String JSON_NUMBER = "number";
	private static final String JSON_SHOW = "show";
	private static final String JSON_TRACKING = "tracking";
	
	JSONObject mEpisodeObject;
	JSONObject mShowObject;
	
	private String mShowName;
	private String mTotalSeasons;
	private String mTotalEpisodes;
	// latest episode
	private String mSeason;
	private String mEpisodeNumber;
	private String mEpisodeTitle;
	private String mReleaseDate; // tells you when the episode airs
	private String mAirStatus;
	// next episode
	private String mNextEpisodeNumber;
	private String mNextSeason;
	private String mNextEpisodeTitle;
	private String mNextReleaseDate;
	
	private boolean mTracked;
	
	public TVShow(){
		mShowName = "Show Title";
		mSeason = "1";
		mEpisodeTitle = "Episode Title";
		mEpisodeNumber = "1";
		mReleaseDate = "Release Date";
	}
	public TVShow(String showName, String season, String episodeTitle, String epNumber, String releaseDate){
		mShowName = showName;
		mSeason = season;
		mEpisodeTitle = episodeTitle;
		mEpisodeNumber = epNumber;
		mReleaseDate = releaseDate;
		
		/*change this initalization but it should work for now*/
		mNextEpisodeNumber = epNumber;
		mNextSeason = season;
		mNextEpisodeTitle = episodeTitle;
		mNextReleaseDate = releaseDate;
	}
	
	public TVShow(JSONObject json) throws JSONException{
		//JSONObject start = json.getJSONObject(JSON_EPISODE);
		JSONObject episode = null;
		String season = null ;
		String release = null;
		String title = null;
		String number = null;
		JSONObject show = null;
		
		if(json.has("error")){
			return;
		}else if(json.has(JSON_SEASON)){
			season = json.getString(JSON_SEASON);
			release = json.getString(JSON_RELEASE_DATE);
			title = json.getString(JSON_TITLE);
			number = json.getString(JSON_NUMBER);
			show = json.getJSONObject(JSON_SHOW);
		}else if(json.has(JSON_EPISODE)){
			episode = json.getJSONObject(JSON_EPISODE);	
			season = episode.getString(JSON_SEASON);
			release = episode.getString(JSON_RELEASE_DATE);
			title = episode.getString(JSON_TITLE);
			number = episode.getString(JSON_NUMBER);
			show = episode.getJSONObject(JSON_SHOW);
		}
		mShowObject = show; // objects for saving
		mEpisodeObject = episode; // objcets for saving
		
		mSeason = season; // number of season
		mReleaseDate = release; // release date / air date
		mEpisodeTitle = title; // episode title
		mEpisodeNumber = number; // episode number

		mShowName = show.getString(JSON_TITLE);
		
		if(json.has(JSON_TRACKING))
			mTracked = json.getBoolean(JSON_TRACKING);
	}
	
	// save single episodes
	public JSONObject toJSON() throws JSONException{
		// Store these values
		// season number
		// episode number
		// epTitle
		// airDate
		JSONObject episode = new JSONObject();
		episode.put(JSON_EPISODE, mEpisodeObject);	
		episode.put(JSON_SEASON, mSeason);
		episode.put(JSON_RELEASE_DATE, mReleaseDate);
		episode.put(JSON_TITLE, mEpisodeTitle);
		episode.put(JSON_NUMBER, mEpisodeNumber);		
		JSONObject show = new JSONObject();
		episode.put(JSON_SHOW, show);
		show.put(JSON_TITLE, mShowName);
		// next season
		// next ep
		// next airdate
		/*
		JSONObject json = episode.getJSONObject(JSON_EPISODE);
		episode.put(JSON_SEASON, mSeason);
		episode.put(JSON_RELEASE_DATE, mReleaseDate);
		episode.put(JSON_TITLE, mEpisodeTitle);
		episode.put(JSON_NUMBER, mEpisodeNumber);
		JSONObject show = new JSONObject();
		show.put(JSON_SHOW, show);
		show.put(JSON_EPGUIDE_NAME, mEpGuideName);
		show.put(JSON_TITLE, mShowName);
		show.put(JSON_IMDB_ID, mImdbId);
		*/
		return episode;
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

	public void setSeason(String season) {
		mSeason = season;
	}

	public String getEpisodeNumber() {
		return mEpisodeNumber;
	}
	public void setEpisodeNumber(String episodeNumber) {
		mEpisodeNumber = episodeNumber;
	}
	public String getEpisodeTitle() {
		return mEpisodeTitle;
	}

	public void setEpisodeTitle(String episodeTitle) {
		mEpisodeTitle = episodeTitle;
	}
	
	public String getReleaseDate() {
		return mReleaseDate;
	}
	
	public void setReleaseDate(String airDate) {
		mReleaseDate = airDate;
	}

	public String getAirStatus() {
		return mAirStatus;
	}	
	
	public void setAirStatus(String airStatus) {
		mAirStatus = airStatus;
	}
	public String getTotalSeasons() {
		return mTotalSeasons;
	}
	public void setTotalSeasons(String totalSeasons) {
		mTotalSeasons = totalSeasons;
	}
	public String getTotalEpisodes() {
		return mTotalEpisodes;
	}
	public void setTotalEpisodes(String totalEpisodes) {
		mTotalEpisodes = totalEpisodes;
	}
	public String getNextEpisodeNumber() {
		return mNextEpisodeNumber;
	}
	public void setNextEpisodeNumber(String nextEpisodeNumber) {
		mNextEpisodeNumber = nextEpisodeNumber;
	}
	public String getNextSeason() {
		return mNextSeason;
	}
	public void setNextSeason(String nextSeason) {
		mNextSeason = nextSeason;
	}
	public String getNextEpisodeTitle() {
		return mNextEpisodeTitle;
	}
	public void setNextEpisodeTitle(String nextEpisodeTitle) {
		mNextEpisodeTitle = nextEpisodeTitle;
	}
	public String getNextReleaseDate() {
		return mNextReleaseDate;
	}
	public void setNextReleaseDate(String nextReleaseDate) {
		mNextReleaseDate = nextReleaseDate;
	}
}
