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
 * SettingsActivity.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.noisyflowers.rangelandhealthmonitor.android.R;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final int REQUEST_CODE = 0xd0;
    public static final String SETTINGS_MODE = "settings_mode";
    public static final String RETURN_CLASS = "return_class";
    public static final int NOT_SPECIFIED = 0;
    public static final int ALL = 1;
    public static final String UI_THEME_KEY = "pref_ui_theme"; 
    public static final String WIFI_ONLY_KEY = "pref_sync_wifi_only"; 
    
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
				returnClass = SiteListActivity.class;
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
				returnClassName = SiteListActivity.class.getName();
			}
        }
                            
	    addPreferencesFromResource(R.xml.preferences);
	
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
    
    
}
