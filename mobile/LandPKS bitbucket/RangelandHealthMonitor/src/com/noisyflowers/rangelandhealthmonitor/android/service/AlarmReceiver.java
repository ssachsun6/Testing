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
 * AlarmReceiver.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.service;

import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String DEBUG_TAG = "AlarmReceiver";
	
    private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";   

	@Override
	public void onReceive(Context context, Intent intent) {
        if (BOOT_ACTION.equals(intent.getAction())) { 
			Log.d(DEBUG_TAG, "RHM Boot alarm, scheduling sync alarms");	
			RHMApplication.getInstance().setRecurringAlarm(context);
        } else {
			Log.d(DEBUG_TAG, "RHM Recurring alarm, starting sync service");	
			RHMApplication.getInstance().startSyncService(context);
        }
	}

}
