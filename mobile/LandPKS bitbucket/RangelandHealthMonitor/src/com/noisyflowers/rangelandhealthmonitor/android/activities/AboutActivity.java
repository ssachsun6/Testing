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
 * com.noisyflowers.rangelandhealthmonitor.android.activities
 * AboutActivity.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.activities;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.util.RHMPagerAdapter;

public class AboutActivity extends ActionBarActivity /*implements View.OnLongClickListener */{
	private static final String TAG = AboutActivity.class.getName(); 

	private RHMPagerAdapter pagerAdapter;
	private ViewPager pager;

	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
        
    	List<Fragment> fragments = RHMApplication.getInstance().getAboutFragments();

        pager = (ViewPager)super.findViewById(R.id.activity_about_pager);
        pager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    } 
                });

        ActionBar actionBar = getSupportActionBar();
    	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle(getString(R.string.title_activity_about)); 

	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	            imm.hideSoftInputFromWindow(pager.getApplicationWindowToken(), 0);
	            pager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };

	    for (Fragment f: fragments) {
	    	String name = "";
			try {
				name = f.getArguments().getString("Name");
			} catch (Exception ex) {}
	    	
			ActionBar.Tab tab = actionBar.newTab().setText(name).setTabListener(tabListener);

			actionBar.addTab(tab);
	    }
	    
        this.pagerAdapter  = new RHMPagerAdapter(super.getSupportFragmentManager(), fragments);
        pager.setAdapter(this.pagerAdapter);
        pager.setCurrentItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.about, menu);
		//return true;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				//NavUtils.navigateUpTo(this, new Intent(this, PlotListActivity.class));
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
	
}
