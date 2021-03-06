package com.jeedan.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;








import android.content.Context;
import android.util.Log;

public class CrimeLab {
	
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	
	private ArrayList<Crime> mCrimes;
	private CriminalIntentJSONSerializer mSerializer;
	
	private static CrimeLab sCrimeLab; // singleton
	private Context mAppContext;
	
	private CrimeLab(Context appContext){
		mAppContext = appContext;
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME); // create a serializer that we will use to save and load files

		//mCrimes = new ArrayList<Crime>();
		load();
	}
	
	public void load(){
		try {
			mCrimes = mSerializer.loadCrimes();
		} catch (Exception e) {
			mCrimes = new ArrayList<Crime>();
			Log.e(TAG, "Error loading crimes: ", e);
		}
	}
	
	public void loadInternal(){
		try {
			mCrimes = mSerializer.loadCrimesInternal();
		} catch (Exception e) {
			mCrimes = new ArrayList<Crime>();
			Log.e(TAG, "Error loading crimes: ", e);
		}
	}
	
	public boolean saveCrimes(){
		try{
			mSerializer.saveCrimes(mCrimes);
			Log.d(TAG, "crimes saved to file");
			return true;	
		}catch(Exception e){
			Log.e(TAG, "Error saving crimes: " + e);
			return false;
		}
	}
	
	public boolean saveCrimesInternal(){
		try{
			mSerializer.saveCrimesInternal(mCrimes);
			Log.d(TAG, "crimes saved to file");
			return true;	
		}catch(Exception e){
			Log.e(TAG, "Error saving crimes: " + e);
			return false;
		}
	}
	
	public static CrimeLab get(Context c){
		if(sCrimeLab == null){
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}	
		return sCrimeLab;
	}

	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}
	
	public void addCrime(Crime c){
		mCrimes.add(c);
	}
	
	public Crime getCrime(UUID id){
		for(Crime c : mCrimes){
			if(c.getID().equals(id))
				return c;
		}
		return null; // return null if the crime id does not exist or equal to the specified id
	}

}
