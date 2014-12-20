package com.jeedan.android.tvshowtracker;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileIO {

	public static final String TAG = "FileIO";
	public static final String FILE_PATH = "/TVShowTracker";

	private Context mAppContext;
	
	private String mFileName;
	private File mFile;
	
	public FileIO(String fileName){
		mFileName = fileName;
	}
	
	public FileIO(Context context, String fileName){
		mAppContext = context;
		mFileName = fileName;
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
