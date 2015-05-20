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
 * com.noisyflowers.landpks.android.service
 * DataSyncService.java
 */

package com.noisyflowers.landpks.android.service;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.activities.PlotListActivity;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;
import com.noisyflowers.landpks.android.dal.RestClient;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat.Builder;

public class DataSyncService extends IntentService {
	public static final String STARTED = "LPKS Data Sync Started";
	public static final String FINISHED = "LPKS Data Sync Finished";
	public static final String SYNC_LEVEL_EXTRA = "syncLevel";
	public static final String SYNC_LEVEL_ALL = "all";
	public static final int NOTIFICATION_ID = 42;

	LandPKSApplication application = LandPKSApplication.getInstance();
	
	public DataSyncService(String name) {
		super(name);
	}

	public DataSyncService() {
		this("LPKSDataSyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		sendBroadcast(new Intent(STARTED));

		//TODO: for testing only!
		//RestClient restClient = RestClient.getInstance();
		//restClient.fetchPlots();

		String syncLevel = intent.getStringExtra(SYNC_LEVEL_EXTRA);
		
		boolean plotsSynced = false;
		
		LandPKSDatabaseAdapter dbAdapter = application.getDatabaseAdapter();
		if (dbAdapter != null) {
			if (SYNC_LEVEL_ALL.equals(syncLevel)) { //Same action for both for now
				plotsSynced = dbAdapter.syncPlots(); 
				dbAdapter.syncPhotos();
			} else {
				plotsSynced = dbAdapter.syncPlots();
				dbAdapter.syncPhotos();
			}
			
			sendBroadcast(new Intent(FINISHED));

			if (plotsSynced) {
				NotificationCompat.Builder builder = 
						new NotificationCompat.Builder(this).
						setSmallIcon(R.drawable.ic_launcher).
						setContentTitle("LandPKS notification").
						setContentText("New plots have been uploaded.").
						setAutoCancel(true);
				Intent lpksIntent = new Intent(this, PlotListActivity.class);
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				stackBuilder.addParentStack(PlotListActivity.class);
				stackBuilder.addNextIntent(lpksIntent);
				PendingIntent lpksPendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(lpksPendingIntent);
				NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
		}
	}

}
