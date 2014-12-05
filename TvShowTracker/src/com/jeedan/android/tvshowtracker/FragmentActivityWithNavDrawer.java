package com.jeedan.android.tvshowtracker;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class FragmentActivityWithNavDrawer extends FragmentActivity {

	private static final String TAG = "FragmentActivityWithNavDrawer";
	
    private static final String EXTRA_TEST = "testString";
	protected abstract Fragment createFragment();

	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerTitles;
    public ActionBarDrawerToggle mDrawerToggle;
    
    @TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_myshows);
		Log.d(TAG, "onCreate() FragmentActivityWithNavDrawer");

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			getActionBar().setDisplayHomeAsUpEnabled(true);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			getActionBar().setHomeButtonEnabled(true);
		
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerTitles));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_closed  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		if(fragment == null){
			fragment = createFragment();
			fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
		}
	}
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
        // update the main content by replacing fragments
        // update selected item and title, then close the drawer
    	switch (position) {
		case 0:
		{
			/* SEARCH SHOWS FRAGMENT */
			createSearchShowFragment();
			// position 1 in the drawer list
			//createSearchTVShowActivity(); /* use this if the fragment becomes buggy*/
			break;
		}
		case 1:
		{
			/* MY SHOWS FRAGMENT*/
			createMyShowsFragment();

			break;
		}
		case 2:
		{
			/* Calendar Today FRAGMENT*/
			//createCalendarTodayFragment();
			createCalendarShowActivity();
			break;
		}
		case 3:
		{
			/* About Me FRAGMENT*/
			//createAboutMeFragment();
		}
		default:
			break;
		}
		
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.myshow_menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }*/
	
	/* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }		
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    
    private void beginFragmentCreation(FragmentManager fm, int id, Fragment fragment){	
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.replace(id,fragment); // (id of fragment to replace, newFragment);
    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	ft.commit();
    }
    
    private void createSearchShowFragment(){
    	FragmentManager fm = getSupportFragmentManager();
		SearchShowsListFragment tvSearchFragment = (SearchShowsListFragment)fm.findFragmentById(R.id.fragmentListContainer);
		if(tvSearchFragment == null){
			String test = "Creating TVShowListFragment fragment";
			tvSearchFragment = SearchShowsListFragment.newInstance(test); // create a new fragment with the information you want to pass
			beginFragmentCreation(fm, R.id.fragmentContainer, (SearchShowsListFragment)tvSearchFragment);
		}else {
			createSearchTVShowActivity();
		}
    }
    
    private void createMyShowsFragment(){
    	FragmentManager fm = getSupportFragmentManager();
		MyShowsListFragment myShowsFragment = (MyShowsListFragment)fm.findFragmentById(R.id.fragmentListContainer);
		if(myShowsFragment == null){
			String test = "Creating TrackedShows fragment";
			myShowsFragment = MyShowsListFragment.newInstance(test); // create a new fragment with the information you want to pass
			beginFragmentCreation(fm, R.id.fragmentContainer, (MyShowsListFragment)myShowsFragment);
		}else {
			// create Tracked Activity
			createMyShowsActivity();
		}
    }    
    
    private void createCalendarTodayFragment(){
    	FragmentManager fm = getSupportFragmentManager();
    	CalendarShowsListFragment calendarShowsFragment = (CalendarShowsListFragment)fm.findFragmentById(R.id.fragmentListContainer);
    	
    	if(calendarShowsFragment == null){
    		String test = "Creating CalendarShows fragment";
    		calendarShowsFragment = CalendarShowsListFragment.newInstance(test);
    		beginFragmentCreation(fm, R.id.fragmentContainer, (CalendarShowsListFragment)calendarShowsFragment);
    	}
    }

    private void createCalendarShowActivity(){	
    	Intent intent = new Intent(this, CalendarShowsActivity.class);
    	String value = "CALENDAR ACTIVITY CREATED";
    	intent.putExtra(EXTRA_TEST, value);
    	startActivityForResult(intent, RESULT_OK);
    }   
    
    private void createSearchTVShowActivity(){	
    	Intent intent = new Intent(this, SearchShowsActivity.class);
    	String value = "THIS IS SOME LONG TEXT THAT I PASS AS AN EXTRA";
    	intent.putExtra(EXTRA_TEST, value);
    	startActivityForResult(intent, RESULT_OK);
    }   
    
    private void createMyShowsActivity(){	
    	Intent intent = new Intent(this, MyShowsActivity.class);
    	String value = "THIS IS SOME LONG TEXT THAT I PASS AS AN EXTRA";
    	intent.putExtra(EXTRA_TEST, value);
    	startActivityForResult(intent, RESULT_OK);
    }

}

