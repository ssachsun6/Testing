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
 * LPKSBarChartView.java
 */

package com.noisyflowers.landpks.android.util;

import java.util.List;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.StandardChartTheme;
import org.afree.chart.axis.Axis;
import org.afree.chart.axis.CategoryAxis;
import org.afree.chart.axis.CategoryLabelPositions;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.plot.CategoryPlot;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.renderer.category.BarRenderer;
import org.afree.data.Range;
import org.afree.data.category.CategoryDataset;
import org.afree.data.category.DefaultCategoryDataset;
import org.afree.graphics.GradientColor;
import org.afree.graphics.PaintType;
import org.afree.graphics.SolidColor;
import org.afree.graphics.geom.Font;

import com.noisyflowers.landpks.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.ChartView;

public class LPKSBarChartView extends ChartView {
	private static final String TAG = LPKSBarChartView.class.getName(); 

	private static final int WARNING_BACKGROUND = Color.rgb(0xFF, 0xee, 0xee);
	
	private String chartTitle;
	private String rangeTitle;
	private int barColor1, barColor2, poiBarColor1, poiBarColor2, backgroundColor;
	
	private int poiColumn = -1;
	
	private int columnCount = 1;
	
    double max = 0;
    String flavor;

	Context context;
	
    public LPKSBarChartView(Context context) {
        this(context, null);
    }

