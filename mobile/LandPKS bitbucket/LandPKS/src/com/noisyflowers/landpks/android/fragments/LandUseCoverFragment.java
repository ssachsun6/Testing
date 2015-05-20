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
 * com.noisyflowers.landpks.android.fragments
 * LandUseCoverFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class LandUseCoverFragment extends PlotEditFragment {

	public enum LandCover {
		LAND_COVER_1 (R.string.server_resource_land_cover_1, R.string.land_use_cover_fragment_land_cover_1),
		LAND_COVER_2 (R.string.server_resource_land_cover_2, R.string.land_use_cover_fragment_land_cover_2),
		LAND_COVER_3 (R.string.server_resource_land_cover_3, R.string.land_use_cover_fragment_land_cover_3),
		LAND_COVER_4 (R.string.server_resource_land_cover_4, R.string.land_use_cover_fragment_land_cover_4),
		LAND_COVER_5 (R.string.server_resource_land_cover_5, R.string.land_use_cover_fragment_land_cover_5),
		LAND_COVER_6 (R.string.server_resource_land_cover_6, R.string.land_use_cover_fragment_land_cover_6),
		LAND_COVER_7 (R.string.server_resource_land_cover_7, R.string.land_use_cover_fragment_land_cover_7),
		LAND_COVER_8 (R.string.server_resource_land_cover_8, R.string.land_use_cover_fragment_land_cover_8),
		LAND_COVER_9 (R.string.server_resource_land_cover_9, R.string.land_use_cover_fragment_land_cover_9);
		
		private final int serverName, displayName;
		
		public static final Map<String, LandCover> displayNameLookup = new HashMap<String, LandCover>();
		static {
			for (LandCover s : LandCover.values()) {
				displayNameLookup.put(LandPKSApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, LandCover> serverNameLookup = new HashMap<String, LandCover>();
		static {
			for (LandCover s : LandCover.values()) {
				serverNameLookup.put(LandPKSApplication.getInstance().getString(s.serverName), s);
			}
		}

		LandCover(int serverName, int displayName) {
			this.serverName = serverName;
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return LandPKSApplication.getInstance().getString(displayName);
		}
		
		public String getServerName() {
			return LandPKSApplication.getInstance().getString(serverName);
		}
	}
	private LandCover landCover = null;
	
	private RadioGroup landcoverRG1, landcoverRG2;
	private RadioGroup floodingRG, grazedRG;
	
	private ArrayList<String> landcoverChoices = new ArrayList();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getFragmentManager().beginTransaction().add(this, DISPLAY_NAME).commit();
	}
			
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_landusecover, container, false);
        
        floodingRG = (RadioGroup) root.findViewById(R.id.floodingRadioGroup);
        grazedRG = (RadioGroup) root.findViewById(R.id.grazedRadioGroup);

		RadioButton notFloodingRB = (RadioButton) root.findViewById(R.id.NotFloodingRadioButton);
		RadioButton floodingRB = (RadioButton) root.findViewById(R.id.FloodingRadioButton);

        landcoverRG1 = (RadioGroup) root.findViewById(R.id.landCoverRadioGroup1);
        landcoverRG2 = (RadioGroup) root.findViewById(R.id.landCoverRadioGroup2);
        landcoverRG1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
        landcoverRG2.clearCheck();
		//landcoverRG1.setOnCheckedChangeListener(landCoverListener1);
		//landcoverRG2.setOnCheckedChangeListener(landCoverListener2);	
		
		for (int i = 0; i < landcoverRG1.getChildCount(); i++) {
			//landcoverChoices.add((String)landcoverRG1.getChildAt(i).getTag());
			View v = landcoverRG1.getChildAt(i);
			if (v instanceof RadioButton) {
				landcoverChoices.add((String)v.getTag());
			}
		}
		for (int i = 0; i < landcoverRG2.getChildCount(); i++) {
			//landcoverChoices.add((String)landcoverRG2.getChildAt(i).getTag());
			View v = landcoverRG2.getChildAt(i);
			if (v instanceof RadioButton) {
				landcoverChoices.add((String)v.getTag());
			}
		}
		
		return root;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		landcoverRG1.setOnCheckedChangeListener(null);
		landcoverRG2.setOnCheckedChangeListener(null);	
		//grazedRG.setOnCheckedChangeListener(null);
		//floodingRG.setOnCheckedChangeListener(null);	

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//Waiting to add these here prevents Toast popping on load.  Still waiting to see if there are side-effects. 
		landcoverRG1.setOnCheckedChangeListener(landCoverListener1);
		landcoverRG2.setOnCheckedChangeListener(landCoverListener2);	
		//grazedRG.setOnCheckedChangeListener(grazedRGListener);
		//floodingRG.setOnCheckedChangeListener(floodsRGListener);	
	
	}
	
	private OnCheckedChangeListener landCoverListener1 = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
            	landcoverRG2.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
            	landcoverRG2.clearCheck(); // clear the second RadioGroup!
            	landcoverRG2.setOnCheckedChangeListener(landCoverListener2); //reset the listener
            	/***
            	try {  
            		View v = landcoverRG1.findViewById(checkedId);
            		String name = (String)v.getTag();
            		Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
            	} catch (NullPointerException nPE) {} 
            	***/
            }
        }
    };

    private OnCheckedChangeListener landCoverListener2 = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
            	landcoverRG1.setOnCheckedChangeListener(null);
            	landcoverRG1.clearCheck();
            	landcoverRG1.setOnCheckedChangeListener(landCoverListener1);
            	/***
            	try {  
            		View v = landcoverRG2.findViewById(checkedId);
            		String name = (String)v.getTag();
            		Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
            	} catch (NullPointerException nPE) {}
            	***/ 
            }
        }
    };
   
	@Override
	public void load(Plot plot) {
		
		landcoverRG1.clearCheck();
		landcoverRG2.clearCheck();
		grazedRG.clearCheck();
		floodingRG.clearCheck();
		
		//String stringValue = plot.landcover;
		String stringValue = plot.landcover == null ? null : LandCover.valueOf(plot.landcover).getDisplayName();
		RadioButton rB = null;
		for (String landcoverChoice : landcoverChoices) {
			if (landcoverChoice.equals(stringValue)) {
				rB = (RadioButton) landcoverRG1.findViewWithTag(landcoverChoice);
				if (rB == null) {
					rB = (RadioButton) landcoverRG2.findViewWithTag(landcoverChoice);
				}
				break;
			}
		}
		if (rB != null) rB.setChecked(true);
		
		rB = null;
		if (plot.flooding != null) {
			if (plot.flooding) {
				//rB = (RadioButton) floodingRG.findViewWithTag("true");
				rB = (RadioButton) floodingRG.findViewWithTag(getString(R.string.land_use_cover_fragment_flooded));
			} else {
				//rB = (RadioButton) floodingRG.findViewWithTag("false");
				rB = (RadioButton) floodingRG.findViewWithTag(getString(R.string.land_use_cover_fragment_not_flooded));
			}
		}
		if (rB != null) rB.setChecked(true);
		
		rB = null;
		if (plot.grazed != null) {
			if (plot.grazed) {
				//rB = (RadioButton) grazedRG.findViewWithTag("true");
				rB = (RadioButton) grazedRG.findViewWithTag(getString(R.string.land_use_cover_fragment_grazed));
			} else {
				//rB = (RadioButton) grazedRG.findViewWithTag("false");
				rB = (RadioButton) grazedRG.findViewWithTag(getString(R.string.land_use_cover_fragment_not_grazed));
			}
		}
		if (rB != null) rB.setChecked(true);
		
		//landcoverRG1.setOnCheckedChangeListener(landCoverListener1);
		//landcoverRG2.setOnCheckedChangeListener(landCoverListener2);	
		//grazedRG.setOnCheckedChangeListener(grazedRGListener);
		//floodingRG.setOnCheckedChangeListener(floodsRGListener);	
	}


	@Override
	public void save(Plot plot) {
		//plot.landcover = getCheckedLandcover();
		String checkedLandCover = getCheckedLandcover();
		plot.landcover = checkedLandCover == null ? null : LandCover.displayNameLookup.get(checkedLandCover).name();

		int radioButtonID = floodingRG.getCheckedRadioButtonId();
		if (radioButtonID != -1) {
			//plot.flooding = "true".equals(floodingRG.findViewById(radioButtonID).getTag());
			plot.flooding = getString(R.string.land_use_cover_fragment_flooded).equals(floodingRG.findViewById(radioButtonID).getTag());
		}

		radioButtonID = grazedRG.getCheckedRadioButtonId();
		if (radioButtonID != -1) {
			//plot.grazed = "true".equals(grazedRG.findViewById(radioButtonID).getTag());
			plot.grazed = getString(R.string.land_use_cover_fragment_grazed).equals(grazedRG.findViewById(radioButtonID).getTag());
		}
		
		if (plot.name != null) LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
	}


	@Override
	public boolean isComplete(Plot plot) {
		return plot.landcover != null && !"".equals(plot.landcover) &&
			   plot.flooding != null && plot.grazed != null;
	}	
    
	private String getCheckedLandcover() {
		//To get checked button in those two groups, put this somewhere:
		//int chkId1 = landcoverRG1.getCheckedRadioButtonId();
		//int chkId2 = landcoverRG2.getCheckedRadioButtonId();
		//int realCheck = chkId1 == -1 ? chkId2 : chkId1;
		//return realCheck;
		
		String retVal = null;
		int chkID = landcoverRG1.getCheckedRadioButtonId();
		if (chkID != -1) {
			retVal = (String)landcoverRG1.findViewById(chkID).getTag();
		} else {
			chkID = landcoverRG2.getCheckedRadioButtonId();
			if (chkID != -1) {
				retVal = (String)landcoverRG2.findViewById(chkID).getTag();
			}
		}
		return retVal;
	}
	
	/***
    private OnCheckedChangeListener floodsRGListener = new FloodsRGListener(), 
									grazedRGListener = new GrazedRGListener(); 
	
    private class FloodsRGListener implements OnCheckedChangeListener {
    	@Override
    	public void onCheckedChanged(RadioGroup group, int checkedId) {
    		if (checkedId != -1) {
    			try {  
    				View v = group.findViewById(checkedId);
    				String tag = (String)v.getTag();
    				//String name = "true".equals(tag) ? "flooded" : "not flooded";
    				String name = "true".equals(tag) ? getString(R.string.land_use_cover_fragment_flooded) : 
						   							   getString(R.string.land_use_cover_fragment_not_flooded);
    				Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
    			} catch (NullPointerException nPE) {} 
    		}
    	}
    };
    
    private class GrazedRGListener implements OnCheckedChangeListener {
    	@Override
    	public void onCheckedChanged(RadioGroup group, int checkedId) {
    		if (checkedId != -1) {
    			try {  
    				View v = group.findViewById(checkedId);
    				String tag = (String)v.getTag();
    				//String name = "true".equals(tag) ? "grazed" : "not grazed";
    				String name = "true".equals(tag) ? getString(R.string.land_use_cover_fragment_grazed) : 
						   							   getString(R.string.land_use_cover_fragment_not_grazed);
    				Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
    			} catch (NullPointerException nPE) {} 
    		}
    	}
    };
    ***/
    
}
