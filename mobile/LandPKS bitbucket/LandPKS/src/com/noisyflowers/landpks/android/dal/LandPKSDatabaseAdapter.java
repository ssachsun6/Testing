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
 * LandPKSDatabaseAdapter.java
 */

package com.noisyflowers.landpks.android.dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.fragments.PhotosFragment.PhotoSubject;
import com.noisyflowers.landpks.android.fragments.SoilHorizonFragment.SoilRockFragmentVolume;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment.HorizonName;
import com.noisyflowers.landpks.android.model.MonthlyClimate;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.model.SoilHorizon;



public class LandPKSDatabaseAdapter {
	private static final String TAG = LandPKSDatabaseAdapter.class.getName(); 
	
	private static final String DB_NAME = "LandPKS.db";
	private static final int DB_VERSION = 20;
	
	public static final String PLOTS_TABLE = "plots";
	private static final String SOIL_HORIZONS_TABLE = "soil_horizons";
	private static final String SOIL_HORIZON_DEFINITIONS_TABLE = "soil_horizons_definitions";

	public static final String PLOTS_TABLE_ID_COLUMN = "_id";
	public static final String PLOTS_TABLE_NAME_COLUMN = "name";
	public static final String PLOTS_TABLE_RECORDER_NAME_COLUMN = "recorder_name";
	public static final String PLOTS_TABLE_REMOTE_ID_COLUMN = "remote_id";

	public static final String SYNC_DATE_TABLE = "sync_date";
	public static final String SYNC_DATE_TABLE_ID_COLUMN = "_id";
	public static final String SYNC_DATE_TABLE_DATE_COLUMN = "sync_date";

	public static final String WEATHER_TYPE_TABLE = "weather_types";
	public static final String WEATHER_TYPE_TABLE_ID_COLUMN = "name";
	public static final String MONTHLY_CLIMATE_TABLE = "monthly_climates";
	public static final String MONTHLY_CLIMATE_TABLE_ID_COLUMN = "_id";

	private RestClient restClient = RestClient.getInstance();

	private static final String SYNC_DATE_TABLE_CREATE = "create table " + SYNC_DATE_TABLE + " (" +
			SYNC_DATE_TABLE_ID_COLUMN + " integer primary key autoincrement, " +
			SYNC_DATE_TABLE_DATE_COLUMN + " datetime" + 
	")";

	/***
	private static final String WEATHER_TYPE_TABLE_CREATE = "create table " + WEATHER_TYPE_TABLE + " (" +
			WEATHER_TYPE_TABLE_ID_COLUMN + " text not null unique" +
		")";
	private static final String WEATHER_TABLE_CREATE = "create table " + WEATHER_TABLE + " (" +
			WEATHER_TABLE_ID_COLUMN + " integer primary key autoincrement, " +
			"plot_id integer not null references " + PLOTS_TABLE + "(" + PLOTS_TABLE_ID_COLUMN + ") on delete cascade, " +
			"type text not null references " + WEATHER_TYPE_TABLE + "(" + WEATHER_TYPE_TABLE_ID_COLUMN + "), " + 
			"month integer not null check (month>1 AND month<12), " +
			"value real, " +
			"unique(plot_id, type, month) on conflict fail" +
		")";
	***/
	private static final String MONTHLY_CLIMATE_TABLE_CREATE = "create table " + MONTHLY_CLIMATE_TABLE + " (" +
			MONTHLY_CLIMATE_TABLE_ID_COLUMN + " integer primary key autoincrement, " +
			"plot_id integer not null references " + PLOTS_TABLE + "(" + PLOTS_TABLE_ID_COLUMN + ") on delete cascade, " +
			"month integer not null check (month>=1 AND month<=12), " +
			"precipitation real, " +
			"avg_temp real, " +
			"max_temp real, " +
			"min_temp real, " +
			"unique(plot_id, month) on conflict fail" +
		")";

