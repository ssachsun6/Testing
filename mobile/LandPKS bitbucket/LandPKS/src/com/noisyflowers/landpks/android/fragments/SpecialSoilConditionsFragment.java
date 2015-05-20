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
 * SpecialSoilConditionsFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SpecialSoilConditionsFragment extends PlotEditFragment {

	public static final String DISPLAY_NAME = "Special Soil Conditions";
	
	private RadioGroup crackedRG, saltRG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getFragmentManager().beginTransaction().add(this, DISPLAY_NAME).commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_specialsoilconditions, container, false);
		
		crackedRG = (RadioGroup) root.findViewById(R.id.soilCrackedRadioGroup);
		saltRG = (RadioGroup) root.findViewById(R.id.saltRadioGroup);
		
		return root;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//crackedRG.setOnCheckedChangeListener(null);
		//saltRG.setOnCheckedChangeListener(null);			
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//Waiting to add these here prevents Toast popping on load.  Still waiting to see if there are side-effects. 
		//crackedRG.setOnCheckedChangeListener(crackedRGListener);
		//saltRG.setOnCheckedChangeListener(saltRGListener);			
	}

	@Override
	public void load(Plot plot) {
		crackedRG.clearCheck();
		saltRG.clearCheck();
		
		RadioButton rB = null;
		if (plot.surfaceCracking != null) {
			if (plot.surfaceCracking) {
				//rB = (RadioButton) crackedRG.findViewWithTag("true");
				rB = (RadioButton) crackedRG.findViewWithTag(getString(R.string.special_soil_conditions_fragment_surface_cracked));
			} else {
				//rB = (RadioButton) crackedRG.findViewWithTag("false");
				rB = (RadioButton) crackedRG.findViewWithTag(getString(R.string.special_soil_conditions_fragment_surface_not_cracked));
			}
		}
		if (rB != null) rB.setChecked(true);
		
		rB = null;
		if (plot.surfaceSalt != null) {
			if (plot.surfaceSalt) {
				//rB = (RadioButton) saltRG.findViewWithTag("true");
				rB = (RadioButton) saltRG.findViewWithTag(getString(R.string.special_soil_conditions_fragment_surface_salt));
			} else {
				//rB = (RadioButton) saltRG.findViewWithTag("false");
				rB = (RadioButton) saltRG.findViewWithTag(getString(R.string.special_soil_conditions_fragment_no_surface_salt));
			}
		}
		if (rB != null) rB.setChecked(true);

		//Waiting to add these here prevents Toast popping on load.  Still waiting to see if there are side-effects. 
		//crackedRG.setOnCheckedChangeListener(crackedRGListener);
		//saltRG.setOnCheckedChangeListener(saltRGListener);			
	}

	@Override
	public void save(Plot plot) {
		int radioButtonID = crackedRG.getCheckedRadioButtonId();
		if (radioButtonID != -1) {
			//plot.surfaceCracking = "true".equals(crackedRG.findViewById(radioButtonID).getTag());
			plot.surfaceCracking = getString(R.string.special_soil_conditions_fragment_surface_cracked).equals(crackedRG.findViewById(radioButtonID).getTag());
		}

		radioButtonID = saltRG.getCheckedRadioButtonId();
		if (radioButtonID != -1) {
			//plot.surfaceSalt = "true".equals(saltRG.findViewById(radioButtonID).getTag());
			plot.surfaceSalt = getString(R.string.special_soil_conditions_fragment_surface_salt).equals(saltRG.findViewById(radioButtonID).getTag());
		}
		
		if (plot.name != null) LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
	}

	@Override
	public boolean isComplete(Plot plot) {
		return plot.surfaceCracking != null && plot.surfaceSalt != null;
	}
	
	/***
    private OnCheckedChangeListener crackedRGListener = new CrackedRGListener(), 
									saltRGListener = new SaltRGListener(); 

    private class CrackedRGListener implements OnCheckedChangeListener {
    	@Override
    	public void onCheckedChanged(RadioGroup group, int checkedId) {
    		if (checkedId != -1) {
    			try {  
    				View v = group.findViewById(checkedId);
    				String tag = (String)v.getTag();
    				//String name = "true".equals(tag) ? "surface cracked" : "surface not cracked";
    				String name = "true".equals(tag) ? getString(R.string.special_soil_conditions_fragment_surface_cracked) : 
    												   getString(R.string.special_soil_conditions_fragment_surface_not_cracked);
    				Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
    			} catch (NullPointerException nPE) {} 
    		}
    	}
    };

    private class SaltRGListener implements OnCheckedChangeListener {
    	@Override
    	public void onCheckedChanged(RadioGroup group, int checkedId) {
    		if (checkedId != -1) {
    			try {  
    				View v = group.findViewById(checkedId);
    				String tag = (String)v.getTag();
    				//String name = "true".equals(tag) ? "surface salt" : "no surface salt";
    				String name = "true".equals(tag) ? getString(R.string.special_soil_conditions_fragment_surface_salt) : 
						   							   getString(R.string.special_soil_conditions_fragment_no_surface_salt);
    				Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
    			} catch (NullPointerException nPE) {} 
    		}
    	}
    };
    ***/
    
}
