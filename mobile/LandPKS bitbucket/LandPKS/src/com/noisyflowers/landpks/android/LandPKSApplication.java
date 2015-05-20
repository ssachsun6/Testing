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
 * com.noisyflowers.landpks.android
 * LandPKSApplication.java
 */

package com.noisyflowers.landpks.android;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.noisyflowers.landpks.android.activities.SettingsActivity;
import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;
import com.noisyflowers.landpks.android.dal.RestClient;
import com.noisyflowers.landpks.android.fragments.ChangelogFragment;
import com.noisyflowers.landpks.android.fragments.InfoFragment;
import com.noisyflowers.landpks.android.fragments.LandUseCoverFragment;
import com.noisyflowers.landpks.android.fragments.LegalNoticesFragment;
import com.noisyflowers.landpks.android.fragments.LicenseFragment;
import com.noisyflowers.landpks.android.fragments.NameFragment;
import com.noisyflowers.landpks.android.fragments.PhotosFragment;
import com.noisyflowers.landpks.android.fragments.ResultsFragment;
import com.noisyflowers.landpks.android.fragments.ReviewFragment;
import com.noisyflowers.landpks.android.fragments.SlopeFragment;
import com.noisyflowers.landpks.android.fragments.SlopeShapeFragment;
import com.noisyflowers.landpks.android.fragments.SoilHorizonFragment;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment.HorizonName;
import com.noisyflowers.landpks.android.fragments.SpecialSoilConditionsFragment;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.service.AlarmReceiver;
import com.noisyflowers.landpks.android.service.DataSyncService;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

public class LandPKSApplication extends Application {
	private static final String TAG = LandPKSApplication.class.getName(); 

	public static final SimpleDateFormat LPKS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	public static final SimpleDateFormat OLD_LPKS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    //public static final String WEB_CLIENT_ID = "410858290704.apps.googleusercontent.com";
    private GoogleAccountCredential credential;
    //private SharedPreferences settings;
    //static final int REQUEST_ACCOUNT_PICKER = 2;
    public static final String PREF_ACCOUNT_NAME_KEY = "PreferredAccountName";

	private static LandPKSApplication myInstance;
	public static LandPKSApplication getInstance(){
		return myInstance;
	}

	private Plot currentPlot;
	private List<Fragment> fragments;
	private List<Fragment> horizonFragments;
	private List<Fragment> aboutFragments;
	private LandPKSDatabaseAdapter databaseAdapter = null;;
	
	public void createDBAdapter(){
		databaseAdapter = new LandPKSDatabaseAdapter(this);
	}
	
	public LandPKSDatabaseAdapter getDatabaseAdapter(){
		return databaseAdapter;
	}
	
	@Override
	public final void onCreate() {
		super.onCreate();
		//Log.i(TAG, "onCreate() enter");

		myInstance = this;
		currentPlot = new Plot();		
        
        createDBAdapter();
        
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);        

		credential = GoogleAccountCredential.usingAudience(this, "server:client_id:" + RestClient.WEB_CLIENT_ID); 		
    	//SharedPreferences settings = getSharedPreferences("LandPKS", 0);
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    	String accountName = settings.getString(PREF_ACCOUNT_NAME_KEY, null);
    	if (accountName != null) {
			credential.setSelectedAccountName(accountName);
    	}
    	
