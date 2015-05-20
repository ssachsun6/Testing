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
 * SettingsActivity.java
 */

package com.noisyflowers.landpks.android.activities;

import java.util.Date;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final int REQUEST_CODE = 0xd0;
    static final int REQUEST_ACCOUNT_PICKER = 2;
    static final int SETUP_GOOGLE_PLAY = 3;
    public static final String SETTINGS_MODE = "settings_mode";
    public static final String RETURN_CLASS = "return_class";
    public static final int NOT_SPECIFIED = 0;
    public static final int ALL = 1;
    public static final int ACCOUNT_ONLY = 2;
    public static final String UI_THEME_KEY = "pref_ui_theme"; //TODO: figure way to put these in one place
    public static final String WIFI_ONLY_KEY = "pref_sync_wifi_only"; //TODO: figure way to put these in one place
    
    private Context context;
    //private boolean calledFromApp;
    private int mode;
    private String returnClassName;
    
	public static void setUITheme(Context context) {
		try {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			context.setTheme(sharedPref.getBoolean(SettingsActivity.UI_THEME_KEY, true) ? 
								R.style.AppTheme : R.style.AppThemeDark);			
		} catch (Exception e) {}
	}

	public static Integer getUITheme(Context context) {
		Integer themeID = null;
		try {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			themeID = sharedPref.getBoolean(SettingsActivity.UI_THEME_KEY, true) ? R.style.AppTheme : R.style.AppThemeDark;			
		} catch (Exception e) {}
		return themeID;
	}
	
	/*
	 * To get list screen to refresh with new theme.
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	// This is necessary to force activity restart so that color scheme takes effect
	@Override
	public void onBackPressed() {
		if (mode != NOT_SPECIFIED) { //called from app so return to app
		//if (calledFromApp) {
			
			Class returnClass;
			try {
				returnClass = Class.forName(returnClassName);
			} catch (Exception eX) {
				returnClass = PlotListActivity.class;
			}
			Intent intent = new Intent(this, returnClass);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
		    
			/***
			setResult(1, null); 
			finish();
			***/
		} else { // not called from app so return wherever parent wants
			super.onBackPressed();
		}
		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setUITheme(this);
        super.onCreate(savedInstanceState);
        context = this;
        
        Intent intent = getIntent();
        mode = intent.getIntExtra(SETTINGS_MODE, NOT_SPECIFIED);

        returnClassName = intent.getStringExtra(RETURN_CLASS);
        if (returnClassName == null) {
			try {
				returnClassName = getCallingActivity().getClassName();
			} catch (Exception eX) {
				returnClassName = PlotListActivity.class.getName();
			}
        }
            
        //calledFromApp = (mode != NOT_SPECIFIED);
        //mode = mode == NOT_SPECIFIED ? ALL : mode;
                
        if (mode == ACCOUNT_ONLY) {
        	chooseAccount();
        } else {
	        addPreferencesFromResource(R.xml.preferences);
	
	        Preference acctValue = (Preference)findPreference("pref_google_account");
	        acctValue.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
	        	@Override
	        	public boolean onPreferenceClick(Preference preference) {
			    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
			    	builder.setMessage(getString(R.string.pref_account_change_warning))
			    			   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			    					   chooseAccount();
			     				   }
			    			   })
			    			   .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			     				   }
			    			   });
			    	AlertDialog alert = builder.create();
			    	alert.show();
	        		
	        		//chooseAccount();     
	        		return false;
	        	}               
	        });
	        
	        acctValue.setSummary(LandPKSApplication.getInstance().getCredential().getSelectedAccountName());
        }        
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
        	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        } catch (Exception e) {}
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        try {
        	getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        } catch (Exception e) {}
    }
    
    //TODO: move this to application?
    private void chooseAccount() {
    	int playStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if (playStatus != ConnectionResult.SUCCESS) {
    		if (GooglePlayServicesUtil.isUserRecoverableError(playStatus)) {
    			GooglePlayServicesUtil.getErrorDialog(playStatus, this, SETUP_GOOGLE_PLAY).show();
    		} else {
    			//Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG);
    			Toast.makeText(this, getString(R.string.plot_list_activity_device_not_supported_message), Toast.LENGTH_LONG);
    			finish();
    		}
    	} else {
    		startActivityForResult(LandPKSApplication.getInstance().getCredential().newChooseAccountIntent(),
    				REQUEST_ACCOUNT_PICKER);
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case SETUP_GOOGLE_PLAY:
        	if (resultCode == RESULT_CANCELED) {
        		//Toast.makeText(this, "Google Play Services must be installed.", Toast.LENGTH_SHORT);
        		Toast.makeText(this, getString(R.string.plot_list_activity_google_play_services_needed_message), Toast.LENGTH_SHORT).show();
        	} else {
        		startActivityForResult(LandPKSApplication.getInstance().getCredential().newChooseAccountIntent(),
        				REQUEST_ACCOUNT_PICKER);
        	}
        	break;
        case REQUEST_ACCOUNT_PICKER:
            if (data != null && data.getExtras() != null) {
                String accountName = data.getExtras().getString(
                        AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
            		LandPKSApplication.getInstance().getCredential().setSelectedAccountName(accountName);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    String oldAccountName = PreferenceManager.getDefaultSharedPreferences(this).getString(LandPKSApplication.PREF_ACCOUNT_NAME_KEY, "");
                    editor.putString(LandPKSApplication.PREF_ACCOUNT_NAME_KEY, accountName);
        			editor.commit();
                    // User is authorized.
        			
        			Intent accountChangeBroadcast = new Intent("com.noisyflowers.landpks.android.accountChange");
        			accountChangeBroadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        			accountChangeBroadcast.putExtra("accountName", accountName); //TODO: put this in constant somewhere
        			sendBroadcast(accountChangeBroadcast);
        			
        			new LoadUserPlotsTask(this, !accountName.equals(oldAccountName)).execute(); //TODO: This, and the changes to the task below, prevent un-uploaded plots from being deleted if the user selects that same account as previous.  Make sure we want to do this before releasing this.
                }
            } else if (mode == ACCOUNT_ONLY){  //Can't let user out without choosing an account
            	//might consider an alert dialog here first
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				settingsIntent.putExtra(SettingsActivity.SETTINGS_MODE, SettingsActivity.ACCOUNT_ONLY);
				settingsIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivityForResult(settingsIntent, 0);
            }
            break;
        }
    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (UI_THEME_KEY.equals(key)) {
        	Intent intent = new Intent(this, SettingsActivity.class);
        	intent.putExtra(SETTINGS_MODE, mode);
        	intent.putExtra(RETURN_CLASS, returnClassName);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(intent);
        }
	}    
    
    
    private class LoadUserPlotsTask extends AsyncTask<Void, Void, Boolean> {
        Context context;
		ProgressDialog progressDialog;
		boolean clearPlotData;
		
    	public LoadUserPlotsTask(Context context, boolean clearPlotData){
    		this.context = context;
    		this.clearPlotData = clearPlotData;
    	}
    	
		protected void onPreExecute() {
			//TODO: locking orientation here to prevent force stop when user reorients.  There is a better way to do this,
			//via a retained fragment, but use of PreferenceActivity prevents this.   When/if we can begin using PreferenceFragment, see:
			//http://androidresearch.wordpress.com/2013/05/10/dealing-with-asynctask-and-screen-orientation/
			//http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
		    int currentOrientation = getResources().getConfiguration().orientation;
		    if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
		        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		    } else {
		        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		    }
			progressDialog = ProgressDialog.show(context, "", context.getString(R.string.plot_list_activity_fetching_plots), true);
		}
        
    	protected Boolean doInBackground(Void... params) {
    		LandPKSApplication.getInstance().getDatabaseAdapter().setLastSyncDate(new Date(0));
			return LandPKSApplication.getInstance().getDatabaseAdapter().loadUserPlots(clearPlotData);
			//return false;
       }

        protected void onPostExecute(Boolean success) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        	progressDialog.dismiss();
        	if (!success) {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    	builder.setMessage(getString(R.string.plot_list_activity_fetching_plots_error))
		    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		    				   @Override
		    				   public void onClick(DialogInterface dialog, int which) {
		    			        	Intent intent = new Intent(context, PlotListActivity.class);
		    			        	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
		    						startActivity(intent);
		     				   }
		    			   });
		    	AlertDialog alert = builder.create();
		    	alert.show(); 	
		    	//return;  //TODO: This leaves us on broken Setting screen.  What to do?
        	} else {
	        	Intent intent = new Intent(context, PlotListActivity.class);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
        	}
       }
    }

    
}
