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
 * com.noisyflowers.rangelandhealthmonitor.android.service
 * AccountChangeReceiver.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.service;


import java.util.Date;

import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AccountChangeReceiver extends BroadcastReceiver {
	private static final String TAG = AccountChangeReceiver.class.getName(); 

	@Override
	public void onReceive(Context context, Intent intent) {
		String accountName = intent.getStringExtra("accountName"); //TODO put this in a constant somewhere
		Log.i(TAG, "Received account change broadcast: " + accountName);
		
    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		editor.putString(RHMApplication.PREF_ACCOUNT_NAME_KEY, accountName);
		editor.commit();

		//TODO: sync data here
		//RHMApplication.getInstance().getDatabaseAdapter().setLastSyncDate(new Date(0).getTime());
		RHMApplication.getInstance().getDatabaseAdapter().setLastSyncDate(null);
		RHMApplication.getInstance().startSyncService(context);
		//RHMApplication.getInstance().getDatabaseAdapter().syncFromServer();
		
		//ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//am.killBackgroundProcesses(/*context.getPackageName()*/"com.noisyflowers.rangelandhealthmonitor.android");
	}

}