        //credential = GoogleAccountCredential.usingAudience(this,
        //        "server:client_id:" + WEB_CLIENT_ID);
		//settings = getSharedPreferences("LandPKS", 0);
    	setRecurringAlarm(getApplicationContext());
	}
	
	public void initFragments() {
        fragments = new ArrayList<Fragment>();
        Bundle bundle = new Bundle();
        bundle.putString("Name", getString(R.string.NameFragment_display_name));
        fragments.add(Fragment.instantiate(this, NameFragment.class.getName(), bundle));
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.LandUseCoverFragment_display_name));
        //bundle.putBoolean("Completed", true);  //for testing
        fragments.add(Fragment.instantiate(this, LandUseCoverFragment.class.getName(), bundle));
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.SlopeFragment_display_name));
        fragments.add(Fragment.instantiate(this, SlopeFragment.class.getName(), bundle));
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.SlopeShapeFragment_display_name));
        fragments.add(Fragment.instantiate(this, SlopeShapeFragment.class.getName(), bundle));
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.SpecialSoilConditionsFragment_display_name));
        fragments.add(Fragment.instantiate(this, SpecialSoilConditionsFragment.class.getName(), bundle));
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.SoilHorizonsFragment_display_name));
        fragments.add(Fragment.instantiate(this, SoilHorizonsFragment.class.getName(), bundle));
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.PhotosFragment_display_name));
        fragments.add(Fragment.instantiate(this, PhotosFragment.class.getName(), bundle));
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.ReviewFragment_display_name));
        fragments.add(Fragment.instantiate(this, ReviewFragment.class.getName(), bundle));
        
	    horizonFragments = new ArrayList<Fragment>();
        for(HorizonName h : HorizonName.values()) {
            bundle = new Bundle();
            bundle.putString("Name", h.name);
            horizonFragments.add(Fragment.instantiate(this, SoilHorizonFragment.class.getName(), bundle));  	
        }	
        
	    aboutFragments = new ArrayList<Fragment>();
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.InfoFragment_display_name));
        aboutFragments.add(Fragment.instantiate(this, InfoFragment.class.getName(), bundle));  	
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.ChangelogFragment_display_name));
        aboutFragments.add(Fragment.instantiate(this, ChangelogFragment.class.getName(), bundle));  	
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.LicenseFragment_display_name));
        aboutFragments.add(Fragment.instantiate(this, LicenseFragment.class.getName(), bundle));  	
        bundle = new Bundle();
        bundle.putString("Name", getString(R.string.LegalNoticesFragment_display_name));
        aboutFragments.add(Fragment.instantiate(this, LegalNoticesFragment.class.getName(), bundle));  	

	}
	
	public GoogleAccountCredential getCredential() {
		return credential;
	}
	public void setCredential(GoogleAccountCredential credential) {
		this.credential = credential;
	}
	
	public Plot getPlot() {
		return currentPlot;
	}
	public void setPlot(Plot plot) {
		currentPlot = plot;
		initFragments();
		for (Fragment f: fragments) {
			try { ((PlotEditFragment)f).load(plot); } catch (NullPointerException nPE) {}
		}
		
	}
	
	public List<Fragment> getEditFragments() {
		//if pending upload or already uploaded, just return ReviewFragment
		if (currentPlot.needsUpload == 1) {
			//if pending upload just return ReviewFragment
			List<Fragment> shortList = new ArrayList<Fragment>();
			shortList.add(fragments.get(fragments.size()-1)); //TODO: is there a better way to get at these particular fragments than positionally?
			return shortList;
		} else if (currentPlot.remoteID != null) {
			//if uploaded return ReviewFragment and ResultsFragment
			List<Fragment> shortList = new ArrayList<Fragment>();
			shortList.add(fragments.get(fragments.size()-1)); //TODO: is there a better way to get at these particular fragments than positionally?
	        Bundle bundle = new Bundle();
	        bundle.putString("Name", getString(R.string.ResultsFragment_display_name));
	        shortList.add(Fragment.instantiate(this, ResultsFragment.class.getName(), bundle));
			return shortList;	
		} else if (currentPlot.name == null || "".equals(currentPlot.name)) {
			//if new plot, just return NameFragment
			List<Fragment> shortList = new ArrayList<Fragment>();
			shortList.add(fragments.get(0)); //TODO: is there a better way to get at these particular fragments than positionally?
			return shortList;
		} else {			
			return fragments;
		}
	}

	public List<Fragment> getHorizonFragments() {
		return horizonFragments;
	}

	public List<Fragment> getAboutFragments() {
		return aboutFragments;
	}

	public boolean isNetworkAvailable() {
		/***/
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean useWifiOnly = sharedPref.getBoolean(SettingsActivity.WIFI_ONLY_KEY, false);		
		
		Context context = LandPKSApplication.getInstance().getApplicationContext();
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected() &&
	    		(useWifiOnly ? activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI : true);
	}
	
	public void setRecurringAlarm(Context context) {
		boolean alarmUp = (PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);

		if (!alarmUp)
		{		
		    Calendar updateTime = Calendar.getInstance();
		    //updateTime.add(Calendar.SECOND, (int)AlarmManager.INTERVAL_FIFTEEN_MINUTES/1000);
		    updateTime.add(Calendar.SECOND, 60);  //five minutes for first run, 15 thereafter
		    
		    Intent syncIntent = new Intent(context, AlarmReceiver.class);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, syncIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		    AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		    alarms.setRepeating(AlarmManager.RTC_WAKEUP,	updateTime.getTimeInMillis(), 300000, pendingIntent);
		    //alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,	updateTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
		    //alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,	updateTime.getTimeInMillis(), 120000, pendingIntent);
		    //alarms.set(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), pendingIntent);
		}
	}
	
	public void startSyncService(Context context) {
		if (isNetworkAvailable()) {
			Intent syncIntent = new Intent(context, DataSyncService.class);
			syncIntent.putExtra(DataSyncService.SYNC_LEVEL_EXTRA, DataSyncService.SYNC_LEVEL_ALL);
			context.startService(syncIntent); 
		} 
	}
	
	public String analyticResults(Plot plot) {
		StringBuilder sB = new StringBuilder();
		LandPKSDatabaseAdapter db = LandPKSApplication.getInstance().getDatabaseAdapter();
		
		sB.append(getString(R.string.results_fragment_recommendations_elevation) + " " + (plot.gdalElevation == null ? "N/A" : plot.gdalElevation) + "\n");
		sB.append(getString(R.string.results_fragment_recommendations_annual_precip) + " " + (plot.avgAnnualPrecipitation == null ? "N/A" : Math.round(plot.avgAnnualPrecipitation)) + "\n");
		sB.append(getString(R.string.results_fragment_recommendations_lgp) + " " + (plot.gdalFaoLgp == null ? "N/A" : plot.gdalFaoLgp) + "\n");
		sB.append(getString(R.string.results_fragment_recommendations_aridity) + " " + (plot.gdalAridityIndex == null ? "N/A" : plot.gdalAridityIndex) + "\n");
		sB.append(getString(R.string.results_fragment_recommendations_awc) + " " + (plot.awcSoilProfile == null ? "N/A" : Math.round(plot.awcSoilProfile)) + "\n");

		/***
		sB.append("\n");
		Double max = db.getMaxValue("grassProductivity");
		sB.append(getString(R.string.results_fragment_recommendations_grass_productivity) + " " + (plot.grassProductivity == null || max == null || max == 0 ? "N/A" : Math.round((plot.grassProductivity/max)*100)) + "\n");
		max = db.getMaxValue("grassErosion");
		sB.append(getString(R.string.results_fragment_recommendations_grass_erosion) + " " + (plot.grassErosion == null || max == null || max == 0 ? "N/A" : Math.round((plot.grassErosion/max)*100)) + "\n");
		max = db.getMaxValue("cropProductivity");
		sB.append(getString(R.string.results_fragment_recommendations_crop_productivity) + " " + (plot.cropProductivity == null || max == null || max == 0 ? "N/A" : Math.round((plot.cropProductivity/max)*100)) + "\n");
		max = db.getMaxValue("cropErosion");
		sB.append(getString(R.string.results_fragment_recommendations_crop_erosion) + " " + (plot.cropErosion == null || max == null || max == 0 ? "N/A" : Math.round((plot.cropErosion/max)*100)) + "\n");
		***/
		
		return sB.toString();
	}

	
}
