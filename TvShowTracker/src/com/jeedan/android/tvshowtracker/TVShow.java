package com.jeedan.android.tvshowtracker;


import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

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
	private static final String JSON_UPDATED_INFO = "updatedInfo";
	private static final String JSON_NEXT_EP = "next_episode";
	private static final String JSON_Next_RELEASE_DATE = "next_Release_Date";
	private static final String JSON_TVRAGE_ID = "tv_Rage_id";
	private static final String JSON_TVDB_ID = "tvDb_id";
	private static final String JSON_TVDB_BANNER = "banner";
	private static final String JSON_TVDB_POSTER = "poster";
	private static final String JSON_TV_NETWORK = "network";
	private static final String JSON_TV_STATUS = "status";
	private static final String JSON_TV_AIR_TIME = "air_time";

	JSONObject mEpisodeObject;
	JSONObject mShowObject;

	// tvRage ID
	private String mTVRage_id;
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
	private boolean mUpdatedInformation;

	private String mTvDb_id; // tvDB id
	private String mOverView;
	private String mNetwork; // the tv network
	private String mAirTime; // weekday + hour  the show airs
	private String mBannerURL;
	private String mPosterURL;
	private Bitmap mBanner;

	public TVShow(){
		mBanner = null;
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
		JSONObject show = null;
		JSONObject nextEp = null;
		String season = "";
		String release = "";
		String title = "";
		String number = "";

		if(json.has("error")){
			return;
		}else if(json.has(JSON_SEASON)){
			season = json.getString(JSON_SEASON);
			release = json.getString(JSON_RELEASE_DATE);
			title = json.getString(JSON_TITLE);
			number = json.getString(JSON_NUMBER);
			show = json.getJSONObject(JSON_SHOW);
			nextEp = json.getJSONObject(JSON_NEXT_EP);
		}else if(json.has(JSON_EPISODE)){
			episode = json.getJSONObject(JSON_EPISODE);	
			season = episode.getString(JSON_SEASON);
			release = episode.getString(JSON_RELEASE_DATE);
			title = episode.getString(JSON_TITLE);
			number = episode.getString(JSON_NUMBER);
			show = episode.getJSONObject(JSON_SHOW);
			nextEp = episode.getJSONObject(JSON_NEXT_EP);
		}
		mShowObject = show; // objects for saving
		mEpisodeObject = episode; // objcets for saving

		mSeason = season; // number of season
		mReleaseDate = release; // release date / air date
		mEpisodeTitle = title; // episode title
		mEpisodeNumber = number; // episode number

		mShowName = show.getString(JSON_TITLE);
		mTVRage_id = hasJsonString(show, JSON_TVRAGE_ID);
		mTvDb_id = hasJsonString(show, JSON_TVDB_ID);
		mNetwork = hasJsonString(show, JSON_TV_NETWORK);

		// next show info
		mNextReleaseDate = hasJsonString(nextEp, JSON_Next_RELEASE_DATE);
		mNextSeason = hasJsonString(nextEp, JSON_SEASON);
		mNextEpisodeTitle = hasJsonString(nextEp, JSON_TITLE);
		mNextEpisodeNumber = hasJsonString(nextEp, JSON_NUMBER);
		mAirStatus = hasJsonString(show, JSON_TV_STATUS);
		mAirTime = hasJsonString(show, JSON_TV_AIR_TIME);
		mBannerURL = hasJsonString(show, JSON_TVDB_BANNER);
		mPosterURL = hasJsonString(show, JSON_TVDB_POSTER);

		if(show.has(JSON_UPDATED_INFO))
			mUpdatedInformation = show.getBoolean(JSON_UPDATED_INFO);
		if(json.has(JSON_TRACKING))
			mTracked = json.getBoolean(JSON_TRACKING);
	}

	private String hasJsonString(JSONObject obj, String tag) throws JSONException{
		String result = "";
		if(obj.has(tag)){
			result = obj.getString(tag);
		}else {
			result = "not found " + tag;
		}
		return result;
	}

	// save single episodes
	public JSONObject toJSON() throws JSONException{
		// Store these values
		// season number
		// episode number
		// epTitle
		// airDate
		if(mSeason == null ){
			mSeason = "s00x00";
		}else if(mReleaseDate == null ){
			mReleaseDate = "TBA";
		}else if(mEpisodeTitle == null ){
			mEpisodeTitle = "TBA";
		}else if(mEpisodeNumber == null ){
			mEpisodeNumber = "TBA";
		}else if(mShowName == null){
			mShowName = "TBA";
		}else if(mTvDb_id == null){
			mTvDb_id = "TBA";
		}else if(mTVRage_id == null){
			mTVRage_id = "TBA";
		}else if(mAirTime == null){
			mAirTime = "TBA";
		}else if(mAirStatus == null){
			mAirStatus = "TBA";
		}else if(mNetwork == null){
			mNetwork = "TBA";
		}else if(mBannerURL == null){
			mBannerURL = "TBA";
		}else if(mPosterURL == null){
			mPosterURL = "TBA";
		}
		
		JSONObject episode = new JSONObject();
		JSONObject show = new JSONObject();
		episode.put(JSON_SHOW, show);
	
		show.put(JSON_TITLE, mShowName);
		show.put(JSON_UPDATED_INFO, mUpdatedInformation);
		show.put(JSON_TVRAGE_ID, mTVRage_id);
		show.put(JSON_TVDB_ID, mTvDb_id);
		show.put(JSON_TV_AIR_TIME, mAirTime);
		show.put(JSON_TV_STATUS, mAirStatus);
		show.put(JSON_TV_NETWORK, mNetwork);
		show.put(JSON_TVDB_BANNER, mBannerURL);
		show.put(JSON_TVDB_POSTER, mPosterURL);
		
		episode.put(JSON_EPISODE, mEpisodeObject);	
		episode.put(JSON_SEASON, mSeason);
		episode.put(JSON_NUMBER, mEpisodeNumber);		
		episode.put(JSON_TITLE, mEpisodeTitle);
		episode.put(JSON_RELEASE_DATE, mReleaseDate);

		// next season
		// next ep
		// next airdate
		JSONObject nextEp = new JSONObject();
		episode.put(JSON_NEXT_EP, nextEp);
		nextEp.put(JSON_SEASON, mNextSeason);
		nextEp.put(JSON_Next_RELEASE_DATE, mNextReleaseDate);
		nextEp.put(JSON_TITLE, mNextEpisodeTitle);
		nextEp.put(JSON_NUMBER, mNextEpisodeNumber);
		return episode;
	}

	public boolean isTracked() {
		return mTracked;
	}

	public void setTracked(boolean tracked) {
		mTracked = tracked;
	}

	public boolean getUpdatedInformation() {
		return mUpdatedInformation;
	}

	public void setUpdatedInformation(boolean updated) {
		mUpdatedInformation = updated;
	}
	public String getBannerURL() {
		return mBannerURL;
	}	

	public void setBannerURL(String imgUrl) {
		mBannerURL = imgUrl;
	}

	public String getPosterURL() {
		return mPosterURL;
	}	

	public void setPosterURL(String imgUrl) {
		mPosterURL = imgUrl;
	}

	public String getNetwork() {
		return mNetwork;
	}	

	public void setNetwork(String network) {
		mNetwork = network;
	}

	public String getAirTime() {
		return mAirTime;
	}	

	public void setAirTime(String airTime) {
		mAirTime = airTime;
	}

	public Bitmap getBanner() {
		return mBanner;
	}

	public void setBanner(Bitmap bm) {
		mBanner = bm;
	}

	public String getTVRage_id() {
		return mTVRage_id;
	}

	public void setTVRage_id(String tvRage_id) {
		mTVRage_id = tvRage_id;
	}

	public String getTvDb_id() {
		return mTvDb_id;
	}

	public void setTvDb_id(String tvDB_id) {
		mTvDb_id = tvDB_id;
	}

	public String getOverView() {
		return mOverView;
	}

	public void setOverView(String overView) {
		mOverView = overView;
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