    public LPKSBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setFlavor((String)getTag());
        CategoryDataset dataset = createDataset((String)getTag());
        AFreeChart chart = createChart(dataset);
        setChart(chart);
    }

    private void setFlavor(String tag) {
    	flavor = tag;
    	
    	
    	//default to Grass Prod because why not?
    	chartTitle = context.getString(R.string.lpksbarchartview_grass_productivity);
    	rangeTitle = context.getString(R.string.lpksbarchartview_productivity_percent);
    	barColor1 = Color.GREEN; 
    	barColor2 = Color.rgb(0, 0xAA, 0);
    	poiBarColor1 = Color.GREEN;
    	poiBarColor2 = Color.rgb(0, 64, 0);
    	backgroundColor = WARNING_BACKGROUND;

    	if ("cropProductivity".equals(tag)) {
        	chartTitle = context.getString(R.string.lpksbarchartview_crop_productivity);
    	} else if ("grassErosion".equals(tag)) {
        	chartTitle = context.getString(R.string.lpksbarchartview_grass_erosion);
        	rangeTitle = context.getString(R.string.lpksbarchartview_erosion_percent);
        	barColor1 = Color.rgb(0xFF, 0x99, 0x33); 
        	barColor2 = Color.rgb(0xFF, 0x80, 0);
        	poiBarColor1 = Color.rgb(0xFF, 0x99, 0x33); 
        	poiBarColor2 = Color.rgb(0x99, 0x4C, 0);
        	backgroundColor = WARNING_BACKGROUND;
    	} else if ("cropErosion".equals(tag)) {
        	chartTitle = context.getString(R.string.lpksbarchartview_crop_erosion);
        	rangeTitle = context.getString(R.string.lpksbarchartview_erosion_percent);
        	barColor1 = Color.rgb(0xFF, 0x99, 0x33); 
        	barColor2 = Color.rgb(0xFF, 0x80, 0);
        	poiBarColor1 = Color.rgb(0xFF, 0x99, 0x33); 
        	poiBarColor2 = Color.rgb(0x99, 0x4C, 0);
        	backgroundColor = WARNING_BACKGROUND;
    	} else if ("awc".equals(tag)) {
        	chartTitle = context.getString(R.string.lpksbarchartview_awc_title);
        	rangeTitle = context.getString(R.string.lpksbarchartview_awc_range);
        	barColor1 = Color.BLUE; 
        	barColor2 = Color.rgb(0, 0, 0xAA);
        	poiBarColor1 = Color.BLUE; 
        	poiBarColor2 = Color.rgb(0, 0, 64);
        	backgroundColor = Color.WHITE;
    	}
    }
    
    private CategoryDataset createDataset(String flavor) {
        String series = "whatever";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Plot> plots = LandPKSApplication.getInstance().getDatabaseAdapter().getAnalyzedPlots();
                
        //New version expects raw values, which are then scaled to percent of largest value
        //double max = 0;
        //first get max value for all plots
        for (Plot plot : plots) {
        	double value = 0;
        	if ("grassProductivity".equals(flavor)) {
        		if (plot.grassProductivity != null) {
        			value = plot.grassProductivity;
        		} 
        	} else if ("grassErosion".equals(flavor)) {
        		if (plot.grassErosion != null) {
        			value = plot.grassErosion;
        		}      		
        	} else if ("cropProductivity".equals(flavor)) {
        		if (plot.cropProductivity != null) {
        			value = plot.cropProductivity;
        		}       		
        	} else if ("cropErosion".equals(flavor)) {
        		if (plot.cropErosion != null) {
        			value = plot.cropErosion;
        		}      		
        	} else if ("awc".equals(flavor)) {
        		if (plot.awcSoilProfile != null) {
        			value = plot.awcSoilProfile;
        		}
        	}
			max = value > max ? value : max; 
        }

        if (columnCount == -1 || columnCount > plots.size()) {
        	columnCount = plots.size();
        }
        
        Plot currentPlot = LandPKSApplication.getInstance().getPlot();
        
        //String plotName = LandPKSApplication.getInstance().getPlot().name;
        
        //first, add the number of plots specified by columnCount
        boolean poiFound = false;
        for (int x = 0; x < columnCount ; x++ ) {
        	Plot plot = plots.get(x);
        	poiFound = poiFound || plot.name.equals(currentPlot.name);
        	if ("grassProductivity".equals(flavor)) {
        		if (plot.grassProductivity != null) {
        			dataset.addValue((plot.grassProductivity / max) * 100, series, plot.name); 
        		} 
        	} else if ("grassErosion".equals(flavor)) {
        		if (plot.grassErosion != null) {
        			dataset.addValue((plot.grassErosion / max) * 100, series, plot.name); 
        		}      		
        	} else if ("cropProductivity".equals(flavor)) {
        		if (plot.cropProductivity != null) {
        			dataset.addValue((plot.cropProductivity / max) * 100, series, plot.name); 
        		}       		
        	} else if ("cropErosion".equals(flavor)) {
        		if (plot.cropErosion != null) {
        			dataset.addValue((plot.cropErosion / max) * 100, series, plot.name); 
        		}      		
        	} else if ("awc".equals(flavor)) {
        		if (plot.awcSoilProfile != null) {
        			dataset.addValue(plot.awcSoilProfile, series, plot.name); 
        		}      		
        	}
        }
        
        //if plot of interest is not found in the plots added, remove the oldest (the last added) and replace with poi
        if (!poiFound && currentPlot.remoteID != null) {
        	if (dataset.getRowCount() > 0) {
        		dataset.removeColumn(dataset.getColumnCount() - 1);
        	}

        	if ("grassProductivity".equals(flavor)) {
        		if (currentPlot.grassProductivity != null) {
        			dataset.addValue((currentPlot.grassProductivity / max) * 100, series, currentPlot.name); 
        		} 
        	} else if ("grassErosion".equals(flavor)) {
        		if (currentPlot.grassErosion != null) {
        			dataset.addValue((currentPlot.grassErosion / max) * 100, series, currentPlot.name); 
        		}      		
        	} else if ("cropProductivity".equals(flavor)) {
        		if (currentPlot.cropProductivity != null) {
        			dataset.addValue((currentPlot.cropProductivity / max) * 100, series, currentPlot.name); 
        		}       		
        	} else if ("cropErosion".equals(flavor)) {
        		if (currentPlot.cropErosion != null) {
        			dataset.addValue((currentPlot.cropErosion / max) * 100, series, currentPlot.name); 
        		}      		
        	} else if ("awc".equals(flavor)) {
        		if (currentPlot.awcSoilProfile != null) {
        			dataset.addValue(currentPlot.awcSoilProfile, series, currentPlot.name); 
        		}      		
        	}
        	
        }
        
        poiColumn = dataset.getColumnIndex(currentPlot.name);
               
        return dataset;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	return true;
    }
    
    private AFreeChart createChart(CategoryDataset dataset) {
        AFreeChart chart = ChartFactory.createBarChart(
                chartTitle,       // chart title
                context.getString(R.string.lpksbarchartview_plot),
                rangeTitle,                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                false,                     // include legend
                true,                     // tooltips?
                false                     // URLs?
            );

        //StandardChartTheme.createJFreeTheme().apply(chart);
        
        //chart.setBackgroundPaintType(new SolidColor(Color.WHITE));
        chart.setBackgroundPaintType(new SolidColor(backgroundColor));
        
        if (backgroundColor == WARNING_BACKGROUND) {
        	chart.setBorderVisible(true);
        	chart.setBorderPaintType(new SolidColor(Color.RED));
        	chart.setBorderStroke(10);
        }
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
        plot.setBackgroundPaintType(new SolidColor(backgroundColor));
        
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
        plot.getDomainAxis().setLabelFont(axisLabelFont);
        plot.getRangeAxis().setLabelFont(axisLabelFont);
        plot.getDomainAxis().setTickLabelFont(axisTickLabelFont);
        plot.getRangeAxis().setTickLabelFont(axisTickLabelFont);
               
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        if ("awc".equals(flavor)) {
            rangeAxis.setRange(new Range(0,max));
        } else {
        	rangeAxis.setRange(new Range(0,100));
        }
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        //BarRenderer renderer = (BarRenderer) plot.getRenderer();
        //renderer.setDrawBarOutline(false);
        BarRenderer renderer = new CustomRenderer();
        plot.setRenderer(renderer);
              
        // set up gradient paints for series...
        //GradientColor gp0 = new GradientColor(Color.GREEN, Color.rgb(0, 64, 0));
        GradientColor gp0 = new GradientColor(barColor1, barColor2);
        renderer.setSeriesPaintType(0, gp0);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));

        return chart;
    }
    
    public void updateColumnCount(int columnCount) {
    	this.columnCount = columnCount;
        CategoryDataset dataset = createDataset((String)getTag());
        AFreeChart chart = createChart(dataset);
        setChart(chart);
    }
    
    class CustomRenderer extends BarRenderer
    {

       public CustomRenderer()
       {
       }

       @Override
       public PaintType getItemPaintType(final int row, final int column)
       {
    	   if (column == poiColumn) 
    		   return new GradientColor(poiBarColor2, poiBarColor1);
    	   else
    		   return super.getItemPaintType(row, column);
       }	
    }    
}
