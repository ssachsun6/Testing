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
 * com.noisyflowers.rangelandhealthmonitor.android
 * RHMApplication.java
 */

package com.noisyflowers.rangelandhealthmonitor.android;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import android.view.View;
import android.view.ViewGroup;

import com.noisyflowers.rangelandhealthmonitor.android.activities.SettingsActivity;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMDatabaseAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.ChangelogFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.CoverFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.HeightGapFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.InfoFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.LegalNoticesFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.LicenseFragment;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.SegmentFragment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.service.AlarmReceiver;
import com.noisyflowers.rangelandhealthmonitor.android.service.DataSyncService;
import com.noisyflowers.rangelandhealthmonitor.android.util.SegmentListAdapter;

public class RHMApplication extends Application {
    public static final String PREF_ACCOUNT_NAME_KEY = "PreferredAccountName";

	private List<Fragment> segmentFragments;
	private List<Fragment> segmentDetailFragments;
	private List<Fragment> aboutFragments;
	private RHMDatabaseAdapter databaseAdapter = null;;
		
	public void createDBAdapter(){
		databaseAdapter = new RHMDatabaseAdapter(this);
	}

	private static RHMApplication myInstance;
	public static RHMApplication getInstance(){
		return myInstance;
	}

	@Override
	public final void onCreate() {
		super.onCreate();
		myInstance = this;
		
		createDBAdapter();
		
		//TODO: This will need to be called from a setSite method
		initFragments();
		
    	setRecurringAlarm(getApplicationContext());
	}

	public void initFragments() {
		segmentFragments = new ArrayList<Fragment>();
        Bundle bundle = new Bundle();
       
        //for(SegmentName h : SegmentName.values()) {
        for(Segment.Range h : Segment.Range.values()) {
            bundle = new Bundle();
            //bundle.putString(SegmentListAdapter.ARG_NAME, h.name);
            //bundle.putString(SegmentListAdapter.ARG_NAME, h.displayName);
            bundle.putString(SegmentListAdapter.ARG_NAME, h.getDisplayName());
            //segmentFragments.add(Fragment.instantiate(this, SegmentFragment.class.getName(), bundle));  	
            Fragment f = Fragment.instantiate(this, SegmentFragment.class.getName(), bundle);
            f.setRetainInstance(true);
            segmentFragments.add(f);  	
        }	
      
		segmentDetailFragments = new ArrayList<Fragment>();
        bundle = new Bundle();
        bundle.putString(SegmentListAdapter.ARG_NAME, "Cover");
        //segmentDetailFragments.add(Fragment.instantiate(this, CoverFragment.class.getName(), bundle));  	
        Fragment f = Fragment.instantiate(this, CoverFragment.class.getName(), bundle);
        f.setRetainInstance(true);
        segmentDetailFragments.add(f);
        bundle = new Bundle();
        bundle.putString(SegmentListAdapter.ARG_NAME, "Height/Gap");
        //segmentDetailFragments.add(Fragment.instantiate(this, HeightGapFragment.class.getName(), bundle));  	
        f = Fragment.instantiate(this, HeightGapFragment.class.getName(), bundle);
        f.setRetainInstance(true);
        segmentDetailFragments.add(f);
        
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

	public List<Fragment> getSegmentFragments() {
		return segmentFragments;
	}

	public List<Fragment> getSegmentDetailFragments() {
		return segmentDetailFragments;
	}

	public List<Fragment> getAboutFragments() {
		return aboutFragments;
	}

	public RHMDatabaseAdapter getDatabaseAdapter(){
		return databaseAdapter;
	}

	public boolean isNetworkAvailable() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean useWifiOnly = sharedPref.getBoolean(SettingsActivity.WIFI_ONLY_KEY, false);	
		//boolean useWifiOnly = false; //TODO: until implemented
		
		Context context = RHMApplication.getInstance().getApplicationContext();
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected() &&
	    		(useWifiOnly ? activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI : true);
	}

	public void startSyncService(Context context) {
		if (isNetworkAvailable()) {
			Intent syncIntent = new Intent(context, DataSyncService.class);
			context.startService(syncIntent); 
		} 
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
	

	//Handy routine. Not sure where to put it though, so here it is.
	public void setViewGroupEnabled(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();
	    for (int i = 0; i < childCount; i++) {
	    	View view = viewGroup.getChildAt(i);
	    	view.setEnabled(enabled);
	    	if (view instanceof ViewGroup) {
	    		setViewGroupEnabled((ViewGroup) view, enabled);
	    	}
	    }
	}

	//TODO: experimental
	private long transectID = -1;
	public long getTransectID() {
		return transectID;
	}
	public void setTransectID(long transectID) {
		this.transectID = transectID;
	}
}
