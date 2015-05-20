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
 * com.noisyflowers.rangelandhealthmonitor.android.util
 * RHMPagerAdapter.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.util;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class RHMPagerAdapter extends FragmentPagerAdapter {
	 
    private List<Fragment> fragments;
    
    /**
     * @param fm
     * @param fragments
     */
    public RHMPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
		//Plot plot = LandPKSApplication.getInstance().getPlot();
    	//if (plot.name == null || "".equals(plot.name)) return this.fragments.get(0);
        return this.fragments.get(position);
    }
 
    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
