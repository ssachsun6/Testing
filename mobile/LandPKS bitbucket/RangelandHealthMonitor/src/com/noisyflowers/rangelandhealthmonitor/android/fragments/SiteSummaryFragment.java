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
 * SiteSummaryFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SiteDetailActivity;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SiteSummaryActivity;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMDatabaseAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.model.StickSegment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

public class SiteSummaryFragment extends Fragment implements IHelp {

	public static final String ARG_ITEM_ID = "item_id";

	private static final double NUM_SEGMENTS = Transect.Direction.values().length * Segment.Range.values().length;
	private static final double NUM_STICK_SEGMENTS = Segment.STICK_SEGMENT_COUNT * NUM_SEGMENTS;
	
	private EditText bareGroundET, totalCoverET, foliarCoverET, canopyGapET, basalGapET, species1DensityET, species2DensityET;
	private LinearLayout bareArea, totalCoverArea, foliarCoverArea, foliarCompositionArea, coverArea, heightArea;
	
	public SiteSummaryFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_site_summary, container, false);
		
		bareArea = (LinearLayout)rootView.findViewById(R.id.fragment_site_summary_landcover_bare_area);
		totalCoverArea = (LinearLayout)rootView.findViewById(R.id.fragment_site_summary_landcover_total_cover_area);
		foliarCoverArea = (LinearLayout)rootView.findViewById(R.id.fragment_site_summary_landcover_foliar_cover_area);
		foliarCompositionArea = (LinearLayout)rootView.findViewById(R.id.fragment_site_summary_foliar_composition_area);
		coverArea = (LinearLayout)rootView.findViewById(R.id.fragment_site_summary_cover_area);
		heightArea = (LinearLayout)rootView.findViewById(R.id.fragment_site_summary_height_area);

		bareGroundET = (EditText)rootView.findViewById(R.id.fragment_site_summary_landcover_bare);
		totalCoverET = (EditText)rootView.findViewById(R.id.fragment_site_summary_landcover_cover);
		foliarCoverET = (EditText)rootView.findViewById(R.id.fragment_site_summary_landcover_foliar_cover);

		canopyGapET = (EditText)rootView.findViewById(R.id.fragment_site_summary_canopy_gap);
		basalGapET = (EditText)rootView.findViewById(R.id.fragment_site_summary_basal_gap);
		species1DensityET = (EditText)rootView.findViewById(R.id.fragment_site_summary_species_1);
		species2DensityET = (EditText)rootView.findViewById(R.id.fragment_site_summary_species_2);

		PercentageReturn percentages = calculatePercentages();
		DecimalFormat displayFormat = new DecimalFormat("0.0");
		
		if (percentages.foliarCoverPercentage != null) {
			totalCoverArea.setVisibility(View.VISIBLE);
			foliarCoverArea.setVisibility(View.VISIBLE);
			foliarCoverET.setText(displayFormat.format(percentages.foliarCoverPercentage) + "%");
		}
		
		/**
		if (percentages.nonfoliarCoverPercentage != null) {
			totalCoverArea.setVisibility(View.VISIBLE);
			nonfoliarCoverArea.setVisibility(View.VISIBLE);
			nonfoliarCoverET.setText(displayFormat.format(percentages.nonfoliarCoverPercentage) + "%");
		}
		**/
		boolean bareShown = false;
		for (StickSegment.Cover cover : StickSegment.Cover.values()) {
			if (percentages.coverPercentages.get(cover) != null) {
				if (cover == StickSegment.Cover.COVER_1) {
					bareArea.setVisibility(View.VISIBLE);
					bareShown = true;
					bareGroundET.setText(displayFormat.format(percentages.coverPercentages.get(cover)) + "%");
					totalCoverArea.setVisibility(View.VISIBLE);
					totalCoverET.setText(displayFormat.format(100 - percentages.coverPercentages.get(cover)) + "%");
				} else {
					//add label and value views to coverArea
					TextView tV = new TextView(getActivity());
					tV.setText(cover.name);
					LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(
	                        LinearLayout.LayoutParams.WRAP_CONTENT,
	                        LinearLayout.LayoutParams.WRAP_CONTENT);
					lP.setMargins(0, 10, 0, 0);
					tV.setLayoutParams(lP);
					EditText eT = new EditText(getActivity());
					eT.setKeyListener(null);
					//eT.setText(percentages.coverPercentages.get(cover).toString() + "%");
					eT.setText(displayFormat.format(percentages.coverPercentages.get(cover)) + "%");
					eT.setLayoutParams(new LinearLayout.LayoutParams(
	                        LinearLayout.LayoutParams.WRAP_CONTENT,
	                        LinearLayout.LayoutParams.WRAP_CONTENT));
					if (cover == StickSegment.Cover.COVER_7 ||
						cover == StickSegment.Cover.COVER_8 ||
						cover == StickSegment.Cover.COVER_9) { 
						coverArea.setVisibility(View.VISIBLE);
						coverArea.addView(tV);
						coverArea.addView(eT);
					} else {
						foliarCompositionArea.setVisibility(View.VISIBLE);
						foliarCompositionArea.addView(tV);
						foliarCompositionArea.addView(eT);
					}
				}
			}
		}
		
		//total cover is 100 - bare ground.  But if there was no bare ground above, this will not have been set and will not be visible
		if (!bareShown) {
			totalCoverArea.setVisibility(View.VISIBLE);
			totalCoverET.setText(displayFormat.format(100) + "%");			
		}

		
		for (Segment.Height height : Segment.Height.values()) {
			if (percentages.heightPercentages.get(height) != null) {
				//add label and value views to coverArea
				TextView tV = new TextView(getActivity());
				tV.setText(height.getDisplayName());
				LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
				lP.setMargins(0, 10, 0, 0);
				tV.setLayoutParams(lP);
				EditText eT = new EditText(getActivity());
				eT.setText(displayFormat.format(percentages.heightPercentages.get(height)) + "%");
				eT.setKeyListener(null);
				eT.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
				heightArea.addView(tV);
				heightArea.addView(eT);
			}
		}
		
		if (percentages.canopyGapPercentage != null) {
			canopyGapET.setText(displayFormat.format(percentages.canopyGapPercentage) + "%");
		}

		if (percentages.basalGapPercentage != null) {
			basalGapET.setText(displayFormat.format(percentages.basalGapPercentage) + "%");
		}
		
		if (percentages.species1Density != null) {
			Spanned s =  Html.fromHtml(displayFormat.format(percentages.species1Density) + "/m<sup><small>2</small></sup>");
			species1DensityET.setText(s);
		}

		if (percentages.species2Density != null) {
			Spanned s =  Html.fromHtml(displayFormat.format(percentages.species2Density) + "/m<sup><small>2</small></sup>");
			species2DensityET.setText(s);
		}

		return rootView;
	}
	
	private class PercentageReturn {
		public Map<StickSegment.Cover, Double> coverPercentages = new HashMap<StickSegment.Cover, Double>();
		public Map<Segment.Height, Double> heightPercentages = new HashMap<Segment.Height, Double>();
		public Double foliarCoverPercentage;
		public Double nonfoliarCoverPercentage;
		public Double basalGapPercentage;
		public Double canopyGapPercentage;
		public Double species1Density;
		public Double species2Density;
	}
		
	private PercentageReturn calculatePercentages() {
		PercentageReturn returnStructure = new PercentageReturn();
		int basalGapCount = 0, canopyGapCount = 0, species1Count = 0, species2Count = 0, foliarCoverCount = 0, nonfoliarCoverCount = 0;
		int[] heightCounts = new int[Segment.Height.values().length];
		int[] coverCounts = new int[StickSegment.Cover.values().length];
		RHMDatabaseAdapter db = RHMApplication.getInstance().getDatabaseAdapter();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date date = null;
		try { date = sdf.parse(((SiteSummaryActivity)getActivity()).date); } catch (Exception eX) {/*log*/}
		for (Transect.Direction direction : Transect.Direction.values()) {
			Transect transect = db.getTransect(((SiteSummaryActivity)getActivity()).siteID, direction);
			if (transect != null) {
				for (Segment.Range range : Segment.Range.values()) {
					Segment segment = db.getSegment(range, transect.ID, date);
					if (segment != null) {
						if (segment.basalGap) {
							++basalGapCount;
						}
						
						if (segment.canopyGap) {
							++canopyGapCount;
						}
						
						if (segment.species1Count != null) {
							species1Count += segment.species1Count;
						}

						if (segment.species2Count != null) {
							species2Count += segment.species2Count;
						}

						for (Segment.Height height : Segment.Height.values()) {
							if (segment.canopyHeight == height) {
								++heightCounts[height.ordinal()];
								break;
							}
						}

						for (StickSegment stickSegment : segment.stickSegments) {
							boolean foliar = false;
							for (StickSegment.Cover cover : StickSegment.Cover.values()) {
								if (stickSegment.covers[cover.ordinal()]) {
									++coverCounts[cover.ordinal()];
									if (cover != StickSegment.Cover.COVER_1) {
										foliar = foliar ||
												 (cover != StickSegment.Cover.COVER_7 &&
												  cover != StickSegment.Cover.COVER_8 &&
												  cover != StickSegment.Cover.COVER_9);
									} else {
										break; //not interested in others if bare is true
									}
								}
							}
							if (foliar) {
								++foliarCoverCount;
							}
						}
						
					}
				}
			}
		}
		
		returnStructure.basalGapPercentage = (basalGapCount/NUM_SEGMENTS) * 100;
		returnStructure.canopyGapPercentage = (canopyGapCount/NUM_SEGMENTS) * 100;
		
		if (species1Count != 0) {
			returnStructure.species1Density = (species1Count/NUM_SEGMENTS);
		}

		if (species2Count != 0) {
			returnStructure.species2Density = (species2Count/NUM_SEGMENTS);
		}

		for (Segment.Height height : Segment.Height.values()) {
			if (heightCounts[height.ordinal()] != 0) {
				returnStructure.heightPercentages.put(height, (double)(heightCounts[height.ordinal()]/NUM_SEGMENTS) * 100);
			}
		}

		for (StickSegment.Cover cover : StickSegment.Cover.values()) {
			if (coverCounts[cover.ordinal()] != 0) {
				returnStructure.coverPercentages.put(cover, (double)(coverCounts[cover.ordinal()]/NUM_STICK_SEGMENTS) * 100);
			}
		}
		returnStructure.foliarCoverPercentage = (double)(foliarCoverCount/NUM_STICK_SEGMENTS) * 100;
		returnStructure.nonfoliarCoverPercentage = (double)(nonfoliarCoverCount/NUM_STICK_SEGMENTS) * 100;

		return returnStructure;
	}
	
	@Override
	public View getHelpView() {
		ScrollView sV = new ScrollView(getActivity());
		TextView tV = new TextView(getActivity());
		tV.setPadding(10,10,10,10);
		tV.setText(Html.fromHtml(getString(R.string.fragment_summary_help)));
		sV.addView(tV);
		return sV;
	}

}
