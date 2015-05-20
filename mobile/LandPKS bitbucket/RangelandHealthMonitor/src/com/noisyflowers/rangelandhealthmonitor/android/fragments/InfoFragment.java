/**
 * 
 * Copyright 2015 Noisy Flowers LLC
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
 * com.noisyflowers.rangelandhealthmonitor.android.fragments
 * InfoFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.activities.AboutActivity;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SettingsActivity;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMDatabaseAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class InfoFragment extends Fragment implements View.OnLongClickListener {
	private static final String TAG = InfoFragment.class.getName(); 

    private TextView versionTV, nfTV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_info, container, false);

		String versionName = "";
		try {
			versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (Exception eX) {}
	    versionTV = (TextView) root.findViewById(R.id.fragment_info_version_tv);
	    versionTV.setText(versionName);

	    nfTV = (TextView) root.findViewById(R.id.fragment_info_noisyflowers_tv);
	    nfTV.setLongClickable(true);
	    nfTV.setOnLongClickListener(this);
	    
	    return root;
	}
	
	@Override
	public boolean onLongClick(View v) {
		Log.i(TAG, "Taking bug report");
		
		String dbBackupPath = RHMDatabaseAdapter.copyToSD(getActivity(), RHMApplication.getInstance().getDatabaseAdapter().getDBPath());
		
		String osVersionName = Build.VERSION.RELEASE;
		String appVersionName = "";
		try {
			appVersionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException nNFE) {
		}
		
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;

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
            log.append("Device Manufacturer " + manufacturer);
            log.append(separator);
            log.append("Device Model " + model);
            log.append(separator);
            log.append("Android Version " + osVersionName);
            log.append(separator);
            log.append("RHM Version " + appVersionName);
            log.append(separator);
            log.append(separator);
	        while ((line = reader.readLine()) != null) {
	        	if (line.contains("noisyflowers")) {
	                log.append(line);
	                log.append(separator);
	        	}
	        }
	    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    	String accountName = settings.getString(RHMApplication.PREF_ACCOUNT_NAME_KEY, null);
	    	if (accountName != null) {
    			Intent i = new Intent(Intent.ACTION_SEND);
    			i.setType("text/plain");
    			//i.putExtra(Intent.EXTRA_EMAIL, new String[] {"noisyflowers@douglasmeredith.net"});
    			i.putExtra(Intent.EXTRA_SUBJECT, "RHM debug report from " + accountName);
    			i.putExtra(Intent.EXTRA_TEXT,log.toString());
    			if (dbBackupPath != null) {
    		        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(dbBackupPath)));
    			}
				PackageManager packageManager = getActivity().getPackageManager();
				List<ResolveInfo> activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
				if (activities.size() > 0) {
        			this.startActivity(Intent.createChooser(i, "Select application"));
				} else {
					Toast.makeText(getActivity(), "No SEND receiver is registered", Toast.LENGTH_LONG).show();
				}
	    	}
    	} catch (IOException e) {
    		Log.e(TAG, "Error reading log", e);
    	} finally {
    		try { reader.close(); } catch (Exception e) {}
    	} 
		return true;
	}

}