	private static final String PLOTS_TABLE_CREATE = "create table " + PLOTS_TABLE + " (" +
		PLOTS_TABLE_ID_COLUMN + " integer primary key autoincrement, " +
		PLOTS_TABLE_REMOTE_ID_COLUMN + " text," + 
		PLOTS_TABLE_NAME_COLUMN + " text not null unique, " +
		PLOTS_TABLE_RECORDER_NAME_COLUMN + " text, " +
		"organization text, " +
		"test_plot integer not null default 0, " + 
		//"latitude numeric not null, " +
		//"longitude numeric not null, " +
		"latitude numeric, " +
		"longitude numeric, " +
		"city text, " +
		"modified_date datetime, " +
		//"landcover_id integer references landcover_definitions(landcover_definition_id), " +
		"landcover text, " +
		"grazed integer, " +
		"flooding integer, " +
		//"slope_angle_id integer references slope_angle_definitions(slope_angle_definition_id), " +
		//"cross_slope_shape_id integer references slope_shape_definitions(slope_shape_definition_id), " +
		//"down_slope_shape_id integer references slope_shape_definitions(slope_shape_definition_id)" +
		"slope text, " +
		"cross_slope_shape text, " +
		"down_slope_shape text, " +
		"slope_shape text, " +
		"soil_horizon_1_fragment text, " +
		"soil_horizon_1_color integer, " +
		"soil_horizon_1_texture text, " +
		"soil_horizon_2_fragment text, " +
		"soil_horizon_2_color integer, " +
		"soil_horizon_2_texture text, " +
		"soil_horizon_3_fragment text, " +
		"soil_horizon_3_color integer, " +
		"soil_horizon_3_texture text, " +
		"soil_horizon_4_fragment text, " +
		"soil_horizon_4_color integer, " +
		"soil_horizon_4_texture text, " +
		"soil_horizon_5_fragment text, " +
		"soil_horizon_5_color integer, " +
		"soil_horizon_5_texture text, " +
		"soil_horizon_6_fragment text, " +
		"soil_horizon_6_color integer, " +
		"soil_horizon_6_texture text, " +
		"soil_horizon_7_fragment text, " +
		"soil_horizon_7_color integer, " +
		"soil_horizon_7_texture text, " +
		"surface_cracking integer, " +
		"surface_salt integer," +
		"north_image_filename text," + 
		"east_image_filename text," + 
		"south_image_filename text," + 
		"west_image_filename text," + 
		"soil_pit_image_filename text," + 
		"soil_samples_image_filename text," + 
		"grass_productivity real," +
		"grass_erosion real," +
		"crop_productivity real," +
		"crop_erosion real," +
		"gdal_elevation real," +
		"gdal_fao_lgp text," +
		"gdal_aridity_index real," +
		"awc_soil_profile real," +
		"avg_annual_precip real," +
		"recommendation text," + 
		"needs_upload integer default 0," +
		"needs_photo_upload integer default 0," +
		"delete_flag integer default 0" +
	")";

	//TODO: unused for now
	private static final String SOIL_HORIZONS_TABLE_CREATE = "create table " + SOIL_HORIZONS_TABLE + " (" +
			"id integer primary key autoincrement, " +
			"plot_id integer references plots(plot_id), " +
			"soil_horizon_id integer references soil_horizon_definitions(soil_horizon_definition_id), " +
			"color text, " +
			"texture text" +
		 ")";

	private static final String SOIL_HORIZON_DEFINITIONS_TABLE_CREATE = "create table " + SOIL_HORIZON_DEFINITIONS_TABLE + " (" +
			"id integer primary key autoincrement, " +
			"depth_range text" +
		 ")";
	
	private LandPKSDBHelper dbHelper;
	
	public LandPKSDatabaseAdapter (Context context) {
		dbHelper = new LandPKSDBHelper(context, DB_NAME, null, DB_VERSION);
		//loadUserPlots(false);
	}

