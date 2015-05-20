/**
 * 
 * Copyright 2015 Noisy Flowers LLC
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
 * LegalNoticesFragment.java
 */
package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.noisyflowers.rangelandhealthmonitor.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Douglas Meredith
 *
 */
public class LegalNoticesFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_legalnotices, container, false);
		
		//ViewGroup vG = ((ViewGroup)root.findViewById(R.id.fragment_legalnotices_mainlayout));
		
		//TextView tV = new TextView(getActivity());
		//tV.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getActivity()));
		//tV.setAutoLinkMask(Linkify.WEB_URLS);
		//tV.setText(Html.fromHtml(getString(R.string.legalnotice01)));
		//vG.addView(tV);
		TextView tV = ((TextView)root.findViewById(R.id.fragment_legalnotices_notice_area));
		tV.setMovementMethod(LinkMovementMethod.getInstance());
		tV.setText(Html.fromHtml(getString(R.string.legalnotice01)));

		
		return root;
	}


}
