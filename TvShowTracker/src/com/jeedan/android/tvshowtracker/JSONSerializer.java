package com.jeedan.android.tvshowtracker;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class JSONSerializer {
	public static final String TAG = "TVShowJSONSerializer";
	public static final String FILE_PATH = "/TVShowTracker";
	
	private Context mAppContext;
	private String mFileName;
	
	private FileIO mFileIO;
	
	public JSONSerializer(String fileName){
		mFileName = fileName;
		mFileIO = new FileIO(mFileName);
	}
	
	public JSONSerializer(Context context, String fileName){
		mAppContext = context;
		mFileName = fileName;
		
		mFileIO = new FileIO(mAppContext, mFileName);
	}
	
	private byte[] getURLBytes(String urlSpec) throws IOException{
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) return null;
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while((bytesRead = in.read(buffer)) > 0){
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
			
		}catch(FileNotFoundException e){
			 // Ignore this one; it happens when starting fresh
			Log.d(TAG, "Error file not found", e);
			return null;
		}
		finally{
			connection.disconnect();
		}
	}
	
	public String getURL(String urlSpec) throws IOException{
		return new String(getURLBytes(urlSpec));
	}
	
	public String parseJSONUrl(String showURL) throws JSONException, IOException{
		String url = Uri.parse(showURL).buildUpon().toString();
		String json = getURL(url);
		
	    return json;
	}
	
	// adds episodes from the save file to the shows arraylist
	public void addEpisodeJSONArray(String json, ArrayList<TVShow> shows) throws JSONException{
		 // Parse the JSON using JSONTokener
		 JSONArray array = (JSONArray) new JSONTokener(json).nextValue();
		 // Build the array of crimes from JSONObjects
		 for (int i = 0; i < array.length(); i++) {
			 shows.add(new TVShow(array.getJSONObject(i)));
		 }
	}

	public void saveShowsToFile(ArrayList<TVShow> shows) throws IOException, JSONException{
		// build a json object
		ArrayList<JSONObject> array = new ArrayList<JSONObject>();

		//Log.d(TAG, "shows size " + shows.size());
		//JSONArray array = new JSONArray();
		for(TVShow s : shows){
			//Log.d(TAG, "" + s.toJSON());
			array.add(s.toJSON());
		}

		//Log.d(TAG, "array size " + array.size());
		mFileIO.makeDirectory();
		
		Writer writer = null;
		try{
			//Log.d(TAG, getFile().toString());
			//String fileName = getFile().toString().substring(34);
			//Log.d(TAG, fileName);
			OutputStream out = new FileOutputStream(mFileIO.getFile().toString());
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		}finally{
			if(writer != null){
				writer.close();
			}
		}
	}
		
	// load JSON shows from file
	// using this 1 to load our tracked shows from file on start up
	public ArrayList<TVShow> loadShowsFromFile() throws JSONException, IOException{
		ArrayList<TVShow> shows = new ArrayList<TVShow>();
		BufferedReader reader = null;
		try{
			InputStream in = null;
			if(mFileIO.isExternalStorageWriteable()){
				in = new FileInputStream(mFileIO.getFile().toString());
				Log.d(TAG, "opening file " + mFileIO.getFile().toString());
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
			//parse the Json now that we have read and stored the data			
			//Log.d(TAG, "file contents " + jsonString.toString());
			//String epInfo = jsonString.toString().substring(1, jsonString.length()-1);
			//Log.d(TAG, "epInfo " + epInfo);
			addEpisodeJSONArray(jsonString.toString(), shows);
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
}

// UNUSED METHODS

/*
 * 	public void addEpisode(String json, ArrayList<TVShow> shows) throws JSONException{
	    JSONObject objects = (JSONObject)new JSONTokener(json).nextValue();
		//Log.d(TAG, objects.toString());
		shows.add(new TVShow(objects));
	}
 * 	public void loadAllShowEpisodes(String json, ArrayList<TVShow> shows) throws JSONException{
	    JSONObject objects = (JSONObject)new JSONTokener(json).nextValue();
		//Log.d(TAG, objects.toString());
		
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
	
		
	// got all episodes for a show from epiguides
	public ArrayList<TVShow> fetchShowAllEpisodes(String showURL) throws IOException, JSONException{
		ArrayList<TVShow> shows = new ArrayList<TVShow>();
		try{
		    loadAllShowEpisodes(parseJSONUrl(showURL), shows);
		    //loadEpisodeLast(objects, shows);
		    
		}catch(IOException ioe){
			Log.e(TAG, "Failed to fetch items", ioe);
			shows = new ArrayList<TVShow>();
		}
		return shows;
	}
	
	*/
