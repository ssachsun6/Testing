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
 * SegmentFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;


import java.util.List;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.util.SegmentListAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.util.PersistenceFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//TODO: Might want to bring over the abstract PlotEditFragment from LPKS here
public class SegmentFragment extends Fragment implements PersistenceFragment {
	private static final String TAG = SegmentFragment.class.getName(); 
	
	private String name;
	
	private TextView segmentNameView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
    	name = "";
		try {
			name = getArguments().getString(SegmentListAdapter.ARG_NAME);
		} catch (Exception ex) {}	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_segment, container, false);

        segmentNameView = (TextView)view.findViewById(R.id.fragment_segment_nameView);
        segmentNameView.setText(name);
        
        return view;
	}

	@Override
	public void load(Segment segment) {
		// TODO Auto-generated method stub
	}

	@Override
	public Segment save(Segment segment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isComplete(Segment segment) {
		/***
		boolean retVal = true;
		if (segment == null) {
			retVal = false;
		} else {
			List<Fragment> fragments = RHMApplication.getInstance().getSegmentDetailFragments();
			for (Fragment f : fragments) {
				retVal = retVal && ((PersistenceFragment)f).isComplete(segment);
			}
		}
		return retVal;
		***/
		return false;
	}
}
