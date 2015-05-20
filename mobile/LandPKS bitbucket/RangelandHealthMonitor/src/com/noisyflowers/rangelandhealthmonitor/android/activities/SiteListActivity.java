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
 * SiteListActivity.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.noisyflowers.landpks.android.LandPKSContract;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SettingsActivity;
import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMDatabaseAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMRestClient;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.SiteDetailFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.SiteListFragment;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;

/**
 * An activity representing a list of TransectSegments. This activity has
 * different presentations for handset and tablet-size devices. On handsets, the
 * activity presents a list of items, which when touched, lead to a
 * {@link SiteDetailActivity} representing item details. On tablets,
 * the activity presents the list of items and item details side-by-side using
 * two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SiteListFragment} and the item details (if present) is a
 * {@link SiteDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SiteListFragment.Callbacks} interface to listen for item
 * selections.
 */
//public class SiteListActivity extends FragmentActivity implements
public class SiteListActivity extends ActionBarActivity implements
//public class SiteListActivity extends PlotListActivity implements
		SiteListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	private static final int MIN_LANDPKS_VERSION_CODE = 33;
	
	private boolean isPackageInstalled(String packagename, Context context) {
		boolean retVal = false;
	    PackageManager pm = context.getPackageManager();
	    try {
	        PackageInfo pI = pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        if (pI.versionCode >= MIN_LANDPKS_VERSION_CODE) retVal = true;
	    } catch (NameNotFoundException e) {
	        retVal = false;
	    }
	    return retVal;
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SettingsActivity.setUITheme(this);

		super.onCreate(savedInstanceState);

		if (!isPackageInstalled("com.noisyflowers.landpks.android", this)) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(getString(R.string.SiteListActivity_LandPKS_install_message))
	    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
	    				   @Override
	    				   public void onClick(DialogInterface dialog, int which) {
	    					   finish();
	     				   }
	    			   });
	    	AlertDialog alert = builder.create();
	    	alert.show(); 			
		} else {
			String URL = LandPKSContract.CONTENT_URI + "/" + LandPKSContract.USER_ACCOUNT_PATH;  
	        Uri uri = Uri.parse(URL);
			String[] cols = {LandPKSContract.ACCOUNT_COLUMN_NAME};
			Cursor cursor = this.getContentResolver().query(uri, cols, null, null, null);  
			if (cursor.moveToFirst()) {
				String accountName = cursor.getString(0);
				if (accountName == null) {
			    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    	builder.setMessage(getString(R.string.SiteListActivity_LandPKS_configure_message))
			    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			    					   finish();
			     				   }
			    			   });
			    	AlertDialog alert = builder.create();
			    	alert.show(); 	
				} else {
			    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit(); 
					editor.putString(RHMApplication.PREF_ACCOUNT_NAME_KEY, accountName);
					editor.commit();
		
					//new LoadSiteNamesTask().execute();
					
					//Note: The fragment initiates the LPKS Loader and must have a valid account name in shared prefs.  
					//setContentView must be called *after* this is obtained.
					setContentView(R.layout.activity_site_list);
			
					if (findViewById(R.id.site_detail_container) != null) {  //TODO: I've commented this out in the xml files for now
						// The detail container view will be present only in the
						// large-screen layouts (res/values-large and
						// res/values-sw600dp). If this view is present, then the
						// activity should be in two-pane mode.
						mTwoPane = true;
			
						// In two-pane mode, list items should be given the
						// 'activated' state when touched.
						//((SiteListFragment) getSupportFragmentManager()
						//		.findFragmentById(R.id.site_list))
						//		.setActivateOnItemClick(true);
					}
		
				}
			}
			cursor.close();
		}
		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link SiteListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String siteStr) {
		//I can't decide if this is lame or not. Need to get both name and id to SiteDetailActivity, but callback 
		//interface only has one string param. I didn't choose to use the callback interface; it was included by the Google
		//code generator.  Could probably get along just fine without it, as we did in LandPKS, just letting fragment start
		//SiteDetailActivity.  So TODO: possibly get rid of callback interface here.
		String[] siteSplit = siteStr.split("/", 2);
		if (siteSplit.length != 2) return;
		String siteID = siteSplit[0];
		String siteName = siteSplit[1];
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			//arguments.putString(SiteDetailFragment.ARG_ITEM_ID, id);
			arguments.putString("siteName", siteName);
			arguments.putString("siteID", siteID);
			SiteDetailFragment fragment = new SiteDetailFragment();
			fragment.setArguments(arguments);
			//getSupportFragmentManager().beginTransaction()
			//		.replace(R.id.site_detail_container, fragment)
			//		.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					SiteDetailActivity.class);
			//detailIntent.putExtra(SiteDetailFragment.ARG_ITEM_ID, id);
			detailIntent.putExtra(SiteDetailActivity.SITE_NAME, siteName);
			detailIntent.putExtra(SiteDetailActivity.SITE_ID, siteID);
			startActivity(detailIntent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.site_list, menu);

		/***
		listItem = menu.findItem(R.id.action_show_list);

		if (showMap) {
			menu.findItem(R.id.action_show_map).setVisible(false);
			menu.findItem(R.id.action_show_list).setVisible(true);
		} else {
			menu.findItem(R.id.action_show_list).setVisible(false);
			//only show map icon when plots are present
			LandPKSDatabaseAdapter dbAdapter = LandPKSApplication.getInstance().getDatabaseAdapter();
			List<Plot> plotList = dbAdapter.getPlots();
			if (plotList.isEmpty()) {
				menu.findItem(R.id.action_show_map).setVisible(false);	
			} else {
				menu.findItem(R.id.action_show_map).setVisible(true);	
				
			}
		}
		***/
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_create_plot: {
				Intent detailIntent = new Intent(this, SiteDetailActivity.class);
				startActivity(detailIntent);
				return true;
			}
			
			/***
			case R.id.action_show_list: {
				item.setVisible(false);
				//actionBarMenu.findItem(R.id.action_show_map).setVisible(true);
				showMap = false;
				//invalidateOptionsMenu();
				supportInvalidateOptionsMenu();
				FragmentManager fM = getSupportFragmentManager();
				Fragment listFrag = fM.findFragmentById(R.id.plot_list);
				Fragment mapFrag = fM.findFragmentById(R.id.plot_map);
				fM.beginTransaction().hide(mapFrag).show(listFrag).commit();
				return true;
			}
			
			case R.id.action_show_map: {
				item.setVisible(false);
				//actionBarMenu.findItem(R.id.action_show_list).setVisible(true);
				showMap = true;
				//invalidateOptionsMenu();
				supportInvalidateOptionsMenu();
				FragmentManager fM = getSupportFragmentManager();
				Fragment listFrag = fM.findFragmentById(R.id.plot_list);
				Fragment mapFrag = fM.findFragmentById(R.id.plot_map);
				fM.beginTransaction().hide(listFrag).show(mapFrag).commit();
				return true;
			}
			***/
			
	        case R.id.action_settings: {
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				settingsIntent.putExtra(SettingsActivity.SETTINGS_MODE, SettingsActivity.ALL);
				startActivityForResult(settingsIntent, SettingsActivity.REQUEST_CODE);
	        	//Toast.makeText(this, "Settings not yet implemented", Toast.LENGTH_SHORT).show();
	            return true;
			}
	        
	        case R.id.action_copy_to_sd: {
	        	//TODO: include db file as attachment in debug email
	        	new DBBackupTask(this).execute();
	        	return true;
	        }
	            
	        case R.id.action_about: {
				Intent settingsIntent = new Intent(this, AboutActivity.class);
				startActivity(settingsIntent);
	        	//Toast.makeText(this, "About not yet implemented", Toast.LENGTH_SHORT).show();
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

	            /**
	            String s = "Help not available";
	            if (frag instanceof IHelp) {
	            	View tV = ((IHelp)frag).getHelpView();
	            	s = ((TextView)tV).getText().toString();
	            }
	        	Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	        	**/
	            return true;
			}

		}
		return super.onOptionsItemSelected(item);
	}
	
    private class LoadSiteNamesTask extends AsyncTask<Void, Void, Boolean> {
        //Context context;
		ProgressDialog progressDialog;

    	public LoadSiteNamesTask(/*Context context*/){
    		//this.context = context;
    	}
    	
		//protected void onPreExecute() {
			//progressDialog = ProgressDialog.show(context, "", context.getString(R.string.plot_list_activity_fetching_plots), true);
		//}
        
    	protected Boolean doInBackground(Void... params) {
    		RHMRestClient restClient = RHMRestClient.getInstance(getApplicationContext());
    		List<String> siteNames = restClient.fetchPlotNames();
			return false;
       }
    }

	private class DBBackupTask extends AsyncTask<Void, Void, String> {
        Context context;
		ProgressDialog progressDialog;
		
    	public DBBackupTask(Context context){
    		this.context = context;
    	}
    	
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(context, "", context.getString(R.string.SiteListActivity_backing_up_db), true);
		}
		
    	protected String doInBackground(Void... params) {
			return RHMDatabaseAdapter.copyToSD(context, RHMApplication.getInstance().getDatabaseAdapter().getDBPath());
       }

    	protected void onPostExecute(String backupFilePath) {
        	if (progressDialog.isShowing()) 
        		progressDialog.dismiss();
        	if (backupFilePath == null) {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    	builder.setMessage(getString(R.string.SiteListActivity_backing_up_db_error))
		    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		    				   @Override
		    				   public void onClick(DialogInterface dialog, int which) {
		     				   }
		    			   });
		    	AlertDialog alert = builder.create();
		    	alert.show(); 	
        	} else {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    	builder.setMessage(getString(R.string.SiteListActivity_backing_up_db_success) + "\n\n(" + backupFilePath + ")")
		    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		    				   @Override
		    				   public void onClick(DialogInterface dialog, int which) {
		     				   }
		    			   });
		    	AlertDialog alert = builder.create();
		    	alert.show(); 	
        	}
        }
		
	}

}
