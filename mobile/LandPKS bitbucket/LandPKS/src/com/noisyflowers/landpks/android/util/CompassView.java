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
 * CompassView.java
 */

package com.noisyflowers.landpks.android.util;

import com.noisyflowers.landpks.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CompassView extends View/*ImageView*/ {

	private static final String TAG = CompassView.class.getSimpleName();
	
	private Context context;
	
	private int direction;
	
	private Paint arrowPaint = new Paint();
	private Bitmap roseBitmap;
	
	private RectF rimRect, ringRect, scaleRect;
	private Paint backgroundPaint; 
	private Paint rimCirclePaint, scalePaint, scalePaintMinor;
	private Paint ringFillPaint;
	private Bitmap checkMap;
	
	// scale configuration
	private static final int totalNicks = 180;
	private static final float degreesPerNick = 360.0f / totalNicks;	
	private static final int centerDegree = 0; // the one in the top center (12 o'clock)
	private static final int minDegrees = 0;
	private static final int maxDegrees = 359;
	
	private boolean northChecked = false;
	private boolean eastChecked = false;
	private boolean southChecked = false;
	private boolean westChecked = false;

	private boolean floating = true;
	
	/***
	//TODO: if ImageView
	private void initView() {
		arrowPaint.setColor(Color.RED);
		arrowPaint.setStyle(Style.STROKE);
		arrowPaint.setStrokeWidth(3);
		arrowPaint.setAntiAlias(true);
		
		this.setImageResource(R.drawable.compass_rose); 
		
	}
	***/
	
	public CompassView(Context context) {
		super(context);
		this.context = context;
		//initView(); //TODO: if ImageView
		//this.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //required for canvas scaling to work properly
		initDrawingTools();
	}

	public CompassView(Context context, AttributeSet attrS) {
		super(context, attrS);
		this.context = context;
		//initView(); //TODO: if ImageView
		//this.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //required for canvas scaling to work properly
		initDrawingTools();
	}
	
	public void setFloating(boolean b) {
		floating = b;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
		this.invalidate();
	}
	
	public int getDirection() {
		return direction;
	}
	
	/***
	//TODO: if ImageView
	 * @Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		int centerX = width/2;
		int centerY = height/2;
		canvas.drawLine(centerX, centerY, centerX, Math.round(centerY*0.25), paint);
		canvas.rotate(-direction, centerX, centerY);
		super.onDraw(canvas);
	}
	***/
	
	private void initDrawingTools() {
		rimRect = new RectF(0.1f, 0.1f, 0.9f, 0.9f);

		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.TRANSPARENT);
		//backgroundPaint.setAntiAlias(true);
		//backgroundPaint.setColor(Color.BLUE);
		backgroundPaint.setStyle(Paint.Style.FILL);
		
		float rimSize = 0.01f;
		rimCirclePaint = new Paint();
		rimCirclePaint.setAntiAlias(true);
		rimCirclePaint.setStyle(Paint.Style.STROKE);
		rimCirclePaint.setColor(Color.BLACK);
		rimCirclePaint.setStrokeWidth(rimSize);


		float ringSize = 0.1f;
		ringFillPaint = new Paint();
		ringFillPaint.setAntiAlias(true);
		ringFillPaint.setStyle(Paint.Style.STROKE);
		//ringFillPaint.setColor(Color.WHITE);
		ringFillPaint.setColor(Color.argb(0x7F, 0xC1, 0xC1, 0xC1));
		ringFillPaint.setStrokeWidth(ringSize);

		ringRect = new RectF();
		ringRect.set(rimRect.left + ringSize/2, rimRect.top + ringSize/2, 
			     rimRect.right - ringSize/2, rimRect.bottom - ringSize/2);		
		
		scalePaint = new Paint();
		scalePaint.setStyle(Paint.Style.STROKE);
		scalePaint.setColor(Color.BLACK);
		scalePaint.setStrokeWidth(0.005f);
		scalePaint.setAntiAlias(true);
		
		scalePaint.setTextSize(0.045f);
		scalePaint.setTypeface(Typeface.SANS_SERIF);
		scalePaint.setLinearText(true);
		//scalePaint.setTextScaleX(0.8f);
		scalePaint.setTextAlign(Paint.Align.CENTER);		
		
		//float scalePosition = 0.10f;
		scaleRect = new RectF();
		scaleRect.set(ringRect.left + ringSize/2, ringRect.top + ringSize/2,
						ringRect.right - ringSize/2, ringRect.bottom - ringSize/2);
		

		scalePaintMinor = new Paint();
		scalePaintMinor.setStyle(Paint.Style.STROKE);
		scalePaintMinor.setColor(Color.RED);
		scalePaintMinor.setStrokeWidth(0.005f);
		scalePaintMinor.setAntiAlias(true);
		
		arrowPaint.setColor(Color.RED);
		arrowPaint.setStyle(Style.STROKE);
		arrowPaint.setStrokeCap(Paint.Cap.ROUND);
		arrowPaint.setStrokeWidth(3);
		arrowPaint.setAntiAlias(true);
		
		checkMap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_check_buttonless_on);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
		//Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		
		setMeasuredDimension(chosenDimension, chosenDimension);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		int centerX = width/2;
		int centerY = height/2;
		
		if (floating) {
			canvas.drawLine(centerX, centerY, centerX, Math.round(centerY*0.25), arrowPaint);
			canvas.rotate(-direction, centerX, centerY);
		} else {
			if (direction == 0)
				canvas.drawLine(centerX, centerY, centerX, Math.round(centerY*0.25), arrowPaint);
			else if (direction == 90)
				canvas.drawLine(centerX, centerY, Math.round(centerX*1.75), centerY, arrowPaint);
			else if (direction == 180)
				canvas.drawLine(centerX, centerY, centerX, Math.round(centerY*1.75), arrowPaint);
			else if (direction == 270)
				canvas.drawLine(centerX, centerY, Math.round(centerX*0.25), centerY, arrowPaint);			
		}


		float scale = (float) getWidth();		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		
		//canvas.drawRect(rimRect, rimCirclePaint);
		drawBackground(canvas);
		drawRingFill(canvas);
		drawOuterRim(canvas);
		drawScale(canvas);
		//drawChecks(canvas);
		
		canvas.restore();
	
		//canvas.drawLine(centerX, centerY, centerX, Math.round(centerY*0.25), arrowPaint);
		//canvas.rotate(-direction, centerX, centerY);
	}	

	private void drawBackground(Canvas canvas) {
		canvas.drawOval(rimRect, backgroundPaint);
	}
	
	private void drawOuterRim(Canvas canvas) {
		canvas.drawOval(rimRect, rimCirclePaint);
		//canvas.drawOval(new RectF(0.3f, 0.3f, 0.5f, 0.5f), rimCirclePaint);
		//canvas.drawLine(0, 0, 1, 1, rimCirclePaint);

	}
	
	private void drawRingFill(Canvas canvas) {
		canvas.drawOval(ringRect, ringFillPaint);
	}

	private void drawScale(Canvas canvas) {
		//canvas.drawRect(scaleRect, scalePaintMinor);
		canvas.drawOval(scaleRect, scalePaint);

		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		for (int i = 0; i < totalNicks; ++i) {
			float y1 = scaleRect.top;
			float y2 = y1 - 0.020f;
			float y3 = y1 - 0.040f;
			
			//canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaint);
			
			if (i % 5 == 0) {
				canvas.drawLine(0.5f, y1, 0.5f, y3, scalePaint);
				int value = nickToDegree(i);
				
				if (value >= minDegrees && value <= maxDegrees) {
					String valueString = Integer.toString(value);
					//canvas.drawText(valueString, 0.5f, y2 - 0.015f, scalePaint);
				}
			} else {
				canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaintMinor);
			}
			
			if (i == 0) {
				canvas.drawText("N", scaleRect.centerX(), scaleRect.top - 0.05f, scalePaint);
				if (northChecked) {
					Rect checkRectSrc = new Rect(0, 0, checkMap.getWidth(), checkMap.getHeight());
					RectF checkRectDst = new RectF(scaleRect.centerX() - 0.02f, scaleRect.top - 0.25f, scaleRect.centerX() + 0.05f, scaleRect.top - 0.05f);
					canvas.drawBitmap(checkMap, checkRectSrc, checkRectDst, null); 
				}
			} else if (i == 45) {
				canvas.drawText("E", scaleRect.centerX(), scaleRect.top - 0.05f, scalePaint);
				if (eastChecked) {
					Rect checkRectSrc = new Rect(0, 0, checkMap.getWidth(), checkMap.getHeight());
					RectF checkRectDst = new RectF(scaleRect.centerX() - 0.02f, scaleRect.top - 0.25f, scaleRect.centerX() + 0.05f, scaleRect.top - 0.05f);
					canvas.drawBitmap(checkMap, checkRectSrc, checkRectDst, null); 
				}
			} else if (i == 90) {
				canvas.drawText("S", scaleRect.centerX(), scaleRect.top - 0.05f, scalePaint);
				if (southChecked) {
					Rect checkRectSrc = new Rect(0, 0, checkMap.getWidth(), checkMap.getHeight());
					RectF checkRectDst = new RectF(scaleRect.centerX() - 0.02f, scaleRect.top - 0.25f, scaleRect.centerX() + 0.05f, scaleRect.top - 0.05f);
					canvas.drawBitmap(checkMap, checkRectSrc, checkRectDst, null); 
				}
			} else if (i == 135) {
				canvas.drawText("W", scaleRect.centerX(), scaleRect.top - 0.05f, scalePaint);
				if (westChecked) {
					Rect checkRectSrc = new Rect(0, 0, checkMap.getWidth(), checkMap.getHeight());
					RectF checkRectDst = new RectF(scaleRect.centerX() - 0.02f, scaleRect.top - 0.25f, scaleRect.centerX() + 0.05f, scaleRect.top - 0.05f);
					canvas.drawBitmap(checkMap, checkRectSrc, checkRectDst, null); 
				}
			}
			
			canvas.rotate(degreesPerNick, 0.5f, 0.5f);
		}
		canvas.restore();		
	}
	
	private int nickToDegree(int nick) {
		int rawDegree = ((nick < totalNicks / 2) ? nick : (nick - totalNicks)) * 2;
		int shiftedDegree = rawDegree + centerDegree;
		return shiftedDegree;
	}

	public void  setNorthChecked(boolean b) {
		northChecked = b;
	}

	public void  setEastChecked(boolean b) {
		eastChecked = b;
	}

	public void  setSouthChecked(boolean b) {
		southChecked = b;
	}

	public void  setWestChecked(boolean b) {
		westChecked = b;
	}

}
