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
 * com.noisyflowers.rangelandhealthmonitor.android.dal
 * RHMDatabaseAdapter.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.dal;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SiteDetailActivity;
import com.noisyflowers.rangelandhealthmonitor.android.fragments.TransectFragment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.model.StickSegment;
import com.noisyflowers.rangelandhealthmonitor.android.model.StickSegment.Cover;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

public class RHMDatabaseAdapter {
	private static final String TAG = RHMDatabaseAdapter.class.getName(); 
	
	private static final String DB_NAME = "RHM.db";
	private static final int DB_VERSION = 7;

	//public static final String SITES_TABLE = "sites";
	public static final String TRANSECTS_TABLE = "transects";
	public static final String SEGMENTS_TABLE = "segments";
	public static final String STICK_SEGMENTS_TABLE = "stick_segments";
	public static final String INDIVIDUALS_TABLE = "individuals";

	public static final String ID_COLUMN = "_id";

	public static final String SYNC_DATE_TABLE = "sync_date";
	public static final String SYNC_DATE_TABLE_ID_COLUMN = "_id";
	public static final String SYNC_DATE_TABLE_DATE_COLUMN = "sync_date";

	private static final String SYNC_DATE_TABLE_CREATE = "create table " + SYNC_DATE_TABLE + " (" +
			SYNC_DATE_TABLE_ID_COLUMN + " integer primary key autoincrement, " +
			SYNC_DATE_TABLE_DATE_COLUMN + " integer" + 
	")";
	
	private static final String TRANSECTS_TABLE_CREATE = "create table " + TRANSECTS_TABLE + " (" +
			ID_COLUMN + " integer primary key autoincrement, " +
			"remote_id text, " +
			"remote_modified_date integer, " +
			//"site_id integer references " + SITES_TABLE + "(" + ID_COLUMN + "), " +  //TODO: maybe ref universal id (from lpks)
			"site_id integer, " +  
			"direction text, " +
			"needs_upload integer default 0, " + //boolean
			//"unique(site_id, direction) on conflict ignore" +
			"unique(site_id, direction) on conflict fail" +
		 ")";

	private static final String SEGMENTS_TABLE_CREATE = "create table " + SEGMENTS_TABLE + " (" +
			ID_COLUMN + " integer primary key autoincrement, " +
			"transect_id integer references " + TRANSECTS_TABLE + "(" + ID_COLUMN + ") on delete cascade, " +  
			"range text, " +  //TODO: int or text here?  
			"canopy_height text, " + //TODO: text for now, might want to use category index or something 
			"basal_gap integer, " + //boolean
			"canopy_gap integer, " + //boolean
			"species_1_count integer, " +
			"species_2_count integer, " +
			"needs_upload integer default 0, " + //boolean
			"uploaded integer default 0, " + //boolean
			"date datetime" +
			//TODO: unique constraint on transect_id, range, and date
			//"date datetime default strftime('%Y-%m-%d',date('now'))" +
		 ")";

