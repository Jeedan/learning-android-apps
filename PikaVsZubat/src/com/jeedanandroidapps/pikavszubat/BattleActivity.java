package com.jeedanandroidapps.pikavszubat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BattleActivity extends Activity {
	Intent titleScreenIntent;
	
	TextView testTextView;
	
	Button fightButton;
	Button pokemonButton;
	Button itemsButton;
	Button runButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle);

		titleScreenIntent = new Intent(this, MainActivity.class);
		testTextView = (TextView)findViewById(R.id.buttonTestTextView);
		
		fightButton = (Button)findViewById(R.id.fightButton);
		pokemonButton = (Button)findViewById(R.id.pokemonButton);
		itemsButton = (Button)findViewById(R.id.itemsButton);
		runButton = (Button)findViewById(R.id.runButton);
		
		// set up the button click events inside functions to clean up the onCreate()
	}
	
	private void fightButton(){
		fightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				testTextView.setText("Fight button clicked");
				
				// hide all the buttons and show the ability buttons
				fightButton.setVisibility(View.INVISIBLE);
				pokemonButton.setVisibility(View.INVISIBLE);
				itemsButton.setVisibility(View.INVISIBLE);
				runButton.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	private void pickMenuState(){
		// FIGHT, POKEMON, ITEMS, RUN  state 
		fightButton();
		pokemonButton();
		itemsButton();
		runButton();
	}
	
	private void pickAbilityState(){
		// set state to pick Ability state
		// hide previous 4 buttons
		// show Ability buttons
		
	}
	
	private void pokemonButton(){
		pokemonButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				testTextView.setText("Pokemon btn clicked");
			}
		});
	}
	
	private void itemsButton(){
		itemsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				testTextView.setText("Items button clicked");
			}
		});
	}
	
	private void runButton(){
		runButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(titleScreenIntent);
			}
		});
	}
}
