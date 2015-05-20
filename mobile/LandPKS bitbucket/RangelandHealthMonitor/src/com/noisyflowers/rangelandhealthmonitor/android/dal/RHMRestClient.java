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
 * RHMRestClient.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.dal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.noisyflowers.rangelandhealthmonitor.android.CloudEndpointUtils;
import com.noisyflowers.landpks.android.LandPKSContract;
import com.noisyflowers.landpks.server.gae.model.plotendpoint.Plotendpoint;
import com.noisyflowers.landpks.server.gae.model.plotendpoint.model.CollectionResponsePlot;
import com.noisyflowers.landpks.server.gae.model.transectendpoint.Transectendpoint;
import com.noisyflowers.landpks.server.gae.model.transectendpoint.model.CollectionResponseTransect;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.model.StickSegment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;

public class RHMRestClient {
	private static final String TAG = RHMRestClient.class.getName();
	
	private static RHMRestClient myInstance = null;
	
    public static final String WEB_CLIENT_ID = "410858290704.apps.googleusercontent.com";
    
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    //private GoogleAccountCredential credential;
    private LongListeningCredentialWrapper credential;
    
    private Context context;
	    
	public static RHMRestClient getInstance(Context context) {
		if (myInstance == null) {
			myInstance = new RHMRestClient(context);
		}
		return myInstance;
	}
	
