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
 * SlopeShapeFragment.java
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
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SlopeShapeFragment extends PlotEditFragment {

	public static final String DISPLAY_NAME = "Slope Shape";

	public enum Curve {
		CONCAVE (R.string.server_resource_slope_curve_concave, R.string.slope_shape_fragment_concave),
		CONVEX (R.string.server_resource_slope_curve_convex, R.string.slope_shape_fragment_convex),
		LINEAR (R.string.server_resource_slope_curve_linear, R.string.slope_shape_fragment_linear);
		
		private final int serverName, displayName;

		public static final Map<String, Curve> displayNameLookup = new HashMap<String, Curve>();
		static {
			for (Curve s : Curve.values()) {
				displayNameLookup.put(LandPKSApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, Curve> serverNameLookup = new HashMap<String, Curve>();
		static {
			for (Curve s : Curve.values()) {
				serverNameLookup.put(LandPKSApplication.getInstance().getString(s.serverName), s);
			}
		}

		Curve(int serverName, int displayName) {
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

	public enum SlopeShape {
		SLOPE_SHAPE_1 (R.string.server_resource_slope_shape_1, R.string.slope_shape_fragment_slope_shape_1),
		SLOPE_SHAPE_2 (R.string.server_resource_slope_shape_2, R.string.slope_shape_fragment_slope_shape_2),
		SLOPE_SHAPE_3 (R.string.server_resource_slope_shape_3, R.string.slope_shape_fragment_slope_shape_3),
		SLOPE_SHAPE_4 (R.string.server_resource_slope_shape_4, R.string.slope_shape_fragment_slope_shape_4),
		SLOPE_SHAPE_5 (R.string.server_resource_slope_shape_5, R.string.slope_shape_fragment_slope_shape_5),
		SLOPE_SHAPE_6 (R.string.server_resource_slope_shape_6, R.string.slope_shape_fragment_slope_shape_6),
		SLOPE_SHAPE_7 (R.string.server_resource_slope_shape_7, R.string.slope_shape_fragment_slope_shape_7),
		SLOPE_SHAPE_8 (R.string.server_resource_slope_shape_8, R.string.slope_shape_fragment_slope_shape_8),
		SLOPE_SHAPE_9 (R.string.server_resource_slope_shape_9, R.string.slope_shape_fragment_slope_shape_9);
		
		private final int serverName, displayName;
		
		public static final Map<String, SlopeShape> displayNameLookup = new HashMap<String, SlopeShape>();
		static {
			for (SlopeShape s : SlopeShape.values()) {
				displayNameLookup.put(LandPKSApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, SlopeShape> serverNameLookup = new HashMap<String, SlopeShape>();
		static {
			for (SlopeShape s : SlopeShape.values()) {
				serverNameLookup.put(LandPKSApplication.getInstance().getString(s.serverName), s);
			}
		}

		SlopeShape(int serverName, int displayName) {
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
	private SlopeShape slopeShape = null;

	private RadioGroup crossSlopeRG, downSlopeRG;

	private ArrayList<String> downSlopeShapeChoices = new ArrayList<String>();
	private ArrayList<String> crossSlopeShapeChoices = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getFragmentManager().beginTransaction().add(this, DISPLAY_NAME).commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_slopeshape, container, false);
		
		crossSlopeRG = (RadioGroup) root.findViewById(R.id.fragment_slopeshape_cross_rg);
		downSlopeRG = (RadioGroup) root.findViewById(R.id.fragment_slopeshape_down_rg);
		crossSlopeRG.clearCheck();
		downSlopeRG.clearCheck();

		for (int i = 0; i < crossSlopeRG.getChildCount(); i++) {
			//slopeChoices.add((String)slopeRG1.getChildAt(i).getTag());
			View v = crossSlopeRG.getChildAt(i);
			if (v instanceof RadioButton) {
				crossSlopeShapeChoices.add((String)v.getTag());
			}
		}
		for (int i = 0; i < downSlopeRG.getChildCount(); i++) {
			//slopeChoices.add((String)slopeRG2.getChildAt(i).getTag());
			View v = downSlopeRG.getChildAt(i);
			if (v instanceof RadioButton) {
				downSlopeShapeChoices.add((String)v.getTag());
			}
		}

		return root;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//downSlopeRG.setOnCheckedChangeListener(null);
		//crossSlopeRG.setOnCheckedChangeListener(null);			
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//Waiting to add these here prevents Toast popping on load.  Still waiting to see if there are side-effects. 
		//downSlopeRG.setOnCheckedChangeListener(downSlopeChangeListener);
		//crossSlopeRG.setOnCheckedChangeListener(crossSlopeChangeListener);			
	}

	@Override
	public void load(Plot plot) {
		downSlopeRG.clearCheck();
		crossSlopeRG.clearCheck();
		
		//String stringValue = plot.slopeShape;
		String downSlopeStringValue = plot.downSlopeShape == null ? null : Curve.valueOf(plot.downSlopeShape).getDisplayName();
		String crossSlopeStringValue = plot.crossSlopeShape == null ? null : Curve.valueOf(plot.crossSlopeShape).getDisplayName();
		
		RadioButton rB = null;
		for (String downSlopeShapeChoice : downSlopeShapeChoices) {
			if (downSlopeShapeChoice.equals(downSlopeStringValue)) {
				rB = (RadioButton) downSlopeRG.findViewWithTag(downSlopeShapeChoice);
				if (rB != null) rB.setChecked(true);
			}
		}

		rB = null;
		for (String crossSlopeShapeChoice : crossSlopeShapeChoices) {
			if (crossSlopeShapeChoice.equals(crossSlopeStringValue)) {
				rB = (RadioButton) crossSlopeRG.findViewWithTag(crossSlopeShapeChoice);
				if (rB != null) rB.setChecked(true);
			}
		}
	}

	@Override
	public void save(Plot plot) {
		int radioButtonID = downSlopeRG.getCheckedRadioButtonId();
		if (radioButtonID != -1) {
			String curve = (String)downSlopeRG.findViewById(radioButtonID).getTag();
			plot.downSlopeShape = Curve.displayNameLookup.get(curve).name();
		}
		
		radioButtonID = crossSlopeRG.getCheckedRadioButtonId();
		if (radioButtonID != -1) {
			String curve = (String)crossSlopeRG.findViewById(radioButtonID).getTag();
			plot.crossSlopeShape = Curve.displayNameLookup.get(curve).name();
		}
		
		if (plot.name != null) LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
	}

	@Override
	public boolean isComplete(Plot plot) {
		return plot.downSlopeShape != null && !"".equals(plot.downSlopeShape) &&
				   plot.crossSlopeShape != null && !"".equals(plot.crossSlopeShape);
	}
	
	/***
	private OnCheckedChangeListener downSlopeChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
            	try {  
            		View v = downSlopeRG.findViewById(checkedId);
            		String name = (String)v.getTag();
            		Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
            	} catch (NullPointerException nPE) {} 
            }
        }
    };

    private OnCheckedChangeListener crossSlopeChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
            	try {  
            		View v = crossSlopeRG.findViewById(checkedId);
            		String name = (String)v.getTag();
            		Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
            	} catch (NullPointerException nPE) {} 
            }
        }
    };
    ***/

}
