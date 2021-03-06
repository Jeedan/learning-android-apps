package com.jeedanandroidapps.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends ActionBarActivity {
	private static final String TAG = "CheatActivity";
	private static final String EXTRA_ANSWER_IS_TRUE = "com.jeedanandroidapps.quiz.answer_is_true";
	public static final String EXTRA_ANSWER_SHOWN = "com.jeedanandroidapps.quiz.answer_shown";
	private static final String EXTRA_ANSWER_HAS_CHEATED = "com.jeedanandroidapps.quiz.cheated";
	public static final String KEY_INDEX = "index";
	
	private boolean mAnswerIsTrue;
	private boolean mHasCheated;
	private TextView mAnswerTextView;
	private Button mShowAnswerButton;
	
	private boolean mAnswerShown;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cheat);
		
		Log.d(TAG, "starting CheatActivity");

		mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
		mHasCheated = getIntent().getBooleanExtra(EXTRA_ANSWER_HAS_CHEATED, false);
		
		if(mHasCheated){
			mAnswerShown = true;
		}
		
		mAnswerTextView = (TextView)findViewById(R.id.answerTextView);
		mShowAnswerButton = (Button)findViewById(R.id.showAnswerButton);

		// getIntent is the intent of the Activity that started this activity
		if(savedInstanceState != null){
			mAnswerShown = savedInstanceState.getBoolean(KEY_INDEX, false);
		}else {
			if(!mHasCheated)
				mAnswerShown = false;
		}
	
		if(mAnswerShown || mHasCheated)
			showAnswerText(mAnswerIsTrue);
		
		setAnswerShownResult(mAnswerShown);
		mShowAnswerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAnswerText(mAnswerIsTrue);

				Log.d(TAG,"Setting mAnswerShown to True" );
				mAnswerShown = true;
				setAnswerShownResult(true);
			}
		});
	}
	
	private void showAnswerText(boolean show){
		if(show)
			mAnswerTextView.setText(R.string.true_button);
		else 
			mAnswerTextView.setText(R.string.false_button);
	}
	
	private void setAnswerShownResult(boolean isAnswerShown){
		Intent data = new Intent();
		data.putExtra(EXTRA_ANSWER_SHOWN, mAnswerShown);
		setResult(RESULT_OK, data);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		Log.i(KEY_INDEX, "onSaveInstanceState");
		Log.i(KEY_INDEX, "Saving mAnswerShown: true");
		savedInstanceState.putBoolean(KEY_INDEX, mAnswerShown);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cheat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
