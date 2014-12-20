package com.jeedan.android.tvshowtracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class XMLSerializer {
	public static final String TAG = "XMLSerializer";

	// TV RAGE 
	public static final String SEARCH_SHOW_URL ="http://services.tvrage.com/feeds/search.php?show=";//"http://services.tvrage.com/feeds/show_list.php"//
	public static final String EPISODE_INFO_URL = "http://services.tvrage.com/feeds/episodeinfo.php?show=";
	public static final String XML_SHOW= "show";

	public static final String XML_LATESTEPISODE= "latestepisode";
	public static final String XML_NEXTEPISODE= "nextepisode";


	// TV DB
	public static final String TVDB_SEARCH_URL ="http://thetvdb.com/api/GetSeries.php?seriesname=";
	public static final String TVDB_SERIES_TAG = "Series";
	public static final String TVDB_SERIES_ID = "seriesid";
	public static final String TVDB_SHOW_ID = "id";
	public static final String TVDB_SERIES_NAME = "SeriesName";
	public static final String TVDB_BANNER = "banner";
	public static final String TVDB_NETWORK = "Network";

	//TODO
	public static final String TVDB_ACTORS = "Actors";
	public static final String TVDB_AIRS_DAYOFWEEK = "Airs_DayOfWeek";
	public static final String TVDB_AIRS_TIME = "Airs_Time";
	public static final String TVDB_OVERVIEW = "Overview";
	public static final String TVDB_STATUS = "Status";
	public static final String TVDB_FANART = "fanart";
	public static final String TVDB_POSTER = "poster";
	public static final String TVDB_LASTUPDATED= "lastupdated";

	//TODO 
	// tv db show + id basic show info for posters and banners
	public static final String TVDB_URL = "http://thetvdb.com/api/";
	public static final String API_KEY = "7806A504560D71C5";
	public static final String SERIES_URL = "/series/";
	public static final String LANGUAGE_XML = "/en.xml";
	//
	public XMLSerializer(){

	}

	public void parseItemsSearchTVDb(ArrayList<TVShow>  shows, XmlPullParser parser) throws XmlPullParserException, IOException{
		String series = "";
		String seriesid = "";
		String seriesName = "";
		String banner  = "";
		String network = "";
		String overView = "";

		int eventType = parser.next();
		String text = "";	// store text here
		while(eventType != XmlPullParser.END_DOCUMENT){
			String nameTag  = parser.getName();
			if(eventType == XmlPullParser.END_TAG)
			{
				if(nameTag.equals(TVDB_SERIES_ID)){
					if(text.equals("")) return;
					seriesid = text;
				}
				else if(nameTag.equals(TVDB_SERIES_NAME)){
					if(text.equals("")) return;
					seriesName = text;
				}
				else if(nameTag.equals(TVDB_BANNER)){
					if(text.equals("")) return;
					banner = text;
				}else if(nameTag.equals(TVDB_NETWORK)){
					if(text.equals("")) return;
					network = text;
				}else if(nameTag.equals(TVDB_OVERVIEW)){
					if(text.equals("")) return;
					overView = text;
				}



				if(nameTag.equals(TVDB_SERIES_TAG)){	
					// TODO
					// update tv show object 
					// set series id
					TVShow show = new TVShow();
					show.setBannerURL(banner);
					show.setShowName(seriesName);
					show.setTvDb_id(seriesid);
					show.setNetwork(network);
					show.setOverView(overView);
					shows.add(show);

					// we clear the values here, incase the next xml tag has missing tags 
					//so that the next show does not have the same info as the previous show
					// for example:
					// Arrow s03e23   --> Flash s03e23  (even though flash does not have a 3rd s\
					seriesid = "";
					seriesName = "";
					banner  = "";
					network = "";
					overView = "";
				}
			}

			if(eventType ==  XmlPullParser.TEXT){
				text = parser.getText();
			}
			eventType = parser.next();
		}
	}

	public void parseItems(ArrayList<TVShow> shows, XmlPullParser parser) throws XmlPullParserException, IOException{
		int eventType = parser.next();
		String text = "";
		String showName = "";
		String seasons = "";
		String airStatus = "";

		String tvRage_id = "";
		//		String showUrl = "";

		while(eventType != XmlPullParser.END_DOCUMENT){
			String name = parser.getName();
			if(eventType == XmlPullParser.END_TAG ) {
				if(name.equals("showid")){
					if(text.equals("")) return;
					tvRage_id = text;
					Log.d(TAG, "MY show tvRage_id: " + tvRage_id);
				}else if(name.equals("name")){
					if(text.equals("")) return;
					showName = text;
				}else if(name.equals("seasons")){
					if(text.equals("")) return;
					seasons = text;
				}else if(name.equals("status")){
					if(text.equals("")) return;
					airStatus = text;
				}
				/*else if(name.equals("link")){
					if(text.equals("")) return;
					showUrl = text;
					//	Log.d(TAG, "TVrage url" + showUrl);
				}*/

				if(seasons == null || seasons.equals(""))
					seasons = "0";

				if(name.equals(XML_SHOW)){
					TVShow show = new TVShow();
					show.setShowName(showName);
					show.setTotalSeasons(seasons);
					show.setAirStatus(airStatus);
					show.setTVRage_id(tvRage_id);
					shows.add(show);
				}
			} else if(eventType == XmlPullParser.TEXT){
				text =  parser.getText();
			}

			eventType = parser.next();
		}
	}

	public void parseNextEpisodeTVdb(TVShow show, XmlPullParser parser) throws XmlPullParserException, IOException{
		// http://thetvdb.com/api/GetSeries.php?seriesname=Arrow
		// more complex method
		//http://thetvdb.com/api/7806A504560D71C5/series/257655/en.xml
		String seriesid = null;

		String Actors = null;
		String Airs_DayOfWeek = null;
		String Airs_Time = null;
		String Overview = null;
		String Status = null;
		String fanart = null;
		String poster = null;
		String lastupdated = null;
		String banner  = null;

		int eventType = parser.next();
		String text = "";	// store text here
		while(eventType != XmlPullParser.END_DOCUMENT){
			String nameTag  = parser.getName();

			if(eventType == XmlPullParser.END_TAG)
			{
				if(nameTag.equals(TVDB_SERIES_ID)){
					if(text.equals("")) return;
					seriesid = text;
				}else if(nameTag.equals(TVDB_SERIES_NAME)){
					if(text.equals("")) return;
					//seriesName = text;
				}else if(nameTag.equals(TVDB_BANNER)){
					if(text.equals("")) return;
					banner = text;
				}else if(nameTag.equals(TVDB_ACTORS)){
					if(text.equals("")) return;
					Actors = text;
					// actors
				}else if(nameTag.equals(TVDB_AIRS_DAYOFWEEK)){
					if(text.equals("")) return;
					Airs_DayOfWeek = text;
				}else if(nameTag.equals(TVDB_AIRS_TIME)){
					if(text.equals("")) return;
					Airs_Time = text;
				}else if(nameTag.equals(TVDB_OVERVIEW)){
					if(text.equals("")) return;
					Overview = text;
				}else if(nameTag.equals(TVDB_STATUS)){
					if(text.equals("")) return;
					Status = text;
				}else if(nameTag.equals(TVDB_BANNER)){
					if(text.equals("")) return;
					banner = text;
				}else if(nameTag.equals(TVDB_FANART)){
					if(text.equals("")) return;
					fanart = text;
				}		
				else if(nameTag.equals(TVDB_POSTER)){
					if(text.equals("")) return;
					poster = text;
				}else if(nameTag.equals(TVDB_LASTUPDATED)){
					if(text.equals("")) return;
					lastupdated = text;
				}

				if(nameTag.equals(TVDB_SERIES_TAG)){			
					// TODO
					// update tv show object 
					// set series id
					// set banner
					show.setPosterURL(poster);
					show.setAirStatus(Status);
					show.setAirTime(Airs_DayOfWeek + " " + Airs_Time);
					
					// poster
					// fanart
					Log.d(TAG,banner + "\n" 
							+ Actors + "\n" 
							+ Airs_DayOfWeek + "\n" 
							+ Airs_Time + "\n" 
							+ Overview + "\n" 
							+ Status + "\n" 
							+ fanart + "\n" 
							+ poster + "\n" 
							+ lastupdated+ "\n" 
							+ seriesid);
				}

			}

			if(eventType ==  XmlPullParser.TEXT){
				text = parser.getText();
			}
			eventType = parser.next();
		}
	}

	// THIS WORKS FOR GRABBING 1 EPISODE FROM TV RAGE
	public void parseNextEpisodeTVRage(TVShow show, XmlPullParser parser) throws XmlPullParserException, IOException{
		int eventType = parser.next();

		/*These variables are the tags inside the XML tree*/
		String tagName = "name";
		String tagAirTime = "airtime";
		String tagEpNumber = "number";
		String tagEpTitle = "title";
		String tagEpAirDate = "airdate";
		/*The next few variables are used to store the information from the parser*/
		String showAirTime = "";
		String epShowName = "";
		String seasonAndEpisodeNumber = "00x00";
		String epTitle = "";
		String epAirDate = "";
		String tvRage_id = "";

		String text = "";	
		int airTimeCounter = 0;
		while(eventType != XmlPullParser.END_DOCUMENT){
			String name = parser.getName();
			if(eventType == XmlPullParser.START_TAG){
				if(name.equals("show")){
					tvRage_id = parser.getAttributeValue(0);
					Log.d(TAG, "MY show tvRage_id: " + tvRage_id);
				}
			}
			if(eventType == XmlPullParser.END_TAG){
				if(name.equals(tagName)){
					if(text.equals("")) return;
					epShowName = text;
				}
				else if(name.equals(tagAirTime) && airTimeCounter == 0){
					if(text.equals("")) return;
					showAirTime = text;
					airTimeCounter++;
					Log.d(TAG, "MY show AirTime: " + showAirTime);
				}

				if(name.equals(tagEpNumber)){
					if(text.equals("")) return;
					seasonAndEpisodeNumber = text;
				}else if(name.equals(tagEpTitle)){
					if(text.equals("")) return;
					epTitle = text;
				}else if(name.equals(tagEpAirDate)){
					if(text.equals("")) return;
					epAirDate = text;
				}

				if(seasonAndEpisodeNumber == null || seasonAndEpisodeNumber.equals(""))
					seasonAndEpisodeNumber = "00x00";

				setShowInformationTVRage(show, name, epShowName,tvRage_id, epTitle, seasonAndEpisodeNumber, epAirDate);

			} else if(eventType == XmlPullParser.TEXT){
				text = parser.getText();
			}

			eventType = parser.next();
		}
	}

	public void setShowInformationTVRage(TVShow show, String xmlTag, String showName, String tvRage_id, String title, String number, String airDate){
		String seasonPrefix = "s";
		String episodePrefix = "e";
		String episodeNumber =  episodePrefix + number.substring(3);
		String seasonNumber = seasonPrefix + number.substring(0, 2);		

		show.setShowName(showName);
		show.setTVRage_id(tvRage_id);
		if(xmlTag.equals(XML_LATESTEPISODE)){
			if((seasonNumber != null) && (episodeNumber != null) && (title != null) && (airDate != null)){
				show.setSeason(seasonNumber);	
				show.setEpisodeNumber(episodeNumber);
				show.setEpisodeTitle(title);
				show.setReleaseDate(airDate);
				Log.d(TAG, "LATEST seasons and epNumber: " + seasonNumber + episodeNumber);
				Log.d(TAG, "LATEST epTitle: " + title);
				Log.d(TAG, "LATEST epAirDate: " + airDate);	
			}
		}
		else if(xmlTag.equals(XML_NEXTEPISODE) && xmlTag != null){		

			show.setNextSeason(seasonNumber);	
			show.setNextEpisodeNumber(episodeNumber);
			show.setNextEpisodeTitle(title);
			show.setNextReleaseDate(airDate);
			Log.d(TAG, "NEXT seasons and epNumber: " + seasonNumber + episodeNumber);
			Log.d(TAG, "NEXT epTitle: " + title);
			Log.d(TAG, "NEXT epAirDate: " + airDate);	

		}
	}

	// serach for shows from tv db
	public ArrayList<TVShow> searchTVdb(String showName){
		ArrayList<TVShow> shows = new ArrayList<TVShow>();
		// get url
		String url = Uri.parse(TVDB_SEARCH_URL + showName).buildUpon().build().toString();
		// parse url from Bytes to String
		String xmlString;
		try {
			xmlString = getURL(url);		
			Log.i(TAG, "Received XML: " + xmlString);
			// create xml factory new instance
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			// create parser with newPullparser
			XmlPullParser parser = factory.newPullParser();
			// setInput
			parser.setInput(new StringReader(xmlString));
			parseItemsSearchTVDb(shows, parser);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Log.e(TAG, "Failed to fetch items", ioe);
		} catch (XmlPullParserException xppe) {
			Log.e(TAG, "Failed to parse items", xppe);
			xppe.printStackTrace();
		}
		return shows;
	}

	// fetch 1 show from tv db id
	public ArrayList<TVShow> fetchTVdb_ID(String showName){
		// get url
		// TODO fetches show based on the search name
		ArrayList<TVShow> shows = new ArrayList<TVShow>();
		String url = Uri.parse(TVDB_SEARCH_URL + showName).buildUpon().build().toString();
		// parse url from Bytes to String
		String xmlString;
		try {
			xmlString = getURL(url);		
			Log.i(TAG, "Received XML: " + xmlString);

			// create xml factory new instance
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			// create parser with newPullparser
			XmlPullParser parser = factory.newPullParser();
			// setInput
			parser.setInput(new StringReader(xmlString));
			parseItemsSearchTVDb(shows, parser);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Log.e(TAG, "Failed to fetch items", ioe);
		} catch (XmlPullParserException xppe) {
			Log.e(TAG, "Failed to parse items", xppe);
			xppe.printStackTrace();
		}
		return shows;
	}

	// fetch 1 show from tv db
	public TVShow fetchItemTVdb(String id){
		TVShow show = new TVShow();
		// get url
		// TODO gets the show using the id
		String finalUrl = TVDB_URL + API_KEY + SERIES_URL + id + LANGUAGE_XML;
		String url = Uri.parse(finalUrl).buildUpon().build().toString();
		// parse url from Bytes to String
		String xmlString;
		try {
			xmlString = getURL(url);		

		//	Log.i(TAG, "Received XML: " + xmlString);

			if(xmlString == null)return new TVShow();
			// create xml factory new instance
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			// create parser with newPullparser
			XmlPullParser parser = factory.newPullParser();
			// setInput
			parser.setInput(new StringReader(xmlString));
			parseNextEpisodeTVdb(show, parser);  // THE ACTUAL INFORMATION GETTING PARSED HERE
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Log.e(TAG, "Failed to fetch items", ioe);
		} catch (XmlPullParserException xppe) {
			Log.e(TAG, "Failed to parse items", xppe);
			xppe.printStackTrace();
		}
		return show;
	}

	// fetch 1 show only
	public TVShow fetchItemTVRage(String showName){
		TVShow show = new TVShow();
		try{
			String exactName =  "&exact=1"; // use this if we need the exact name
			String url = Uri.parse(EPISODE_INFO_URL + showName).buildUpon().build().toString();

			String xmlString = getURL(url);
			//	Log.i(TAG, "Received XML: " + xmlString);

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			parseNextEpisodeTVRage(show, parser);
		}catch(IOException ioe){
			Log.e(TAG, "Failed to fetch items", ioe);

		}catch(XmlPullParserException xppe){

			Log.e(TAG, "Failed to parse items", xppe);
		}
		return show;
	}

	public ArrayList<TVShow> fetchItemsTVRage(String showName){
		ArrayList<TVShow> shows = new ArrayList<TVShow>();
		try{
			String url = Uri.parse(SEARCH_SHOW_URL + showName).buildUpon().build().toString();

			String xmlString = getURL(url);
			//Log.i(TAG, "Received XML: " + xmlString);

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			parseItems(shows, parser);
		}catch(IOException ioe){
			Log.e(TAG, "Failed to fetch items", ioe);
		}catch(XmlPullParserException xppe){
			Log.e(TAG, "Failed to parse items", xppe);
		}

		return shows;
	}

	public void downloadBitmap(String showName, TVShow show, Bitmap bm){
		try {
			String url = "http://www.thetvdb.com/banners/";
			byte[] data = null;
			data = new XMLSerializer().getURLBytes( url + showName);
			bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			Log.d(TAG, "downloading image: " + url + showName);
			show.setBanner(bm);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void downloadPosterandSave(String name, TVShow show, Bitmap bm) {

		downloadBitmap(name, show, bm);
		String imgName = name.substring(8);
		
		try {
			saveBitmapToFile(bm, imgName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "saving image to file: " + imgName);
	}

	public void saveBitmapToFile(Bitmap bm, String imgName) throws IOException{	
		FileIO serializer = new FileIO(imgName);
		Log.d(TAG, imgName);
		if(serializer.isExternalStorageWriteable()){
			serializer.makeDirectory();
			File f = serializer.getFile();	
			OutputStream out = null;

			out = new FileOutputStream(f.toString());

			bm.compress(Bitmap.CompressFormat.JPEG, 85, out); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
			out.flush();
			out.close(); // do not forget to close the stream
		}
	}

	public Bitmap readBitmapFromFile(String imgName){
		//if(imgName == null) return null;
		String name = imgName.substring(8);
		FileIO serializer = new FileIO(name);
		Bitmap bm = null;
		File f = null;
		if(serializer.isExternalStorageWriteable()){
			f = serializer.getFile();	
			if(f.exists()){
				bm = BitmapFactory.decodeFile(f.toString());//f.getAbsolutePath());
			}
			Log.d(TAG, "read bitmap from file: " + name);
			return bm;

		}
		return null;
	}

	public byte[] getURLBytes(String urlSpec) throws IOException{
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
}
