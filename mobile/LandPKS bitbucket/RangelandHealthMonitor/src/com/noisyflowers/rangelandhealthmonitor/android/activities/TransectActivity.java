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
 * TransectActivity.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.activities;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.TransectFragment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;

public class TransectActivity extends ActionBarActivity {
	private static final String TAG = TransectActivity.class.getName(); 
	
	public static final String TRANSECT_DIRECTION = "transectDirection";
	public static final String SITE_ID = "siteID";
	public static final String SITE_NAME = "siteName";
	public static final String DATE = "date";
	
	//public String transectDirection = null;
	public Transect.Direction transectDirection = null;
	private String siteID = null;
	public String siteName = null;
	public String date = null;
	//public Long transectID = null;
	public Transect transect = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.i(TAG, "onCreate() enter");
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		
	    Intent intent = getIntent();
	    transectDirection = Transect.Direction.valueOf(intent.getStringExtra(TRANSECT_DIRECTION));
	    siteID = intent.getStringExtra(SITE_ID);
	    siteName = intent.getStringExtra(SITE_NAME);
	    date = intent.getStringExtra(DATE);
		
	    //TODO: change to Transect model object
	    //transectID = RHMApplication.getInstance().getDatabaseAdapter().getsertTransectID(siteID, transectDirection);
	    //RHMApplication.getInstance().setTransectID(transectID); //TODO: experimental
	    transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(siteID, transectDirection, true);
	    RHMApplication.getInstance().setTransectID(transect.ID); //TODO: experimental
	    
		setContentView(R.layout.activity_transect);
	}
	
	
	@Override
	protected void onResume() {
		//Log.i(TAG, "onResume() enter");
        ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    String title = siteName;
	    if (date != null) {
	    	title = title + ", " + date;
	    }
	    actionBar.setTitle(title + " - " + getString(transectDirection.displayName) + " " + getString(R.string.activity_transect_transect)); 

	    //setContentView(R.layout.activity_ploteditlist);
		Fragment fragment = new TransectFragment();
		FragmentManager     fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.activity_transect_segment_list, fragment);
		ft.commit(); 		
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.transect, menu);
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
						SiteDetailActivity.class));
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

}
