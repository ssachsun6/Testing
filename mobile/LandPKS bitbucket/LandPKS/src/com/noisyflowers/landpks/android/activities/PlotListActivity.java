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
 * PlotListActivity.java
 */

package com.noisyflowers.landpks.android.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;
import com.noisyflowers.landpks.android.dal.RestClient;
import com.noisyflowers.landpks.android.fragments.NameFragment;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment;
import com.noisyflowers.landpks.android.model.Plot;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PlotListActivity extends ActionBarActivity {
	private static final String TAG = PlotListActivity.class.getName(); 

	//TODO: put these somewhere else
    private GoogleAccountCredential credential;
    private SharedPreferences settings;
    static final int REQUEST_ACCOUNT_PICKER = 2;
    static final int SETUP_GOOGLE_PLAY = 3;
    public static final String PREF_ACCOUNT_NAME_KEY = "PreferredAccountName";

    //private Menu actionBarMenu;
	
    private boolean showMap = false;
	private MenuItem listItem = null;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		SettingsActivity.setUITheme(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plot_list);	
		
        ActionBar actionBar = getSupportActionBar();
	    actionBar.setTitle(getString(R.string.app_name)); 

		//testing
		FragmentManager fM = getSupportFragmentManager();
		Fragment sF = fM.findFragmentById(R.id.plot_map);
		fM.beginTransaction().hide(sF).commit();
		
		
		/***
        credential = GoogleAccountCredential.usingAudience(this,
                "server:client_id:" + RestClient.WEB_CLIENT_ID);
		settings = getSharedPreferences("LandPKS", 0);
		setAccountName();
		***/
		if (LandPKSApplication.getInstance().getCredential().getSelectedAccountName() == null) {
			//chooseAccount();
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			settingsIntent.putExtra(SettingsActivity.SETTINGS_MODE, SettingsActivity.ACCOUNT_ONLY);
			startActivity(settingsIntent);
			finish();
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plot_list, menu);
		//this.actionBarMenu = menu;
		//menu.findItem(R.id.action_show_list).setVisible(false);
		
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
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_create_plot: {
			    LandPKSApplication.getInstance().setPlot(new Plot());
				Intent detailIntent = new Intent(this, PlotEditListActivity.class);
				//detailIntent.putExtra("position", 0);
				detailIntent.putExtra("fragment", NameFragment.class);
				startActivity(detailIntent);
				return true;
			}

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
			
	        case R.id.action_settings: {
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				settingsIntent.putExtra(SettingsActivity.SETTINGS_MODE, SettingsActivity.ALL);
				startActivityForResult(settingsIntent, SettingsActivity.REQUEST_CODE);
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
	            return true;
			}
	        
	        /*** hidden in About instead
	        case R.id.action_take_bug_report: {
        		Log.i(TAG, "Taking bug report");
	        	Process mLogcatProc = null;
	        	BufferedReader reader = null;
	        	try {
        	        //mLogcatProc = Runtime.getRuntime().exec(new String[] {"logcat", "-d", "com.noisyflowers.*:V AndroidRuntime:E  *:S"});
        	        //mLogcatProc = Runtime.getRuntime().exec(new String[] {"logcat", "-d", "com.noisyflowers.landpks.android:V"});
        	        mLogcatProc = Runtime.getRuntime().exec("logcat -d");
        	        reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
        	        String line;
        	        final StringBuilder log = new StringBuilder();
        	        String separator = System.getProperty("line.separator"); 
        	        while ((line = reader.readLine()) != null) {
        	        	if (line.contains("noisyflowers")) {
        	                log.append(line);
        	                log.append(separator);
        	        	}
        	        }
        	    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        	    	String accountName = settings.getString(PREF_ACCOUNT_NAME_KEY, null);
        	    	if (accountName != null) {
	        			Intent i = new Intent(Intent.ACTION_SEND);
	        			i.setType("text/plain");
	        			//i.putExtra(Intent.EXTRA_EMAIL, new String[] {"noisyflowers@douglasmeredith.net"});
	        			i.putExtra(Intent.EXTRA_SUBJECT, "Debug report from " + accountName);
	        			i.putExtra(Intent.EXTRA_TEXT,log.toString());
	    				PackageManager packageManager = getPackageManager();
	    				List<ResolveInfo> activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
	    				if (activities.size() > 0) {
		        			this.startActivity(Intent.createChooser(i, "Select application"));
	    				} else {
	    					Toast.makeText(this, "No SEND receiver is registered", Toast.LENGTH_LONG).show();
	    				}
        	    	}
	        	} catch (IOException e) {
	        		Log.e(TAG, "Error reading log", e);
	        	} finally {
	        		try { reader.close(); } catch (Exception e) {}
	        	} 
	        }
	        ***/
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onBackPressed() {
		if (showMap) {
			onOptionsItemSelected(listItem);
		} else {
			super.onBackPressed();
		}
	}	    
	
	private class DBBackupTask extends AsyncTask<Void, Void, String> {
        Context context;
		ProgressDialog progressDialog;
		
    	public DBBackupTask(Context context){
    		this.context = context;
    	}
    	
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(context, "", context.getString(R.string.plot_list_activity_backing_up_db), true);
		}
		
    	protected String doInBackground(Void... params) {
			return LandPKSDatabaseAdapter.copyToSD(context, LandPKSApplication.getInstance().getDatabaseAdapter().getDBPath());
       }

    	protected void onPostExecute(String backupFilePath) {
        	if (progressDialog.isShowing()) 
        		progressDialog.dismiss();
        	if (backupFilePath == null) {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    	builder.setMessage(getString(R.string.plot_list_activity_backing_up_db_error))
		    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		    				   @Override
		    				   public void onClick(DialogInterface dialog, int which) {
		     				   }
		    			   });
		    	AlertDialog alert = builder.create();
		    	alert.show(); 	
        	} else {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    	builder.setMessage(getString(R.string.plot_list_activity_backing_up_db_success) + "\n\n(" + backupFilePath + ")")
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
