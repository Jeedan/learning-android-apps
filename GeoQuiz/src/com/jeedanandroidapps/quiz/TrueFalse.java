package com.jeedanandroidapps.quiz;

public class TrueFalse {
	private int mQuestion;
	private boolean mTrueQuestion;
	
	public TrueFalse(int question, boolean trueQuestion){
		mQuestion = question;
		mTrueQuestion = trueQuestion;
	}
	
	public int getQuestion(){
		return mQuestion;
	}
	
	public void setQuestion(int value){
		mQuestion = value;
	}
	
	public boolean getTrueQuestion(){
		return mTrueQuestion;
	}
	
	public void setTrueQuestion(boolean value){
		mTrueQuestion = value;
	}	
}
