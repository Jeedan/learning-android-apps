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
import android.widget.Toast;

public class QuizActivity extends ActionBarActivity {
	
	private static final String TAG = "QuizActivity";
	private static final String KEY_INDEX = "index";
	private static final String EXTRA_ANSWER_IS_TRUE = "com.jeedanandroidapps.quiz.answer_is_true";
	private static final String EXTRA_ANSWER_HAS_CHEATED = "com.jeedanandroidapps.quiz.cheated";
	
	private Button mTrueButton;
	private Button mFalseButton;
	private Button mNextButton;
	private Button mPreviousButton;
	private Button mCheatButton;
	private TextView mQuestionTextView;
	
	private TrueFalse[] mQuestionBank = new TrueFalse[] {
			new TrueFalse(R.string.question_oceans, true),
			new TrueFalse(R.string.question_mideast, false),
			new TrueFalse(R.string.question_africa, false),
			new TrueFalse(R.string.question_americas, true),
			new TrueFalse(R.string.question_asia, true) 
	};
	
	private int mCurrentIndex = 0;
	private boolean[] mIsCheater = new boolean[5];

	//private ImageButton mNextImageButton;
	//private ImageButton mPreviousImageButton;
	
	private void updateQuestion(){
		int question = mQuestionBank[mCurrentIndex].getQuestion();
		mQuestionTextView.setText(question);
	}
	
	private void checkAnswer(boolean userPressedTrue){

		boolean answerisTrue = mQuestionBank[mCurrentIndex].getTrueQuestion();
		int messageResId = 0;
		boolean cheated = mIsCheater[mCurrentIndex];
		
		if(cheated){
			messageResId = R.string.judgment_toast;
		}else {
			if(userPressedTrue == answerisTrue)
				messageResId = R.string.correct_toast;
			else
				messageResId = R.string.incorrect_toast;
		}
		
		Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate(Bundle) called");
		setContentView(R.layout.activity_quiz);

		if(savedInstanceState != null){
			mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
			mIsCheater[mCurrentIndex] = savedInstanceState.getBoolean(KEY_INDEX,false);
		}else {
			for(boolean cheated : mIsCheater)
				cheated = false;
		}
		
		mQuestionTextView = (TextView)findViewById(R.id.question_TextView);;
		mQuestionTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
				updateQuestion();
			}
		});
		
		mTrueButton = (Button)findViewById(R.id.true_button);
		mTrueButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkAnswer(true);
					
			}
		});

		mFalseButton = (Button)findViewById(R.id.false_button);
		mFalseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkAnswer(false);
			}
		});
	
		mNextButton = (Button)findViewById(R.id.next_button);
		mNextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// show next question
				mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
				updateQuestion();
			}
		});
		
		mPreviousButton = (Button)findViewById(R.id.previous_button);
		mPreviousButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// show next question
				if(mCurrentIndex > 0)
					mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
				else 
					Toast.makeText(QuizActivity.this, R.string.no_more_previous_toast, Toast.LENGTH_SHORT).show();
				
				updateQuestion();
			}
		});
		
		mCheatButton = (Button)findViewById(R.id.cheat_button);
		mCheatButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// show cheat activity
				Intent cheatIntent = new Intent(QuizActivity.this, CheatActivity.class);
				
				Log.d(TAG, "starting CheatActivity");
				boolean answerIsTrue = mQuestionBank[mCurrentIndex].getTrueQuestion();
				boolean hasCheated = mIsCheater[mCurrentIndex];
				cheatIntent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
				cheatIntent.putExtra(EXTRA_ANSWER_HAS_CHEATED, hasCheated);
				startActivityForResult(cheatIntent, 0);
			}
		});
		
		updateQuestion(); // uppdate the display
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		
		Log.i(KEY_INDEX, "onSaveInstanceState");
		
		savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
		
		savedInstanceState.putBoolean(KEY_INDEX, mIsCheater[mCurrentIndex]);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode , Intent data){
		if(data == null)
			return;
		
		mIsCheater[mCurrentIndex] = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
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
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG, "onStart() called");
	}

	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG, "onPause() called");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG, "onResume() called");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		Log.d(TAG, "onStop() called");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "onDestroy() called");
	}
}
