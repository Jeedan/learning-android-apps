package com.jeedan.android.tvshowtracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class TVShowJSONSerializer {
	public static final String TAG = "TVShowJSONSerializer";
	public static final String FILE_PATH = "/TVShowTracker";
	
	private Context mAppContext;
	private String mFileName;
	
	private File mFile;
	
	public TVShowJSONSerializer(Context context, String fileName){
		mAppContext = context;
		mFileName = fileName;
		//makeDirectory();
		Log.d(TAG, "created directory");
	}
	
	public ArrayList<TVShow> loadShowsFromFile() throws JSONException, IOException{
		ArrayList<TVShow> shows = new ArrayList<TVShow>();
		BufferedReader reader = null;
		try{
			InputStream in = null;
			if(isExternalStorageWriteable()){
				in = new FileInputStream(getFile().toString());
				Log.d(TAG, "opening file");
				reader = new BufferedReader(new InputStreamReader(in));
			} else { 
				Log.d(TAG, "No external storage found");
				return null;
			}
			
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				// read from file here
				jsonString.append(line);
			}
			// parse the Json now that we have read and stored the data7
			/*JSONArray array = (JSONArray)new JSONTokener(jsonString.toString()).nextValue();
			for(int i = 0; i < array.length(); i++){
				shows.add(new TVShow(array.getJSONObject(i)));
			}	*/	
			
			// this works to read all show information, but need to grab each array by hand 
		    JSONObject objects = (JSONObject)new JSONTokener(jsonString.toString()).nextValue();
		    loadAllShowEpisodes(objects, shows);
		   //loadEpisodeLast(objects, shows);
		    //loadEpisodeNext();
		}
		catch(FileNotFoundException e){
			 // Ignore this one; it happens when starting fresh
			Log.d(TAG, "Error file not found", e);
		}
		finally{
			if(reader != null)
				reader.close();
		}
		return shows;
	}
	
	public void loadEpisodeLast(JSONObject objects, ArrayList<TVShow> shows) throws JSONException{
		shows.add(new TVShow(objects));
	}
	

	public void loadEpisodeNext(JSONObject objects, ArrayList<TVShow> shows) throws JSONException{
		shows.add(new TVShow(objects));
	}
	
	public void loadAllShowEpisodes(JSONObject objects, ArrayList<TVShow> shows) throws JSONException{
		
		ArrayList<String> seasonArray = new ArrayList<String>();
		JSONArray testArr = objects.names();
	    // loop through the testArr strings backwards becuse it adds them in reverse... don't know why
	    // add each string seperately in to the seasonArray arraylist
	    for(int k = testArr.length()-1; k > -1; k--){
	    	seasonArray.add(objects.names().getString(k));
	    }
	    // extract each episode information from each season for a tv-show from the JSON file.
	    for(int s = 0; s < seasonArray.size(); s++){
		    JSONArray array = objects.getJSONArray(seasonArray.get(s));
		    for(int i = 0; i < array.length(); i++){
		    	if(array.getJSONObject(s) != null)
		    		shows.add(new TVShow(array.getJSONObject(i)));
			}  
	    }
	}
	
	public boolean isExternalStorageWriteable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state))
			return true;
		return false;
	}
	
	public File makeDirectory(){
		File root = Environment.getExternalStorageDirectory();
		
		File dir = new File(root.getAbsolutePath() + FILE_PATH);
		dir.mkdirs();
		File file = new File(dir, mFileName);
		
		return file;
	}
	
	public File getFile(){
		mFile = makeDirectory();
		return mFile;
	}
}