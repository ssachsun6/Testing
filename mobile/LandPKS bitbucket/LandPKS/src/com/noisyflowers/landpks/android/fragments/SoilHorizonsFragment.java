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
 * SoilHorizonsFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.activities.SoilHorizonActivity;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;
import com.noisyflowers.landpks.android.util.PlotEditListAdapter;

public class SoilHorizonsFragment extends PlotEditFragment/*ListFragment*/ {

	//TODO: move all to strings.xml
	public static final String DISPLAY_NAME = "Soil Layers";
	/***
	public static final String HORIZON_1_NAME = "0-1cm";
	public static final String HORIZON_2_NAME = "1-10cm";
	public static final String HORIZON_3_NAME = "10-20cm";
	public static final String HORIZON_4_NAME = "20-50cm";
	public static final String HORIZON_5_NAME = "50-70cm";
	public static final String HORIZON_6_NAME = ">70cm";
	***/
	
	//TODO: change this to strings/server constants arrangement
	public enum HorizonName {
		HORIZON_1_NAME ("0-1cm"),
		HORIZON_2_NAME ("1-10cm"),
		HORIZON_3_NAME ("10-20cm"),
		HORIZON_4_NAME ("20-50cm"),
		HORIZON_5_NAME ("50-70cm"),
		HORIZON_6_NAME ("70-100cm"),
		HORIZON_7_NAME ("100-120cm");
		
		public final String name;
		
		HorizonName(String name) {
			this.name = name;
		}
	}
	
	private List<Fragment> horizonFragments;
	
	private ListView listView; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getFragmentManager().beginTransaction().add(this, DISPLAY_NAME).commit();
				
		horizonFragments = LandPKSApplication.getInstance().getHorizonFragments();
		
		/***  use this if descending from ListFragment
        PlotEditListAdapter aD = new PlotEditListAdapter(getActivity(), android.R.layout.simple_list_item_1, horizonFragments);
		setListAdapter(aD);
		***/

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//use this if descending from ListFragment
        //return inflater.inflate(R.layout.fragment_soilhorizons, container, false);
		
		View view = inflater.inflate(R.layout.fragment_soilhorizons, container, false);
        PlotEditListAdapter aD = new PlotEditListAdapter(getActivity(), android.R.layout.simple_list_item_1, horizonFragments);
		listView = (ListView)view.findViewById(R.id.list);
		listView.setAdapter(aD);
	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, final View view,
	            int position, long id) {
	    		Intent detailIntent = new Intent(getActivity(), SoilHorizonActivity.class);
	    		detailIntent.putExtra("position", position); //TODO: better way to get horizon id than by position
	    		startActivity(detailIntent);
	        }

	      });
	    return view;
        
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	/*** use this if descending from ListFragment
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		Intent detailIntent = new Intent(getActivity(), SoilHorizonActivity.class);
		detailIntent.putExtra("position", position); //TODO: better way to get horizon id than by position
		startActivity(detailIntent);
	}
	***/

	public boolean isComplete(Plot plot) {
		boolean returnVal = true;
		List<Fragment> horizonFragments = LandPKSApplication.getInstance().getHorizonFragments();
		for (Fragment pEF : horizonFragments) {
			returnVal = returnVal && ((PlotEditFragment)pEF).isComplete(plot);
			if (((SoilHorizonFragment)pEF).isRestrictiveLayer(plot)) 
				break;
		}
		return returnVal;
	}

	@Override
	public void load(Plot plot) {
		// defer to list members
	}

	@Override
	public void save(Plot plot) {
		// defer to list members	
	}
	
}