	private static final String STICK_SEGMENTS_TABLE_CREATE = "create table " + STICK_SEGMENTS_TABLE + " (" +
			ID_COLUMN + " integer primary key autoincrement, " +
			"segment_id integer references " + SEGMENTS_TABLE + "(" + ID_COLUMN + "), " +  
			"stick_segment integer, " +  //TODO: int or text here?  
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_1).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_2).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_3).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_4).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_5).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_6).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_7).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_8).replaceAll("[^a-zA-Z0-9]", "_") + " integer, " + //boolean
			RHMApplication.getInstance().getString(R.string.fragment_cover_cover_9).replaceAll("[^a-zA-Z0-9]", "_") + " integer" + //boolean
		 ")";
	
	private static final String INDIVIDUALS_TABLE_CREATE = "create table " + INDIVIDUALS_TABLE + " (" +
			ID_COLUMN + " integer primary key autoincrement, " +
			"segment_id integer references " + SEGMENTS_TABLE + "(" + ID_COLUMN + "), " +  
			"species text" +
		 ")";
	
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	private RHMDBHelper dbHelper;
	
	public RHMDatabaseAdapter (Context context) {
		dbHelper = new RHMDBHelper(context, DB_NAME, null, DB_VERSION);
	}
	
	public static String copyToSD(Context context, String currentDBPath) {
		String retVal = null;
    	try {
            File sd = null;//= Environment.getExternalStorageDirectory();
            File[] files = ContextCompat.getExternalFilesDirs(context, null);
            if (files.length > 0) {
            	if (Environment.isExternalStorageRemovable() || files.length == 1) {
            		sd = files[0];
            	} else if (files.length > 1) {
            		sd = files[1];
            	}
            }
            
            if (sd == null) {
            	throw new Exception("No external storage");
            }
            
            if (sd.canWrite()) {
            	File rhmDir = new File (sd.getAbsolutePath() + "/dbBackups");
            	rhmDir.mkdirs();
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
                String backupDBFile = "RHM-" + sdf.format(new Date()) + ".db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(rhmDir, backupDBFile);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    retVal = backupDB.getAbsolutePath();
                }
            } else {
            	throw new Exception("External storage not writable");
            }
        } catch (Exception e) {
        	Log.e(TAG, "Unable to copy db", e);
        }	
    	
    	return retVal;
	}

	public String getDBPath() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.getPath();
	}	
	
	public Transect getTransect(long transectID) {
    	Log.i(TAG, "getTransect, enter");
		Transect retVal = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor resultSet = db.query("transects", null, "_id = " + transectID, null, null, null, null);
			if (resultSet.moveToNext()) {
				retVal = new Transect();
				retVal.ID = resultSet.getLong(resultSet.getColumnIndex("_id"));
				retVal.remoteID = resultSet.getString(resultSet.getColumnIndex("remote_id"));
				retVal.direction = Transect.Direction.valueOf(resultSet.getString(resultSet.getColumnIndex("direction")));
				retVal.siteID = resultSet.getLong(resultSet.getColumnIndex("site_id"));
				retVal.needsUpload = resultSet.getLong(resultSet.getColumnIndex("needs_upload")) == 1;
			}
			resultSet.close(); //TODO: put in finally?
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
    	Log.i(TAG, "getTransect, returning");
		return retVal;
	}

	public Transect getTransect(String siteID, Transect.Direction transectDirection) {
		return getTransect(siteID, transectDirection, false);
	}
	public Transect getTransect(String siteID, Transect.Direction transectDirection, boolean createIfNecessary) {
    	//Log.i(TAG, "getTransect(siteID, direction), enter");
		Transect retVal = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor resultSet = db.query("transects", null, "site_id = ? and direction = ?", new String[] {siteID, transectDirection.name()}, null, null, null);
			if (resultSet.moveToNext()) {
				retVal = new Transect();
				retVal.ID = resultSet.getLong(resultSet.getColumnIndex("_id"));
				retVal.remoteID = resultSet.getString(resultSet.getColumnIndex("remote_id"));
				retVal.direction = Transect.Direction.valueOf(resultSet.getString(resultSet.getColumnIndex("direction")));
				retVal.siteID = resultSet.getLong(resultSet.getColumnIndex("site_id"));
				retVal.needsUpload = resultSet.getLong(resultSet.getColumnIndex("needs_upload")) == 1;
			} else {
		    	//Log.i(TAG, "getTransect, transect does not yet exist.  Creating.");
				retVal = new Transect();
				retVal.direction = transectDirection;
				retVal.siteID = Long.parseLong(siteID);
				retVal.needsUpload = false;					
				retVal = upsertTransect(retVal);
			}
			resultSet.close(); //TODO: put in finally?
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
    	//Log.i(TAG, "getTransect(siteID, direction), returning");
		return retVal;

	}
	
	public Date getMostRecentSegmentDate(long transectID) {
		Date date = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor resultSet = db.query("segments", new String [] {"MAX(date) as date"}, "transect_id = ?", new String[] {Long.toString(transectID)}, null, null, null);
			if (resultSet.moveToNext()) {
				try {date = sdf.parse(resultSet.getString(resultSet.getColumnIndex("date")));} catch (Exception e) {}
			}
			resultSet.close(); //TODO: put in finally?
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
			
		return date;
	}
	
	public Segment getSegment(Segment.Range range, long transectID, Date date) {
    	//Log.i(TAG, "getSegment, enter");
		Segment segment = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor resultSet = db.query("segments", null, "range = ? and transect_id = ? and date = ?", new String[] {range.name(), Long.toString(transectID), sdf.format(date)}, null, null, null);
			if (resultSet.moveToNext()) {
		    	//Log.i(TAG, "getSegment, segment found");
				segment = new Segment(range, transectID);
				segment.ID = resultSet.getLong(resultSet.getColumnIndex("_id"));
				segment.canopyHeight = resultSet.isNull((resultSet.getColumnIndex("canopy_height"))) ? null : Segment.Height.valueOf(resultSet.getString(resultSet.getColumnIndex("canopy_height")));
				segment.basalGap = resultSet.isNull((resultSet.getColumnIndex("basal_gap"))) ? null : resultSet.getInt(resultSet.getColumnIndex("basal_gap")) == 1;
				segment.canopyGap = resultSet.isNull((resultSet.getColumnIndex("canopy_gap"))) ? null : resultSet.getInt(resultSet.getColumnIndex("canopy_gap")) == 1;
				segment.species1Count = resultSet.isNull((resultSet.getColumnIndex("species_1_count"))) ? null : resultSet.getInt(resultSet.getColumnIndex("species_1_count"));
				segment.species2Count = resultSet.isNull((resultSet.getColumnIndex("species_2_count"))) ? null : resultSet.getInt(resultSet.getColumnIndex("species_2_count"));				
				segment.date = date;
				segment.needsUpload = resultSet.getLong(resultSet.getColumnIndex("needs_upload")) == 1;
				segment.uploaded = resultSet.getLong(resultSet.getColumnIndex("uploaded")) == 1;
				
				resultSet.close();
				resultSet = db.query(INDIVIDUALS_TABLE, new String[] {"species"}, "segment_id = ?", new String[] {Long.toString(segment.ID)}, null, null, null);
				List<String> speciesList = new ArrayList<String>();
				while (resultSet.moveToNext()) {
					speciesList.add(resultSet.getString(0));
				}
				segment.speciesList = speciesList;

				resultSet.close();
				resultSet = db.query(STICK_SEGMENTS_TABLE, new String[] {RHMApplication.getInstance().getString(R.string.fragment_cover_cover_1).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_2).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_3).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_4).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_5).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_6).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_7).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_8).replaceAll("[^a-zA-Z0-9]", "_"),
																		 RHMApplication.getInstance().getString(R.string.fragment_cover_cover_9).replaceAll("[^a-zA-Z0-9]", "_"),
																		 "_id", "segment_id", "stick_segment"}, "segment_id = ?", new String[] {Long.toString(segment.ID)
																	    }, null, null, null);
				int segmentIdx = 0;
				while (resultSet.moveToNext()) {
					for (int coverIdx = 0; coverIdx < StickSegment.Cover.values().length; coverIdx++) {
						segment.stickSegments[segmentIdx].covers[coverIdx] = resultSet.getInt(coverIdx) == 1;
					}
					segment.stickSegments[segmentIdx].ID = resultSet.getLong(resultSet.getColumnIndex("_id"));
					segment.stickSegments[segmentIdx].segmentID = resultSet.getLong(resultSet.getColumnIndex("segment_id"));
					segment.stickSegments[segmentIdx].segmentIndex = resultSet.getInt(resultSet.getColumnIndex("stick_segment"));
					segmentIdx++;
				}
							
			}
			resultSet.close(); //TODO: put in finally?
			
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		
    	//Log.i(TAG, "getSegment, returning");
		return segment;
	}
	
	
	
	public List<String> getSpeciesList() {
		List<String> speciesList = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			Cursor resultSet = db.query(true, INDIVIDUALS_TABLE, new String[] {"species"}, null, null, "species", null, null, null);
			while (resultSet.moveToNext()) {
				speciesList.add(resultSet.getString(0));
			}
			resultSet.close(); //TODO: put in finally?
		} catch (SQLException sEX) {
			Log.w(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		
		return speciesList;
	}
	
	public Long insertIndividual(String speciesName, long segmentID) {
    	//Log.i(TAG, "insertIndividual, enter");
		//TODO: check if exists first
		Long retVal = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	values.put("segment_id", segmentID);
	    	values.put("species", speciesName);
			//retVal = db.insert(INDIVIDUALS_TABLE, null, values);
			String[] qVals = {""+segmentID, speciesName};
			int rows = db.update(INDIVIDUALS_TABLE,  values, "segment_id = ? and species = ?", qVals);
			if (rows == 0) {
				retVal = db.insert(INDIVIDUALS_TABLE, null, values);								
			} else {
				Cursor resultSet = db.query(INDIVIDUALS_TABLE, new String[] {"_id"}, "segment_id = ? and species = ?", qVals, null, null, null);
				if (resultSet.moveToNext()) {
					retVal = resultSet.getLong(0);
				}
				resultSet.close(); //TODO: put in finally?
			}			
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		
    	//Log.i(TAG, "insertIndividual, returning " + retVal);
		return retVal;		
	}

	public Long insertStickSegment(StickSegment sS, long segmentID) {
    	//Log.i(TAG, "insertStickSegment, enter");
		//TODO: check if exists first
		Long retVal = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	for (StickSegment.Cover cover : StickSegment.Cover.values()) {
		    	values.put(RHMApplication.getInstance().getString(cover.name).replaceAll("[^a-zA-Z0-9]", "_"), sS.covers[cover.ordinal()] ? 1 : 0);	    		
	    	}
	    	values.put("segment_id", segmentID);
	    	values.put("stick_segment", sS.segmentIndex);
			//retVal = db.insert(STICK_SEGMENTS_TABLE, null, values);
			String[] qVals = {""+segmentID, ""+sS.segmentIndex};
			int rows = db.update(STICK_SEGMENTS_TABLE,  values, "segment_id = ? and stick_segment = ?", qVals);
			if (rows == 0) {
				retVal = db.insert(STICK_SEGMENTS_TABLE, null, values);								
			} else {
				Cursor resultSet = db.query(STICK_SEGMENTS_TABLE, new String[] {"_id"}, "segment_id = ? and stick_segment = ?", qVals, null, null, null);
				if (resultSet.moveToNext()) {
					retVal = resultSet.getLong(0);
				}
				resultSet.close(); //TODO: put in finally?
			}			
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		
    	//Log.i(TAG, "insertStickSegment, returning " + retVal);
		return retVal;		
	}
	
	public Long upsertSegmentEntry(Segment segment) {
    	//Log.i(TAG, "upsertSegmentEntry, enter");
		Long retVal = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	values.put("transect_id", segment.transectID);
	    	values.put("range", segment.range.name());
	    	values.put("canopy_height", segment.canopyHeight == null ? null : segment.canopyHeight.name());
	    	values.put("basal_gap", segment.basalGap);
	    	values.put("canopy_gap", segment.canopyGap);
	    	values.put("species_1_count", segment.species1Count);
	    	values.put("species_2_count", segment.species2Count);
	    	values.put("date", sdf.format(segment.date));  
	    	values.put("uploaded", segment.uploaded);	//TODO: for syncing; tentative for other uses
			String[] qVals = {segment.transectID.toString(), segment.range.name(), sdf.format(segment.date)};
			int rows = db.update("segments",  values, "transect_id = ? and range = ? and date = ?", qVals);
			if (rows == 0) {
				retVal = db.insert("segments", null, values);								
			} else {
				Cursor resultSet = db.query("segments", new String[] {"_id"}, "transect_id = ? and range = ? and date = ?", qVals, null, null, null);
				if (resultSet.moveToNext()) {
					retVal = resultSet.getLong(0);
				}
				resultSet.close(); //TODO: put in finally?
			}			
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		
		if (segment.speciesList != null) {
			for (String speciesName : segment.speciesList) {
				insertIndividual(speciesName, retVal);
			}
		}
		
		for (StickSegment sS : segment.stickSegments) {
			if (sS == null) {
				Log.i(TAG, "Null stick segment in segment " + segment.range);
			} else {
				try {
					insertStickSegment(sS, retVal);
				} catch (Exception eX) {
					Log.e(TAG, "Hogan's goat!", eX);
				}
			}
		}

    	//Log.i(TAG, "upsertSegmentEntry, returning " + retVal);
		return retVal;
	
	}
	
	public List<String> getSegmentDates(long transectID) {
		List<String> retList = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor resultSet = db.query(true, "segments", new String[] {"date"}, "transect_id = ? and (needs_upload = ? or uploaded = ?)", new String[] {Long.toString(transectID), "1", "1"}, null, null, null, null);
			while (resultSet.moveToNext()) {
				retList.add(resultSet.getString(0));
			}
			resultSet.close(); //TODO: put in finally?
			
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		return retList;
	}
	
	public List<Segment> getSegments(long transectID, Date date) {
    	//Log.i(TAG, "getSegments, enter");
		List<Segment> retList = new ArrayList<Segment>();
		for (Segment.Range sN : Segment.Range.values()) {
			Segment s = getSegment(sN, transectID, date);
			if (s != null)
				retList.add(s);
		}
    	//Log.i(TAG, "getSegments, returning list with length " + retList.size());
		return retList;
	}
	
	public List<StickSegment> getStickSegments(long segmentID) {
		return new ArrayList<StickSegment>();
	}
		
	public void markTransectsForUpload(long siteID, Date date) {
    	//Log.i(TAG, "markTransectsForUpload1, enter");
		markTransectsForUpload(siteID, date, true);
    	//Log.i(TAG, "markTransectsForUpload1, return");
	}
	public void markTransectsForUpload(long siteID, Date date, boolean upload) {
    	//Log.i(TAG, "markTransectsForUpload2, enter with upload = " + upload);
		for (Transect.Direction d : Transect.Direction.values()) {
			Transect transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(""+siteID, d);
			markSegmentsForUpload(transect.ID, date, upload);
		}
    	//Log.i(TAG, "markTransectsForUpload2, return");
	}
	
	public void markSegmentsForUpload(long transectID, Date date, boolean upload) {
    	//Log.i(TAG, "markSegmentsForUpload, enter with upload = " + upload);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	values.put("needs_upload", upload ? 1 : 0);
	    	values.put("uploaded", upload ? 0 : 1);  //TODO: ok?
			int rows = db.update("segments",  values, "transect_id = ? and date = ?", new String[] {"" + transectID, sdf.format(date)});
			int dummy = 0; //for breakpoint
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}	
    	//Log.i(TAG, "markTransectsForUpload2, return");
	}
	
	private Transect upsertTransect(Transect transect) { 
    	//Log.i(TAG, "upsertSegmentEntry, enter");
		Long transectID = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	values.put("remote_id", transect.remoteID);
	    	values.put("remote_modified_date", transect.remoteModifiedDate);
	    	values.put("site_id", transect.siteID);
	    	values.put("direction", transect.direction == null ? null : transect.direction.name());
			String[] qVals = {transect.siteID.toString(), transect.direction.name()};
			int rows = db.update("transects",  values, "site_id = ? and direction = ?", qVals);
			if (rows == 0) {
				transectID = db.insert("transects", null, values);								
			} else {
				String[] qVals2 = {transect.remoteID};
				Cursor resultSet = db.query("transects", new String[] {"_id"}, "remote_id = ?", qVals2, null, null, null);
				if (resultSet.moveToNext()) {
					transectID = resultSet.getLong(0);
				}
				resultSet.close(); //TODO: put in finally?
			}
			transect.ID = transectID;
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		
		return transect;
	}	

	private Long getMostRecentTransectModifiedDate() {
		Long date = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor resultSet = db.query("transects", new String [] {"MAX(remote_modified_date) as date"}, null, null, null, null, null);
			if (resultSet.moveToNext()) {
				date = resultSet.getLong(resultSet.getColumnIndex("date"));
			}
			resultSet.close(); //TODO: put in finally?
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
			
		return date;
	}

	private boolean cleanOldData() {
		boolean returnVal = true;
 		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(STICK_SEGMENTS_TABLE, null, null);
			db.delete(INDIVIDUALS_TABLE, null, null);
			db.delete(SEGMENTS_TABLE, null, null);
			db.delete(TRANSECTS_TABLE, null, null);
		} catch (Exception ex) {
			Log.w(TAG, Log.getStackTraceString(ex));
			returnVal = false;
		} finally {
			//db.close();
		}
		return returnVal;
	}
	
	private Long getLastSyncDate() {
		Long returnVal = null;
 		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.rawQuery("select * from sync_date", null);
			if (resultSet.moveToNext()) {
				returnVal = resultSet.isNull(1) ? null : resultSet.getLong(1); 
			}
		} catch (Exception ex) {
			Log.w(TAG, Log.getStackTraceString(ex));
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			//db.close();
		}
		return returnVal;
	}
	
	public void setLastSyncDate(Long date) {
 		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor resultSet = null;
		try {
			ContentValues values = new ContentValues();
	    	values.put("sync_date", date);
			int rows = db.update("sync_date", values, null, null);
			if (rows == 0) {
				db.insert("sync_date", null, values);								
			}
		} catch (Exception ex) {
			Log.w(TAG, Log.getStackTraceString(ex));
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			//db.close();
		}
	}

	private boolean syncFromServer() {
    	Log.i(TAG, "syncFromServer, enter");
		if (!cleanOldData()) {
	    	Log.w(TAG, "syncFromServer, could not clean old data");
			return false;
		}
		
		boolean retVal = false;
		Long afterDate = getLastSyncDate();
		afterDate = afterDate == null ? new Date(0).getTime() : afterDate;
    	Log.i(TAG, "syncFromServer, afterDate = " + new Date(afterDate));
    	List<Transect> transects = RHMRestClient.getInstance(RHMApplication.getInstance().getApplicationContext()).fetchTransects(new Date(afterDate));
		if (transects != null) {
	    	Log.i(TAG, "syncFromServer, transect.size = " + transects.size());
	    	for (Transect t : transects) {
				Log.i(TAG, t.remoteID);
				t = upsertTransect(t);
				for (Segment s : t.segments) {
					//Log.i(TAG, "    " + s.range);
			    	s.uploaded = true;
					s.transectID = t.ID;
					upsertSegmentEntry(s);
					retVal = true;
				}
			}
			setLastSyncDate(getMostRecentTransectModifiedDate()); 
		}
    	Log.i(TAG, "syncFromServer, exit with " + retVal);
		return retVal;
	}

	public synchronized boolean syncWithServer() {
    	Log.i(TAG, "synTransects, enter");
		boolean retVal = uploadTransectData();
		//syncFromServer(); //TODO: possibly incorporate result of this into return value.
		if (getLastSyncDate() == null) {  //TODO: for now, only sync when null (in other words, when account change).  Future?
			syncFromServer();
		}
    	Log.i(TAG, "syncTransects, exit");
		return retVal;
	}
	
	private boolean uploadTransectData() {
    	//Log.i(TAG, "uploadTransectData, enter");

    	boolean retVal = false;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			//select distinct transect_id, date from segments where needs_upload == true
			String[] selectColumns = new String[] {"transect_id", "date"};
			Cursor resultSet = db.query(true, "segments", selectColumns, "needs_upload = 1", null, null, null, null, null);
			//loop through results, creating transect record for each transect/date, calling putTransect(transect), and deleting segment records if success
			while(resultSet.moveToNext()) {
				Long transectID = resultSet.getLong(resultSet.getColumnIndex("transect_id"));
				Date date = null;
				try { date = sdf.parse(resultSet.getString(resultSet.getColumnIndex("date"))); } catch (ParseException pE) {}
				Transect transect = getTransect(resultSet.getLong(resultSet.getColumnIndex("transect_id")));
				transect = RHMRestClient.getInstance(RHMApplication.getInstance().getApplicationContext()).putTransect(transect, date);
				if (transect.remoteID != null) {
					retVal = true;
			    	ContentValues values = new ContentValues();
			    	values.put("needs_upload", 0);
			    	values.put("uploaded", 1);
					int rowsUpdated = db.update("segments", values, "transect_id = ? and date = ?", new String[]{transectID.toString(), sdf.format(date)});			
					values = new ContentValues();
			    	values.put("remote_modified_date", transect.remoteModifiedDate);				
					rowsUpdated = db.update("transects", values, "_id = ?", new String[]{transectID.toString()});			
			
				}
			}
			resultSet.close(); //TODO: put in finally?
		} catch (SQLException sEX) {
			Log.e(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
		
		//syncFromServer();
		
    	//Log.i(TAG, "uploadTransectData, returning");
		return retVal;
	}
	
	
		
	private static class RHMDBHelper extends SQLiteOpenHelper {
		
		Context context;
		
		public RHMDBHelper(Context context, String name, CursorFactory cFactory, int version) {
			super(context, name, cFactory, version);
			this.context = context;
		}
		
		private void renameColumns(SQLiteDatabase db, String createTableCmd, String tableName, String[] oldColumnNames, String[] newColumnNames) throws SQLException, IllegalArgumentException {
		    //if name arrays are not equal length we cannot map them
			if (oldColumnNames.length != newColumnNames.length)
		    	throw new IllegalArgumentException("Length of old column names does not match length of new column names");
		    
			//get list of current columns in table
			List<String> columns = getTableColumns(db, tableName);
		    				    
		    //make csv list of old column names
		    String oldColumnsSeperated = TextUtils.join(",", columns);
		    
		    // Rename columns in the list
		    for (int i = 0; i < oldColumnNames.length; i++) {
		    	int pos = columns.indexOf(oldColumnNames[i]);
		    	//if column is present in table...
		    	if (pos != -1) {
		    		//...remove from list and replace with new column name
		    		columns.remove(pos);
		    		columns.add(pos, newColumnNames[i]);
		    	//otherwise, if corresponding new column is not already present...
		    	} else if (!columns.contains(newColumnNames[i])) {
		    		//...throw an error
			    	throw new IllegalArgumentException("No column " + oldColumnNames[i] + " in table " + tableName);		    		
		    	}
		    }
		    
		    //make csv list of new column names
		    String newColumnsSeperated = TextUtils.join(",", columns);

		    db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;");

		    // Creating the table on its new format (no redundant columns)
		    db.execSQL(createTableCmd);

		    // Populating the table with the data
		    db.execSQL("INSERT INTO " + tableName + "(" + newColumnsSeperated + ") SELECT "
		            + oldColumnsSeperated + " FROM " + tableName + "_old;");
		    db.execSQL("DROP TABLE " + tableName + "_old;");
		}
		
		private List<String> getTableColumns(SQLiteDatabase db, String tableName) {
		    ArrayList<String> columns = new ArrayList<String>();
		    String cmd = "pragma table_info(" + tableName + ");";
		    Cursor cur = db.rawQuery(cmd, null);

		    while (cur.moveToNext()) {
		        columns.add(cur.getString(cur.getColumnIndex("name")));
		    }
		    cur.close();

		    return columns;
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			//db.execSQL("PRAGMA foreign_keys=ON;");
			//db.execSQL(SITES_TABLE_CREATE);
			db.execSQL(TRANSECTS_TABLE_CREATE);
			db.execSQL(SEGMENTS_TABLE_CREATE);
			db.execSQL(STICK_SEGMENTS_TABLE_CREATE);
			db.execSQL(INDIVIDUALS_TABLE_CREATE);
			db.execSQL(SYNC_DATE_TABLE_CREATE);			
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			if (copyToSD(context, db.getPath()) == null) {
				Log.e(TAG, "Unable to backup old DB before upgrading.");
			}
			
			boolean hosed = false;
			switch (oldVersion) {
				case 1:
				case 2:
				case 3:
					//For all previous, just wipe and start over
					Log.i(TAG, "Upgrading from ancient version " + oldVersion + ".");
					hosed = true;
					break;
				case 4: 
					try {
						renameColumns(db, SEGMENTS_TABLE_CREATE, SEGMENTS_TABLE, new String[] {"species_1_density", "species_2_density"}, new String[] {"species_1_count", "species_2_count"});
						//TODO: Setting this sync date prevents un-uploaded data from getting deleted by the sync routine.  However, it also means user will have to manually sync after install.
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
						ContentValues values = new ContentValues();
				    	values.put("sync_date", sdf.format(new Date()));
						int rows = db.update("sync_date", values, null, null);
						if (rows == 0) {
							db.insert("sync_date", null, values);								
						}
					} catch (Exception eX) {
						Log.e(TAG, "Upgrade from db4: Unable to rename columns", eX);
						hosed = true;
					}
					//let fall through to later versions
				case 5: 
					try {
						renameColumns(db, STICK_SEGMENTS_TABLE_CREATE, STICK_SEGMENTS_TABLE, 
									  new String[] {"trees", "shrubs", "subshrubs", "perennial_grasses", "annuals", "rock"}, 
									  new String[] {"tree_canopy", "shrub_canopy", "sub_shrub_canopy", "perennial_grass_canopy", "annual_grass_canopy", "rock_fragment"});
						//TODO: Setting this sync date prevents un-uploaded data from getting deleted by the sync routine.  However, it also means user will have to manually sync after install.
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
						ContentValues values = new ContentValues();
				    	values.put("sync_date", sdf.format(new Date()));
						int rows = db.update("sync_date", values, null, null);
						if (rows == 0) {
							db.insert("sync_date", null, values);								
						}
					} catch (Exception eX) {
						Log.e(TAG, "Upgrade from db5: Unable to rename columns", eX);
						hosed = true;
					}
					//let fall through to later versions
				case 6: 
					try {
						renameColumns(db, STICK_SEGMENTS_TABLE_CREATE, STICK_SEGMENTS_TABLE, 
									  new String[] {"bare", "tree_canopy", "shrub_canopy", "sub_shrub_canopy", "perennial_grass_canopy", "annual_grass_canopy", "herb_litter", "wood_litter"}, 
									  new String[] {"bare_ground", "tree", "shrub", "sub_shrub", "perennial_grass", "annual_grass", "herbaceous litter", "woody_litter"});
						//TODO: Setting this sync date prevents un-uploaded data from getting deleted by the sync routine.  However, it also means user will have to manually sync after install.
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
						ContentValues values = new ContentValues();
				    	values.put("sync_date", sdf.format(new Date()));
						int rows = db.update("sync_date", values, null, null);
						if (rows == 0) {
							db.insert("sync_date", null, values);								
						}
					} catch (Exception eX) {
						Log.e(TAG, "Upgrade from db6: Unable to rename columns", eX);
						hosed = true;
					}
					//let fall through to later versions
			}
			
			
			
			if (hosed) {
				Log.w(TAG, "DB update, dropping and recreating all tables");
				db.execSQL("drop table if exists " + TRANSECTS_TABLE);
				db.execSQL("drop table if exists " + SEGMENTS_TABLE);
				db.execSQL("drop table if exists " + STICK_SEGMENTS_TABLE);
				db.execSQL("drop table if exists " + INDIVIDUALS_TABLE);
				db.execSQL("drop table if exists " + SYNC_DATE_TABLE);
				onCreate(db);
			}
		}
	}

}
