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
 * com.noisyflowers.landpks.server.gae.util
 * Constants.java
 */

package com.noisyflowers.landpks.server.gae.util;

public class Constants {
    public static final String WEB_CLIENT_ID = "410858290704.apps.googleusercontent.com";
    public static final String INSTALLED_CLIENT_ID = "410858290704-pjirleeo4m55hme1ammq00fsbeb8nk33.apps.googleusercontent.com"; //Nasim Python script
    public static final String ANDROID_CLIENT_ID_0 = "410858290704-4onhv4s5hm7uu0orqrhkgjct8nf80874.apps.googleusercontent.com"; //com.noisyflowers.landpks on Melville
    public static final String ANDROID_CLIENT_ID_1 = "410858290704-115smpeaac2co4v5vaft93bhcko7g5lm.apps.googleusercontent.com"; //com.noisyflowers.landpks.android on Melville
    public static final String ANDROID_CLIENT_ID_2 = "410858290704-5h6kk1adfgiojhjco55ssp1lr3ci59dn.apps.googleusercontent.com"; //com.noisyflowers.landpks.android on Whitman
    public static final String ANDROID_CLIENT_ID_3 = "410858290704-u1iuk20e9d6ia8028nfge2l1fe6h8jmn.apps.googleusercontent.com"; //com.noisyflowers.rangelandhealthmonitor.android on Whitman
    public static final String ANDROID_CLIENT_ID_4 = "410858290704-rpckr0sru7j78ncmnb9iqujch9epkko5.apps.googleusercontent.com"; //com.noisyflowers.landpks.android for Release
    public static final String ANDROID_CLIENT_ID_5 = "410858290704-d7l65fiivc1qkld7kngpjve24vdsvbn0.apps.googleusercontent.com"; //com.noisyflowers.rangelandhealthmonitor.android for Release
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
    public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    public static final String PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    
	public enum SoilHorizonName {
		HORIZON_1_NAME ("0-1cm"),
		HORIZON_2_NAME ("1-10cm"),
		HORIZON_3_NAME ("10-20cm"),
		HORIZON_4_NAME ("20-50cm"),
		HORIZON_5_NAME ("50-70cm"),
		HORIZON_6_NAME (">70cm");
		
		public final String name;
		
		SoilHorizonName(String name) {
			this.name = name;
		}
	}
	
	public enum SoilTexture {
		SAND ("Sand"),
		LOAMY_SAND ("Loamy sand"),
		SANDY_LOAM ("Sandy loam"),
		SILT_LOAM ("Silt loam"),
		LOAM ("Loam"),
		SANDY_CLAY_LOAM ("Sandy clay loam"),
		SILTY_CLAY_LOAM ("Silty clay loam"),
		CLAY_LOAM ("Clay loam"),
		SANDY_CLAY ("Sandy clay"),
		SILTY_CLAY ("Silty clay"),
		CLAY ("Clay");
		
		public final String name;
		
		SoilTexture(String name) {
			this.name = name;
		}
	}

	public enum RockFragmentRange {
		RANGE_1 ("0-1%"),
		RANGE_2 ("1-10%"),
		RANGE_3 ("10-20%"),
		RANGE_4 ("20-50%"),
		RANGE_5 ("50-70%"),
		RANGE_6 (">70%");
		
		public final String name;
		
		RockFragmentRange(String name) {
			this.name = name;
		}
	}
    
}
