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
 * Plot.java
 */

package com.noisyflowers.landpks.android.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Plot {
	//public Long ID;
	public Long ID;
	//public Long remoteID; //TODO:  figure out how to do this with server.  Probably use GAE key instead
	public String remoteID; //TODO:  figure out how to do this with server.  Probably use GAE key instead
	public Boolean testPlot;
	public String name;
	public String recorderName;
	public String organization;
	public Double latitude;
	public Double longitude;
	public String city;
	public Date dateModified; //TODO: string?
	public String landcover;
	public Boolean grazed; //TODO: boolean in model class?
	public Boolean flooding;
	public String slope;
	public String crossSlopeShape;
	public String downSlopeShape;
	public String slopeShape;
	//public ArrayList<SoilHorizon> soilHorizons;
	public HashMap<String, SoilHorizon> soilHorizons = new HashMap<String, SoilHorizon>();
	public Boolean surfaceCracking;
	public Boolean surfaceSalt;
	public String northImageFilename;
	public String eastImageFilename;
	public String southImageFilename;
	public String westImageFilename;
	public String soilPitImageFilename;
	public String soilSamplesImageFilename;
	
	public String recommendation;
	public Double grassProductivity;
	public Double grassErosion;
	//public Double maizeProductivity;
	//public Double maizeErosion;
	public Double cropProductivity;
	public Double cropErosion;
	public Double gdalElevation;
	public Double gdalAridityIndex;
	public String gdalFaoLgp;
	public Double avgAnnualPrecipitation;
	public Double awcSoilProfile;
	public List<MonthlyClimate> monthlyClimates;
	
	public int needsUpload;
	public int needsPhotoUpload;
	
	public Plot () {
	}

	public String toString() {
		return name;
	}
}
