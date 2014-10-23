package com.jeedan.android.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Crime {
	private UUID mID;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;

	private String mFormatDate;

	DateFormat simpleFormat;
	public Crime(){
		// generate unique id
		mID = UUID.randomUUID();
		mDate = new Date();
		
		//impleFormat = DateFormat.getDateInstance();
		simpleFormat = new SimpleDateFormat("EEEE, MMM, dd, yyyy.", Locale.US);
		mFormatDate = simpleFormat.format(mDate);
	}
	
	public String getFormatedDate(){
		mFormatDate = simpleFormat.format(mDate);
		return mFormatDate;
	}
	
	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getID() {
		return mID;
	}
	
	@Override
	public String toString(){
		return mTitle;
	}
}
