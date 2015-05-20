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
 * com.noisyflowers.landpks.android.activities
 * PlotEditDetailActivity.java
 */

package com.noisyflowers.landpks.android.activities;

import java.util.ArrayList;
import java.util.List;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.fragments.LandUseCoverFragment;
import com.noisyflowers.landpks.android.fragments.NameFragment;
import com.noisyflowers.landpks.android.fragments.PhotosFragment;
import com.noisyflowers.landpks.android.fragments.ReviewFragment;
import com.noisyflowers.landpks.android.fragments.SlopeFragment;
import com.noisyflowers.landpks.android.fragments.SlopeShapeFragment;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment;
import com.noisyflowers.landpks.android.fragments.SpecialSoilConditionsFragment;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.IPlotEditTask;
import com.noisyflowers.landpks.android.util.PlotEditFragment;
import com.noisyflowers.landpks.android.util.PlotEditPagerAdapter;
import com.noisyflowers.landpks.android.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import 	android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Main Activity.
 * 
 * This activity starts up the RegisterActivity immediately, which communicates
 * with your App Engine backend using Cloud Endpoints. It also receives push
 * notifications from backend via Google Cloud Messaging (GCM).
 * 
 * Check out RegisterActivity.java for more details.
 */
public class PlotEditDetailActivity extends /*FragmentActivity*/ActionBarActivity {
	
	public static final String POSITION = "position";
	
	//private PlotPagerAdapter plotPagerAdapter;
	private PlotEditPagerAdapter plotPagerAdapter;
	private ViewPager pager;
	//private String plotName;
	boolean newPlot = false;
	
	Context context;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        Intent intent = getIntent();

    	List<Fragment> fragments = LandPKSApplication.getInstance().getEditFragments();

        Class<Fragment> fragmentClass = (Class<Fragment>)intent.getSerializableExtra("fragment");
    	int position = 0;
        if (fragmentClass != null) {
	    	for (Fragment f : fragments) {
	    		if (fragmentClass.isInstance(f))
	    			break;
	    		position++;
	    	}
	    	position = position > fragments.size() ? 0 : position;
        } else {
        	position = intent.getIntExtra(POSITION, 0);      
        }
       
    	
        Plot plot = LandPKSApplication.getInstance().getPlot();
        String plotName = plot.name;
        if (plotName == null) {  
        	newPlot = true;
        	//plotName = "New Plot";
        	plotName = getString(R.string.new_plot);
        }

        pager = (ViewPager)super.findViewById(R.id.pager);
        pager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    	//ActionBar aB = getSupportActionBar();
                    	//if (aB.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS)
                    	//	aB.setSelectedNavigationItem(position);
                    } 
                });

        ActionBar actionBar = getSupportActionBar();
    	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    //actionBar.setTitle(plotName + " Details"); //TODO: put in strings
	    actionBar.setTitle(plotName + " " + getString(R.string.plot_edit_detail_activity_title)); //TODO: put in strings

	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	        	//Plot plot = LandPKSApplication.getInstance().getPlot();
	        	//if (plot.name == null || "".equals(plot.name)) {
	        	//	getSupportActionBar().setSelectedNavigationItem(0);
	        	//	pager.setCurrentItem(0);
	        	//} else
	            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	            imm.hideSoftInputFromWindow(pager.getApplicationWindowToken(), 0);
	            pager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	    	    Fragment f = plotPagerAdapter.getItem(tab.getPosition());
		        boolean completed = false;
	    	    if (f instanceof PlotEditFragment) {
	    			Plot plot = LandPKSApplication.getInstance().getPlot();
	    			//isAdded insures that the fragment is attached to the activity.  Had occasional and unpredictable problems with this.
	    			if (f.isAdded()) { 
	    	    		((PlotEditFragment) f).save(plot);
	    	    	}
		    		completed = ((PlotEditFragment) f).isComplete(plot);
	    	    }
	    	    
				if (completed) {
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
				name = f.getArguments().getString("Name");
			} catch (Exception ex) {}
	    	
			ActionBar.Tab tab = actionBar.newTab().setText(name).setTabListener(tabListener);

			if (((PlotEditFragment) f).isComplete(plot)) {
				tab.setIcon(R.drawable.btn_check_buttonless_on);
			} else {
				tab.setIcon(null);
			}
			actionBar.addTab(tab);
	    }
	    
        this.plotPagerAdapter  = new PlotEditPagerAdapter(super.getSupportFragmentManager(), fragments);
        pager.setAdapter(this.plotPagerAdapter);
        pager.setCurrentItem(position);
      

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private boolean aborted = false;
	public boolean isAborted() {
		return aborted;
	}
	@Override
	public void onBackPressed() {
		if (newPlot) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(getString(R.string.name_fragment_abort_new_plot))
				   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					   @Override
					   public void onClick(DialogInterface dialog, int which) {
						   aborted = true;
						   //Intent upIntent = new Intent(context, PlotListActivity.class);
						   Intent upIntent = new Intent(context, PlotEditListActivity.class);
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
		} else {
			//super.onBackPressed();
			Intent upIntent = new Intent(this, PlotEditListActivity.class);
			upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			NavUtils.navigateUpTo(this, upIntent);
		}
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.plot_edit_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}


	/***
	//TODO: Experimentally moved this to PlotEditFragment.  Watch for problems.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent upIntent = new Intent(this, PlotEditListActivity.class);
			//upIntent.putExtra("plotName", plotName);
			NavUtils.navigateUpTo(this, upIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	***/
	
	public boolean isNewPlot() {
		return newPlot;
	}
	
}



