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
 * ResultsFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.logging.Logger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.dal.LandPKSDatabaseAdapter;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.ClimateChartView;
import com.noisyflowers.landpks.android.util.LPKSBarChartView;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

public class ResultsFragment extends PlotEditFragment {
	private static final String TAG = ResultsFragment.class.getName(); 

	private static final String COLUMN_COUNT = "columnCount";
	private static final int MIN_COLUMNS = 1;
	private static final int TICS_PER_PLOT = 4;

	private View root;
	
	private TextView recommendationField, itemCountTV, itemCountLabelTV, disclaimerTV;
	private SeekBar itemCountSeekBar;
	private LPKSBarChartView grassProdChartView, grassErosionChartView, cropProdChartView, cropErosionChartView, awcChartView;
	private ClimateChartView climateChartView;
	private LinearLayout grassProdChartViewArea;
	
	private ViewGroup mainArea;
	
	private LandPKSApplication application;
	private int columnCount = MIN_COLUMNS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		application = LandPKSApplication.getInstance();
		
		if (savedInstanceState != null && savedInstanceState.containsKey(COLUMN_COUNT)) {
			columnCount = savedInstanceState.getInt(COLUMN_COUNT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_results, container, false);
		
		Plot plot = application.getPlot();
		
		mainArea = (ViewGroup)root.findViewById(R.id.results_main_area);
		
		recommendationField = (TextView) root.findViewById(R.id.results_TextView_recommendation);

		climateChartView = (ClimateChartView) root.findViewById(R.id.results_Climate_Chart);
		grassProdChartView = (LPKSBarChartView) root.findViewById(R.id.results_GrassProductivity_BarChart);
		grassErosionChartView = (LPKSBarChartView) root.findViewById(R.id.results_GrassErosion_BarChart);
		cropProdChartView = (LPKSBarChartView) root.findViewById(R.id.results_CropProductivity_BarChart);
		cropErosionChartView = (LPKSBarChartView) root.findViewById(R.id.results_CropErosion_BarChart);
		awcChartView = (LPKSBarChartView) root.findViewById(R.id.results_AWC_BarChart);
        disclaimerTV = (TextView) root.findViewById(R.id.results_TextView_disclaimer);
        itemCountLabelTV = (TextView) root.findViewById(R.id.results_TextView_itemCountLabel);
        itemCountTV = (TextView) root.findViewById(R.id.results_TextView_itemCount);
        itemCountTV.setText("" + MIN_COLUMNS);
		itemCountSeekBar = (SeekBar) root.findViewById(R.id.results_graph_number_seekbar);
		itemCountSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String count = progress == seekBar.getMax() ? (" " + getActivity().getString(R.string.results_fragment_recommendations_slider_max)) : (" " + columnsFromProgress(progress));
                itemCountTV.setText(count);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            	int progress = seekBar.getProgress();
                columnCount = progress == seekBar.getMax() ? -1 : columnsFromProgress(progress);
                grassProdChartView.updateColumnCount(columnCount);
                grassErosionChartView.updateColumnCount(columnCount);
                cropProdChartView.updateColumnCount(columnCount);
                cropErosionChartView.updateColumnCount(columnCount);
                awcChartView.updateColumnCount(columnCount);
            }
        });
		
		return root;
	}
	
	private int columnsFromProgress(int progress) {
		return ((progress/TICS_PER_PLOT) + MIN_COLUMNS);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(COLUMN_COUNT, columnCount);
	}


	@Override
	public void load(Plot plot) {
		if (plot.remoteID != null) {
			//String results = analyticResults(plot);
			String results = LandPKSApplication.getInstance().analyticResults(plot);
			recommendationField.setText(results);		
			
			if (plot.monthlyClimates != null && plot.monthlyClimates.size() > 0) {
				climateChartView.setVisibility(View.VISIBLE);
			}

			boolean showCountSlider = false;
			if (plot.grassProductivity != null) {
				grassProdChartView.setVisibility(View.VISIBLE);
				disclaimerTV.setVisibility(View.VISIBLE);
				showCountSlider = true;
			}
			if (plot.grassErosion != null) {
				grassErosionChartView.setVisibility(View.VISIBLE);
				disclaimerTV.setVisibility(View.VISIBLE);
				showCountSlider = true;
			}
			if (plot.cropProductivity != null) {
				cropProdChartView.setVisibility(View.VISIBLE);
				disclaimerTV.setVisibility(View.VISIBLE);
				showCountSlider = true;
			}
			if (plot.cropErosion != null) {
				cropErosionChartView.setVisibility(View.VISIBLE);
				disclaimerTV.setVisibility(View.VISIBLE);
				showCountSlider = true;
			}
			if (plot.awcSoilProfile != null) {
				awcChartView.setVisibility(View.VISIBLE);
				showCountSlider = true;
			}
			if (showCountSlider) {
				itemCountSeekBar.setVisibility(View.VISIBLE);
				itemCountLabelTV.setVisibility(View.VISIBLE);
				itemCountTV.setVisibility(View.VISIBLE);
				if (columnCount != MIN_COLUMNS) {
					grassProdChartView.updateColumnCount(columnCount);
					grassErosionChartView.updateColumnCount(columnCount);
					cropProdChartView.updateColumnCount(columnCount);
					cropErosionChartView.updateColumnCount(columnCount);
					awcChartView.updateColumnCount(columnCount);					
				}
			}	
		}
	}

	@Override
	public void save(Plot plot) {
	}

	@Override
	public boolean isComplete(Plot plot) {
		return true;
	}

	private String analyticResults(Plot plot) {
		StringBuilder sB = new StringBuilder();
		LandPKSDatabaseAdapter db = LandPKSApplication.getInstance().getDatabaseAdapter();
		Double max = db.getMaxValue("grassProductivity");
		sB.append(getString(R.string.results_fragment_recommendations_grass_productivity) + " " + (plot.grassProductivity == null || max == null || max == 0 ? "N/A" : Math.round((plot.grassProductivity/max)*100)) + "\n");
		max = db.getMaxValue("grassErosion");
		sB.append(getString(R.string.results_fragment_recommendations_grass_erosion) + " " + (plot.grassErosion == null || max == null || max == 0 ? "N/A" : Math.round((plot.grassErosion/max)*100)) + "\n");
		max = db.getMaxValue("cropProductivity");
		sB.append(getString(R.string.results_fragment_recommendations_crop_productivity) + " " + (plot.cropProductivity == null || max == null || max == 0 ? "N/A" : Math.round((plot.cropProductivity/max)*100)) + "\n");
		max = db.getMaxValue("cropErosion");
		sB.append(getString(R.string.results_fragment_recommendations_crop_erosion) + " " + (plot.cropErosion == null || max == null || max == 0 ? "N/A" : Math.round((plot.cropErosion/max)*100)) + "\n");
		return sB.toString();
	}

}
