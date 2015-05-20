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
 * SiteDetailFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.noisyflowers.landpks.android.LandPKSContract;
import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.noisyflowers.rangelandhealthmonitor.android.activities.SiteDetailActivity;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SiteListActivity;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SiteSummaryActivity;
import com.noisyflowers.rangelandhealthmonitor.android.activities.TransectActivity;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMRestClient;
import com.noisyflowers.rangelandhealthmonitor.android.dummy.DummyContent;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;

/**
 * A fragment representing a single TransectSegment detail screen. This fragment
 * is either contained in a {@link SiteListActivity} in two-pane mode
 * (on tablets) or a {@link SiteDetailActivity} on handsets.
 */
public class SiteDetailFragment extends Fragment implements IHelp, OnClickListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	public static final int LANDPKS_REQUEST_CODE = 1;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	private Button siteCharacterizationButton, northButton, southButton, eastButton, westButton, submitButton, summaryButton;
	private Spinner dateSpinner;
	private TextView statusTV;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SiteDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_site_detail, container, false);

		// Show the dummy content as text in a TextView.
		/**
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.site_detail))
					.setText(mItem.content);
		}
		**/
		statusTV = (TextView)rootView.findViewById(R.id.fragment_site_detail_textview_status);
		siteCharacterizationButton = (Button)rootView.findViewById(R.id.fragment_site_detail_button_site_characterization);
		siteCharacterizationButton.setOnClickListener(this);
		northButton = (Button)rootView.findViewById(R.id.fragment_site_detail_button_transect_north);
		northButton.setOnClickListener(this);
		southButton = (Button)rootView.findViewById(R.id.fragment_site_detail_button_transect_south);
		southButton.setOnClickListener(this);
		eastButton = (Button)rootView.findViewById(R.id.fragment_site_detail_button_transect_east);
		eastButton.setOnClickListener(this);
		westButton = (Button)rootView.findViewById(R.id.fragment_site_detail_button_transect_west);
		westButton.setOnClickListener(this);

		submitButton = (Button)rootView.findViewById(R.id.fragment_site_detail_button_submit);
		submitButton.setOnClickListener(this);
		submitButton.setEnabled(false); 

		summaryButton = (Button)rootView.findViewById(R.id.fragment_site_detail_button_summary);
		summaryButton.setOnClickListener(this);

		dateSpinner = (Spinner)rootView.findViewById(R.id.fragment_site_detail_spinner_date);
		/***
		if (((SiteDetailActivity)getActivity()).siteID == null) {
			dateSpinner.setVisibility(View.GONE);
			rootView.findViewById(R.id.fragment_site_detail_textview_date_label).setVisibility(View.GONE);
		} else {
		***/
		if (((SiteDetailActivity)getActivity()).siteID != null) {
	        //ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item);
	        ArrayAdapter<String> adapter = new DateSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        adapter.add(getString(R.string.fragment_site_detail_spinner_choose_date));
			//Long transectID = RHMApplication.getInstance().getDatabaseAdapter().getsertTransectID(((SiteDetailActivity)getActivity()).siteID, Transect.Direction.NORTH);
			Transect transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(((SiteDetailActivity)getActivity()).siteID, Transect.Direction.NORTH);
			List<String> dates = RHMApplication.getInstance().getDatabaseAdapter().getSegmentDates(transect.ID);
			if (dates.size() > 0) { 
				dateSpinner.setVisibility(View.VISIBLE);
				rootView.findViewById(R.id.fragment_site_detail_textview_date_label).setVisibility(View.VISIBLE);
		        adapter.addAll(dates);
		        dateSpinner.setAdapter(adapter);
		        dateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    	@Override
			    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			    		String date = (String)parent.getAdapter().getItem(pos);
			    		if (!getString(R.string.fragment_site_detail_spinner_choose_date).equals(date)) {
							Intent detailIntent = new Intent(getActivity(), SiteDetailActivity.class);
							detailIntent.putExtra(SiteDetailActivity.SITE_NAME, ((SiteDetailActivity)getActivity()).siteName);
							detailIntent.putExtra(SiteDetailActivity.SITE_ID, ((SiteDetailActivity)getActivity()).siteID);
							detailIntent.putExtra(SiteDetailActivity.DATE, date);
							//detailIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							//detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							getActivity().finish();
							startActivity(detailIntent);
			    		}
			    	}
		
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
		        });
			}
		}
		return rootView;
	}

	
	@Override
	public void onResume() {
		if (((SiteDetailActivity)getActivity()).siteID == null) {
			northButton.setEnabled(false);
			southButton.setEnabled(false);
			eastButton.setEnabled(false);
			westButton.setEnabled(false);
		} else {
			Date date;
			try {date = sdf.parse(((SiteDetailActivity)getActivity()).date);} catch (Exception e) {date = new Date();}
			Transect transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(((SiteDetailActivity)getActivity()).siteID, Transect.Direction.NORTH);
			boolean northComplete = transect.isComplete(date);
			boolean northSubmitted = transect.isSubmitted(date);
			boolean northUploaded = transect.isUploaded(date);
			transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(((SiteDetailActivity)getActivity()).siteID, Transect.Direction.SOUTH);
			boolean southComplete = transect.isComplete(date);
			transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(((SiteDetailActivity)getActivity()).siteID, Transect.Direction.EAST);
			boolean eastComplete = transect.isComplete(date);
			transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(((SiteDetailActivity)getActivity()).siteID, Transect.Direction.WEST);
			boolean westComplete = transect.isComplete(date);
			northButton.setEnabled(true);
			if (northComplete) {
				northButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_buttonless_on, 0);
			}
			southButton.setEnabled(true);
			if (southComplete) {
				southButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_buttonless_on, 0);
			}
			eastButton.setEnabled(true);
			if (eastComplete) {
				eastButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_buttonless_on, 0);
			}
			westButton.setEnabled(true);
			if (westComplete) {
				westButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_buttonless_on, 0);
			}
			
			if (northSubmitted) {
				showUploadStatus(northUploaded, sdf.format(date));
			} else if (northComplete && southComplete && eastComplete && westComplete) {
				submitButton.setEnabled(true); 
			}
		}
		super.onResume();
	}

	private void showUploadStatus(boolean uploaded, String date) {
		((SiteDetailActivity)getActivity()).date = date;
		submitButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_buttonless_on, 0);
		summaryButton.setVisibility(View.VISIBLE);
		if (uploaded) {
			statusTV.setText(getString(R.string.fragment_site_detail_status_uploaded));
		} else {
			statusTV.setText(getString(R.string.fragment_site_detail_status_upload_pending));
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_site_detail_button_site_characterization: {
				//Toast.makeText(getActivity(), "launch LandPKS here", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(LandPKSContract.SITE_CHARACTERIZATION_ACTION); 
				intent.putExtra(LandPKSContract.SITE_CHARACTERIZATION_ACTION_RETURN_EXTRA_SITE_ID, ((SiteDetailActivity)getActivity()).siteID);
				PackageManager packageManager = getActivity().getPackageManager();
				List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
				boolean isIntentSafe = activities.size() > 0;

				// Start an activity if it's safe
				if (isIntentSafe) {				
					getActivity().startActivityForResult(intent, LANDPKS_REQUEST_CODE);
				} else {
					Toast.makeText(getActivity(), getString(R.string.SiteDetailFragment_LandPKS_not_found), Toast.LENGTH_LONG).show();					
				}
			}
			break;
			//TODO: lot of redundancy below; can it be consolidated?
			case R.id.fragment_site_detail_button_transect_north: {
				//Toast.makeText(getActivity(), "North transect here", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getActivity(), TransectActivity.class);
				intent.putExtra(TransectActivity.SITE_ID, ((SiteDetailActivity)getActivity()).siteID); 
				intent.putExtra(TransectActivity.SITE_NAME, ((SiteDetailActivity)getActivity()).siteName); 
				intent.putExtra(TransectActivity.DATE, ((SiteDetailActivity)getActivity()).date); 
				intent.putExtra(TransectActivity.TRANSECT_DIRECTION, Transect.Direction.NORTH.name()); //TODO: put in strings.xml
				startActivity(intent);
			}
			break;
			case R.id.fragment_site_detail_button_transect_south: {
				//Toast.makeText(getActivity(), "South transect here", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getActivity(), TransectActivity.class);
				intent.putExtra(TransectActivity.SITE_ID, ((SiteDetailActivity)getActivity()).siteID); 
				intent.putExtra(TransectActivity.SITE_NAME, ((SiteDetailActivity)getActivity()).siteName); 
				intent.putExtra(TransectActivity.DATE, ((SiteDetailActivity)getActivity()).date); 
				intent.putExtra(TransectActivity.TRANSECT_DIRECTION, Transect.Direction.SOUTH.name()); 
				startActivity(intent);
			}
			break;
			case R.id.fragment_site_detail_button_transect_east: {
				//Toast.makeText(getActivity(), "East transect here", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getActivity(), TransectActivity.class);
				intent.putExtra(TransectActivity.SITE_ID, ((SiteDetailActivity)getActivity()).siteID); 
				intent.putExtra(TransectActivity.SITE_NAME, ((SiteDetailActivity)getActivity()).siteName); 
				intent.putExtra(TransectActivity.DATE, ((SiteDetailActivity)getActivity()).date); 
				intent.putExtra(TransectActivity.TRANSECT_DIRECTION, Transect.Direction.EAST.name()); 
				startActivity(intent);
			}
			break;
			case R.id.fragment_site_detail_button_transect_west: {
				//Toast.makeText(getActivity(), "West transect here", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getActivity(), TransectActivity.class);
				intent.putExtra(TransectActivity.SITE_ID, ((SiteDetailActivity)getActivity()).siteID); 
				intent.putExtra(TransectActivity.SITE_NAME, ((SiteDetailActivity)getActivity()).siteName); 
				intent.putExtra(TransectActivity.DATE, ((SiteDetailActivity)getActivity()).date); 
				intent.putExtra(TransectActivity.TRANSECT_DIRECTION, Transect.Direction.WEST.name()); 
				startActivity(intent);
			}
			break;

			case R.id.fragment_site_detail_button_submit: {
				//Toast.makeText(getActivity(), "Data upload not yet implemented", Toast.LENGTH_LONG).show();
				Transect transect = RHMApplication.getInstance().getDatabaseAdapter().getTransect(((SiteDetailActivity)getActivity()).siteID, Transect.Direction.NORTH);
				new UploadTransectTask(getActivity(), transect).execute();
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    	builder.setMessage(getString(R.string.fragment_site_detail_submitted_message))
		    		.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			    			@Override
			    			public void onClick(DialogInterface dialog, int which) {
			    				String date = sdf.format(new Date());
			    				showUploadStatus(false, date);
			    			}
		    			});	
		    	AlertDialog alert = builder.create();
		    	alert.show(); 		
			}
			break;
			case R.id.fragment_site_detail_button_summary: {
				//Toast.makeText(getActivity(), "Summary not yet implemented", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getActivity(), SiteSummaryActivity.class);
				intent.putExtra(SiteSummaryActivity.SITE_ID, ((SiteDetailActivity)getActivity()).siteID); 
				intent.putExtra(SiteSummaryActivity.SITE_NAME, ((SiteDetailActivity)getActivity()).siteName); 
				intent.putExtra(SiteSummaryActivity.DATE, ((SiteDetailActivity)getActivity()).date); 
				startActivity(intent);
			}
			break;
			
		}
	}
	
    private class UploadTransectTask extends AsyncTask<Void, Void, Boolean> {
        Context context;
        Transect transect;
		ProgressDialog progressDialog;

    	public UploadTransectTask(Context context, Transect transect){
    		this.context = context;
    		this.transect = transect;
    	}
    	
		protected void onPreExecute() {
			//progressDialog = ProgressDialog.show(context, "", "Uploading transect data...", true);
		}
        
    	protected Boolean doInBackground(Void... params) {
    		//TODO: check if transect already uplaoded for this date and pop dialog if so.
    		//RHMApplication.getInstance().getDatabaseAdapter().markSegmentsForUpload(Long.parseLong(((SiteDetailActivity)getActivity()).siteID), new Date());
    		RHMApplication.getInstance().getDatabaseAdapter().markTransectsForUpload(Long.parseLong(((SiteDetailActivity)getActivity()).siteID), new Date());
    		//RHMRestClient restClient = RHMRestClient.getInstance(context);
    		//transect = restClient.putTransect(transect, new Date());
    		//RHMApplication.getInstance().getDatabaseAdapter().uploadTransectData();
			return false;
    	}
    	
        protected void onPostExecute(Boolean success) {
        	//progressDialog.dismiss();
			submitButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_buttonless_on, 0);
			submitButton.setEnabled(false);

        }
	
    }
    
	@Override
	public View getHelpView() {
		ScrollView sV = new ScrollView(getActivity());
		TextView tV = new TextView(getActivity());
		tV.setPadding(10,10,10,10);
		tV.setText(Html.fromHtml(getString(R.string.fragment_site_detail_help)));
		sV.addView(tV);
		return sV;
	}

	
    private class DateSpinnerAdapter extends ArrayAdapter {

    	Context context;
    	
		public DateSpinnerAdapter(Context context, int resource) {
			super(context, resource);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return super.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			//return super.getDropDownView(position, convertView, parent);
			/***
			View view = super.getDropDownView(position, convertView, parent);
			if (position == 0) {
				view.setVisibility(View.GONE);
			}
			return view;
			***/
			LayoutInflater inflater = LayoutInflater.from(context); 
			View mySpinnerItem = inflater.inflate(R.layout.date_spinner_item, parent, false); 
			
			TextView v = (TextView)mySpinnerItem.findViewById(R.id.date_spinner_text_view);
			if (position == 0) {
				v.setVisibility(View.GONE);
			} else {
				v.setVisibility(View.VISIBLE);
				v.setText((String)super.getItem(position));
			}
			
			return mySpinnerItem;
			
		}
    	
    }
}
