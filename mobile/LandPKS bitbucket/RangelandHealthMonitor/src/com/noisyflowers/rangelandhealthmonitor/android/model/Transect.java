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
 * Transect.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;

public class Transect {
	public enum Direction {
		NORTH (R.string.fragment_transect_direction_1),
		EAST (R.string.fragment_transect_direction_2),
		SOUTH (R.string.fragment_transect_direction_3),
		WEST (R.string.fragment_transect_direction_4);
		
		public final int displayName;
		
		Direction(int name) {
			this.displayName = name;
		}
	}
	
	public Long ID;
	public String remoteID = null;
	public Long remoteModifiedDate;
	public Long siteID;
	public Direction direction;
	public boolean needsUpload;
	
	public List<Segment> segments = new ArrayList<Segment>();  //TODO: testing this for sync
	
	public boolean isComplete() {
		return isComplete(new Date());
	}
	public boolean isComplete(Date date) {
		boolean retVal = true;
		//List<Segment> segments = RHMApplication.getInstance().getDatabaseAdapter().getSegments(ID, new Date()); //get list of segments for date
		List<Segment> segments = RHMApplication.getInstance().getDatabaseAdapter().getSegments(ID, date); //get list of segments for date
		if (segments.size() < Segment.Range.values().length) {
			retVal = false;
		} else {
			for (Segment s : segments) {
				retVal = retVal && s.isComplete();
			}
		}
		return retVal;
	}
	
	public boolean isSubmitted() {
		return isSubmitted(new Date());
	}
	public boolean isSubmitted(Date date) {
		boolean retVal = false;
		List<Segment> segments = RHMApplication.getInstance().getDatabaseAdapter().getSegments(ID, date); //get list of segments for date
		if (segments.size() == Segment.Range.values().length && (segments.get(0).uploaded || segments.get(0).needsUpload)) {
			retVal = true;
		}
		return retVal;
	}

	public boolean isUploaded() {
		return isSubmitted(new Date());
	}
	public boolean isUploaded(Date date) {
		boolean retVal = false;
		List<Segment> segments = RHMApplication.getInstance().getDatabaseAdapter().getSegments(ID, date); //get list of segments for date
		if (segments.size() == Segment.Range.values().length && segments.get(0).uploaded) {
			retVal = true;
		}
		return retVal;
	}
	
}
