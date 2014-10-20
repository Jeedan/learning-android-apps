package com.example.nasadailyimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class NasaDailyImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        IotdHandler iotdHandler = new IotdHandler();
        iotdHandler.processFeed();
        
        resetDisplay(iotdHandler.getTitle(),iotdHandler.getDate(), iotdHandler.getImage(), iotdHandler.getDescription());
    }
    
    protected void resetDisplay(String title, String date, Bitmap imageURL,StringBuffer description){
    	TextView imgTitlView = (TextView)findViewById(R.id.imageTitle);
    	imgTitlView.setText(title);
    	TextView imgDateView = (TextView)findViewById(R.id.imageDate);
    	imgDateView.setText(date);
    	TextView imgDescriptionView = (TextView)findViewById(R.id.imageDescription);
    	imgDescriptionView.setText(description);
    	ImageView imgView = (ImageView)findViewById(R.id.imageDisplay);
    	imgView.setImageBitmap(imageURL);
    }
}
