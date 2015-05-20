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
 * com.noisyflowers.landpks.android.model
 * MonthlyClimate.java
 */

package com.noisyflowers.landpks.android.model;

import com.noisyflowers.landpks.android.R;

public class MonthlyClimate {
	
	/***
	public enum Month {
		MONTH_1 (R.string.monthlyclimate_month_1),
		MONTH_2 (R.string.monthlyclimate_month_2),
		MONTH_3 (R.string.monthlyclimate_month_3),
		MONTH_4 (R.string.monthlyclimate_month_4),
		MONTH_5 (R.string.monthlyclimate_month_5),
		MONTH_6 (R.string.monthlyclimate_month_6),
		MONTH_7 (R.string.monthlyclimate_month_7),
		MONTH_8 (R.string.monthlyclimate_month_8),
		MONTH_9 (R.string.monthlyclimate_month_9),
		MONTH_10 (R.string.monthlyclimate_month_10),
		MONTH_11 (R.string.monthlyclimate_month_11),
		MONTH_12 (R.string.monthlyclimate_month_12);
		
		public final int name;
		
		Month(int name) {
			this.name = name;
		}
	}
	***/
	
	public Long ID;
	public Long plotID;
	public Integer month;
	public Double precipitation;
	public Double avgTemp;
	public Double maxTemp;
	public Double minTemp;
	
	public MonthlyClimate() {
	}

	public String toString() {
		return "plot ID: " + plotID + ", month: " + month;
	}

}
