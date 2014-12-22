package com.jeedan.android.tvshowtracker;


public class TVRageData {

	/* These variables are the tags inside the XML tree */
	public static final String TAG_NAME = "name";
	public static final String TAG_AIRTIME = "airtime";
	public static final String TAG_EPNUMBER = "number";
	public static final String TAG_EPTITLE = "title";
	public static final String TAG_EPAIRDATE = "airdate";
	/*
	 * The next few variables are used to store the information from the
	 * parser
	 */
	private String mShowAirTime;
	private String mEpShowName;
	private String mSeasonAndEpisodeNumber;
	private String mEpTitle;
	private String mEpAirDate;
	private String mTvRage_id;

	public TVRageData(){
		mShowAirTime = "";
		mEpShowName = "";
		mSeasonAndEpisodeNumber = "00x00";
		mEpTitle = "";
		mEpAirDate = "";
		mTvRage_id = "";
	}
	
	public void parseEpisodeInfo(String name, String text, TVShow show, int airTimeCounter){
		if (name.equals(TVRageData.TAG_NAME)) {
			if (text.equals(""))
				return;
			 setEpShowName(text);
		} else if (name.equals(TVRageData.TAG_AIRTIME) && airTimeCounter == 0) {
			if (text.equals(""))
				return;
			 setShowAirTime(text);
			airTimeCounter++;
		}

		if (name.equals(TVRageData.TAG_EPNUMBER)) {
			if (text.equals(""))
				return;
			 setSeasonAndEpisodeNumber(text);
		} else if (name.equals(TVRageData.TAG_EPTITLE)) {
			if (text.equals(""))
				return;
			 setEpTitle(text);
		} else if (name.equals(TVRageData.TAG_EPAIRDATE)) {
			if (text.equals(""))
				return;
			 setEpAirDate(text);
		}

		if ( getSeasonAndEpisodeNumber() == null ||  getSeasonAndEpisodeNumber().equals(""))
			 setSeasonAndEpisodeNumber("00x00") ;


	}
	
	public String getTvRage_id() {
		return mTvRage_id;
	}
	public void setTvRage_id(String tvRage_id) {
		mTvRage_id = tvRage_id;
	}
	public String getShowAirTime() {
		return mShowAirTime;
	}
	public void setShowAirTime(String showAirTime) {
		mShowAirTime = showAirTime;
	}
	public String getEpShowName() {
		return mEpShowName;
	}
	public void setEpShowName(String epShowName) {
		mEpShowName = epShowName;
	}
	public String getSeasonAndEpisodeNumber() {
		return mSeasonAndEpisodeNumber;
	}
	public void setSeasonAndEpisodeNumber(String seasonAndEpisodeNumber) {
		mSeasonAndEpisodeNumber = seasonAndEpisodeNumber;
	}
	public String getEpTitle() {
		return mEpTitle;
	}
	public void setEpTitle(String epTitle) {
		mEpTitle = epTitle;
	}
	public String getEpAirDate() {
		return mEpAirDate;
	}
	public void setEpAirDate(String epAirDate) {
		mEpAirDate = epAirDate;
	}
}
