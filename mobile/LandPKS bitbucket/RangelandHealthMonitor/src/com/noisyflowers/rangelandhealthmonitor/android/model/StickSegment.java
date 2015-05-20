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
 * StickSegment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.model;

import com.noisyflowers.rangelandhealthmonitor.android.R;

public class StickSegment {
	/***
	 * Note: Original specs for the cover selections said users could choose multiple values per stick segment.
	 * I implemented this with custom checkboxes feeding the covers boolean array. Specs were changed to allow
	 * only a single entry.  I switched to custom radio buttons for the UI, but kept the covers array, even though 
	 * it really isn't the best approach for single values.  Changing this will have ripple effects all the way into
	 * the server and I'm not confident the specs won't change back in the future.
	 */

	public enum Cover {
		COVER_1 (R.string.fragment_cover_cover_1),
		COVER_2 (R.string.fragment_cover_cover_2),
		COVER_3 (R.string.fragment_cover_cover_3),
		COVER_4 (R.string.fragment_cover_cover_4),
		COVER_5 (R.string.fragment_cover_cover_5),
		COVER_6 (R.string.fragment_cover_cover_6),
		COVER_7 (R.string.fragment_cover_cover_7),
		COVER_8 (R.string.fragment_cover_cover_8),
		COVER_9 (R.string.fragment_cover_cover_9);
		
		public final int name;
		
		Cover(int name) {
			this.name = name;
		}
	}

	public Long ID;
	public Long segmentID; 
	public int segmentIndex;
	public boolean[] covers = new boolean[Cover.values().length];
	
	public StickSegment(int segmentIndex) { //TODO: testing this for use in sync
		this.segmentIndex = segmentIndex;
	}
	
	public StickSegment(int segmentIndex, long segmentID) {
		this.segmentID = segmentID;
		this.segmentIndex = segmentIndex;
	}

	public boolean isComplete() {
		boolean retVal = true;
		boolean somethingChecked = false;
		for (StickSegment.Cover cover : StickSegment.Cover.values()) {  //...for each Cover type... 
			somethingChecked = covers[cover.ordinal()];
			if (somethingChecked) {
				break;
			}
		}
		if (!somethingChecked) {
			retVal = false;
		}
		return retVal;
	}

}
