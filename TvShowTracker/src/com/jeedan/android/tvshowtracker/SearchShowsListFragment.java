package com.jeedan.android.tvshowtracker;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class SearchShowsListFragment extends ListFragment {
    private static final String TAG = "SearchShowsListFragment";

    public  static final String EXTRA_SHOW_TRACKED = "com.jeedan.android.tvshowtracker.tracked";
    private static final String EXTRA_TEST = "testString";

    private ArrayList<TVShow> mTVShows;
    private EditText searchEditText;

    private ShowDatabase showDB; 

    private String mShowURL;  // the edit text string we type will be stored so we can search tvrage with this string.
    private boolean mReturnedShows; // if we get shows back from searching, show a toast

    //TODO 
    private ImageView showImage;
    private String testString = "";


    public static SearchShowsListFragment newInstance(String test){
	SearchShowsListFragment tvLF = new SearchShowsListFragment();

	// supply input
	Bundle args = new Bundle();
	args.putString(EXTRA_TEST, test);
	tvLF.setArguments(args);
	return tvLF;
    }

    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	getActivity().setTitle(R.string.app_search_shows);
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    setHasOptionsMenu(true);
	    if(savedInstanceState != null){
		Log.d(TAG, "savedInstance is not null");
	    }
	}	

	// get arguments passed by from starting a new fragment
	if(getArguments() != null){
	    testString = getArguments().getString(EXTRA_TEST, "getArguments() did not work");	
	    Log.d(TAG, "getArguments: " + testString);
	}

	showDB = ShowDatabase.getInstance(getActivity());
	// get the tvshow list from the singleton ShowDatabase 
	mTVShows = showDB.getTVShows();

	// set up the adapter to display list view
	setupAdapter();
	setRetainInstance(true);// retain this fragment upon rotation
	getActivity().getActionBar().setHomeButtonEnabled(true);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
	View v = inflater.inflate(R.layout.fragment_alltvshowslist, parent, false);

	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

	// the edit text is where we type in the show we are looking for
	// we can search for more than 1 show at a time thanks to tvRage database's search functionality
	searchEditText = (EditText)v.findViewById(R.id.search_show_edit_text);
	searchEditText.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
		String str1 = s.toString().replaceAll(" ", "_"); // replaces all numbers with an _
		mShowURL = str1;
	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
		    int after) {

	    }

	    @Override
	    public void afterTextChanged(Editable s) {
	    }
	});

	searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		    searchShows();
		    hideKeyboard();
		    return true;
		}
		return false;
	    }
	});
	return v; 
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
	TVShow show = ((AllTvShowsAdapter)getListAdapter()).getItem(position);
	boolean tracking = !show.isTracked() ? true : false;
	show.setTracked(tracking);
	/*When you click on a tv show, add it to the "My tracked tv shows list" */
	String showName = show.getShowName().replaceAll(" ", "_"); // replaces all spaces with an _

	Log.d(TAG, "id " + show.getTvDb_id());

	if(showDB.containsShow(showName)){
	    Toast.makeText(getActivity(), R.string.already_tracking_toast, Toast.LENGTH_SHORT).show();
	}else
	{
	    show.setUpdatedInformation(false);
	    show.setBanner(null);
	    showDB.addTrackedShows(show); // add show only if we don't already track it
	    Toast.makeText(getActivity(), R.string.added_show_toast, Toast.LENGTH_SHORT).show();	
	}		

	((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPause(){
	super.onPause();
    }

    @Override
    public void onResume(){
	super.onResume();
	getActivity().setTitle(R.string.app_search_shows);
	((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	if (resultCode != Activity.RESULT_OK)
	    return;

	testString = data.getStringExtra(EXTRA_TEST);
	Log.d(TAG, "onActivityResult:" + testString);

	((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	// Inflate the menu; this adds items to the action bar	 if it is present.
	inflater.inflate(R.menu.fragment_show_list, menu);

    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	switch(item.getItemId()){
	case android.R.id.home:			
	    // check if we have a parent activity to navigate to
	    // if we do navigate there from here
	    if(NavUtils.getParentActivityIntent(getActivity()) != null){
		NavUtils.navigateUpFromSameTask(getActivity());
	    }
	    return true;
	case R.id.action_settings:
	    Toast.makeText(getActivity(), "One day this will open the settings window!", Toast.LENGTH_SHORT).show();
	    //searchShows();  we only used this because the enter button in the emulator did not work
	    return true;
	default:
	    return super.onOptionsItemSelected(item);

	}
    }

    public void setupAdapter(){
	//ArrayAdapter<TVShow> adapter = new ArrayAdapter(getActivity(), android.R.layout.list_item_tvshow, mTVShows);
	//AllTvShowsAdapter adapter = new AllTvShowsAdapter(mTVShows);	    			
	//setListAdapter(adapter);
	if(getActivity() == null && getListView() == null)return;

	if(mTVShows != null){
	    Log.d(TAG, "setupAdapter");
	    AllTvShowsAdapter adapter = new AllTvShowsAdapter(mTVShows);

	    setListAdapter(adapter);
	}else {
	    setListAdapter(null);
	    Log.d(TAG, "setupAdapter failed");
	}

    }

    private void searchShows(){
	//if(mTVShows.size() <= 0) // only load if we haven't loaded already

	if(mShowURL != null)
	    new FetchShowInfo(getActivity()).execute(); // create a task that loads show information from the api
	else
	    Toast.makeText(getActivity(), R.string.enter_show_search_toast, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    // ArrayAdapter for the listView
    private class AllTvShowsAdapter extends ArrayAdapter<TVShow>{

	public AllTvShowsAdapter(ArrayList<TVShow> tvShows) {
	    super(getActivity(),0, tvShows);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

	    // if we weren't given a view, inflate one
	    if(convertView == null)
		convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_tvshow, null);

	    TVShow show = getItem(position);

	    // show name
	    TextView nameTextView = (TextView)convertView.findViewById(R.id.tv_show_name_text_view);
	    nameTextView.setText(show.getShowName());

	    // Overview
	    TextView overViewTextView = (TextView)convertView.findViewById(R.id.overview_text_view);
	    overViewTextView.setText("Overview: " + show.getOverView());

	    // network
	    TextView networkTextView = (TextView)convertView.findViewById(R.id.network_text_view);
	    networkTextView.setText("Network: " + show.getNetwork());

	    showImage = (ImageView)convertView.findViewById(R.id.show_icon_imageView);

	    if(show.getBanner() != null)
		showImage.setImageBitmap(show.getBanner());

	    return convertView;
	}
    }

    // fetch tvDB id 
    private class FetchShowInfo extends AsyncTask<Void, Void, ArrayList<TVShow>> {

	private ProgressDialog dialog;
	public FetchShowInfo(Context context){
	    dialog = new ProgressDialog(context);
	}

	@Override
	protected ArrayList<TVShow> doInBackground(Void... params) {
	    // fetch either the latest episode or all episodes for a show
	    //	ArrayList<TVShow> fetchedShow =  new XMLSerializer().fetchItemsTVRage(mShowURL); // use the edit text here
	    String showName_TV_DB = mShowURL.replaceAll(" ", "%20"); // replaces all spaces with an _
	    ArrayList<TVShow>  fetchedShow  = new XMLSerializer().fetchTVdb_ID(showName_TV_DB);
	    mReturnedShows = fetchedShow.size() > 0;

	    return fetchedShow;
	}

	@Override 
	protected void onPreExecute(){
	    dialog.setMessage("Searching for Shows");
	    dialog.show();
	}

	@Override 
	protected void onPostExecute(ArrayList<TVShow> shows){
	    super.onPostExecute(shows);
	    if(dialog.isShowing()) dialog.dismiss(); // close the dialog
	    if(getActivity() != null){
		mTVShows = shows;
		setupAdapter();
		for(int i = 0; i < mTVShows.size(); i++){
		    new DownloadBanner(getActivity(),mTVShows.get(i)).execute(); 
		}

		if(!mReturnedShows){
		    Toast.makeText(getActivity(), R.string.error_fetched_toast, Toast.LENGTH_LONG).show();
		}
	    }
	}
    }

    private class DownloadBanner extends AsyncTask<Void, Void,ArrayList<TVShow>> {
	private TVShow show;
	private Bitmap bm;

	public DownloadBanner (Context context, TVShow show){
	    this.show = show;
	}

	@Override
	protected ArrayList<TVShow> doInBackground(Void... params) {

	    if(show.getBannerURL() != null){
		new XMLSerializer().downloadBitmap(show.getBannerURL(), show, bm);
	    }

	    return null;
	}

	@Override 
	protected void onPostExecute(ArrayList<TVShow> shows){
	    super.onPostExecute(shows);

	    ((AllTvShowsAdapter)getListAdapter()).notifyDataSetChanged();

	    if(!mReturnedShows){
		Toast.makeText(getActivity(), R.string.error_fetched_toast, Toast.LENGTH_LONG).show();		    
	    }
	    else{
		//Toast.makeText(getActivity(), R.string.fetched_info_toast, Toast.LENGTH_SHORT).show();
	    }
	}
    }
}
