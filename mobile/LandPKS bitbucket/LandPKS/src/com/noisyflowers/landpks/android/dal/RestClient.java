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
 * RestClient.java
 */

package com.noisyflowers.landpks.android.dal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.noisyflowers.landpks.android.CloudEndpointUtils;
import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.activities.SoilTextureActivity.SoilTexture;
import com.noisyflowers.landpks.android.fragments.SoilHorizonFragment.SoilRockFragmentVolume;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.fragments.LandUseCoverFragment.LandCover;
import com.noisyflowers.landpks.android.fragments.PhotosFragment.PhotoSubject;
import com.noisyflowers.landpks.android.fragments.SlopeFragment.Slope;
import com.noisyflowers.landpks.android.fragments.SlopeShapeFragment.Curve;
import com.noisyflowers.landpks.android.fragments.SlopeShapeFragment.SlopeShape;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment.HorizonName;
import com.noisyflowers.landpks.android.model.MonthlyClimate;
import com.noisyflowers.landpks.android.model.SoilHorizon;
import com.noisyflowers.landpks.server.gae.model.plotendpoint.Plotendpoint;
import com.noisyflowers.landpks.server.gae.model.plotendpoint.Plotendpoint.ListPlot;
import com.noisyflowers.landpks.server.gae.model.plotendpoint.model.CollectionResponsePlot;
//import com.noisyflowers.landpks.android.server.gae.model.plotendpoint.model.StringCollection;
import com.noisyflowers.landpks.server.gae.model.plotendpoint.model.StringReturn;

public class RestClient {
	private static final String TAG = RestClient.class.getName();
	
	private static RestClient myInstance = null;
	
    public static final String WEB_CLIENT_ID = "410858290704.apps.googleusercontent.com";
    
    private GoogleAccountCredential credential;
	    
	public static RestClient getInstance() {
		if (myInstance == null) {
			myInstance = new RestClient();
		}
		return myInstance;
	}
	
	private Plotendpoint buildPlotEndpoint(){
		Plotendpoint.Builder endpointBuilder = new Plotendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				LandPKSApplication.getInstance().getCredential()
				/*new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) { }
		}*/).setApplicationName("LandPKS");
		
		Plotendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		return endpoint;
	}
	
	public com.noisyflowers.landpks.android.model.Plot putPhotosForPlot(com.noisyflowers.landpks.android.model.Plot plot) {
		com.noisyflowers.landpks.android.model.Plot result = null;
				
		//TODO: Need to figure out how to get failure out to caller
		for (PhotoSubject photoSubject : PhotoSubject.values()) {
			result = uploadPhoto(plot, photoSubject);
		}
		
		return result;
	}
	
	private com.noisyflowers.landpks.android.model.Plot uploadPhoto(com.noisyflowers.landpks.android.model.Plot plot, PhotoSubject imageSubject) {
		//com.noisyflowers.landpks.android.model.Plot result = null;

		String photoFileName = null;
		switch (imageSubject) {
			case LANDSCAPE_NORTH:
				if (plot.northImageFilename != null && !"".equals(plot.northImageFilename) && !plot.northImageFilename.contains("http://")) {
					photoFileName = LandPKSApplication.getInstance().getFilesDir() + "/" + plot.northImageFilename;
				}
				break;
			case LANDSCAPE_EAST:
				if (plot.eastImageFilename != null && !"".equals(plot.eastImageFilename) && !plot.eastImageFilename.contains("http://")) {
					photoFileName = LandPKSApplication.getInstance().getFilesDir() + "/" + plot.eastImageFilename;
				}
				break;
			case LANDSCAPE_SOUTH:
				if (plot.southImageFilename != null && !"".equals(plot.southImageFilename) && !plot.southImageFilename.contains("http://")) {
					photoFileName = LandPKSApplication.getInstance().getFilesDir() + "/" + plot.southImageFilename;
				}
				break;
			case LANDSCAPE_WEST:
				if (plot.westImageFilename != null && !"".equals(plot.westImageFilename) && !plot.westImageFilename.contains("http://")) {
					photoFileName = LandPKSApplication.getInstance().getFilesDir() + "/" + plot.westImageFilename;
				}
				break;
			case SOIL_PIT:
				if (plot.soilPitImageFilename != null && !"".equals(plot.soilPitImageFilename) && !plot.soilPitImageFilename.contains("http://")) {
					photoFileName = LandPKSApplication.getInstance().getFilesDir() + "/" + plot.soilPitImageFilename;
				}
				break;
			case SOIL_SAMPLES:
				if (plot.soilSamplesImageFilename != null && !"".equals(plot.soilSamplesImageFilename) && !plot.soilSamplesImageFilename.contains("http://")) {
					photoFileName = LandPKSApplication.getInstance().getFilesDir() + "/" + plot.soilSamplesImageFilename;
				}
				break;
		}
		
		if (photoFileName != null) {
			String uploadURL = null;
			Plotendpoint endpoint = buildPlotEndpoint(); 
			try {
				//StringCollection results = endpoint.getPhotoUploadURL().execute();
				//uploadURL = results.getItems().get(0);
				StringReturn result = endpoint.getPhotoUploadURL().execute();
				uploadURL = result.getTheString();
			} catch (IOException iOEx) {
				Log.e(TAG, "Unable to get photo upload url.", iOEx);
			}
			
			if (uploadURL != null) {
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
				HttpPost httpPost = new HttpPost(uploadURL);
				
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();    
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				File file = new File(photoFileName);
				FileBody fb = new FileBody(file);	
				LandPKSApplication lpkApp = LandPKSApplication.getInstance();
				builder.addPart(lpkApp.getString(R.string.server_resource_photoupload_file_parameter_name), fb);  
				builder.addTextBody(lpkApp.getString(R.string.server_resource_photoupload_plotid_parameter_name), plot.remoteID);  
				builder.addTextBody(lpkApp.getString(R.string.server_resource_photoupload_photo_subject_parameter_name), imageSubject.getServerName());
				HttpEntity entity = builder.build();
				httpPost.setEntity(entity);
				try {
					HttpResponse response = httpClient.execute(httpPost);  
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						//response.getEntity().getContent();
						String imageURL = EntityUtils.toString(response.getEntity());
						switch (imageSubject) {
							case LANDSCAPE_NORTH:
	    						LandPKSApplication.getInstance().deleteFile(plot.northImageFilename);
								plot.northImageFilename = imageURL;  
								break;
							case LANDSCAPE_EAST:
	    						LandPKSApplication.getInstance().deleteFile(plot.eastImageFilename);
								plot.eastImageFilename = imageURL;  
								break;
							case LANDSCAPE_SOUTH:
	    						LandPKSApplication.getInstance().deleteFile(plot.southImageFilename);
								plot.southImageFilename = imageURL;  
								break;
							case LANDSCAPE_WEST:
	    						LandPKSApplication.getInstance().deleteFile(plot.westImageFilename);
								plot.westImageFilename = imageURL;  
								break;
							case SOIL_PIT:
	    						LandPKSApplication.getInstance().deleteFile(plot.soilPitImageFilename);
								plot.soilPitImageFilename = imageURL;  
								break;
							case SOIL_SAMPLES:
	    						LandPKSApplication.getInstance().deleteFile(plot.soilSamplesImageFilename);
								plot.soilSamplesImageFilename = imageURL;  
								break;
						}
					} else {
						Log.e(TAG, "Unable to upload photo. Status code: " + response.getStatusLine().getStatusCode());						
					}
				} catch (IOException iOEx) {
					Log.e(TAG, "Unable to upload photo.", iOEx);
				}
			}		
		}
		//return result;
		return plot;
	}
	
	public com.noisyflowers.landpks.android.model.Plot putPlot(com.noisyflowers.landpks.android.model.Plot plot) {
		com.noisyflowers.landpks.android.model.Plot result = null;
		
    	com.noisyflowers.landpks.server.gae.model.plotendpoint.model.Plot remotePlot = new com.noisyflowers.landpks.server.gae.model.plotendpoint.model.Plot();
    	//remotePlot.setId(System.currentTimeMillis()); //TODO:  !!!this is just for development, to get something working.  Revisit immediately
    	remotePlot.setName(plot.name);
    	remotePlot.setTestPlot(plot.testPlot);
    	remotePlot.setRecorderName(plot.recorderName);
    	remotePlot.setOrganization(plot.organization);
    	remotePlot.setLatitude(plot.latitude);
    	remotePlot.setLongitude(plot.longitude);
    	remotePlot.setCity(plot.city);
    	//remotePlot.setLandCover(plot.landcover);
    	remotePlot.setLandCover(plot.landcover == null ? null : LandCover.valueOf(plot.landcover).getServerName());
    	remotePlot.setFlooding(plot.flooding);
    	remotePlot.setGrazed(plot.grazed);
    	//remotePlot.setSlope(plot.slope); 
    	try {
    		remotePlot.setSlope(plot.slope == null ? null : Slope.valueOf(plot.slope).getServerName());  
    	} catch (IllegalArgumentException iAE) {
    		remotePlot.setSlope(plot.slope);      		
    	}
    	//remotePlot.setDownSlopeShape(plot.downSlopeShape);
    	//remotePlot.setCrossSlopeShape(plot.crossSlopeShape);
    	//remotePlot.setSlopeShape(plot.slopeShape);
    	//remotePlot.setSlopeShape(plot.slopeShape == null ? null :SlopeShape.valueOf(plot.slopeShape).getServerName());
    	remotePlot.setSlopeShape(plot.downSlopeShape == null || plot.crossSlopeShape == null ? null :
    		Curve.valueOf(plot.downSlopeShape).getServerName() + LandPKSApplication.getInstance().getString(R.string.server_resource_slope_curve_separator) + Curve.valueOf(plot.crossSlopeShape).getServerName());
    	remotePlot.setSurfaceCracking(plot.surfaceCracking);
    	remotePlot.setSurfaceSalt(plot.surfaceSalt); 
    	
    	/***
    	//TODO: Using arrays for these did not work.  The underlying REST implementation ate nulls and lost positional relationships.  Might be worth another look.
    	List<String> remoteHorizonsRockFragments = new ArrayList<String>();
    	List<Integer> remoteHorizonsColors = new ArrayList<Integer>();
    	List<String> remoteHorizonsTextures = new ArrayList<String>();
        for(HorizonName h : HorizonName.values()) {
        	SoilHorizon horizon = plot.soilHorizons.get(h.name);
	    	if (horizon != null) {
	    		remoteHorizonsRockFragments.add(h.ordinal(), horizon.rockFragment);
	    		remoteHorizonsColors.add(h.ordinal(), horizon.color);
	    		remoteHorizonsTextures.add(h.ordinal(), horizon.texture);
	    	}	
        }
    	remotePlot.setSoilHorizonsRockFragments(remoteHorizonsRockFragments);
		remotePlot.setSoilHorizonsColors(remoteHorizonsColors);
		remotePlot.setSoilHorizonsTextures(remoteHorizonsTextures);
		***/
    	Class[] rockFragmentFormalparams = new Class[] {String.class};
    	Class[] colorFormalparams = new Class[] {Integer.class};
    	Class[] textureFormalparams = new Class[] {String.class};
		Class<? extends com.noisyflowers.landpks.server.gae.model.plotendpoint.model.Plot> remotePlotClass = remotePlot.getClass();
        
		for(HorizonName h : HorizonName.values()) {
        	SoilHorizon horizon = plot.soilHorizons.get(h.name);
	    	if (horizon != null) {
	    		try {
		    		Method method = remotePlotClass.getMethod("setRockFragmentForSoilHorizon" + (h.ordinal()+1), rockFragmentFormalparams);
		    		//method.invoke(remotePlot, new Object[]{horizon.rockFragment});
		    		method.invoke(remotePlot, new Object[]{(horizon.rockFragment == null ? null : SoilRockFragmentVolume.valueOf(horizon.rockFragment).getServerName())});
	
		    		method = remotePlotClass.getMethod("setColorForSoilHorizon" + (h.ordinal()+1), colorFormalparams);
		    		method.invoke(remotePlot, new Object[]{horizon.color});
		    		
		    		if (horizon.texture != null) {
			    		method = remotePlotClass.getMethod("setTextureForSoilHorizon" + (h.ordinal()+1), textureFormalparams);
			    		//method.invoke(remotePlot, new Object[]{horizon.texture});
			    		//method.invoke(remotePlot, new Object[]{SoilTexture.serverNameLookup.get(horizon.texture)});
			    		method.invoke(remotePlot, new Object[]{(horizon.texture == null ? null : SoilTexture.valueOf(horizon.texture).getServerName())});
		    		}
		    	} catch (Exception eX) {
	    			Log.e(TAG, "Failed to execute method.", eX);
	    		}
	    	}	
        }
    	
		Plotendpoint endpoint = buildPlotEndpoint();
		
		int x = 0;
		while (x++ < 2) { //try twice, in case of timeout
			try { // first try to insert
				remotePlot = endpoint.insertPlot(remotePlot).execute();
				x = 2;
			} catch (Exception e) {
				Log.e(TAG, "Error inserting plot.", e);
				if (e.getMessage() != null && e.getMessage().contains("EntityExistsException")) { //if record already exists, then try update
					try {
						remotePlot = endpoint.updatePlot(remotePlot).execute();
						x = 2;
					} catch (Exception eX) {
						Log.e(TAG, "Error updating plot.", eX);
					}
				}
			}
		}
		
		if (remotePlot.getId() != null) {
			result = plot;
			result.remoteID = remotePlot.getId();
			result.recommendation = remotePlot.getRecommendation();
			result.grassProductivity = remotePlot.getGrassProductivity();
			result.grassErosion = remotePlot.getGrassErosion();
			result.cropProductivity = remotePlot.getCropProductivity();
			result.cropErosion = remotePlot.getCropErosion();
			result.gdalElevation = remotePlot.getGdalElevation();
			result.gdalFaoLgp = remotePlot.getGdalFaoLgp();
			result.gdalAridityIndex = remotePlot.getGdalAridityIndex();
			result.awcSoilProfile = remotePlot.getAwcSoilProfile();
			result.avgAnnualPrecipitation = remotePlot.getAverageAnnualPrecipitation();
			if (remotePlot.getMonthlyPrecipitation() != null &&
					remotePlot.getMonthlyAvgTemperature() != null &&
					remotePlot.getMonthlyMaxTemperature() != null &&
					remotePlot.getMonthlyMinTemperature() != null) {
				List<MonthlyClimate> mCs = new ArrayList<MonthlyClimate>();
				for (int month = 0; month < 12; month++) {
					MonthlyClimate mC = new MonthlyClimate();
					mC.month = month + 1;
					mC.precipitation = remotePlot.getMonthlyPrecipitation().get(month);
					mC.avgTemp = remotePlot.getMonthlyAvgTemperature().get(month);
					mC.maxTemp = remotePlot.getMonthlyMaxTemperature().get(month);
					mC.minTemp = remotePlot.getMonthlyMinTemperature().get(month);
					mCs.add(mC);
				}
				result.monthlyClimates = mCs;
			}
			
			try { result.dateModified = LandPKSApplication.LPKS_DATE_FORMAT.parse(remotePlot.getModifiedDate().toString()); } catch (Exception eX) {/*TODO: something?*/}  

		}
		
		return result;
	}
	
	//public List<com.noisyflowers.landpks.android.model.Plot> fetchPlots() {
	public synchronized List<com.noisyflowers.landpks.android.model.Plot> fetchPlots(Date date) {
		List<com.noisyflowers.landpks.android.model.Plot> plotList = null;
		
		DateTime dT = date == null ? null : new DateTime(date);
		
		Plotendpoint endpoint = buildPlotEndpoint();
		CollectionResponsePlot plots = null;
		int x = 0;
		while (x++ < 2) { //try twice, in case of timeout
			try { 
				//plots = endpoint.listPlot().execute();
				plots = endpoint.listPlot().setAfterDate(dT).execute();
				x = 2;
			} catch (Exception e) {
				Log.e(TAG, "Error fetching plots.", e);
			}
		}
		
		if (plots != null) {
			plotList = new ArrayList<com.noisyflowers.landpks.android.model.Plot>();
			if (plots.getItems() != null) {
				for (com.noisyflowers.landpks.server.gae.model.plotendpoint.model.Plot remotePlot: plots.getItems()) {
					com.noisyflowers.landpks.android.model.Plot plot = new com.noisyflowers.landpks.android.model.Plot();
					plot.remoteID = remotePlot.getId();
			    	plot.name = remotePlot.getName();
			    	plot.testPlot = remotePlot.getTestPlot(); //remotePlot.getTestPlot() == null ? true : remotePlot.getTestPlot();
			    	plot.recorderName = remotePlot.getRecorderName();
			    	plot.organization = remotePlot.getOrganization();
			    	plot.latitude = remotePlot.getLatitude();
			    	plot.longitude = remotePlot.getLongitude();
			    	plot.city = remotePlot.getCity();
			    	//plot.landcover = remotePlot.getLandCover();
			    	plot.landcover = remotePlot.getLandCover() == null ? null : LandCover.serverNameLookup.get(remotePlot.getLandCover()).name();
			    	plot.flooding = remotePlot.getFlooding();
			    	plot.grazed = remotePlot.getGrazed();
			    	//plot.slope = remotePlot.getSlope(); 
			    	try {
			    		plot.slope = remotePlot.getSlope() == null ? null : Slope.serverNameLookup.get(remotePlot.getSlope()).name();
			    	} catch (NullPointerException nPE) {
			    		plot.slope = remotePlot.getSlope();
			    	}
			    	//plot.slopeShape = remotePlot.getSlopeShape();
			    	//plot.slopeShape = remotePlot.getSlopeShape() == null ? null : SlopeShape.serverNameLookup.get(remotePlot.getSlopeShape()).name();
			    	LandPKSApplication appInstance = LandPKSApplication.getInstance();
			    	String[] slopeCurves = remotePlot.getSlopeShape() == null ? null :
			    		remotePlot.getSlopeShape().split(appInstance.getString(R.string.server_resource_slope_curve_separator));
			    	plot.downSlopeShape = slopeCurves == null ? null : 
			    		Curve.serverNameLookup.get(slopeCurves[Integer.parseInt(appInstance.getString(R.string.server_resource_slope_curve_down_position))]).name();
			    	plot.crossSlopeShape = slopeCurves == null ? null : 
			    		Curve.serverNameLookup.get(slopeCurves[Integer.parseInt(appInstance.getString(R.string.server_resource_slope_curve_cross_position))]).name();
			    	plot.surfaceCracking = remotePlot.getSurfaceCracking();
			    	plot.surfaceSalt = remotePlot.getSurfaceSalt(); 
			    	plot.northImageFilename = remotePlot.getLandscapeNorthPhotoURL(); 
			    	plot.eastImageFilename = remotePlot.getLandscapeEastPhotoURL(); 
			    	plot.southImageFilename = remotePlot.getLandscapeSouthPhotoURL(); 
			    	plot.westImageFilename = remotePlot.getLandscapeWestPhotoURL(); 
			    	plot.soilPitImageFilename = remotePlot.getSoilPitPhotoURL(); 
			    	plot.soilSamplesImageFilename = remotePlot.getSoilSamplesPhotoURL(); 
			    	plot.grassErosion = remotePlot.getGrassErosion();
			    	plot.grassProductivity = remotePlot.getGrassProductivity();
			    	plot.cropErosion = remotePlot.getCropErosion();
			    	plot.cropProductivity = remotePlot.getCropProductivity();
			    	plot.recommendation = remotePlot.getRecommendation();
			    	plot.gdalElevation = remotePlot.getGdalElevation();
			    	plot.gdalFaoLgp = remotePlot.getGdalFaoLgp();
			    	plot.gdalAridityIndex = remotePlot.getGdalAridityIndex();
			    	plot.awcSoilProfile = remotePlot.getAwcSoilProfile();
			    	plot.avgAnnualPrecipitation = remotePlot.getAverageAnnualPrecipitation();
					if (plot.remoteID.contains("uh4")) {
						boolean xx = true;
						xx = false;
					}
					List<MonthlyClimate> mCs = new ArrayList<MonthlyClimate>();
					if (remotePlot.getMonthlyPrecipitation() != null &&
						remotePlot.getMonthlyAvgTemperature() != null &&
						remotePlot.getMonthlyMaxTemperature() != null &&
						remotePlot.getMonthlyMinTemperature() != null) {
						for (int month = 0; month < 12; month++) {
							MonthlyClimate mC = new MonthlyClimate();
							mC.month = month + 1;
							mC.precipitation = remotePlot.getMonthlyPrecipitation().get(month);
							mC.avgTemp = remotePlot.getMonthlyAvgTemperature().get(month);
							mC.maxTemp = remotePlot.getMonthlyMaxTemperature().get(month);
							mC.minTemp = remotePlot.getMonthlyMinTemperature().get(month);
							mCs.add(mC);
						}
						plot.monthlyClimates = mCs;
					}
					try { 
						plot.dateModified = LandPKSApplication.LPKS_DATE_FORMAT.parse(remotePlot.getModifiedDate().toString()); 
					} catch (Exception eX) {
						Log.e(TAG, "", eX);/*TODO: something?*/
					}  
			    	//TODO: image file names
	
			    	plot.soilHorizons = new HashMap<String, SoilHorizon>();
					Class<? extends com.noisyflowers.landpks.server.gae.model.plotendpoint.model.Plot> remotePlotClass = remotePlot.getClass();
					for(HorizonName h : HorizonName.values()) {
			        	SoilHorizon horizon = new SoilHorizon();
			    		try {
				    		Method method = remotePlotClass.getMethod("getRockFragmentForSoilHorizon" + (h.ordinal()+1));
				    		//horizon.rockFragment = (String)method.invoke(remotePlot);
					    	String rockFragment = (String)method.invoke(remotePlot);
				    		horizon.rockFragment = rockFragment == null ? null : SoilRockFragmentVolume.serverNameLookup.get(rockFragment).name();
			
				    		method = remotePlotClass.getMethod("getColorForSoilHorizon" + (h.ordinal()+1));
				    		horizon.color = (Integer)method.invoke(remotePlot);
				    		
					    	method = remotePlotClass.getMethod("getTextureForSoilHorizon" + (h.ordinal()+1));
					    	//horizon.texture = (String)method.invoke(remotePlot);
					    	String texture = (String)method.invoke(remotePlot);
					    	horizon.texture = texture == null ? null : SoilTexture.serverNameLookup.get(texture).name();
					    	
					    	plot.soilHorizons.put(h.name, horizon);
				    	} catch (Exception eX) {
			    			Log.e(TAG, "Failed to execute method.", eX);
			    		}
			        }
					
					plotList.add(plot);
	
				}
			}
		}		
		
		return plotList;
	}
}