	public String getDBPath() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.getPath();
	}
	/***
	public static String getDBPath() {
		return dbPath;
	}
	***/
	
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
            	File lpksDir = new File (sd.getAbsolutePath() + "/dbBackups");
            	lpksDir.mkdirs();
                //String currentDBPath = "/data/" + getPackageName() + "/databases/LandPKS.db";
                //String currentDBPath = LandPKSApplication.getInstance().getDatabaseAdapter().getDBPath();
                //String currentDBPath = LandPKSDatabaseAdapter.getDBPath();
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
                String backupDBFile = "LandPKS-" + sdf.format(new Date()) + ".db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(lpksDir, backupDBFile);

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

	
	private Plot loadPlot(Cursor resultSet) {
		Plot plot = new Plot();
		plot.ID = resultSet.getLong(resultSet.getColumnIndex("_id"));
		if (!resultSet.isNull(resultSet.getColumnIndex("test_plot")))
			plot.testPlot = resultSet.getInt(resultSet.getColumnIndex("test_plot")) != 0;
		plot.remoteID = resultSet.getString(resultSet.getColumnIndex("remote_id"));
		plot.name = resultSet.getString(resultSet.getColumnIndex("name"));
		plot.recorderName = resultSet.getString(resultSet.getColumnIndex("recorder_name"));
		plot.organization = resultSet.getString(resultSet.getColumnIndex("organization"));
		if (!resultSet.isNull(resultSet.getColumnIndex("latitude")))
			plot.latitude = resultSet.getDouble(resultSet.getColumnIndex("latitude"));
		if (!resultSet.isNull(resultSet.getColumnIndex("longitude")))
			plot.longitude = resultSet.getDouble(resultSet.getColumnIndex("longitude"));
		plot.city = resultSet.getString(resultSet.getColumnIndex("city"));
		try {
			if (plot.remoteID != null) {
				//plot.dateModified = LandPKSApplication.LPKS_DATE_FORMAT.parse(resultSet.getString(resultSet.getColumnIndex("modified_date")));
				try {
					plot.dateModified = LandPKSApplication.LPKS_DATE_FORMAT.parse(resultSet.getString(resultSet.getColumnIndex("modified_date")));
				} catch (ParseException eX) {
					plot.dateModified = LandPKSApplication.OLD_LPKS_DATE_FORMAT.parse(resultSet.getString(resultSet.getColumnIndex("modified_date")));
				}
			}
		} catch (Exception eX) {
			Log.i(TAG, "", eX);
		} 
		plot.landcover = resultSet.getString(resultSet.getColumnIndex("landcover"));
		if (!resultSet.isNull(resultSet.getColumnIndex("grazed")))
			plot.grazed = resultSet.getInt(resultSet.getColumnIndex("grazed")) != 0; //TODO: boolean in model class?
		if (!resultSet.isNull(resultSet.getColumnIndex("flooding")))
			plot.flooding = resultSet.getInt(resultSet.getColumnIndex("flooding")) != 0;
		plot.slope = resultSet.getString(resultSet.getColumnIndex("slope"));
		plot.crossSlopeShape = resultSet.getString(resultSet.getColumnIndex("cross_slope_shape"));
		plot.downSlopeShape = resultSet.getString(resultSet.getColumnIndex("down_slope_shape"));
		//plot.slopeShape = resultSet.getString(resultSet.getColumnIndex("slope_shape"));
		
        for(HorizonName h : HorizonName.values()) {
        	SoilHorizon horizon = new SoilHorizon();
			String horizonNumber = "" + (h.ordinal() + 1);
			if (!resultSet.isNull(resultSet.getColumnIndex("soil_horizon_" + horizonNumber + "_fragment")))
				horizon.rockFragment = resultSet.getString(resultSet.getColumnIndex("soil_horizon_" + horizonNumber + "_fragment"));
			if (!resultSet.isNull(resultSet.getColumnIndex("soil_horizon_" + horizonNumber + "_color")))
				horizon.color = resultSet.getInt(resultSet.getColumnIndex("soil_horizon_" + horizonNumber + "_color"));
			if (!resultSet.isNull(resultSet.getColumnIndex("soil_horizon_" + horizonNumber + "_texture")))
				horizon.texture = resultSet.getString(resultSet.getColumnIndex("soil_horizon_" + horizonNumber + "_texture"));
			
			if (horizon.rockFragment != null || horizon.color != null || horizon.texture != null)
				plot.soilHorizons.put(h.name, horizon);
        }
		
		if (!resultSet.isNull(resultSet.getColumnIndex("surface_cracking")))
			plot.surfaceCracking = resultSet.getInt(resultSet.getColumnIndex("surface_cracking")) != 0;
		if (!resultSet.isNull(resultSet.getColumnIndex("surface_salt")))
			plot.surfaceSalt = resultSet.getInt(resultSet.getColumnIndex("surface_salt")) != 0;

		plot.northImageFilename = resultSet.getString(resultSet.getColumnIndex("north_image_filename"));
		plot.eastImageFilename = resultSet.getString(resultSet.getColumnIndex("east_image_filename"));
		plot.southImageFilename = resultSet.getString(resultSet.getColumnIndex("south_image_filename"));
		plot.westImageFilename = resultSet.getString(resultSet.getColumnIndex("west_image_filename"));
		plot.soilPitImageFilename = resultSet.getString(resultSet.getColumnIndex("soil_pit_image_filename"));
		plot.soilSamplesImageFilename = resultSet.getString(resultSet.getColumnIndex("soil_samples_image_filename"));

		plot.recommendation = resultSet.getString(resultSet.getColumnIndex("recommendation"));

		plot.grassProductivity = resultSet.isNull(resultSet.getColumnIndex("grass_productivity")) ? null : resultSet.getDouble(resultSet.getColumnIndex("grass_productivity"));
		plot.grassErosion = resultSet.isNull(resultSet.getColumnIndex("grass_erosion")) ? null : resultSet.getDouble(resultSet.getColumnIndex("grass_erosion"));
		plot.cropProductivity = resultSet.isNull(resultSet.getColumnIndex("crop_productivity")) ? null : resultSet.getDouble(resultSet.getColumnIndex("crop_productivity"));
		plot.cropErosion = resultSet.isNull(resultSet.getColumnIndex("crop_erosion")) ? null : resultSet.getDouble(resultSet.getColumnIndex("crop_erosion"));
		plot.gdalElevation = resultSet.isNull(resultSet.getColumnIndex("gdal_elevation")) ? null : resultSet.getDouble(resultSet.getColumnIndex("gdal_elevation"));
		plot.gdalFaoLgp = resultSet.isNull(resultSet.getColumnIndex("gdal_fao_lgp")) ? null : resultSet.getString(resultSet.getColumnIndex("gdal_fao_lgp"));
		plot.gdalAridityIndex = resultSet.isNull(resultSet.getColumnIndex("gdal_aridity_index")) ? null : resultSet.getDouble(resultSet.getColumnIndex("gdal_aridity_index"));
		plot.avgAnnualPrecipitation = resultSet.isNull(resultSet.getColumnIndex("avg_annual_precip")) ? null : resultSet.getDouble(resultSet.getColumnIndex("avg_annual_precip"));
		plot.awcSoilProfile = resultSet.isNull(resultSet.getColumnIndex("awc_soil_profile")) ? null : resultSet.getDouble(resultSet.getColumnIndex("awc_soil_profile"));
		
		//TODO query climate table and then:
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet2 = null;
		try {
			resultSet2 = db.query(MONTHLY_CLIMATE_TABLE, null, "plot_id = " + plot.ID, null, null, null, "month asc");
			List<MonthlyClimate> monthlyClimates = new ArrayList<MonthlyClimate>();
			//if (plot.remoteID.contains("uh4")) {
			//	boolean x = true;
			//	x = false;
			//}
			while (resultSet2.moveToNext()) {
				MonthlyClimate mC = new MonthlyClimate();
				mC.month = resultSet2.isNull(resultSet2.getColumnIndex("month")) ? null : resultSet2.getInt(resultSet2.getColumnIndex("month"));
				mC.precipitation = resultSet2.isNull(resultSet2.getColumnIndex("precipitation")) ? null : resultSet2.getDouble(resultSet2.getColumnIndex("precipitation"));
				mC.avgTemp = resultSet2.isNull(resultSet2.getColumnIndex("avg_temp")) ? null : resultSet2.getDouble(resultSet2.getColumnIndex("avg_temp"));
				mC.maxTemp = resultSet2.isNull(resultSet2.getColumnIndex("max_temp")) ? null : resultSet2.getDouble(resultSet2.getColumnIndex("max_temp"));
				mC.minTemp = resultSet2.isNull(resultSet2.getColumnIndex("min_temp")) ? null : resultSet2.getDouble(resultSet2.getColumnIndex("min_temp"));
				monthlyClimates.add(mC);
				//Log.i(TAG, "loadPlot, plot " + plot.name + ", added mC " + mC.month);
			}
			plot.monthlyClimates = monthlyClimates.size() == 0 ? null : monthlyClimates;
		} finally {
			if (resultSet2 != null) resultSet2.close();
			//db.close();
		}
		
		plot.needsPhotoUpload = resultSet.getInt(resultSet.getColumnIndex("needs_photo_upload"));
		plot.needsUpload = resultSet.getInt(resultSet.getColumnIndex("needs_upload"));
		
		return plot;
	}
	
	private Date getLatestModifiedDate() {
		Date retDate = null;
 		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.rawQuery("select max(modified_date) from plots", null);
			if (resultSet.moveToNext()) {
				try {
					retDate = LandPKSApplication.LPKS_DATE_FORMAT.parse(resultSet.getString(0));
				} catch (ParseException pEX) {
					retDate = LandPKSApplication.OLD_LPKS_DATE_FORMAT.parse(resultSet.getString(0));
				}
			}
		} catch (Exception ex) {
			Log.w(TAG, Log.getStackTraceString(ex));
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			//db.close();
		}		
		return retDate;
	}
	
	//public boolean loadUserPlots() {
	//	return loadUserPlots(true);
	//}
	public boolean loadUserPlots(boolean clearOldData) {  //TODO: take an optional date here and pass on to fetchPlots
		boolean retVal = false;
		if (clearOldData) {
			clearPlots();
		}

		Date afterDate = getLastSyncDate();
		
		RestClient restClient = RestClient.getInstance();
		//List<Plot> plotList = restClient.fetchPlots();
		List<Plot> plotList = restClient.fetchPlots(afterDate);
		if (plotList != null) {
			retVal = true;
			for (Plot plot: plotList) {
				if (getPlotID(plot.name, plot.recorderName) == null) {
					addPlot(plot);  
				}
			}
		}
		
		setLastSyncDate(getLatestModifiedDate()); //TODO: originally used new Date() here but I was concerned about possible time zone issue here?

		return retVal;
	}
	
	protected SQLiteDatabase getReadableDB() {
		return dbHelper.getReadableDatabase();
	}
	
	protected boolean columnsOK(String[] projection) {
		boolean retVal = false;
		if (projection == null) {
			retVal = true;
		} else {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM plots LIMIT 1", null);
			String[] colNames = cursor.getColumnNames();
			if (projection != null) {
				HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
				HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(colNames));
				// check if all columns which are requested are available
				retVal = availableColumns.containsAll(requestedColumns);
		    }
		}
		return retVal;
	}
  

	public ArrayList<Plot> getAnalyzedPlots(){
		ArrayList<Plot> plots = new ArrayList<Plot>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			//resultSet = db.query("plots", null, "grass_productivity is not null or grass_erosion is not null or crop_productivity is not null or crop_erosion is not null", null, null, null, "name collate nocase asc");
			//resultSet = db.query("plots", null, "gdal_elevation is not null or gdal_fao_lgp is not null or gdal_aridity_index is not null or grass_productivity is not null or grass_erosion is not null or crop_productivity is not null or crop_erosion is not null", null, null, null, "modified_date desc");
			resultSet = db.query("plots", null, "remote_id is not null", null, null, null, "modified_date desc");
			Plot plot;
			while (resultSet.moveToNext()) {
				plot = loadPlot(resultSet);
				plots.add(plot);
			}
		} finally {
			if (resultSet != null) resultSet.close();
			//db.close();
		}
		return plots;
	}
	
	public ArrayList<Plot> getPlots(){
		ArrayList<Plot> plots = new ArrayList<Plot>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.query("plots", null, "delete_flag != 1", null, null, null, "name collate nocase asc");
			Plot plot;
			while (resultSet.moveToNext()) {
				plot = loadPlot(resultSet);
				plots.add(plot);
			}
		} finally {
			if (resultSet != null) resultSet.close();
			//db.close();
		}
		return plots;
	}
	
	
	public Plot getPlot(long id){
		Plot plot = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.query("plots", null, "_id=" + id, null, null, null, null);
			while (resultSet.moveToNext()) {
				plot = loadPlot(resultSet);

			}
		} finally {
			if (resultSet != null) resultSet.close();
			//db.close();
		}
		return plot;
	}

	public Long getPlotID(String name, String recorderName){
		Long id = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.query("plots", new String[] {"_id"}, "name = ? and recorder_name = ?", new String[] {name, recorderName}, null, null, null);
			while (resultSet.moveToNext()) {
				id = resultSet.getLong(0);
			}
		} finally {
			if (resultSet != null) resultSet.close();
			//db.close();
		}
		return id;
	}

	public Double getMaxValue(String columnName) {
		columnName = columnName.replaceAll("(\\p{Ll})(\\p{Lu})","$1_$2").toLowerCase(); 
		Double retVal = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.query("plots", new String[] {"max(" + columnName + ")"}, null, null, null, null, null);
			while (resultSet.moveToNext()) {
				retVal = resultSet.getDouble(0);
			}			
		} catch (Exception eX){
			int i = 1;
		} finally {
			if (resultSet != null) resultSet.close();
			//db.close();
		}
		return retVal;
	}
	
	public boolean plotExists (String name) {
		boolean retVal = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			//resultSet = db.query("plots", null, "name='" + name + "'", null, null, null, null);
			resultSet = db.query("plots", null, "name=? and delete_flag=0", new String[] {name}, null, null, null);
			retVal = (resultSet.moveToFirst());
		} catch (Exception eX) {
			retVal = false;
		}
		return retVal;
	}
	
	public void addPlot(Plot plot) {
		//Log.i(TAG, "addPlot, entered with plot " + plot.name);

		if (plot.name == null || "".equals(plot.name))
			return;
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	values.put("delete_flag", 0);
	    	values.put("remote_id", plot.remoteID);
	    	if (plot.testPlot != null) values.put("test_plot", plot.testPlot ? 1 : 0);
	    	values.put("name", plot.name);
	    	values.put("recorder_name", plot.recorderName);
	    	values.put("organization", plot.organization);
	    	values.put("latitude", plot.latitude);
	    	values.put("longitude", plot.longitude);
	    	values.put("city", plot.city);
	    	if (plot.dateModified != null) 
    			values.put("modified_date", LandPKSApplication.LPKS_DATE_FORMAT.format(plot.dateModified));
	    	values.put("landcover", plot.landcover);
	    	if (plot.grazed != null) values.put("grazed", plot.grazed ? 1 : 0);
	    	if (plot.flooding != null) values.put("flooding", plot.flooding ? 1 : 0);
	    	values.put("slope", plot.slope);
	    	values.put("cross_slope_shape", plot.crossSlopeShape);
	    	values.put("down_slope_shape", plot.downSlopeShape);
	    	//values.put("slope_shape", plot.slopeShape);
	    	
	        for(HorizonName h : HorizonName.values()) {
	        	SoilHorizon horizon = plot.soilHorizons.get(h.name);
		    	if (horizon != null) {
					String horizonNumber = "" + (h.ordinal() + 1);
		    		values.put("soil_horizon_" + horizonNumber + "_fragment", horizon.rockFragment);
		    		values.put("soil_horizon_" + horizonNumber + "_color", horizon.color);
		    		values.put("soil_horizon_" + horizonNumber + "_texture", horizon.texture);
		    	}
	        }
	    	
	    	if (plot.surfaceCracking != null) values.put("surface_cracking", plot.surfaceCracking ? 1 : 0);
	    	if (plot.surfaceSalt != null) values.put("surface_salt", plot.surfaceSalt ? 1 : 0);

	    	values.put("north_image_filename", plot.northImageFilename);
	    	values.put("east_image_filename", plot.eastImageFilename);
	    	values.put("south_image_filename", plot.southImageFilename);
	    	values.put("west_image_filename", plot.westImageFilename);
	    	values.put("soil_pit_image_filename", plot.soilPitImageFilename);
	    	values.put("soil_samples_image_filename", plot.soilSamplesImageFilename);

	    	values.put("recommendation", plot.recommendation);
	    	values.put("grass_productivity", plot.grassProductivity);
	    	values.put("grass_erosion", plot.grassErosion);
	    	values.put("crop_productivity", plot.cropProductivity);
	    	values.put("crop_erosion", plot.cropErosion);
        	values.put("gdal_elevation", plot.gdalElevation);
        	values.put("gdal_fao_lgp", plot.gdalFaoLgp);
        	values.put("gdal_aridity_index", plot.gdalAridityIndex);
        	values.put("avg_annual_precip", plot.avgAnnualPrecipitation);
        	values.put("awc_soil_profile", plot.awcSoilProfile);		        	
	
	    	values.put("needs_upload", plot.needsUpload);
	    	values.put("needs_photo_upload", plot.needsPhotoUpload);
	    	
			String[] qVals = {plot.name, plot.recorderName};
			int rows = db.update("plots",  values, "name = ? and recorder_name = ?", qVals);
			//Log.i(TAG, "addPlot, update " + plot.name + " rows = " + rows);
			if (rows == 0) {
				plot.ID  = db.insert("plots", null, values);		
				//Log.i(TAG, "addPlot, plot " + plot.name + " inserted, calling addClimateRecords");
				//if (plot.monthlyClimates != null) 
				//	addClimateRecords(plot);
			} else {
				plot.ID = getPlotID(plot.name, plot.recorderName);			 
			}
			if (plot.monthlyClimates != null) 
				addClimateRecords(plot);
		} catch (SQLException sEX) {
			Log.w(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
	}
	
	public void deletePlot(Plot plot) {
		if (plot.name == null || "".equals(plot.name)) return;
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	values.put("delete_flag", 1);
			db.update("plots", values, "name = '" + plot.name + "'", null);  
		} catch (SQLException sEX) {
			Log.w(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
	}
	
	public void clearPlots() {
		//Log.i(TAG, "clearPlots");

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	db.delete(PLOTS_TABLE, null, null);
	    	db.delete(MONTHLY_CLIMATE_TABLE, null, null);
		} catch (SQLException sEX) {
			Log.w(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
	}

	public void setPlotUpload(Plot plot, int uploadState) {
		if (plot.name == null || "".equals(plot.name)) return;
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	values.put("needs_upload", uploadState);
			db.update("plots", values, "_id = " + plot.ID, null);  
		} catch (SQLException sEX) {
			Log.w(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
	}

	private boolean allPhotosUploaded(Plot plot) {
		boolean retVal = true;
		for (PhotoSubject photoSubject : PhotoSubject.values()) {
			String imageFileName =  null;
			switch (photoSubject) {
				case LANDSCAPE_NORTH:
					imageFileName = plot.northImageFilename;
					break;
				case LANDSCAPE_EAST:
					imageFileName = plot.eastImageFilename;					
					break;
				case LANDSCAPE_SOUTH:
					imageFileName = plot.southImageFilename;
					break;
				case LANDSCAPE_WEST:
					imageFileName = plot.westImageFilename;
					break;
				case SOIL_PIT:
					imageFileName = plot.soilPitImageFilename;
					break;
				case SOIL_SAMPLES:
					imageFileName = plot.soilSamplesImageFilename;
					break;
			}
			//if any have filename but not url, return false
			if (imageFileName != null && !"".equals(imageFileName) && !imageFileName.contains("http://")) {
				retVal = false;				
				break;
			}	
		}
		return retVal;
	}
	
	//public boolean syncPhotos() {
	public synchronized void syncPhotos() {
		//boolean returnVal = false;
 		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor resultSet = null;
		try {
			//resultSet = db.rawQuery("select * from plots where needs_photo_upload = 1", null);
			resultSet = db.rawQuery("select * from plots where remote_id is not null and remote_id != '' and needs_photo_upload = 1", null);
			Plot plot;
			while (resultSet.moveToNext()) {
				plot = loadPlot(resultSet);
				plot = restClient.putPhotosForPlot(plot);
				
				if (plot != null) {
		        	ContentValues values = new ContentValues();
		        	values.put("north_image_filename", plot.northImageFilename);
		        	values.put("east_image_filename", plot.eastImageFilename);
		        	values.put("south_image_filename", plot.southImageFilename);
		        	values.put("west_image_filename", plot.westImageFilename);
		        	values.put("soil_pit_image_filename", plot.soilPitImageFilename);
		        	values.put("soil_samples_image_filename", plot.soilSamplesImageFilename);
			    	if (allPhotosUploaded(plot)) values.put("needs_photo_upload", 0);  //TODO: check for urls in each first
		        	//db.update("plots", values, "id=?",  new String[] {Long.toString(plot.ID)});
		        	db.update("plots", values, "_id=?",  new String[] {Long.toString(plot.ID)});
		        	//returnVal = true;
				}
			}
		} catch (Exception ex) {
			//returnVal = false; //TODO: appropriate action?
			Log.w(TAG, Log.getStackTraceString(ex));
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			//db.close();
		}
		//return returnVal;	
	}
	
	public synchronized boolean syncPlots() {  //true if *any* plots successfully synced
		Log.i(TAG, "syncPlots, enter");
		boolean returnVal = false;
 		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.rawQuery("select * from plots where needs_upload = 1", null);
			Plot plot;
			while (resultSet.moveToNext()) {
				plot = loadPlot(resultSet);
				Log.i(TAG, "syncPlots, attempting to upload " + plot.name);
				plot = restClient.putPlot(plot);
				
				if (plot != null) {
					Log.i(TAG, "syncPlots, successful upload of " + plot.name);
		        	ContentValues values = new ContentValues();
		        	values.put("remote_id", plot.remoteID);
		    		values.put("modified_date", LandPKSApplication.LPKS_DATE_FORMAT.format(plot.dateModified));
		        	values.put("recommendation", plot.recommendation);
		        	values.put("grass_productivity", plot.grassProductivity);
		        	values.put("grass_erosion", plot.grassErosion);
		        	values.put("crop_productivity", plot.cropProductivity);
		        	values.put("crop_erosion", plot.cropErosion);
		        	values.put("gdal_elevation", plot.gdalElevation);
		        	values.put("gdal_fao_lgp", plot.gdalFaoLgp);
		        	values.put("gdal_aridity_index", plot.gdalAridityIndex);
		        	values.put("avg_annual_precip", plot.avgAnnualPrecipitation);
		        	values.put("awc_soil_profile", plot.awcSoilProfile);		        	
			    	values.put("needs_upload", 0);
		        	db.update("plots", values, "_id=?",  new String[] {Long.toString(plot.ID)});

		        	if (plot.monthlyClimates!= null) 
		        		addClimateRecords(plot);
		        	
		        	returnVal = true;
				}
			}
			
			loadUserPlots(false); 
			
		} catch (Exception ex) {
			returnVal = false; //TODO: appropriate action?
			Log.w(TAG, Log.getStackTraceString(ex));
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			//db.close();
		}
		
		Log.i(TAG, "syncPlots, exit");
		return returnVal;
		
	}
	
	private void addClimateRecords(Plot plot) {
		//Log.i(TAG, "addClimateRecords, enter with plot " + plot.name);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
	    	ContentValues values = new ContentValues();
	    	for (MonthlyClimate mC : plot.monthlyClimates) {
	    		//Log.i(TAG, "addClimateRecords, mC =  " + mC.month);
	    		values.clear();
	    		values.put("month", mC.month); 	
	    		values.put("precipitation", mC.precipitation); 	
	    		values.put("avg_temp", mC.avgTemp); 	
	    		values.put("max_temp", mC.maxTemp); 	
	    		values.put("min_temp", mC.minTemp); 	
	    		String[] qVals = {""+plot.ID, ""+mC.month};
	    		int rows = db.update(MONTHLY_CLIMATE_TABLE,  values, "plot_id = ? and month = ?", qVals);
	    		if (rows == 0) {
		    		values.put("plot_id", plot.ID);
	    			long row = db.insert(MONTHLY_CLIMATE_TABLE, null, values);		
	    			if (row == -1) 
	    				throw new SQLException("Hogan's goat! problem inserting climate record for plot " + plot.remoteID + ", month " + mC.month);
	    		}
	    	}
		} catch (SQLException sEX) {
			Log.w(TAG, Log.getStackTraceString(sEX));
		} finally {
			//db.close();
		}
	}
	
	private Date getLastSyncDate() {
		Date returnVal = null;
 		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor resultSet = null;
		try {
			resultSet = db.rawQuery("select * from sync_date", null);
			if (resultSet.moveToNext()) {
				try {
					returnVal = LandPKSApplication.LPKS_DATE_FORMAT.parse(resultSet.getString(1));
				} catch (ParseException pEX) {
					returnVal = LandPKSApplication.OLD_LPKS_DATE_FORMAT.parse(resultSet.getString(1));
				}
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
	
	public void setLastSyncDate(Date date) {
 		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor resultSet = null;
		try {
			ContentValues values = new ContentValues();
	    	values.put("sync_date", LandPKSApplication.LPKS_DATE_FORMAT.format(date));
	    	//db.update("sync_date", values, null, null);
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
	
	//private static class LandPKSDBHelper extends SQLiteOpenHelper {
	private class LandPKSDBHelper extends SQLiteOpenHelper {
		
		Context context;
		
		public LandPKSDBHelper(Context context, String name, CursorFactory cFactory, int version) {
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
			db.execSQL(SOIL_HORIZON_DEFINITIONS_TABLE_CREATE);
			db.execSQL(SOIL_HORIZONS_TABLE_CREATE);
			db.execSQL(PLOTS_TABLE_CREATE);			
			db.execSQL(SYNC_DATE_TABLE_CREATE);			
			db.execSQL(MONTHLY_CLIMATE_TABLE_CREATE);
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
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
					//For all previous, just wipe and start over
					Log.i(TAG, "Upgrading from ancient version " + oldVersion + ".");
					hosed = true;
					break;
				case 15: 
					try {
						renameColumns(db, PLOTS_TABLE_CREATE, PLOTS_TABLE, new String[] {"maize_productivity", "maize_erosion"}, new String[] {"crop_productivity", "crop_erosion"});
						//renameColumns(db, PLOTS_TABLE_CREATE, PLOTS_TABLE, new String[] {"maize_productivity", "gubba"}, new String[] {"crop_productivity", "crop_erosion"});
					} catch (Exception eX) {
						Log.e(TAG, "Unable to rename columns", eX);
						hosed = true;
					}
				case 16:
					try {
						db.execSQL(SYNC_DATE_TABLE_CREATE);
					} catch (Exception eX) {
						Log.e(TAG, "Unable to create sync data table", eX);
						hosed = true;
					}
				case 17:
					try {
						//rename all soil horizon rock fragment values					
						ContentValues values = new ContentValues();
						for (SoilRockFragmentVolume sRFV : SoilRockFragmentVolume.values()) {
							for (HorizonName hN : HorizonName.values()) {
								String columnName = "soil_horizon_" + (hN.ordinal()+1) + "_fragment";
								values.clear();
						    	values.put(columnName, sRFV.name());
								db.update("plots", values, columnName + " = ?", new String[] {sRFV.getDisplayName()});							
							}
						}
					} catch (Exception eX) {
						Log.e(TAG, "Unable to reassign rock fragment values", eX);
						hosed = true;
					}
				case 18:
					try {
					    db.execSQL("ALTER TABLE " + PLOTS_TABLE + " add column test_plot integer not null default 0");
					} catch (Exception eX) {
						Log.e(TAG, "Unable to add column test_plot", eX);
						hosed = true;
					}
				case 19:
					try {
					    db.execSQL("ALTER TABLE " + PLOTS_TABLE + " add column gdal_elevation real");
					} catch (Exception eX) {
						Log.e(TAG, "Unable to add column gdal_elevation", eX);
						hosed = true;
					}
					try {
					    db.execSQL("ALTER TABLE " + PLOTS_TABLE + " add column gdal_fao_lgp text");
					} catch (Exception eX) {
						Log.e(TAG, "Unable to add column gdal_fao_lgp", eX);
						hosed = true;
					}
					try {
					    db.execSQL("ALTER TABLE " + PLOTS_TABLE + " add column gdal_aridity_index real");
					} catch (Exception eX) {
						Log.e(TAG, "Unable to add column gdal_aridity_index", eX);
						hosed = true;
					}
					try {
					    db.execSQL("ALTER TABLE " + PLOTS_TABLE + " add column awc_soil_profile real");
					} catch (Exception eX) {
						Log.e(TAG, "Unable to add column awc_soil_profile", eX);
						hosed = true;
					}
					try {
					    db.execSQL("ALTER TABLE " + PLOTS_TABLE + " add column avg_annual_precip real");
					} catch (Exception eX) {
						Log.e(TAG, "Unable to add column avg_annual_precip", eX);
						hosed = true;
					}
					try {
						db.execSQL(MONTHLY_CLIMATE_TABLE_CREATE);
					} catch (Exception eX) {
						Log.e(TAG, "Unable to create climate table", eX);
						hosed = true;
					}
			}
			
			
			
			if (hosed) {
				Log.i(TAG, "Wiping db and starting over.");
				//drop everything and recreate
				db.execSQL("drop table if exists " + SOIL_HORIZON_DEFINITIONS_TABLE);
				db.execSQL("drop table if exists " + SOIL_HORIZONS_TABLE);
				db.execSQL("drop table if exists " + PLOTS_TABLE);
				db.execSQL("drop table if exists " + SYNC_DATE_TABLE);
				db.execSQL("drop table if exists " + MONTHLY_CLIMATE_TABLE);
				onCreate(db);				
			}
			
			
			//TODO: This only works if loadUserPlots is set to not delete un-uploaded plots for the same account.  Make sure we are doing that before keeping this.
			//bounce to force data refresh
			LandPKSApplication.getInstance().getCredential().setSelectedAccountName(null);  //TODO: causes PlotListActivity to initiate account selection.  Smarmy?
			Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("FORCE_REFRESH", "true");
			context.startActivity(i);
			
		}
	}
	
}
