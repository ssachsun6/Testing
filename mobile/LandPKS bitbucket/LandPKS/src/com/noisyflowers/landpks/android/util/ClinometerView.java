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
 * ClinometerView.java
 */

package com.noisyflowers.landpks.android.util;

import com.noisyflowers.landpks.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class ClinometerView extends View {
	
	private Context context;
	
	private float gravityDirection = 0;

	private Paint arrowPaint = new Paint();
	
	private RectF rimRect, ringRect, scaleRect;
	private Paint backgroundPaint; 
	private Paint rimCirclePaint, scalePaint, scalePaintMinor;
	private Paint ringFillPaint;
	private Bitmap checkMap;
	
	// scale configuration
	private static final int totalNicks = 90;
	private static final float degreesPerNick = 180.0f / totalNicks;	
	private static final int centerDegree = 0; // the one in the top center (12 o'clock)
	private static final int minDegrees = 0;
	private static final int maxDegrees = 359;

	public ClinometerView(Context context) {
		this(context, null);
	}

	public ClinometerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initDrawingTools();
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
		
		scalePaint.setTextSize(0.035f);
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

		scalePaintMinor.setTextSize(0.035f);
		scalePaintMinor.setTypeface(Typeface.SANS_SERIF);
		scalePaintMinor.setLinearText(true);
		//scalePaint.setTextScaleX(0.8f);
		scalePaintMinor.setTextAlign(Paint.Align.CENTER);		

		arrowPaint.setColor(Color.RED);
		arrowPaint.setStyle(Style.STROKE);
		arrowPaint.setStrokeCap(Paint.Cap.ROUND);
		arrowPaint.setStrokeWidth(3);
		arrowPaint.setAntiAlias(true);
		
		checkMap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_check_buttonless_on);
	}

	public void setGravityDirection(float direction) {
		this.gravityDirection = direction;
		this.invalidate();
	}
	
	public float getGravityDirection() {
		return gravityDirection;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		int centerX = width/2;
		int centerY = height/2;
		
		//canvas.drawLine(centerX, centerY, Math.round(centerX*0.25), centerY, arrowPaint);
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		//canvas.rotate(-gravityDirection, centerX, centerY);
		canvas.rotate((gravityDirection+90), centerX, centerY);
		canvas.drawLine(centerX, centerY, centerX, Math.round(centerY*1.75), arrowPaint);
		canvas.restore();
		//canvas.rotate(-gravityDirection, centerX, centerY);

		float scale = (float) getWidth();		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		
		drawBackground(canvas);
		drawRingFill(canvas);
		drawOuterRim(canvas);
		drawScale(canvas);
		
		canvas.restore();
		
	}	

	private void drawBackground(Canvas canvas) {
		//canvas.drawArc(rimRect, 0.0f, 180.0f, false, backgroundPaint);
		canvas.drawArc(rimRect, 90.0f, 180.0f, false, backgroundPaint);
	}
	
	private void drawOuterRim(Canvas canvas) {
		//canvas.drawArc(rimRect, 0.0f, 180.0f, false, rimCirclePaint);
		canvas.drawArc(rimRect, 90.0f, 180.0f, false, rimCirclePaint);
	}
	
	private void drawRingFill(Canvas canvas) {
		//canvas.drawArc(ringRect, 0.0f, 180.0f, false, ringFillPaint);
		canvas.drawArc(ringRect, 90.0f, 180.0f, false, ringFillPaint);
	}

	private void drawScale(Canvas canvas) {
		//canvas.drawArc(scaleRect, 0.0f, 180.0f, false, scalePaint);
		canvas.drawArc(scaleRect, 90.0f, 180.0f, false, scalePaint);

		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		//canvas.rotate(90.0f, 0.5f, 0.5f);
		canvas.rotate(180.0f, 0.5f, 0.5f);

		int degreeValue = 90;
		for (int i = 0; i <= totalNicks; ++i) {
			float y1 = scaleRect.top;
			float y2 = y1 - 0.020f;
			float y3 = y1 - 0.040f;
			
			if (i % 5 == 0) {
				canvas.drawLine(0.5f, y1, 0.5f, y3, scalePaint);
				//canvas.drawText(""+Math.abs(degreeValue), scaleRect.centerX(), scaleRect.top - 0.05f, scalePaint);
				canvas.save(Canvas.MATRIX_SAVE_FLAG);
				canvas.rotate(180.0f, scaleRect.centerX(), scaleRect.top);
				if (Math.abs(degreeValue) != 90) { //omit boundaries, as they project past drawing
					canvas.drawText(""+Math.abs(degreeValue), scaleRect.centerX(), scaleRect.top + 0.08f, scalePaint);
				}
				canvas.restore();		
				degreeValue -= 10;
				int value = nickToDegree(i);
				
				if (value >= minDegrees && value <= maxDegrees) {
					String valueString = Integer.toString(value);
					//canvas.drawText(valueString, 0.5f, y2 - 0.015f, scalePaint);
				}
			} else {
				canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaintMinor);
			}
			
			if (i == Math.round((totalNicks/2) * 0.75) || i == Math.round((totalNicks/2) * 1.25)) {
				canvas.save(Canvas.MATRIX_SAVE_FLAG);
				canvas.rotate(180.0f, scaleRect.centerX(), scaleRect.top);
				canvas.drawText("50%", scaleRect.centerX(), scaleRect.top - 0.03f, scalePaintMinor);								
				canvas.restore();		
			} else if (i == Math.round((totalNicks/2) * 0.5) || i == Math.round((totalNicks/2) * 1.5)) {
				canvas.save(Canvas.MATRIX_SAVE_FLAG);
				canvas.rotate(180.0f, scaleRect.centerX(), scaleRect.top);
				canvas.drawText("100%", scaleRect.centerX(), scaleRect.top - 0.03f, scalePaintMinor);								
				canvas.restore();		
			} else if (i == totalNicks/2) {
				//canvas.drawText("0", scaleRect.centerX(), scaleRect.top - 0.05f, scalePaint);
				canvas.save(Canvas.MATRIX_SAVE_FLAG);
				canvas.rotate(180.0f, scaleRect.centerX(), scaleRect.top);
				canvas.drawText("0%", scaleRect.centerX(), scaleRect.top - 0.03f, scalePaintMinor);
				canvas.restore();		
			} 
			/**
			 200% doesn't fit well
			 else if (i == Math.round((totalNicks/2) * 0.30) || i == Math.round((totalNicks/2) * 1.70)) {
				canvas.save(Canvas.MATRIX_SAVE_FLAG);
				canvas.rotate(180.0f, scaleRect.centerX(), scaleRect.top);
				canvas.drawText("200%", scaleRect.centerX(), scaleRect.top - 0.03f, scalePaintMinor);								
				canvas.restore();		
			}
			**/
			canvas.rotate(degreesPerNick, 0.5f, 0.5f);
		}
		canvas.restore();		
	}
	
	private int nickToDegree(int nick) {
		int rawDegree = ((nick < totalNicks / 2) ? nick : (nick - totalNicks)) * 2;
		int shiftedDegree = rawDegree + centerDegree;
		return shiftedDegree;
	}
	
}
