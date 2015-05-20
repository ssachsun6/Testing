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
 * LandPKSContract.java
 */

package com.noisyflowers.landpks.android;

import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;

import android.net.Uri;

public class LandPKSContract {
	public static final String AUTHORITY = "com.noisyflowers.landpks.android.dal.LandPKSContentProvider";
	public static final String SITES_PATH = "sites";
	public static final String USER_ACCOUNT_PATH = "user_account";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final String ACCOUNT_COLUMN_NAME = "user_account";
	public static final String SITE_COLUMN_NAME = LandPKSDatabaseAdapter.PLOTS_TABLE_NAME_COLUMN;
	public static final String SITE_COLUMN_ID = LandPKSDatabaseAdapter.PLOTS_TABLE_ID_COLUMN;
	public static final String SITE_COLUMN_RECORDER_NAME = LandPKSDatabaseAdapter.PLOTS_TABLE_RECORDER_NAME_COLUMN;
	public static final String SITE_COLUMN_REMOTE_ID = LandPKSDatabaseAdapter.PLOTS_TABLE_REMOTE_ID_COLUMN;
	
	public static final String SITE_CHARACTERIZATION_ACTION = "com.noisyflowers.landpks.android.ACTION_SOIL_CHARACTERIZATION";
	public static final String SITE_CHARACTERIZATION_ACTION_EXTRA_SITE_ID = "siteID";
	public static final String SITE_CHARACTERIZATION_ACTION_RETURN_EXTRA_SITE_ID = "siteID";
	public static final String SITE_CHARACTERIZATION_ACTION_RETURN_EXTRA_SITE_NAME = "siteName";
}
