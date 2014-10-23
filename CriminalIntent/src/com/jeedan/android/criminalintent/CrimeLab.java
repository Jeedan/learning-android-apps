package com.jeedan.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {

	private ArrayList<Crime> mCrimes;
	
	private static CrimeLab sCrimeLab; // singleton
	private Context mAppContext;
	
	private CrimeLab(Context appContext){
		mAppContext = appContext;
		mCrimes = new ArrayList<Crime>(); 
		
/*
		// temporary
		for(int i = 0; i < 100; i++){
			Crime c = new Crime();
			c.setTitle("Crime #" + i);
			c.setSolved(i % 2 == 0); // every other 1
			mCrimes.add(c);
		}
*/
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
