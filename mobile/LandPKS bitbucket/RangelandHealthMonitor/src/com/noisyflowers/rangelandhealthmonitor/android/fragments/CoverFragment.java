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
 * com.noisyflowers.rangelandhealthmonitor.android.fragments
 * CoverFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SegmentActivity;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.model.StickSegment;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;
import com.noisyflowers.rangelandhealthmonitor.android.util.NicelyToastedCheckBox;
import com.noisyflowers.rangelandhealthmonitor.android.util.NicelyToastedRadioButton;
import com.noisyflowers.rangelandhealthmonitor.android.util.PersistenceFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class CoverFragment extends Fragment implements IHelp, PersistenceFragment, OnCheckedChangeListener {
	View rootView;
		
	/***
	 * Note: Original specs for the cover selections said users could choose multiple values per stick segment.
	 * I implemented this with custom checkboxes feeding the covers boolean array. Specs were changed to allow
	 * only a single entry.  I switched to custom radio buttons for the UI, but kept the covers array, even though 
	 * it really isn't the best approach for single values.  Changing this will have ripple effects all the way into
	 * the server and I'm not confident the specs won't change back in the future.
	 */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cover, container, false);
        rootView = view;
        
        //cover11CB = (CheckBox)view.findViewById(R.id.fragment_cover_coverSegment1_button1);
        //cover11CB.setOnCheckedChangeListener(this);
		for (int x = 0; x < Segment.STICK_SEGMENT_COUNT; x++ ) {  //for each stick segment...
			ViewGroup vG = (ViewGroup)rootView.findViewWithTag(""+x);  //...find its ViewGroup...
			((CheckBox)vG.getChildAt(0)).setOnCheckedChangeListener(this); //...set listener for first checkbox
		}
		
		load(((SegmentActivity)getActivity()).segment);
		
		if (((SegmentActivity)getActivity()).date != null) {
			RHMApplication.getInstance().setViewGroupEnabled((ViewGroup)view, false);
		}
        return view;
	}
	
	@Override
	public void load(Segment segment) {
		for (int x = 0; x < segment.stickSegments.length; x++ ) {  //for each stick segment...
			//ViewGroup vG = (ViewGroup)getView().findViewWithTag(""+x);  //...find its ViewGroup...
			ViewGroup vG = (ViewGroup)rootView.findViewWithTag(""+x);  //...find its ViewGroup...
			if (vG != null) {
				for (StickSegment.Cover cover : StickSegment.Cover.values()) {  //...for each Cover type... 
					View v = vG.findViewWithTag(getString(cover.name));
					if (v instanceof CheckBox) {
						((CheckBox)v).setChecked(segment.stickSegments[x].covers[cover.ordinal()]);
					} else if (v instanceof RadioButton) {
						((RadioButton)v).setChecked(segment.stickSegments[x].covers[cover.ordinal()]);
					}
				}	
			}
		}		
	}

	@Override
	public Segment save(Segment segment) {
		NicelyToastedRadioButton.cancelToast();
		NicelyToastedCheckBox.cancelToast();
		for (int x = 0; x < segment.stickSegments.length; x++ ) {  //for each stick segment...
			ViewGroup vG = (ViewGroup)getView().findViewWithTag(""+x);  //...find its ViewGroup...
			if (vG != null) {
				for (StickSegment.Cover cover : StickSegment.Cover.values()) {  //...for each Cover type... 
					View v = vG.findViewWithTag(getString(cover.name));
					if (v instanceof CheckBox) {
						segment.stickSegments[x].covers[cover.ordinal()] = ((CheckBox)v).isChecked(); //...set corresponding boolean
					} else if (v instanceof RadioButton) {
						segment.stickSegments[x].covers[cover.ordinal()] = ((RadioButton)v).isChecked(); //...set corresponding boolean
					}
				}	
			}
		}
		return segment;
	}

	@Override
	public boolean isComplete(Segment segment) {
		boolean retVal = true;
		for (int x = 0; x < segment.stickSegments.length; x++ ) {  //for each stick segment...
			boolean somethingChecked = false;
			for (StickSegment.Cover cover : StickSegment.Cover.values()) {  //...for each Cover type... 
				somethingChecked = segment.stickSegments[x].covers[cover.ordinal()];
				if (somethingChecked) {
					break;
				}
			}
			if (!somethingChecked) {
				retVal = false;
				break;
			}
		}				
		return retVal;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		ViewGroup vG = (ViewGroup)buttonView.getParent();  //...find its ViewGroup...
		for (StickSegment.Cover cover : StickSegment.Cover.values()) {  //...for each Cover type... 
			if (cover.ordinal() > 0) {
				CheckBox cB = (CheckBox)vG.findViewWithTag(getString(cover.name));
				if (isChecked) {
					cB.setChecked(false);
					cB.setEnabled(false);
				} else {
					cB.setEnabled(true);
				}
			}
		}
		
	}

	@Override
	public View getHelpView() {
		ScrollView sV = new ScrollView(getActivity());
		TextView tV = new TextView(getActivity());
		tV.setPadding(10,10,10,10);
		tV.setText(Html.fromHtml(getString(R.string.fragment_cover_help)));
		sV.addView(tV);
		return sV;
	}

}
