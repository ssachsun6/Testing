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
 * com.noisyflowers.rangelandhealthmonitor.android.model
 * Segment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;




import java.util.Map;

import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.model.StickSegment;

public class Segment {
	/***
	public enum Range {
		SEGMENT_1 ("0-5m"),
		SEGMENT_2 ("5-10m"),
		SEGMENT_3 ("10-15m"),
		SEGMENT_4 ("15-20m"),
		SEGMENT_5 ("20-25m");
		
		public final String displayName;
		
		Range(String name) {
			this.displayName = name;
		}
	}
	***/
	
	public enum Range {
		SEGMENT_1 (R.string.server_resource_segment_range_1, R.string.display_segment_range_1),
		SEGMENT_2 (R.string.server_resource_segment_range_2, R.string.display_segment_range_2),
		SEGMENT_3 (R.string.server_resource_segment_range_3, R.string.display_segment_range_3),
		SEGMENT_4 (R.string.server_resource_segment_range_4, R.string.display_segment_range_4),
		SEGMENT_5 (R.string.server_resource_segment_range_5, R.string.display_segment_range_5);
		
		private final int serverName, displayName;
		
		public static final Map<String, Range> displayNameLookup = new HashMap<String, Range>();
		static {
			for (Range s : Range.values()) {
				displayNameLookup.put(RHMApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, Range> serverNameLookup = new HashMap<String, Range>();
		static {
			for (Range s : Range.values()) {
				serverNameLookup.put(RHMApplication.getInstance().getString(s.serverName), s);
			}
		}

		Range(int serverName, int displayName) {
			this.serverName = serverName;
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return RHMApplication.getInstance().getString(displayName);
		}
		
		public String getServerName() {
			return RHMApplication.getInstance().getString(serverName);
		}
	}
	
	public enum Height {
		HEIGHT_1 (R.string.server_resource_height_01, R.string.fragment_heightGapSpecies_height_01),
		HEIGHT_2 (R.string.server_resource_height_02, R.string.fragment_heightGapSpecies_height_02),
		HEIGHT_3 (R.string.server_resource_height_03, R.string.fragment_heightGapSpecies_height_03),
		HEIGHT_4 (R.string.server_resource_height_04, R.string.fragment_heightGapSpecies_height_04),
		HEIGHT_5 (R.string.server_resource_height_05, R.string.fragment_heightGapSpecies_height_05),
		HEIGHT_6 (R.string.server_resource_height_06, R.string.fragment_heightGapSpecies_height_06);
		
		private final int serverName, displayName;
		
		public static final Map<String, Height> displayNameLookup = new HashMap<String, Height>();
		static {
			for (Height s : Height.values()) {
				displayNameLookup.put(RHMApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, Height> serverNameLookup = new HashMap<String, Height>();
		static {
			for (Height s : Height.values()) {
				serverNameLookup.put(RHMApplication.getInstance().getString(s.serverName), s);
			}
		}

		Height(int serverName, int displayName) {
			this.serverName = serverName;
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return RHMApplication.getInstance().getString(displayName);
		}
		
		public String getServerName() {
			return RHMApplication.getInstance().getString(serverName);
		}
	}
	

	public Long ID;
	public Long transectID;
	public Range range;
	public StickSegment[] stickSegments = new StickSegment[STICK_SEGMENT_COUNT];
	//public String canopyHeight;
	public Height canopyHeight;
	public Boolean basalGap;
	public Boolean canopyGap;
	public Integer species1Count;
	public Integer species2Count;
	public List<String> speciesList;
	public Date date;
	public Boolean needsUpload = false;
	public Boolean uploaded = false;
	
	//public static final int STICK_SEGMENT_COUNT = 6;
	public static final int STICK_SEGMENT_COUNT = 5;
	
	public Segment() {  //TODO: testing this for use in sync
	}
	
	//public Segment(int segment, long transectID) {
	public Segment(Range segment, long transectID) {
		this.transectID = transectID;
		this.range = segment;
		//speciesList = new ArrayList<String>();
		
		for (int x = 0; x < STICK_SEGMENT_COUNT; x++) {
			stickSegments[x] = new StickSegment(x); 
		}
	}
	
	public boolean isComplete() {
		boolean retVal = true;
		for (StickSegment sS : stickSegments) {
			retVal = retVal && sS.isComplete();
		}
		
		retVal = retVal && canopyHeight != null;
		
		retVal = retVal && basalGap != null;
		retVal = retVal && canopyGap != null;

		retVal = retVal && species1Count != null;
		retVal = retVal && species2Count != null;
		
		return retVal;
	}
	
}
