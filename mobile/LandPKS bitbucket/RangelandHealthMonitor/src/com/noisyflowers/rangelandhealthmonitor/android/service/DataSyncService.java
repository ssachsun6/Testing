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
 * DataSyncService.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.service;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SiteListActivity;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMDatabaseAdapter;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class DataSyncService extends IntentService {
	public static final String STARTED = "LPKS Data Sync Started";
	public static final String FINISHED = "LPKS Data Sync Finished";
	public static final int NOTIFICATION_ID = 1138;

	RHMApplication application = RHMApplication.getInstance();
	
	public DataSyncService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public DataSyncService() {
		this("RHMDataSyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		sendBroadcast(new Intent(STARTED));

		boolean transectDataUploaded = false;
		
		RHMDatabaseAdapter dbAdapter = application.getDatabaseAdapter();
		if (dbAdapter != null) {
			transectDataUploaded = dbAdapter.syncWithServer();
			
			sendBroadcast(new Intent(FINISHED));

			if (transectDataUploaded) {
				NotificationCompat.Builder builder = 
						new NotificationCompat.Builder(this).
						setSmallIcon(R.drawable.ic_launcher).
						setContentTitle("RHM notification").  //TODO: srtings.xml for both of these
						setContentText("Transect data has been uploaded.").
						setAutoCancel(true);
				Intent lpksIntent = new Intent(this, SiteListActivity.class);
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				stackBuilder.addParentStack(SiteListActivity.class);
				stackBuilder.addNextIntent(lpksIntent);
				PendingIntent lpksPendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(lpksPendingIntent);
				NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
		}
	}

}
