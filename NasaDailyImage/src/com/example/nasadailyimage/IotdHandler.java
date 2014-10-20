package com.example.nasadailyimage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.jar.Attributes;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class IotdHandler extends DefaultHandler{
	private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
	private boolean inURL = false;
	private boolean inTitle = false;
	private boolean inDescription = false;
	private boolean inItem = false;
	private boolean inDate = false;
	private Bitmap image = null;
	private String title = null;
	private StringBuffer description = new StringBuffer();
	private String date = null;
	
	public void processFeed(){
		try{
			// configure the XML parser (in this case we use SAX-Parser)
			SAXParserFactory factory = SAXParserFactory.newInstance(); // create a new factory instance
			SAXParser parser = factory.newSAXParser();  // tell the factory to create a new parser
			XMLReader reader = parser.getXMLReader(); // create a XML reader from a SAXParser object
		
			reader.setContentHandler(this);	// pass our IotdHandler as the reader handler
			InputStream inputStream = new URL(url).openStream(); // store and open the url inside an inputstream to be read
			reader.parse(new InputSource(inputStream)); // read our url that is stored in the inputStream
			
		}catch(Exception e){
			
		}
	}
	
	// read bitmap information from a URL and return it as a Bitmap object
	private Bitmap getBitMap(String url){
		try{
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection(); // open a url and store it
			connection.setDoInput(true); // enable input on the url site
			connection.connect(); // connect to the url
			InputStream input = connection.getInputStream(); // read data from the url
			Bitmap bitmap = BitmapFactory.decodeStream(input); // store and decode the url information (in this case an image) in a bitmaps
			input.close(); // always close input
			return bitmap;
		}catch(IOException ioe){
			return null; // shit happens
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		
		if(localName.equals("url")) inURL = true;
		else inURL = false;
		if(localName.equals("enclosure")){ 
			inURL = true;
			uri = attributes.getValue("url");
		}
		if(localName.startsWith("item")){
			inItem = true;
		}else if(inItem){
			if(localName.equals("title")) inTitle = true;
			else inTitle = false;
			
			if(localName.equals("description")) inDescription = true;
			else inDescription = false;
			
			if(localName.equals("pubDate")) inDate = true;
			else inDate = false;
		}
	}
	
	public void characters(char ch[], int start, int length){
		String chars = new String(ch).substring(start, start + length);
		if(inURL && url == null) image = getBitMap(chars);
		if(inTitle && title == null) title = chars;
		if(inDescription) description.append(chars);
		if(inDate && date == null) date = chars;
	}
	
	public Bitmap getImage() { return image;}
	public String getTitle() { return title;}
	public StringBuffer getDescription() { return description;}
	public String getDate() { return date;}
}
