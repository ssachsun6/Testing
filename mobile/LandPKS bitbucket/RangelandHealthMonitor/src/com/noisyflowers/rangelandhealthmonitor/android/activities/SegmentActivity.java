/**
 * 
 * Copyright 2014 Noisy Flowers LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 * com.noisyflowers.rangelandhealthmonitor.android.activities
 * SegmentActivity.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.SegmentFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.TransectFragment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;
import com.noisyflowers.rangelandhealthmonitor.android.util.SegmentListAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.util.RHMPagerAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.util.PersistenceFragment;

public class SegmentActivity extends ActionBarActivity {

	public static final String SEGMENT_INDEX = "segmentIndex";
	public static final String TRANSECT_ID = "transectID";
	public static final String SITE_NAME = "siteName";
	public static final String DATE = "date";
	public static final String TRANSECT_DIRECTION = "transectDirection";
	private int segmentIndex;
	private long transectID;
	private String segmentName;
	private String siteName;
	public String date;
	//private String transectName;
	private Transect.Direction transectDirection;
		
	private ViewPager pager;
	RHMPagerAdapter pagerAdapter;
	
	private View segmentFragmentView;

	public Segment segment = null;
	
	private Context context;
	
	List<Fragment> fragments;
	
	/***
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		
	    Intent intent = getIntent();
	    segmentIndex = intent.getIntExtra(SEGMENT_INDEX, 0);
	    transectID = intent.getLongExtra(TRANSECT_ID, -1); //TODO: better default (possibly change to string?

	    range = new Segment(segmentIndex, transectID);
	    
		setContentView(R.layout.activity_segment);
				
	}
	
	@Override
	protected void onResume() {
		//Log.i(TAG, "onResume() enter");
    	//List<Fragment> fragments = RHMApplication.getInstance().getSegmentFragments();
		//Fragment fragment = fragments.get(segmentIndex);
		List<Fragment> fragments = RHMApplication.getInstance().getSegmentDetailFragments();
		Fragment fragment = fragments.get(0);//TODO: 0 for now

		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle(fragment.getArguments().getString(SegmentListAdapter.ARG_NAME)); 

		FragmentManager     fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.segment_fragment_view, fragment);
		ft.commit(); 		

		super.onResume();
	}
	***/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_segment);

		context = this;
		
	    Intent intent = getIntent();
	    segmentIndex = intent.getIntExtra(SEGMENT_INDEX, 0);
	    transectID = intent.getLongExtra(TRANSECT_ID, -1); //TODO: better default (possibly change to string?
	    siteName = intent.getStringExtra(SITE_NAME); 
	    date = intent.getStringExtra(DATE); 
	    //transectName = intent.getStringExtra(TRANSECT_DIRECTION); 
	    transectDirection = Transect.Direction.valueOf(intent.getStringExtra(TRANSECT_DIRECTION)); 

	    //range = new Segment(segmentIndex, transectID);
	    //range = RHMApplication.getInstance().getDatabaseAdapter().getSegment(segmentIndex, transectID);
	    //segment = RHMApplication.getInstance().getDatabaseAdapter().getSegment(segmentIndex, transectID, new Date());
	    //segment = RHMApplication.getInstance().getDatabaseAdapter().getSegment(Segment.Range.values()[segmentIndex], transectID, new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date theDate;
		try {theDate = sdf.parse(date);} catch (Exception e) {theDate = new Date();}
	    segment = RHMApplication.getInstance().getDatabaseAdapter().getSegment(Segment.Range.values()[segmentIndex], transectID, theDate);
	    if (segment == null) {
	    	//segment = new Segment(segmentIndex, transectID);
	    	segment = new Segment(Segment.Range.values()[segmentIndex], transectID);
	    }
	    
	    
        pager = (ViewPager)findViewById(R.id.activity_segment_taskPager);
        /***
         * replaced below
        pager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });   
        ***/
        
		fragments = RHMApplication.getInstance().getSegmentDetailFragments();
		Fragment fragment = fragments.get(0);//TODO: 0 for now

		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    //actionBar.setTitle(fragment.getArguments().getString(SegmentListAdapter.ARG_NAME)); 
	    //actionBar.setTitle(siteName + " - " + transectName + ", " + TransectFragment.SegmentName.values()[segmentIndex].name); 
	    //actionBar.setTitle(siteName + " - " + transectDirection.displayName + ", " + TransectFragment.SegmentName.values()[segmentIndex].name); 
	    //actionBar.setTitle(siteName + " - " + getString(transectDirection.displayName) + ", " + Segment.Range.values()[segmentIndex].displayName); 
	    String title = siteName;
	    if (date != null) {
	    	title = title + ", " + date;
	    }
	    actionBar.setTitle(title + " - " + getString(transectDirection.displayName) + ", " + Segment.Range.values()[segmentIndex].getDisplayName()); 
		//setContentView(R.layout.activity_segment);
		
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            pager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // hide the given tab
	    	    //List<Fragment> fragments = LandPKSApplication.getInstance().getHorizonFragments();
	    	    //TODO:  call fragment's data store routine here
	    	    //Fragment f = fragments.get(tab.getPosition());
		        //boolean completed = false;
    			//Plot plot = LandPKSApplication.getInstance().getPlot();
    	    	//((PlotEditFragment) f).save(plot);
    	    	//completed = ((PlotEditFragment) f).isComplete(plot);

	        	Fragment f = fragments.get(tab.getPosition());
	    	    if (((PersistenceFragment)f).isComplete(segment)) {
	    	    //if (segment.isComplete()) {
					tab.setIcon(R.drawable.btn_check_buttonless_on);
				} else {
					tab.setIcon(null);
				}
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };

	    for (Fragment f: fragments) {
	    	String name = "";
			try {
				name = f.getArguments().getString(SegmentListAdapter.ARG_NAME);
				//((PersistenceFragment)f).load(range);
			} catch (Exception ex) {}
	    	
			ActionBar.Tab tab = actionBar.newTab().setText(name).setTabListener(tabListener);

    	    if (((PersistenceFragment)f).isComplete(segment)) {
				tab.setIcon(R.drawable.btn_check_buttonless_on);
			} else {
				tab.setIcon(null);
			}
			actionBar.addTab(tab);
	    }
	    
        //this.plotPagerAdapter  = new PlotPagerAdapter(super.getSupportFragmentManager(), fragments);
	    pagerAdapter  = new RHMPagerAdapter(super.getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0); //TODO: for now
        pager.setOnPageChangeListener(pageChangeListener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
	    for (Fragment f: fragments) {
				//((PersistenceFragment)f).load(range);
	    }

		//Log.i(TAG, "onResume() enter");
    	//List<Fragment> fragments = RHMApplication.getInstance().getSegmentFragments();
		//Fragment fragment = fragments.get(segmentIndex);

		//FragmentManager     fm = getSupportFragmentManager();
		//FragmentTransaction ft = fm.beginTransaction();
		//ft.replace(R.id.segment_fragment_view, fragment);
		//ft.commit(); 		

		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.segment, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (item.getItemId()) {
				case android.R.id.home: {
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				if (date == null) {				
					for (Fragment f: fragments) {
						((PersistenceFragment)f).save(segment);
					}
					segment.date = new Date(); 
					RHMApplication.getInstance().getDatabaseAdapter().upsertSegmentEntry(segment);
			    }
				NavUtils.navigateUpTo(this, new Intent(this,
						TransectActivity.class));
				return true;
			}

		        case R.id.action_about: {
					//Intent settingsIntent = new Intent(this, AboutActivity.class);
					//startActivity(settingsIntent);
		        	Toast.makeText(this, "About not yet implemented", Toast.LENGTH_SHORT).show();
		            return true;
				}
		
		        case R.id.action_help: {
					//Intent settingsIntent = new Intent(this, HelpActivity.class);
					//startActivity(settingsIntent);
		        	
		        	//FragmentManager fragManager = this.getSupportFragmentManager();
		            //int count = this.getSupportFragmentManager().getBackStackEntryCount();
		            //Fragment frag = fragManager.getFragments().get(count>0?count-1:count);

		        	//This is a little hacky, but it works for now
		            Fragment frag = getSupportFragmentManager().findFragmentByTag("android:switcher:" + pager.getId() + ":" + pager.getCurrentItem());
		    		TextView tV = new TextView(this);
		    		tV.setText("Help not available");
		    		View v = tV;
		            if (frag instanceof IHelp) {
		            	v = ((IHelp)frag).getHelpView();
		            }
		            
		    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		    				   @Override
		    				   public void onClick(DialogInterface dialog, int which) {
		    				   }
		    			   });
		    		builder.setView(v);
		    		AlertDialog alert = builder.create();
		    		alert.show();

		        	//Toast.makeText(this, "Help not yet implemented", Toast.LENGTH_SHORT).show();
		            return true;
				}
			}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (date != null) {
			super.onBackPressed();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Discard changes? (hint: use Up to save)")
			   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					   Intent upIntent = new Intent(context, TransectActivity.class);
					   upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					   startActivity(upIntent);
				   }
			   }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
 				   }
			   });
		AlertDialog alert = builder.create();
		alert.show();
	}	

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		//int oldPosition = pager.getCurrentItem();
		int oldPosition = 0;

		@Override
		public void onPageSelected(int newPosition) {
			if (date == null) {
				segment = ((PersistenceFragment)pagerAdapter.getItem(oldPosition)).save(segment);
			}
            getSupportActionBar().setSelectedNavigationItem(newPosition);
			oldPosition = newPosition;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) { }

		public void onPageScrollStateChanged(int arg0) { }
	};	
}
