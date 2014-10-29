package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

public class TVShow {

	private String mShowName;
	private String mSeason;
	private String mEpisodeTitle;
	private String mAirDate; // tells you when the episode airs
	
	private ArrayList<TVSeason> mSeasons;
	private ArrayList<TVSeason> mEpisodes;
	
	private boolean mTracked;
	
	public TVShow(String showName, String season, String episodeTitle, boolean tracking){
		mShowName = showName;
		mSeason = season;
		mEpisodeTitle = episodeTitle;
		mTracked = tracking;
		// add seasons here, get the number of seasons from the JSON file
		// get each episode and add them to the according season from the JSON file
		//mSeasons.add(seasons);
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

	public String getEpisodeTitle() {
		return mEpisodeTitle;
	}

	public void setEpisodeTitle(String mEpisodeTitle) {
		this.mEpisodeTitle = mEpisodeTitle;
	}

	public String getAirDate() {
		return mAirDate;
	}

	public void setAirDate(String airDate) {
		mAirDate = airDate;
	}

	public ArrayList<TVSeason> getSeasons() {
		return mSeasons;
	}

	public void setSeasons(ArrayList<TVSeason> seasons) {
		mSeasons = seasons;
	}
}