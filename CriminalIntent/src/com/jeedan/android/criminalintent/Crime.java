package com.jeedan.android.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	
	DateFormat simpleFormat;
	public Crime(){
		// generate unique id
		mId = UUID.randomUUID();
		mDate = new Date();
		
		//impleFormat = DateFormat.getDateInstance();
		simpleFormat = new SimpleDateFormat("EEEE, MMM, dd, yyyy.", Locale.US);
	}
	
	public Crime(JSONObject json) throws JSONException {
		 mId = UUID.fromString(json.getString(JSON_ID));
		 if (json.has(JSON_TITLE)) {
			 mTitle = json.getString(JSON_TITLE);
		 }
		 mSolved = json.getBoolean(JSON_SOLVED);
		 mDate = new Date(json.getLong(JSON_DATE));
	 }

	
	public JSONObject toJSON() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_DATE, mDate.getTime());
		json.put(JSON_SOLVED, mSolved);
		return json;
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
		return mId;
	}
	
	@Override
	public String toString(){
		return mTitle;
	}
}
