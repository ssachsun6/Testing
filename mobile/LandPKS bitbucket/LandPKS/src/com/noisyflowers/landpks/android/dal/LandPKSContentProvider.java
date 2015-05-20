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
 * com.noisyflowers.landpks.android.dal
 * LandPKSContentProvider.java
 */

package com.noisyflowers.landpks.android.dal;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.LandPKSContract;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;

public class LandPKSContentProvider extends ContentProvider {

	private LandPKSDatabaseAdapter dbAdapter;

	private static final int SITES = 10;
	private static final int SITE_ID = 20;
	private static final int USER_ACCOUNT = 30;

	//private static final String AUTHORITY = "com.noisyflowers.landpks.android.dal.LandPKSContentProvider";

	//private static final String SITES_PATH = "sites";
	//private static final String USER_ACCOUNT_PATH = "user_account";
	//public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	//public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/sites";
	//public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/site";

	//private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	//static {
	//	uriMatcher.addURI(AUTHORITY, BASE_PATH, SITES);
	//	uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SITE_ID);
	//}
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		uriMatcher.addURI(LandPKSContract.AUTHORITY, LandPKSContract.SITES_PATH, SITES);
		uriMatcher.addURI(LandPKSContract.AUTHORITY, LandPKSContract.SITES_PATH + "/#", SITE_ID);
		uriMatcher.addURI(LandPKSContract.AUTHORITY, LandPKSContract.USER_ACCOUNT_PATH , USER_ACCOUNT);
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		dbAdapter = new LandPKSDatabaseAdapter(getContext());
		return false;
	}

	/*****
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    if (!dbAdapter.columnsOK(projection)) {
	        throw new IllegalArgumentException("Unknown columns in projection");
	    }
	    queryBuilder.setTables(LandPKSDatabaseAdapter.PLOTS_TABLE);

	    int uriType = uriMatcher.match(uri);
	    switch (uriType) {
	    	case USER_ACCOUNT:
	    		String userAccount = "noisyflowers@douglasmeredith.net";
	    		Cursor thisCursor = new MatrixCursor(new String[] {"user_account"});
	    		MatrixCursor.RowBuilder rB = ((MatrixCursor)thisCursor).newRow();
	    		rB.add(userAccount);
	    		break;
		    case SITES:
		    	break;
		    case SITE_ID:
		    	queryBuilder.appendWhere(LandPKSDatabaseAdapter.PLOTS_TABLE_ID_COLUMN + "=" + uri.getLastPathSegment());
		    	break;
		    default:
		    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    SQLiteDatabase db = dbAdapter.getReadableDB();
	    Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
	    // make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	*****/

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    queryBuilder.setTables(LandPKSDatabaseAdapter.PLOTS_TABLE);

	    Cursor cursor = null;
	    int uriType = uriMatcher.match(uri);
	    switch (uriType) {
	    	case USER_ACCOUNT:
	    		//String userAccount = "noisyflowers@douglasmeredith.net"; //TODO: get this from Prefs
	        	//SharedPreferences settings = getContext().getSharedPreferences("LandPKS", 0);
	        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
	        	String userAccount = settings.getString(LandPKSApplication.PREF_ACCOUNT_NAME_KEY, null);
	    		cursor = new MatrixCursor(new String[] {LandPKSContract.ACCOUNT_COLUMN_NAME});
	    		MatrixCursor.RowBuilder rB = ((MatrixCursor)cursor).newRow();
	    		rB.add(userAccount);
	    		break;
		    case SITE_ID:
		    	// adding the ID to the original query then fall through to sites query
		    	queryBuilder.appendWhere(LandPKSDatabaseAdapter.PLOTS_TABLE_ID_COLUMN + "=" + uri.getLastPathSegment());
		    case SITES:
			    if (!dbAdapter.columnsOK(projection)) { //TODO: change this method to accept a table param
			        throw new IllegalArgumentException("Unknown columns in projection");
			    }
			    SQLiteDatabase db = dbAdapter.getReadableDB();
			    cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		    	break;
		    default:
		    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    // make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
