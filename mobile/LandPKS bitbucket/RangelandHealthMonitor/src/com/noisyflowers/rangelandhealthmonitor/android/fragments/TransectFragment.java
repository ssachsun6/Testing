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
 * TransectFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SegmentActivity;
import com.noisyflowers.rangelandhealthmonitor.android.activities.TransectActivity;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;
import com.noisyflowers.rangelandhealthmonitor.android.util.SegmentListAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.util.PersistenceFragment;

//TODO: Might want to bring over the abstract PlotEditFragment from LPKS here
public class TransectFragment extends Fragment implements IHelp, PersistenceFragment {
	private static final String TAG = TransectFragment.class.getName(); 

	//TODO: change this to strings/server constants arrangement
	/***
	public enum SegmentName {
		SEGMENT_1_NAME ("0-5m"),
		SEGMENT_2_NAME ("5-10m"),
		SEGMENT_3_NAME ("10-15m"),
		SEGMENT_4_NAME ("15-20m"),
		SEGMENT_5_NAME ("20-25m");
		
		public final String name;
		
		SegmentName(String name) {
			this.name = name;
		}
	}
	***/
	
	private List<Fragment> segmentFragments;
	
	private ListView listView; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		segmentFragments = RHMApplication.getInstance().getSegmentFragments();		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_transect, container, false);
		//TODO: port SegmentListAdapter from LPKS PlotEditListAdapter
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date date;
		try {date = sdf.parse(((TransectActivity)getActivity()).date);} catch (Exception e) {date = new Date();}
        SegmentListAdapter aD = new SegmentListAdapter(getActivity(), android.R.layout.simple_list_item_1, segmentFragments, date);
		listView = (ListView)view.findViewById(R.id.fragment_transect_list);
		listView.setAdapter(aD);
	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
	        	Intent detailIntent = new Intent(getActivity(), SegmentActivity.class);
	    		detailIntent.putExtra(SegmentActivity.SEGMENT_INDEX, position); //TODO: better way to get horizon id than by position
	    		//detailIntent.putExtra(SegmentActivity.TRANSECT_ID, ((TransectActivity)getActivity()).transectID); 
	    		detailIntent.putExtra(SegmentActivity.TRANSECT_ID, ((TransectActivity)getActivity()).transect.ID); 
	    		detailIntent.putExtra(SegmentActivity.SITE_NAME, ((TransectActivity)getActivity()).siteName); 
	    		detailIntent.putExtra(SegmentActivity.DATE, ((TransectActivity)getActivity()).date); 
	    		detailIntent.putExtra(SegmentActivity.TRANSECT_DIRECTION, ((TransectActivity)getActivity()).transectDirection.name()); 
	    		startActivity(detailIntent);
	    		//Toast.makeText(getActivity(), "range " + position + " here", Toast.LENGTH_SHORT).show();
	        }

	      });
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
		//TODO: this whole thing is cheesy.  Redo.  I'm thinking all these isCompletes should be moved to the db adapter and fed straight from the db.
		boolean retVal = true;
		List<Fragment> segmentFragments = RHMApplication.getInstance().getSegmentFragments();
		int position = 0; //TODO: cheesy
		for (Fragment sF : segmentFragments) {
			//Segment segmentx = RHMApplication.getInstance().getDatabaseAdapter().getSegment(position, ((TransectActivity)getActivity()).transectID, new Date());
			//Segment segmentx = RHMApplication.getInstance().getDatabaseAdapter().getSegment(position, ((TransectActivity)getActivity()).transect.ID, new Date());
			Segment segmentx = RHMApplication.getInstance().getDatabaseAdapter().getSegment(Segment.Range.values()[position], ((TransectActivity)getActivity()).transect.ID, new Date());
			retVal = retVal && ((PersistenceFragment)sF).isComplete(segmentx);
			position++;
		}
		return retVal;
		***/
		return false;
	}
	
	@Override
	public View getHelpView() {
		ScrollView sV = new ScrollView(getActivity());
		TextView tV = new TextView(getActivity());
		tV.setPadding(10,10,10,10);
		tV.setText(Html.fromHtml(getString(R.string.fragment_transect_help)));
		sV.addView(tV);
		return sV;
	}

}
