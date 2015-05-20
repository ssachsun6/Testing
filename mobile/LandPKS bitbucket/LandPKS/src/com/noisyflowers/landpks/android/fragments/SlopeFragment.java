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
 * SlopeFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.activities.ClinometerActivity;
import com.noisyflowers.landpks.android.activities.PlotListActivity;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class SlopeFragment extends PlotEditFragment implements OnClickListener {

	public static final String DISPLAY_NAME = "Slope Gradient";
		
	public enum Slope {
		SLOPE_1 (R.string.server_resource_slope_1, R.string.slope_fragment_slope_1),
		SLOPE_2 (R.string.server_resource_slope_2, R.string.slope_fragment_slope_2),
		SLOPE_3 (R.string.server_resource_slope_3, R.string.slope_fragment_slope_3),
		SLOPE_4 (R.string.server_resource_slope_4, R.string.slope_fragment_slope_4),
		SLOPE_5 (R.string.server_resource_slope_5, R.string.slope_fragment_slope_5),
		SLOPE_6 (R.string.server_resource_slope_6, R.string.slope_fragment_slope_6),
		SLOPE_7 (R.string.server_resource_slope_7, R.string.slope_fragment_slope_7);
		
		private final int serverName, displayName;
		
		public static final Map<String, Slope> displayNameLookup = new HashMap<String, Slope>();
		static {
			for (Slope s : Slope.values()) {
				displayNameLookup.put(LandPKSApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, Slope> serverNameLookup = new HashMap<String, Slope>();
		static {
			for (Slope s : Slope.values()) {
				serverNameLookup.put(LandPKSApplication.getInstance().getString(s.serverName), s);
			}
		}

		Slope(int serverName, int displayName) {
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
	private Slope slope = null;

	private RadioGroup slopeRG1, slopeRG2;
	TextView slopeTV;
	
	private ArrayList<String> slopeChoices = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getFragmentManager().beginTransaction().add(this, DISPLAY_NAME).commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_slope, container, false);
		slopeRG1 = (RadioGroup) root.findViewById(R.id.slopeRadioGroup1);
		slopeRG2 = (RadioGroup) root.findViewById(R.id.slopeRadioGroup2);
        slopeRG1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
        slopeRG2.clearCheck();
        //slopeRG1.setOnCheckedChangeListener(slopeListener1);
        //slopeRG2.setOnCheckedChangeListener(slopeListener2);			
        
		for (int i = 0; i < slopeRG1.getChildCount(); i++) {
			//slopeChoices.add((String)slopeRG1.getChildAt(i).getTag());
			View v = slopeRG1.getChildAt(i);
			if (v instanceof RadioButton) {
				slopeChoices.add((String)v.getTag());
			}
		}
		for (int i = 0; i < slopeRG2.getChildCount(); i++) {
			//slopeChoices.add((String)slopeRG2.getChildAt(i).getTag());
			View v = slopeRG2.getChildAt(i);
			if (v instanceof RadioButton) {
				slopeChoices.add((String)v.getTag());
			}
		}
		
		Button clinometerB = (Button) root.findViewById(R.id.fragment_slope_clinometerButton);
		clinometerB.setOnClickListener(this);
		
		slopeTV = (TextView)root.findViewById(R.id.fragment_slope_clinometerResult);
		slopeTV.setVisibility(View.GONE);
		
		return root;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
        slopeRG1.setOnCheckedChangeListener(null);
        slopeRG2.setOnCheckedChangeListener(null);			
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//Waiting to add these here prevents Toast popping on load.  Still waiting to see if there are side-effects. 
        slopeRG1.setOnCheckedChangeListener(slopeListener1);
        slopeRG2.setOnCheckedChangeListener(slopeListener2);			
	}

	@Override
	public void load(Plot plot) {
		slopeRG1.clearCheck();
		slopeRG2.clearCheck();
		slopeTV.setText(null);
		
		//String stringValue = plot.slope;
		//String stringValue = plot.slope == null ? null : Slope.valueOf(plot.slope).getDisplayName();
		String stringValue;
		try {
			stringValue = plot.slope == null ? null : Slope.valueOf(plot.slope).getDisplayName();
		} catch (IllegalArgumentException iAE) {
			stringValue = plot.slope;
			slopeTV.setText(stringValue);
			slopeTV.setVisibility(View.VISIBLE);
		}
		RadioButton rB = null;
		for (String slopeChoice : slopeChoices) {
			if (slopeChoice.equals(stringValue)) {
				rB = (RadioButton) slopeRG1.findViewWithTag(slopeChoice);
				if (rB == null) {
					rB = (RadioButton) slopeRG2.findViewWithTag(slopeChoice);
				}
				break;
			}
		}
		if (rB != null) rB.setChecked(true);
		
		//Waiting to add these here prevents Toast popping on load.  Still waiting to see if there are side-effects. 
        //slopeRG1.setOnCheckedChangeListener(slopeListener1);
        //slopeRG2.setOnCheckedChangeListener(slopeListener2);			
	}

	@Override
	public void save(Plot plot) {
		if (slopeTV.getText() != null && !"".equals(slopeTV.getText().toString())) {
			plot.slope = slopeTV.getText().toString();
		} else {
			//plot.slope = getCheckedSlope();
			String checkedSlope = getCheckedSlope();
			plot.slope = checkedSlope == null ? null : Slope.displayNameLookup.get(checkedSlope).name();
		}
		if (plot.name != null) LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
	}

	@Override
	public boolean isComplete(Plot plot) {
		return plot.slope != null && !"".equals(plot.slope);
	}
	
	
	private void clearSelection(RadioGroup rG, OnCheckedChangeListener listener, boolean clearText) {
		rG.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
		rG.clearCheck(); // clear the second RadioGroup
		rG.setOnCheckedChangeListener(listener); //reset the listener
		if (clearText) {
        	slopeTV.setText(null);
        	slopeTV.setVisibility(View.GONE);				
		}
	}
	
	private OnCheckedChangeListener slopeListener1 = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
        	/***
            if (checkedId != -1) {
            	slopeTV.setText(null);
            	slopeTV.setVisibility(View.GONE);
            	slopeRG2.setOnCheckedChangeListener(null);
            	slopeRG2.clearCheck();
            	slopeRG2.setOnCheckedChangeListener(slopeListener1);
            }
            ***/
            if (checkedId != -1) {
        		if (slopeTV.getText() != null && !"".equals(slopeTV.getText().toString())) {
    				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
    		    		.setMessage(getActivity().getString(R.string.slope_fragment_override_confirm))
    		    		.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
	    		    		@Override
			    			public void onClick(DialogInterface dialog, int which) {
	    		    			clearSelection(slopeRG2, slopeListener2, true);
			    			}
		    			})	
    		    		.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
	    		    		@Override
			    			public void onClick(DialogInterface dialog, int which) {
	    		    			clearSelection(slopeRG1, slopeListener1, false);
			    			}
		    			});		    		
    		    	AlertDialog alert = builder.create();
    		    	alert.show(); 		
		    	} else {       			
	    			clearSelection(slopeRG2, slopeListener2, true);
		    	}
            }
        }
    };

    private OnCheckedChangeListener slopeListener2 = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
        	/***
            if (checkedId != -1) {
            	slopeTV.setText(null);
            	slopeTV.setVisibility(View.GONE);
            	slopeRG1.setOnCheckedChangeListener(null);
            	slopeRG1.clearCheck();
            	slopeRG1.setOnCheckedChangeListener(slopeListener1);
            }
            ***/
            if (checkedId != -1) {
        		if (slopeTV.getText() != null && !"".equals(slopeTV.getText().toString())) {
    				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
    		    		.setMessage(getActivity().getString(R.string.slope_fragment_override_confirm))
    		    		.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
	    		    		@Override
			    			public void onClick(DialogInterface dialog, int which) {
	    		    			clearSelection(slopeRG1, slopeListener1, true);
			    			}
		    			})	
    		    		.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
	    		    		@Override
			    			public void onClick(DialogInterface dialog, int which) {
	    		    			clearSelection(slopeRG2, slopeListener2, false);
			    			}
		    			});		    		
    		    	AlertDialog alert = builder.create();
    		    	alert.show(); 		
		    	} else {       			
	    			clearSelection(slopeRG1, slopeListener1, true);
		    	}
            }
        }
    };

	private String getCheckedSlope() {
		//To get checked button in those two groups, put this somewhere:
		//int chkId1 = landcoverRG1.getCheckedRadioButtonId();
		//int chkId2 = landcoverRG2.getCheckedRadioButtonId();
		//int realCheck = chkId1 == -1 ? chkId2 : chkId1;
		//return realCheck;
		
		String retVal = null;
		int chkID = slopeRG1.getCheckedRadioButtonId();
		if (chkID != -1) {
			retVal = (String)slopeRG1.findViewById(chkID).getTag();
		} else {
			chkID = slopeRG2.getCheckedRadioButtonId();
			if (chkID != -1) {
				retVal = (String)slopeRG2.findViewById(chkID).getTag();
			}
		}
		return retVal;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_slope_clinometerButton: {
				startActivityForResult(new Intent(getActivity(), ClinometerActivity.class), 0);
			}
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			slopeRG1.clearCheck();
			slopeRG2.clearCheck();
			//slopeTV.setText(data.getStringExtra("slope") + (char)176);
			slopeTV.setText(data.getStringExtra("slope"));
			slopeTV.setVisibility(View.VISIBLE);
			save(LandPKSApplication.getInstance().getPlot()); //???
		}
	}
}
