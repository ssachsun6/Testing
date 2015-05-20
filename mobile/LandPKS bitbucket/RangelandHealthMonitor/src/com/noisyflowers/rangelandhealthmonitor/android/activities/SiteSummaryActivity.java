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
 * SiteSummaryActivity.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.activities;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.SiteDetailFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.SiteSummaryFragment;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SiteSummaryActivity extends FragmentActivity {
	public static final String SITE_ID = "siteID";
	public static final String SITE_NAME = "siteName";
	public static final String DATE = "date";

	public String siteID = null;
	public String siteName = null;
	public String date = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_site_summary);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(
					SiteDetailFragment.ARG_ITEM_ID,
					getIntent().getStringExtra(
							SiteSummaryFragment.ARG_ITEM_ID));
			SiteSummaryFragment fragment = new SiteSummaryFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.site_summary_container, fragment)
					.commit();
		}
		siteName = getIntent().getStringExtra(SITE_NAME);
		siteID = getIntent().getStringExtra(SITE_ID);
		date = getIntent().getStringExtra(DATE);
				
		String title;
		if (siteName != null) {
			title = siteName;
			if (date != null) {
				title = title + ", " + date;
			}
		} else {
			title = getString(R.string.new_site); 			
		}
		getActionBar().setTitle(title);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.site_summary, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpTo(this, new Intent(this,
						SiteDetailActivity.class));  //TODO: probably pass back site params
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
	        	FragmentManager fragManager = this.getSupportFragmentManager();
	            int count = this.getSupportFragmentManager().getBackStackEntryCount();
	            Fragment frag = fragManager.getFragments().get(count>0?count-1:count);
	            	    		
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
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(SITE_NAME, siteName);  
	  	savedInstanceState.putString(SITE_ID, siteID);    
	  	savedInstanceState.putString(DATE, date);    
	  	super.onSaveInstanceState(savedInstanceState);  
	}  
	
	@Override  
	public void onRestoreInstanceState(Bundle savedInstanceState) {  
		super.onRestoreInstanceState(savedInstanceState);  
		siteName = savedInstanceState.getString(SITE_NAME);  
		siteID = savedInstanceState.getString(SITE_ID);  
		date = savedInstanceState.getString(DATE);  
		/***
		if (siteName != null) {
			getActionBar().setTitle(siteName);
		} else {
			getActionBar().setTitle(getString(R.string.new_site)); 			
		}
		***/
		String title;
		if (siteName != null) {
			title = siteName;
			if (date != null) {
				title = title + ", " + date;
			}
		} else {
			title = getString(R.string.new_site); 			
		}
		getActionBar().setTitle(title);
	}

	public void onHelpClick(View view) {
		String dialogText = null;

		switch (view.getId()) {
			case R.id.fragment_site_summary_total_cover_help:
				dialogText = getString(R.string.fragment_site_summary_total_cover_help);
				break;
			case R.id.fragment_site_summary_bare_ground_help:
				dialogText = getString(R.string.fragment_site_summary_bare_ground_help);  //TODO: move all these to strings.xml
				break;
			case R.id.fragment_site_summary_foliar_cover_help:
				dialogText = getString(R.string.fragment_site_summary_foliar_cover_help);
				break;
			case R.id.fragment_site_summary_foliar_composition_help:
				dialogText = getString(R.string.fragment_site_summary_foliar_composition_help);
				break;
			case R.id.fragment_site_summary_canopy_height_help:
				dialogText = getString(R.string.fragment_site_summary_canopy_height_help);
				break;
			case R.id.fragment_site_summary_gaps_help:
				dialogText = getString(R.string.fragment_site_summary_gaps_help);
				break;
			case R.id.fragment_site_summary_species_density_help:
				dialogText = getString(R.string.fragment_site_summary_species_density_help);
				break;
			default:
				dialogText = "Placeholder text";
				break;
				
		}
		
		if (dialogText != null) {
			AlertDialog.Builder aB = new AlertDialog.Builder(this);
			aB.setMessage(dialogText);
			aB.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			AlertDialog aD = aB.create();
			aD.show();
		}
	}

}
