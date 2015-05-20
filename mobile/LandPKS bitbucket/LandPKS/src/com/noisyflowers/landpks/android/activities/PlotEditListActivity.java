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
 * PlotEditListActivity.java
 */

package com.noisyflowers.landpks.android.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.LandPKSContract;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;
import com.noisyflowers.landpks.android.fragments.LandUseCoverFragment;
import com.noisyflowers.landpks.android.fragments.NameFragment;
import com.noisyflowers.landpks.android.fragments.PhotosFragment;
import com.noisyflowers.landpks.android.fragments.PlotEditListFragment;
import com.noisyflowers.landpks.android.fragments.ReviewFragment;
import com.noisyflowers.landpks.android.fragments.SlopeFragment;
import com.noisyflowers.landpks.android.fragments.SlopeShapeFragment;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment;
import com.noisyflowers.landpks.android.fragments.SpecialSoilConditionsFragment;
import com.noisyflowers.landpks.android.model.Plot;

/**
 * An activity representing a list of Plots. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link PlotDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PlotEditListFragment} and the item details (if present) is a
 * {@link PlotDetailFragment}.
 * <p>
 * This activity also implements the required {@link PlotEditListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class PlotEditListActivity extends /*FragmentActivity*/ActionBarActivity implements
		PlotEditListFragment.Callbacks {
	
	private static final String TAG = PlotEditListActivity.class.getName(); 

	private String plotName;
		
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */

	private boolean externalCall = false;
	
	private Plot oldPlot = null;
	private boolean onCreateExecuted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.i(TAG, "onCreate() enter");
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		
	    Intent intent = getIntent();
	    String action = intent.getAction();
	    //Log.i(TAG, "intent action = " + action);
	    
		externalCall = LandPKSContract.SITE_CHARACTERIZATION_ACTION.equals(action);
		if (externalCall){
			Plot inPlot = null;
			String plotIDStr = intent.getStringExtra(LandPKSContract.SITE_CHARACTERIZATION_ACTION_EXTRA_SITE_ID); 
			if (plotIDStr != null) {
				inPlot = LandPKSApplication.getInstance().getDatabaseAdapter().getPlot(Long.parseLong(plotIDStr));
			} else {
				inPlot = new Plot();
			}
			//This oldPlot thing is for weird devices that don't handle singleTop properly.  
			//Coming back from PlotEditDetail, we should only see onNewIntent.  This works as expected on a Nexus 7.
			//On my PocketPlus I see instead an onCreate that is passed the original implicit intent, followed by 
			//onNewIntent that is passed the new intent coming from PlotEditDetail.  I found no discussion of this 
			//online so no idea what's going on, but here's oldPlot to work around it.
			oldPlot = LandPKSApplication.getInstance().getPlot();
			onCreateExecuted = true;
			
		    LandPKSApplication.getInstance().setPlot(inPlot);
		}
		
		setContentView(R.layout.activity_ploteditlist);

		Plot plot = LandPKSApplication.getInstance().getPlot();
        plotName = plot.name;
        plotName = plotName == null || "".equals(plotName) ? getString(R.string.new_plot) : plotName;
	}
	

	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onNewIntent() enter");
		if (oldPlot != null && onCreateExecuted) {
			plotName = oldPlot.name;
	        plotName = plotName == null || "".equals(plotName) ? getString(R.string.new_plot) : plotName;
		    LandPKSApplication.getInstance().setPlot(oldPlot);
		} else {
			Plot plot = LandPKSApplication.getInstance().getPlot();
	        plotName = plot.name;
	        plotName = plotName == null || "".equals(plotName) ? getString(R.string.new_plot) : plotName;
	        if (Build.VERSION.SDK_INT >= 11) {
	        	invalidateOptionsMenu(); // get delete back if gone
	        } else {
	        	supportInvalidateOptionsMenu();
	        }
		}
		super.onNewIntent(intent);
	}


	@Override
	protected void onDestroy() {
		//Log.i(TAG, "onDestroy() enter");
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	@Override
	protected void onResume() {
		onCreateExecuted = false;
		
		Log.i(TAG, "onResume() enter");
        ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle(plotName); 

	    //setContentView(R.layout.activity_ploteditlist);
		Fragment fragment = new PlotEditListFragment();
		FragmentManager     fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.plot_edit_list, fragment);
		ft.commit(); 		
		super.onResume();
	}

	/**
	 * Callback method from {@link PlotEditListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int position) {
		Intent detailIntent = new Intent(this, PlotEditDetailActivity.class);
		detailIntent.putExtra("position", position);
		//detailIntent.putExtra("plotName", plotName);
		startActivity(detailIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		Plot plot = LandPKSApplication.getInstance().getPlot();
		//if (!getString(R.string.new_plot).equals(plotName)) {	
			getMenuInflater().inflate(R.menu.plot_edit_list, menu);
		//}
		if (getString(R.string.new_plot).equals(plotName) ||	//no delete if new plot
			plot.remoteID != null || plot.needsUpload == 1) {	//no delete if plot has not been uploaded and is not upload pending
			menu.removeItem(R.id.action_delete_plot);
		}
		return super.onCreateOptionsMenu(menu);
	}

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
				if (externalCall) {
					returnToExternalCaller();
				} else {
					NavUtils.navigateUpTo(this,
							new Intent(this, PlotListActivity.class));
				}
				return true;
			case R.id.action_delete_plot:
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setMessage(getString(R.string.plot_edit_list_activity_delete_confirm))
        			   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
        				   @Override
        				   public void onClick(DialogInterface dialog, int which) {
        						LandPKSApplication app = LandPKSApplication.getInstance();
        						Plot plot = app.getPlot();
        						app.getDatabaseAdapter().deletePlot(plot);
        						NavUtils.navigateUpTo(PlotEditListActivity.this,
        								new Intent(PlotEditListActivity.this, PlotListActivity.class));
        				   }
        			   })
		 			   .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
						   @Override
						   public void onClick(DialogInterface dialog, int which) {
		 				   }
		 			   });
        		AlertDialog alert = builder.create();
        		alert.show();

				//LandPKSApplication app = LandPKSApplication.getInstance();
				//Plot plot = app.getPlot();
				//app.getDatabaseAdapter().deletePlot(plot);
				return true;
				
			//***tentative	
	        case R.id.action_settings: {
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				settingsIntent.putExtra(SettingsActivity.SETTINGS_MODE, SettingsActivity.ALL);
				startActivityForResult(settingsIntent, 0);
	            return true;
			}
	            
	        case R.id.action_about: {
				Intent settingsIntent = new Intent(this, AboutActivity.class);
				startActivity(settingsIntent);
	            return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void returnToExternalCaller() {
		Intent intent = new Intent();
		//Interestingly, the ID is not in the plot held by LandPKSApplication.  It's never needed except here.  So I had to add getPlotID to the database adapter.
		Plot plot = LandPKSApplication.getInstance().getPlot();
		if (plot.name != null) {
			intent.putExtra(LandPKSContract.SITE_CHARACTERIZATION_ACTION_RETURN_EXTRA_SITE_ID, Long.toString(LandPKSApplication.getInstance().getDatabaseAdapter().getPlotID(plot.name, plot.recorderName)));
			intent.putExtra(LandPKSContract.SITE_CHARACTERIZATION_ACTION_RETURN_EXTRA_SITE_NAME, plot.name);
		}
		
		setResult(Activity.RESULT_OK, intent);
		finish();					

	}
	@Override
	public void onBackPressed() {
		if (externalCall) {
			returnToExternalCaller();
		} else {
			super.onBackPressed();
		}
			
	}

}
