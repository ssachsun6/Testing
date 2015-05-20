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
 * PlotListFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.activities.PlotEditListActivity;
import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

public class PlotListFragment extends Fragment {
	
	private ListView listView;
	private ImageView logoView;
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		LandPKSDatabaseAdapter dbAdapter = LandPKSApplication.getInstance().getDatabaseAdapter();
		List<Plot> plotList = dbAdapter.getPlots();
		if (plotList.isEmpty()) {
			listView.setVisibility(View.GONE);
			logoView.setVisibility(View.VISIBLE);
		} else {
			PlotListAdapter aD = new PlotListAdapter(getActivity(), android.R.layout.simple_list_item_1, plotList);
			listView.setAdapter(aD);	
			
		    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    		Plot plot = (Plot)listView.getAdapter().getItem(position);
		    		plot = LandPKSApplication.getInstance().getDatabaseAdapter().getPlot(plot.ID);
		    	    LandPKSApplication.getInstance().setPlot(plot);
		    	    
		    		Intent detailIntent = new Intent(getActivity(), PlotEditListActivity.class);
		    		startActivity(detailIntent);
		        }
		    });
		    
			listView.setVisibility(View.VISIBLE);
			logoView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_plot_list, container, false);
		
		listView = (ListView)view.findViewById(R.id.fragment_plot_list_list);
		logoView = (ImageView)view.findViewById(R.id.fragment_plot_list_logo);

		//listView.setVisibility(View.GONE);
		//logoView.setVisibility(View.VISIBLE);
		
		return view;
	}
	
	public class PlotListAdapter extends ArrayAdapter<Plot>  implements SectionIndexer {

	   	Context context; 
	    int layoutResourceId;    
	 	List<Plot> list = null;
	 	
	 	LinkedHashMap<String, Integer> sectionNameToStartPositionMap;
	 	LinkedHashMap<Integer, Integer> rawPositionToSectionMap;
	 	ArrayList<String> sections;
	
		public PlotListAdapter(Context context, int textViewResourceId, List<Plot> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.layoutResourceId = textViewResourceId;
			list = objects;
			
			sectionNameToStartPositionMap = new LinkedHashMap<String, Integer>();
			rawPositionToSectionMap = new LinkedHashMap<Integer, Integer>();
			sections = new ArrayList<String>();
			int size = objects.size();
			
			int sectionsIdx = -1;
			for (int x = 0; x < size; x++) {
				Plot plot = objects.get(x);
				//get first letter of each plot name and convert to upper case
				String ch = plot.name.substring(0,1).toUpperCase();
				//if not in start position map
				if (!sectionNameToStartPositionMap.containsKey(ch)) {
					sectionNameToStartPositionMap.put(ch, x); //add it
					sections.add(ch);  //add new section
					++sectionsIdx;
				}
				rawPositionToSectionMap.put(x, sectionsIdx); //add raw position entry
			}
			
			/***
			sections = new String[sectionStartPositionMap.size()];
			int x = 0;
			for (String s : sectionStartPositionMap.keySet()) {
				sections[x++] = s;  //will be ordered same as added to sectionStartPositionMap since it's a LinkedHashMap
			}
			***/
			
			/***
			//create an ordered array of the keys
			Set<String> sectionLetters = sectionStartPositionMap.keySet();
			List<String> sectionList = new ArrayList<String>(sectionLetters);
			Collections.sort(sectionList);
			sections = new String[sectionList.size()];
			sectionList.toArray(sections);
			***/
		}
		
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
			View rowView = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
			TextView textView = (TextView) rowView.findViewById(android.R.id.text1);  //TODO: can't assume simple_list_item_1.  Should probably use our own layout

	        Plot plot = list.get(position);
	        
	        LandPKSApplication.getInstance().setPlot(plot);  //TODO:  this generates some wasted work; maybe figure something better
	        textView.setText(plot.name);
	        
	        boolean completed = true;
	        List<Fragment> editFragments = LandPKSApplication.getInstance().getEditFragments();
	        for (Fragment f : editFragments) {
	        	completed = completed && ((PlotEditFragment)f).isComplete(plot);
	        }
	        
			if (completed) {
				((CheckedTextView)rowView).setChecked(true);
			}

			return rowView;
	        
	    }

		@Override
		public int getPositionForSection(int sectionIndex) {
			return sectionNameToStartPositionMap.get(sections.get(sectionIndex));
		}

		@Override
		public int getSectionForPosition(int position) {
			return rawPositionToSectionMap.get(position);
		}

		@Override
		public Object[] getSections() {
			return sections.toArray();
		}
		
	}
	
}

/****
public class PlotListFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		//TODO: move to background thread?
		LandPKSDatabaseAdapter dbAdapter = LandPKSApplication.getInstance().getDatabaseAdapter();
		List<Plot> plotList = dbAdapter.getPlots();
		//ArrayAdapter<Plot> aD = new ArrayAdapter<Plot>(getActivity(), android.R.layout.simple_list_item_1, plotList);
		PlotListAdapter aD = new PlotListAdapter(getActivity(), android.R.layout.simple_list_item_1, plotList);
		setListAdapter(aD);	
		if (plotList.isEmpty()) {
			getView().setBackgroundResource(R.drawable.lpks_logo);
		}
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
 
		//Plot plot = (Plot)listView.getAdapter().getItem(position);
		Plot plot = (Plot)listView.getAdapter().getItem(position);
		plot = LandPKSApplication.getInstance().getDatabaseAdapter().getPlot(plot.ID);
	    LandPKSApplication.getInstance().setPlot(plot);
	    
		Intent detailIntent = new Intent(getActivity(), PlotEditListActivity.class);
		startActivity(detailIntent);

	}

	public class PlotListAdapter extends ArrayAdapter<Plot> {

	   	Context context; 
	    int layoutResourceId;    
	 	List<Plot> list = null;
	
		public PlotListAdapter(Context context, int textViewResourceId,
				List<Plot> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.layoutResourceId = textViewResourceId;
			list = objects;
		}
		
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
			View rowView = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
			TextView textView = (TextView) rowView.findViewById(android.R.id.text1);  //TODO: can't assume simple_list_item_1.  Should probably use our own layout

	        Plot plot = list.get(position);
	        
	        LandPKSApplication.getInstance().setPlot(plot);  //TODO:  this generates some wasted work; maybe figure something better
	        textView.setText(plot.name);
	        
	        boolean completed = true;
	        List<Fragment> editFragments = LandPKSApplication.getInstance().getEditFragments();
	        for (Fragment f : editFragments) {
	        	completed = completed && ((PlotEditFragment)f).isComplete(plot);
	        }
	        
			if (completed) {
				((CheckedTextView)rowView).setChecked(true);
			}

			return rowView;
	        
	    }
		
	}

}
***/