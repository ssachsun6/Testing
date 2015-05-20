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
 * MinMaxTextWatcher.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.util;

import android.text.Editable;
import android.text.TextWatcher;

	public class MinMaxTextWatcher implements TextWatcher {
		
		//boolean ignore = false;
		double min, max;
		
		boolean intRange = false;
		
		public MinMaxTextWatcher(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public MinMaxTextWatcher(int min, int max) {
			this.min = min;
			this.max = max;
			intRange = true;
		}
		
    	@Override
    	public void afterTextChanged(Editable s) {
    		//if (!ignore) {
	    		try {
	    			double d = Double.parseDouble(s.toString());
	    			if (d > max) {
	    				if (intRange) {
	    					s.replace(0, s.length(), Integer.toString(Math.round((float)max)));	    					
	    				} else {
	    					s.replace(0, s.length(), Double.toString(max));
		    	    		//ignore = true;
	    				}
	    			} else if (d < min) {
	    				if (intRange) {
	    					s.replace(0, s.length(), Integer.toString(Math.round((float)min)));        					    					
	    				} else {
	    					s.replace(0, s.length(), Double.toString(min));        				
	    					//ignore = true;
	    				}
	    			}
	    		} catch (NumberFormatException e) {}
    		//} else {
    		//	ignore = false;
    		//}
    	}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1,
				int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
        	
	}

