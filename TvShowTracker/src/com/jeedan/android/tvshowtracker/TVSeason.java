package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

public class TVSeason {
	
	private String mTitle;
	private ArrayList<TVEpisode> mEpisodes;

	public TVSeason(String title){
		mTitle = title;
	}
	
	public TVSeason(String title, TVEpisode episodes){
		mEpisodes = new ArrayList<TVEpisode>();		
		mTitle = title;
		
		mEpisodes.add(episodes);
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public ArrayList<TVEpisode> getEpisodes() {
		return mEpisodes;
	}

	public void setEpisodes(ArrayList<TVEpisode> episodes) {
		mEpisodes = episodes;
	}
}
