package com.jeedan.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CriminalIntentJSONSerializer {
	private static final String TAG = "CriminalIntentJSONSerializer";
	private Context mContext;
	private String mFilename;
	private File mFile;
	
	public CriminalIntentJSONSerializer(Context context, String fileName){
		mContext = context;
		mFilename = fileName;
	}
	

    public ArrayList<Crime> loadCrimesInternal() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
            // open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // we will ignore this one, since it happens when we start fresh
        } finally {
            if (reader != null)
                reader.close();
            }

        return crimes;
    }

    public void saveCrimesInternal(ArrayList<Crime> crimes) throws JSONException, IOException {
        // build an array in JSON
        JSONArray array = new JSONArray();
        for (Crime c : crimes)
            array.put(c.toJSON());

        // write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
    
	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException{
		// build a JSON array
		JSONArray array = new JSONArray();
		for(Crime c : crimes){
			array.put(c.toJSON());
		}
		
		// write file to disk
		makeDirectory(); // create the directory to save to 
		Writer writer = null;
		try{
			//File file = mContext.getDir("CriminalIntentApp", Context.MODE_PRIVATE);	
			Log.d(TAG, getFile().toString());
			String fileName = getFile().toString().substring(35);
			Log.d(TAG, fileName);
			OutputStream out = new FileOutputStream(getFile().toString());
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		}finally{
			if(writer != null){
				writer.close();
			}
		}
	}
	
	public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
		 ArrayList<Crime> crimes = new ArrayList<Crime>();
		 BufferedReader reader = null;
		 try {
			 // Open and read the file into a StringBuilder 
			 InputStream in = null;
			 if(isExternalStorageWriteable()){
				 in = new FileInputStream(getFile().toString());
				 Log.d(TAG, "trying to read file");
			     reader = new BufferedReader(new InputStreamReader(in));
			 } 
			 StringBuilder jsonString = new StringBuilder();
			 String line = null;
				 while ((line = reader.readLine()) != null) {
					 // Line breaks are omitted and irrelevant
					 jsonString.append(line);
				 }
				 // Parse the JSON using JSONTokener
				 JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
				 // Build the array of crimes from JSONObjects
				 for (int i = 0; i < array.length(); i++) {
					 crimes.add(new Crime(array.getJSONObject(i)));
			 }
				Log.d(TAG, "loaded crimes");
		 } catch (FileNotFoundException e) {
		 // Ignore this one; it happens when starting fresh
				Log.d(TAG, "Error file not found", e);
		 } finally {
			 if (reader != null)
			 reader.close();
		 }
		 Log.d(TAG, "crimes returned");
		 return crimes;
	}

    public boolean isExternalStorageWriteable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state))
			return true;
		
		return false;// no sd card found to write to 
	}
	
	public File makeDirectory(){
		File root = Environment.getExternalStorageDirectory();
		
		File dir = new File(root.getAbsolutePath() + "/CriminalIntent");
		dir.mkdirs();
		File file = new File(dir, mFilename);
		
		return file;
	}
	
	public File getFile(){
		mFile = makeDirectory();
		return mFile;
	}
}
    /*
	
*/