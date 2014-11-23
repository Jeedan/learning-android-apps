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
	private static final String JSON_EPGUIDE_NAME = "epguide_name";
	private static final String JSON_IMDB_ID = "imdb_id";
	private static final String JSON_TRACKING = "tracking";
	
	JSONObject mEpisodeObject;
	JSONObject mShowObject;
	private String mImdbId;
	private String mShowName;
	private String mSeason;
	
	private int mEpisodeNumber;
	private String mEpisodeTitle;
	
	private String mReleaseDate; // tells you when the episode airs
	private String mEpGuideName;
//	private ArrayList<TVSeason> mSeasons;
//	private ArrayList<TVSeason> mEpisodes;
	
	private boolean mTracked;
	
	public TVShow(){
		mShowName = "Show Title";
		mSeason = "1";
		mEpisodeTitle = "Episode Title";
		mEpisodeNumber = 1;
		mReleaseDate = "Release Date";
		mImdbId = "some imdbID";
		mEpGuideName = "showTitle";
	}
	public TVShow(String showName, String season, String episodeTitle, int epNumber, String releaseDate, String imdbId){
		mShowName = showName;
		mSeason = season;
		mEpisodeTitle = episodeTitle;
		mEpisodeNumber = epNumber;
		mReleaseDate = releaseDate;
		mImdbId = imdbId;
		mEpGuideName = "showTitle";
	}
	
	public TVShow(JSONObject json) throws JSONException{
		//JSONObject start = json.getJSONObject(JSON_EPISODE);
		JSONObject episode = null;
		String season = null ;
		String release = null;
		String title = null;
		int number = 0;
		JSONObject show = null;
		
		if(json.has("error")){
			return;
		}else if(json.has(JSON_SEASON)){
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
		mShowObject = show; // objects for saving
		mEpisodeObject = episode; // objcets for saving
		
		mSeason = season; // number of season
		mReleaseDate = release; // release date / air date
		mEpisodeTitle = title; // episode title
		mEpisodeNumber = number; // episode number

		mEpGuideName = show.getString(JSON_EPGUIDE_NAME);
		mShowName = show.getString(JSON_TITLE);
		mImdbId = show.getString(JSON_IMDB_ID);
		
		if(json.has(JSON_TRACKING))
			mTracked = json.getBoolean(JSON_TRACKING);
	}
	
	// save single episodes
	public JSONObject toJSON() throws JSONException{

		JSONObject episode = new JSONObject();
		episode.put(JSON_EPISODE, mEpisodeObject);
		episode.put(JSON_TRACKING, mTracked);
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
	public String getEpGuideName() {
		return mEpGuideName;
	}
	public void setEpGuideName(String epGuideName) {
		mEpGuideName = epGuideName;
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
