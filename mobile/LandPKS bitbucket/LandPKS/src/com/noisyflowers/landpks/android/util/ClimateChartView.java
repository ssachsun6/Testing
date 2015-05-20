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
 * com.noisyflowers.landpks.android.util
 * ClimateChartView.java
 */

package com.noisyflowers.landpks.android.util;

import org.afree.chart.AFreeChart;
import org.afree.chart.LegendItem;
import org.afree.chart.axis.CategoryAxis;
import org.afree.chart.axis.CategoryLabelPositions;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.CategoryPlot;
import org.afree.chart.plot.DatasetRenderingOrder;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.renderer.category.BarRenderer;
import org.afree.chart.renderer.category.CategoryItemRenderer;
import org.afree.chart.renderer.category.LineAndShapeRenderer;
import org.afree.chart.title.LegendTitle;
import org.afree.data.category.CategoryDataset;
import org.afree.data.category.DefaultCategoryDataset;
import org.afree.graphics.GradientColor;
import org.afree.graphics.SolidColor;
import org.afree.graphics.geom.Font;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.MonthlyClimate;
import com.noisyflowers.landpks.android.model.Plot;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class ClimateChartView extends ChartView {
	
	public enum Month {
		MONTH_1 (R.string.climatechartview_month_1),
		MONTH_2 (R.string.climatechartview_month_2),
		MONTH_3 (R.string.climatechartview_month_3),
		MONTH_4 (R.string.climatechartview_month_4),
		MONTH_5 (R.string.climatechartview_month_5),
		MONTH_6 (R.string.climatechartview_month_6),
		MONTH_7 (R.string.climatechartview_month_7),
		MONTH_8 (R.string.climatechartview_month_8),
		MONTH_9 (R.string.climatechartview_month_9),
		MONTH_10 (R.string.climatechartview_month_10),
		MONTH_11 (R.string.climatechartview_month_11),
		MONTH_12 (R.string.climatechartview_month_12);
		
		public final int name;
		
		Month(int name) {
			this.name = name;
		}
	}

	Context context;
	
    public ClimateChartView(Context context) {
        this(context, null);
    }

    public ClimateChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        
        DefaultCategoryDataset precipDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset maxTempDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset minTempDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset avgTempDataset = new DefaultCategoryDataset();
        
        double maxPrecip = 0;
        double maxTemp = 0;
        Plot plot = LandPKSApplication.getInstance().getPlot();
        if (plot.monthlyClimates != null) {
        	for (MonthlyClimate mC : plot.monthlyClimates) {
        		String month = context.getString(Month.values()[mC.month-1].name);
        		/***
        		String month = "";
        		switch (mC.month) {
        			case 1:
        				month = "Jan";
        				break;
        			case 2:
	    				month = "Feb";
        				break;
        			case 3:
	    				month = "Mar";
        				break;
        			case 4:
	    				month = "Apr";
        				break;
        			case 5:
	    				month = "May";
        				break;
        			case 6:
	    				month = "Jun";
        				break;
        			case 7:
	    				month = "Jul";
        				break;
        			case 8:
	    				month = "Aug";
        				break;
        			case 9:
	    				month = "Sep";
        				break;
        			case 10:
	    				month = "Oct";
        				break;
        			case 11:
	    				month = "Now";
        				break;
        			case 12:
	    				month = "Dec";
        				break;        		
        		}
        		***/
        		
        		maxPrecip = mC.precipitation > maxPrecip ? mC.precipitation : maxPrecip;
        		maxTemp = mC.maxTemp > maxTemp ? mC.maxTemp : maxTemp;
        		precipDataset.addValue(mC.precipitation, context.getString(R.string.climatechartview_precip), month);
        		maxTempDataset.addValue(mC.maxTemp, context.getString(R.string.climatechartview_max_temp), month);
        		minTempDataset.addValue(mC.minTemp, context.getString(R.string.climatechartview_min_temp), month);
        		avgTempDataset.addValue(mC.avgTemp, context.getString(R.string.climatechartview_avg_temp), month);
        	}
        }

        CategoryItemRenderer maxTempRenderer = new LineAndShapeRenderer();
        maxTempRenderer.setSeriesPaintType(0, new SolidColor(Color.RED));
        CategoryItemRenderer minTempRenderer = new LineAndShapeRenderer();
        minTempRenderer.setSeriesPaintType(0, new SolidColor(Color.BLUE));
        CategoryItemRenderer avgTempRenderer = new LineAndShapeRenderer();
        avgTempRenderer.setSeriesPaintType(0, new SolidColor(Color.MAGENTA));
        CategoryItemRenderer precipRenderer = new BarRenderer();
        precipRenderer.setSeriesPaintType(0, new SolidColor(Color.rgb(0x33,0xcc,0xff)));
                
        CategoryPlot catPlot = new CategoryPlot();
        catPlot.setOrientation(PlotOrientation.VERTICAL);
        catPlot.setRangeGridlinesVisible(true);
        catPlot.setDomainGridlinesVisible(true);
        catPlot.setDomainAxis(new CategoryAxis(context.getString(R.string.climatechartview_month_axis)));
        ValueAxis tempRangeAxis = new NumberAxis(context.getString(R.string.climatechartview_temp_axis));
        tempRangeAxis.setRange(0, maxTemp);
        catPlot.setRangeAxis(0, tempRangeAxis);
        catPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        ValueAxis precipRangeAxis = new NumberAxis(context.getString(R.string.climatechartview_precip_axis));
        precipRangeAxis.setRange(0, maxPrecip); //TODO: figure out why I am having to do this manually
        catPlot.setRangeAxis(1, precipRangeAxis);
        
        catPlot.setDataset(maxTempDataset);
        maxTempRenderer.setBaseItemLabelsVisible(true);
        catPlot.setRenderer(maxTempRenderer);
        
        catPlot.setDataset(1, minTempDataset);
        catPlot.setRenderer(1, minTempRenderer);

        catPlot.setDataset(2, avgTempDataset);
        catPlot.setRenderer(2, avgTempRenderer);

        catPlot.setDataset(3, precipDataset);
        catPlot.setRenderer(3, precipRenderer);
        catPlot.mapDatasetToRangeAxis(3, 1);
        
        AFreeChart chart = new AFreeChart(catPlot);  
        chart.setTitle(context.getString(R.string.climatechartview_title));
        
        //TODO: I'm not very happy with this. I need the fonts to adjust for screen size.  Surely there is a better way than this.
		Font chartTitleFont = new Font("Dialog", Typeface.BOLD, 40); 
		Font axisLabelFont = new Font("Dialog", Typeface.BOLD, 30); 
		Font axisTickLabelFont = new Font("Dialog", Typeface.NORMAL, 30); 
		DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
		if (dm.widthPixels < 300) {
			chartTitleFont = new Font("Dialog", Typeface.BOLD, 15); 
			axisLabelFont = new Font("Dialog", Typeface.BOLD, 10); 
			axisTickLabelFont = new Font("Dialog", Typeface.NORMAL, 10); 
		} else if (dm.widthPixels < 800) {
			chartTitleFont = new Font("Dialog", Typeface.BOLD, 30); 
			axisLabelFont = new Font("Dialog", Typeface.BOLD, 20); 
			axisTickLabelFont = new Font("Dialog", Typeface.NORMAL, 20); 
		}
        chart.getTitle().setFont(chartTitleFont);
        catPlot.getDomainAxis().setLabelFont(axisLabelFont);
        catPlot.getRangeAxis().setLabelFont(axisLabelFont);
        catPlot.getRangeAxis(1).setLabelFont(axisLabelFont);
        catPlot.getDomainAxis().setTickLabelFont(axisTickLabelFont);
        catPlot.getRangeAxis().setTickLabelFont(axisTickLabelFont);
        catPlot.getRangeAxis(1).setTickLabelFont(axisTickLabelFont);
        LegendTitle legendTitle = chart.getLegend();
        legendTitle.setItemFont(axisTickLabelFont);
        
        chart.setBackgroundPaintType(new SolidColor(Color.WHITE));
                
        setChart(chart);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	return true;
    }

    /*** Play with this to enlarge legend images
    private static class MyLineAndShapeRenderer extends LineAndShapeRenderer {

        @Override
        public LegendItem getLegendItem(int dataset, int series) {
            LegendItem legendItem = super.getLegendItem(dataset, series);
            //System.out.println(dataset + " " + series + " " + legendItem.getShape());
            // modify legendItem here
            legendItem.
            return legendItem;
        }
    }
    ***/

}
