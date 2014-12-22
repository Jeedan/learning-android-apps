package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;


public class TVDBData {
	// TV DB
	public static final String TVDB_SEARCH_URL = "http://thetvdb.com/api/GetSeries.php?seriesname=";
	public static final String TVDB_SERIES_TAG = "Series";
	public static final String TVDB_SERIES_ID = "seriesid";
	public static final String TVDB_SHOW_ID = "id";
	public static final String TVDB_SERIES_NAME = "SeriesName";
	public static final String TVDB_BANNER = "banner";
	public static final String TVDB_NETWORK = "Network";

	// TODO
	public static final String TVDB_ACTORS = "Actors";
	public static final String TVDB_AIRS_DAYOFWEEK = "Airs_DayOfWeek";
	public static final String TVDB_AIRS_TIME = "Airs_Time";
	public static final String TVDB_OVERVIEW = "Overview";
	public static final String TVDB_STATUS = "Status";
	public static final String TVDB_FANART = "fanart";
	public static final String TVDB_POSTER = "poster";
	public static final String TVDB_LASTUPDATED = "lastupdated";

	private String mSeries;
	private String mSeriesid;
	private String mSeriesName;
	private String mBanner;
	private String mNetwork;
	private String mOverView;


	private String mActors;
	private String mAirs_DayOfWeek;
	private String mAirs_Time;
	private String mStatus;
	private String mFanart;
	private String mPoster;
	private String mLastupdated;

	public TVDBData(){
		mSeries = "";
		mSeriesid = "";
		mSeriesName = "";
		mBanner = "";
		mNetwork = "";
		mOverView = "";

		// this is for when we have an id
		mActors = "";
		mAirs_DayOfWeek = "";
		mAirs_Time = "";
		mStatus = "";
		mFanart = "";
		mPoster = "";
		mLastupdated = "";
	}

	public void parseSearchTVDB(String nameTag, String text, ArrayList<TVShow> shows){
		if (nameTag.equals(TVDB_SERIES_ID)) {
			if (text.equals(""))
				return;
			setSeriesid(text);
		} else if (nameTag.equals(TVDB_SERIES_NAME)) {
			if (text.equals(""))
				return;
			setSeriesName(text);
		} else if (nameTag.equals(TVDB_BANNER)) {
			if (text.equals(""))
				return;
			setBanner(text);
		} else if (nameTag.equals(TVDB_NETWORK)) {
			if (text.equals(""))
				return;
			setNetwork(text);
		} else if (nameTag.equals(TVDB_OVERVIEW)) {
			if (text.equals(""))
				return;
			setOverView(text);
		}

		if (nameTag.equals(TVDB_SERIES_TAG)) {
			// TODO
			// update tv show object
			// set series id
			TVShow show = new TVShow();
			show.setBannerURL(getBanner());
			show.setShowName(getSeriesName());
			show.setTvDb_id(getSeriesid());
			show.setNetwork(getNetwork());
			show.setOverView(getOverView());
			shows.add(show);

			// we clear the values here, incase the next xml tag has
			// missing tags
			// so that the next show does not have the same info as the
			// previous show
			// for example:
			// Arrow s03e23 --> Flash s03e23 (even though flash does not
			// have a 3rd s\
			setSeries("");
			setSeriesName("");
			setBanner("");
			setNetwork("");
			setOverView("");
		}
	}

	public void parseEpisodeWithID(String nameTag, String text ,TVShow show){
		if (nameTag.equals(TVDB_SERIES_ID)) {
			if (text.equals(""))
				return;
			//seriesid = text;
		} else if (nameTag.equals(TVDBData.TVDB_SERIES_NAME)) {
			if (text.equals(""))
				return;
			// seriesName = text;
		} else if (nameTag.equals(TVDBData.TVDB_BANNER)) {
			if (text.equals(""))
				return;
			setBanner(text);
		} else if (nameTag.equals(TVDBData.TVDB_ACTORS)) {
			if (text.equals(""))
				return;
			setActors(text);
		} else if (nameTag.equals(TVDBData.TVDB_AIRS_DAYOFWEEK)) {
			if (text.equals(""))
				return;
			setAirs_DayOfWeek(text);
		} else if (nameTag.equals(TVDBData.TVDB_AIRS_TIME)) {
			if (text.equals(""))
				return;
			setAirs_Time(text);
		} else if (nameTag.equals(TVDBData.TVDB_OVERVIEW)) {
			if (text.equals(""))
				return;
			setOverView(text);
		} else if (nameTag.equals(TVDBData.TVDB_STATUS)) {
			if (text.equals(""))
				return;
			setStatus(text);
		}else if (nameTag.equals(TVDBData.TVDB_FANART)) {
			if (text.equals(""))
				return;
			setFanart(text);
		} else if (nameTag.equals(TVDBData.TVDB_POSTER)) {
			if (text.equals(""))
				return;
			setPoster(text);
		} else if (nameTag.equals(TVDBData.TVDB_LASTUPDATED)) {
			if (text.equals(""))
				return;
			setLastupdated(text);
		}

		if (nameTag.equals(TVDBData.TVDB_SERIES_TAG)) {
			// TODO
			// update tv show object
			// set series id
			// set banner
			// poster
			// fanart
			show.setPosterURL(getPoster());
			show.setAirStatus(getStatus());
			show.setAirTime(getAirs_DayOfWeek() + " " + getAirs_Time());
		}

	}

	public String getSeries() {
		return mSeries;
	}

	public void setSeries(String series) {
		mSeries = series;
	}

	public String getSeriesid() {
		return mSeriesid;
	}

	public void setSeriesid(String seriesid) {
		mSeriesid = seriesid;
	}

	public String getSeriesName() {
		return mSeriesName;
	}

	public void setSeriesName(String seriesName) {
		mSeriesName = seriesName;
	}

	public String getBanner() {
		return mBanner;
	}

	public void setBanner(String banner) {
		mBanner = banner;
	}

	public String getNetwork() {
		return mNetwork;
	}

	public void setNetwork(String network) {
		mNetwork = network;
	}

	public String getOverView() {
		return mOverView;
	}

	public void setOverView(String overView) {
		mOverView = overView;
	}

	public String getActors() {
		return mActors;
	}

	public void setActors(String actors) {
		mActors = actors;
	}

	public String getAirs_DayOfWeek() {
		return mAirs_DayOfWeek;
	}

	public void setAirs_DayOfWeek(String airs_DayOfWeek) {
		mAirs_DayOfWeek = airs_DayOfWeek;
	}

	public String getAirs_Time() {
		return mAirs_Time;
	}

	public void setAirs_Time(String airs_Time) {
		mAirs_Time = airs_Time;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		mStatus = status;
	}

	public String getFanart() {
		return mFanart;
	}

	public void setFanart(String fanart) {
		mFanart = fanart;
	}

	public String getPoster() {
		return mPoster;
	}

	public void setPoster(String poster) {
		mPoster = poster;
	}

	public String getLastupdated() {
		return mLastupdated;
	}

	public void setLastupdated(String lastupdated) {
		mLastupdated = lastupdated;
	}

}