	private RHMRestClient(Context context) {
		this.context = context;
		//credential = GoogleAccountCredential.usingAudience(this.context, "server:client_id:" + WEB_CLIENT_ID); 		
		GoogleAccountCredential gAC = GoogleAccountCredential.usingAudience(this.context, "server:client_id:" + WEB_CLIENT_ID); 		
		credential = new LongListeningCredentialWrapper(gAC); 		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.context);
		String accountName = settings.getString(RHMApplication.PREF_ACCOUNT_NAME_KEY, null);
		if (accountName != null) {
			credential.setSelectedAccountName(accountName);
		}
	}

	private class LongListeningCredentialWrapper extends GoogleAccountCredential{
		GoogleAccountCredential credential;
		
		public LongListeningCredentialWrapper(GoogleAccountCredential credential) {
			super(credential.getContext(), credential.getScope());
			this.credential = credential;
		}
		
		@Override
        public void initialize(HttpRequest httpRequest) {
			super.initialize(httpRequest);
            httpRequest.setConnectTimeout(120 * 1000);
            httpRequest.setReadTimeout(120 * 1000);
        }
	}
	
	private Plotendpoint buildPlotEndpoint(){
		Plotendpoint.Builder endpointBuilder = new Plotendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				credential 
				);
		Plotendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();  //TODO: need one of these
		return endpoint;
	}

	private Transectendpoint buildTransectEndpoint(){
		//Need to do this each time in case account has been changed by LPKS
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.context);
		String accountName = settings.getString(RHMApplication.PREF_ACCOUNT_NAME_KEY, null);
    	//Log.i(TAG, "buildTransectEndpoint, accountName = " + accountName);
		if (accountName != null) {
			credential.setSelectedAccountName(accountName);
		}
		
		Transectendpoint.Builder endpointBuilder = new Transectendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				credential
				).setApplicationName("RHM");
		Transectendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();  
		return endpoint;
	}

	public List<String> fetchPlotNames() {
		List<String> plotList = null;
		
		Plotendpoint endpoint = buildPlotEndpoint();
		CollectionResponsePlot plots = null;
		int x = 0;
		while (x++ < 2) { //try twice, in case of timeout
			try { 
				plots = endpoint.listPlot().execute();
				x = 2;
			} catch (Exception e) {
				Log.e(TAG, "Error fetching plots.", e);
			}
		}
		
		if (plots != null) {
			plotList = new ArrayList<String>();
			if (plots.getItems() != null) {
				for (com.noisyflowers.landpks.server.gae.model.plotendpoint.model.Plot remotePlot: plots.getItems()) {
					String name = remotePlot.getName();					
					plotList.add(name);
	
				}
			}
		}		
		
		return plotList;
	}
	
	private Map<String,Long> buildSiteIDMap() {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    	String accountName = settings.getString(RHMApplication.PREF_ACCOUNT_NAME_KEY, null);
	    String[] projection = {LandPKSContract.SITE_COLUMN_ID, LandPKSContract.SITE_COLUMN_NAME, LandPKSContract.SITE_COLUMN_REMOTE_ID};  
        String URL = LandPKSContract.CONTENT_URI + "/" + LandPKSContract.SITES_PATH;
        Uri sites = Uri.parse(URL);
	    Cursor cursor = context.getContentResolver().query(sites, projection, LandPKSContract.SITE_COLUMN_RECORDER_NAME + "=?", new String[] {accountName}, LandPKSContract.SITE_COLUMN_NAME);
	    HashMap<String, Long> map = new HashMap<String, Long>();
	    while (cursor.moveToNext()) {
	    	map.put(cursor.getString(cursor.getColumnIndex(LandPKSContract.SITE_COLUMN_REMOTE_ID)), cursor.getLong(cursor.getColumnIndex(LandPKSContract.SITE_COLUMN_ID)));
	    }
	    cursor.close();
	    return map;
	}
	
	public List<Transect> fetchTransects(Date date) {
    	//Log.i(TAG, "fetchTransects, enter");
		List<Transect> transectList = null;
		
		DateTime dT = date == null ? null : new DateTime(date);

		Transectendpoint endpoint = buildTransectEndpoint();
		CollectionResponseTransect transects = null;
		int x = 0;
		while (x++ < 2) { //try twice, in case of timeout
			try { 
				transects = endpoint.listTransect().setAfterDate(dT).execute();
				x = 2;
			} catch (Exception e) {
				Log.e(TAG, "Error fetching transects.", e);
			}
		}
		
		if (transects != null) {
	    	//Log.i(TAG, "fetchTransects, transect.size = " + transects.size());
			Map<String, Long> siteIDMap = buildSiteIDMap();
			transectList = new ArrayList<Transect>();
			if (transects.getItems() != null) {
				for (com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Transect remoteTransect: transects.getItems()) {
			    	//Log.i(TAG, "fetchTransects, processing " + remoteTransect.getId());
					Transect transect = new Transect();	
					transect.remoteID = remoteTransect.getId();
					transect.remoteModifiedDate = remoteTransect.getModifiedDate().getValue();
					transect.direction = Transect.Direction.valueOf(remoteTransect.getDirection());
					
					Long siteID = siteIDMap.get(remoteTransect.getSiteID());
					if (siteID == null) {
						continue; //means this site has not yet been synced by LPKS, so ignore this time around.
					}
					transect.siteID = siteID; 
										
					for (com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Segment remoteSegment : remoteTransect.getSegments()) {
				    	//Log.i(TAG, "    fetchTransects, processing " + remoteSegment.getId());
						Segment segment = new Segment();
						segment.basalGap = remoteSegment.getBasalGap();
						segment.canopyGap = remoteSegment.getCanopyGap();
						segment.canopyHeight = Segment.Height.serverNameLookup.get(remoteSegment.getCanopyHeight());
						try { segment.date = sdf.parse(remoteSegment.getDate()); } catch (Exception eX) {continue;} // if date is bad from server, something is really wrong.  Ignore this transect.
						segment.range = Segment.Range.serverNameLookup.get(remoteSegment.getRange());
						segment.species1Count = remoteSegment.getSpecies1Density();
						segment.species2Count = remoteSegment.getSpecies2Density();
						segment.speciesList = remoteSegment.getSpeciesList();
						for (int stickSegmentIndex = 0; stickSegmentIndex < Segment.STICK_SEGMENT_COUNT; stickSegmentIndex++) {
							StickSegment stickSegment = new StickSegment(stickSegmentIndex);
							stickSegment.covers = new boolean[StickSegment.Cover.values().length];
							for (int coverIndex = 0; coverIndex < StickSegment.Cover.values().length; coverIndex++) {
								stickSegment.covers[coverIndex] = remoteSegment.getStickSegments().get(stickSegmentIndex).getCovers().get(coverIndex);
							}
							segment.stickSegments[stickSegmentIndex] = stickSegment;
						}
						transect.segments.add(segment);
					}
					
					transectList.add(transect);
				}
			}
		}		
    	//Log.i(TAG, "fetchTransects, exit");
		
		return transectList;
	}


	public com.noisyflowers.rangelandhealthmonitor.android.model.Transect putTransect(com.noisyflowers.rangelandhealthmonitor.android.model.Transect transect, Date date) {
    	Log.i(TAG, "putTransect, enter");
		
    	com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Transect remoteTransect = new com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Transect();
    	//remoteTransect.setId(transect.ID.toString()); //TODO: id is seriously broken
    	remoteTransect.setDirection(transect.direction.name());
    	
		String URL = LandPKSContract.CONTENT_URI + "/" + LandPKSContract.SITES_PATH + "/" + transect.siteID;  
        Uri uri = Uri.parse(URL);
		String[] cols = {LandPKSContract.SITE_COLUMN_REMOTE_ID};
		Cursor cursor = context.getContentResolver().query(uri, cols, null, null, null);  
		try {
			if (cursor.moveToFirst()) {
				String remoteSiteID = cursor.getString(0);
				if (remoteSiteID != null && !"".equals(remoteSiteID)) {
					remoteTransect.setSiteID(remoteSiteID);
				} else {
					Log.e(TAG, "No remote site ID.  Site has probably not been uploaded.  Aborting transect upload.");			
					return transect; //TODO: what?				
				}
			} else {
				Log.e(TAG, "Site ID not found by LPKS. Aborting transect upload.");			
				return transect; //TODO: what?
			}
		} finally {
			cursor.close();
		}

    	List<com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Segment> remoteSegments = new ArrayList<com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Segment>();
		List<com.noisyflowers.rangelandhealthmonitor.android.model.Segment> segments = RHMApplication.getInstance().getDatabaseAdapter().getSegments(transect.ID, date); //get list of segments for date
		for (com.noisyflowers.rangelandhealthmonitor.android.model.Segment s : segments) {
			com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Segment remoteSegment = new com.noisyflowers.landpks.server.gae.model.transectendpoint.model.Segment();
			remoteSegment.setDate(sdf.format(s.date));
			//remoteSegment.setDate("2014-08-26");  //TODO: for testing only
			remoteSegment.setCanopyHeight(s.canopyHeight.getServerName());
			remoteSegment.setBasalGap(s.basalGap);
			remoteSegment.setCanopyGap(s.canopyGap);
			remoteSegment.setSpecies1Density(s.species1Count);
			remoteSegment.setSpecies2Density(s.species2Count);
			remoteSegment.setSpeciesList(s.speciesList);
			remoteSegment.setRange(s.range.getServerName());
			List<com.noisyflowers.landpks.server.gae.model.transectendpoint.model.StickSegment> remoteStickSegments = new ArrayList<com.noisyflowers.landpks.server.gae.model.transectendpoint.model.StickSegment>();
			for (com.noisyflowers.rangelandhealthmonitor.android.model.StickSegment sS : s.stickSegments) {
				com.noisyflowers.landpks.server.gae.model.transectendpoint.model.StickSegment remoteStickSegment = new com.noisyflowers.landpks.server.gae.model.transectendpoint.model.StickSegment();
				remoteStickSegment.setSegmentIndex(sS.segmentIndex);  //TODO: Do this with a category enum, as elsewhere
				List<Boolean> remoteCovers = new ArrayList<Boolean>();
				for (boolean b : sS.covers) {
					remoteCovers.add(b);
				}
				remoteStickSegment.setCovers(remoteCovers);
				remoteStickSegments.add(remoteStickSegment);
				
			}
			remoteSegment.setStickSegments(remoteStickSegments);
			
			remoteSegments.add(remoteSegment);
		}
		remoteTransect.setSegments(remoteSegments);
		
		Transectendpoint endpoint = buildTransectEndpoint();
		
		int x = 0;
		while (x++ < 2) { //try twice, in case of timeout
			Log.i(TAG, "Upload loop, iteration " + x);
			try { // first try to insert
				remoteTransect = endpoint.insertTransect(remoteTransect).execute();
				x = 2;
			} catch (Exception e) {
				Log.e(TAG, "Error inserting transect.", e);
				if (e.getMessage() != null && e.getMessage().contains("EntityExistsException")) { //if record already exists, then try update
					try {
						remoteTransect = endpoint.updateTransect(remoteTransect).execute();
						x = 2;
					} catch (Exception eX) {
						Log.e(TAG, "Error updating transect.", eX);
						if (eX.getMessage() != null && eX.getMessage().contains("EntityExistsException")) { //means there are already segment records for this date, lower needs_upload flag
							RHMApplication.getInstance().getDatabaseAdapter().markSegmentsForUpload(transect.ID, date, false);
							x = 2;
						}
					}
				}
			}
		}
		
		if (remoteTransect.getId() != null) {
			transect.remoteID = remoteTransect.getId();
			transect.remoteModifiedDate = remoteTransect.getModifiedDate().getValue();
		}

    	Log.i(TAG, "putTransect, returning with remoteID = " + transect.remoteID);
		return transect;

	}
	
		

}
