package com.jeedan.android.tvshowtracker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.util.Log;

public class XMLSerializer {
	public static final String TAG = "XMLSerializer";
	public static final String SEARCH_SHOW_URL ="http://services.tvrage.com/feeds/search.php?show=";//"http://services.tvrage.com/feeds/show_list.php"//
	public static final String EPISODE_INFO_URL = "http://services.tvrage.com/feeds/episodeinfo.php?show=";
	public static final String XML_SHOW= "show";

	public static final String XML_LATESTEPISODE= "latestepisode";
	public static final String XML_NEXTEPISODE= "nextepisode";
	public XMLSerializer(){
		
	}
	
	public void parseItems(ArrayList<TVShow> shows, XmlPullParser parser) throws XmlPullParserException, IOException{
		int eventType = parser.next();
		String text = null;
		String showName = null;
		String seasons = null;
		String airStatus = null;
		String showUrl = null;
		String tvRage_id = null;
		
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
				}else if(name.equals("link")){
					if(text.equals("")) return;
					showUrl = text;
				//	Log.d(TAG, "TVrage url" + showUrl);
				}else if(name.equals("status")){
					if(text.equals("")) return;
					airStatus = text;
				}
				
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
	
	public void parseNextEpisode(TVShow show, XmlPullParser parser) throws XmlPullParserException, IOException{
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

				setShowInformation(show, name, epShowName,tvRage_id, epTitle, seasonAndEpisodeNumber, epAirDate);
			
			} else if(eventType == XmlPullParser.TEXT){
				text = parser.getText();
			}
			
			eventType = parser.next();
		}
	}
	
	public void setShowInformation(TVShow show, String xmlTag, String showName, String tvRage_id, String title, String number, String airDate){
		String seasonPrefix = "s";
		String episodePrefix = "e";String episodeNumber =  episodePrefix + number.substring(3);
		String seasonNumber = seasonPrefix + number.substring(0, 2);		
		
		show.setShowName(showName);
		show.setTVRage_id(tvRage_id);
		if(xmlTag.equals(XML_LATESTEPISODE)){
			show.setSeason(seasonNumber);	
			show.setEpisodeNumber(episodeNumber);
			show.setEpisodeTitle(title);
			show.setReleaseDate(airDate);
			Log.d(TAG, "MY seasons and epNumber: " + seasonNumber + episodeNumber);
			Log.d(TAG, "MY epTitle: " + title);
			Log.d(TAG, "MY epAirDate: " + airDate);	
		}else if(xmlTag.equals(XML_NEXTEPISODE)){		
			show.setNextSeason(seasonNumber);	
			show.setNextEpisodeNumber(episodeNumber);
			show.setNextEpisodeTitle(title);
			show.setNextReleaseDate(airDate);
			Log.d(TAG, "MY seasons and epNumber: " + seasonNumber + episodeNumber);
			Log.d(TAG, "MY epTitle: " + title);
			Log.d(TAG, "MY epAirDate: " + airDate);	
		}
	}
	
	public TVShow fetchItem(String showName){
		TVShow show = new TVShow();
		try{
			String url = Uri.parse(EPISODE_INFO_URL + showName + "&exact=1").buildUpon().build().toString();
			
			String xmlString = getURL(url);
		//	Log.i(TAG, "Received XML: " + xmlString);
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			parseNextEpisode(show, parser);
		}catch(IOException ioe){
			Log.e(TAG, "Failed to fetch items", ioe);
			
		}catch(XmlPullParserException xppe){

			Log.e(TAG, "Failed to parse items", xppe);
		}
		return show;
	}
	
	public ArrayList<TVShow> fetchItems(String showName){
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
