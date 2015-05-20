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
 * PlotMapFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.activities.PlotEditListActivity;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;
import com.noisyflowers.landpks.android.model.Plot;

public class PlotMapFragment extends SupportMapFragment implements OnMarkerClickListener, OnInfoWindowClickListener {
	
	private GoogleMap map;
	private CameraUpdate cU;
	private List<Plot> plotList;
	private Map<String, Long> markerMap = null;
	
	public PlotMapFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflator, container, savedInstanceState);

		if (savedInstanceState != null) {
			markerMap = (Map<String, Long>)savedInstanceState.get("markerMap");
		}

		//initMap(); //TODO: here or in onResume?
		/**
		v.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						map.moveCamera(cU);
					}
				});
		**/
		return v;
	}
	
	/**/
	@Override
	public void onResume() {
		super.onResume();
		if (markerMap == null) {
			initMap();
		}
	}
	/**/
	
	/**
	@Override
	public void onStart() {
		super.onStart();
		map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition pos) {
				map.moveCamera(cU);
				map.setOnCameraChangeListener(null);
			}
		});
		
	}
	**/

	private void initMap() {
		//load markers using plot list
		map = this.getMap();
		//map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		
		map.setOnMarkerClickListener(this);
		map.setOnInfoWindowClickListener(this);
		
		//This ridiculous thing is necessary to get multiline snippet in info window
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
						
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				//View v = getLayoutInflater().inflate(R.layout.map_plot_info_window, null);
				LayoutInflater inflater = (LayoutInflater) LandPKSApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.map_plot_info_window, null);
				
				TextView title = (TextView) v.findViewById(R.id.map_info_window_plot_name);
				TextView snippet = (TextView) v.findViewById(R.id.map_info_window_plot_data);
				title.setText(marker.getTitle());
				snippet.setText(marker.getSnippet());
				return v;
			}
		});
		
		LatLngBounds.Builder llBuilder = new LatLngBounds.Builder();
		LandPKSDatabaseAdapter dbAdapter = LandPKSApplication.getInstance().getDatabaseAdapter();
		//List<Plot> plotList = dbAdapter.getPlots();
		markerMap = new HashMap<String, Long>();
		plotList = dbAdapter.getPlots();
		
		boolean markerAdded = false;
		for (Plot plot : plotList) {
			if (plot.latitude != null && plot.longitude != null) {
				markerAdded = true;
				//map.addMarker(new MarkerOptions().position(new LatLng(plot.latitude, plot.longitude)).title(Long.toString(plot.ID)));
				//String snippet = plot.recommendation == null || "".equals(plot.recommendation) ? getString(R.string.plot_map_fragment_plot_not_analyzed) : plot.recommendation;
				String snippet = plot.remoteID == null ? getString(R.string.plot_map_fragment_plot_not_analyzed) : LandPKSApplication.getInstance().analyticResults(plot);
				Marker m = map.addMarker(new MarkerOptions().position(new LatLng(plot.latitude, plot.longitude)).title(plot.name).snippet(snippet));
				markerMap.put(m.getId(), plot.ID);
				llBuilder.include(new LatLng(plot.latitude, plot.longitude));
			}
		}
		
		if (markerAdded) {
			//set bounds to markers
			LatLngBounds bounds = llBuilder.build();
			cU = CameraUpdateFactory.newLatLngBounds(bounds,0);
			
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition pos) {
					map.animateCamera(cU);
					map.setOnCameraChangeListener(null);
				}
			});
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		//TODO:  set current plot using marker title (which is ID)
		//start editlistactivity
		/**
		long plotID = Long.parseLong(marker.getTitle());
		Plot plot = LandPKSApplication.getInstance().getDatabaseAdapter().getPlot(plotID);
	    LandPKSApplication.getInstance().setPlot(plot);
		Intent detailIntent = new Intent(getActivity(), PlotEditListActivity.class);
		startActivity(detailIntent);
		return true;
		**/
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		long plotID = (Long)markerMap.get(marker.getId());
		Plot plot = LandPKSApplication.getInstance().getDatabaseAdapter().getPlot(plotID);
	    LandPKSApplication.getInstance().setPlot(plot);
		Intent detailIntent = new Intent(getActivity(), PlotEditListActivity.class);
		startActivity(detailIntent);		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("markerMap", (HashMap)markerMap);
		super.onSaveInstanceState(outState);
	}
	
	
}
