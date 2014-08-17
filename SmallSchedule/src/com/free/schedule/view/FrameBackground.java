package com.free.schedule.view;

import com.free.schedule.All;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

/**星期栏与节课栏的边框
 * @author Administrator
 *
 */
public class FrameBackground extends Drawable{
	private int width,height;
	private boolean vertical = false;
	private boolean isFocus = false;
	/**
	 * @param width 宽
	 * @param height 高
	 * @param vertical 框架的方向，true为垂直,false为水平
	 */
	public FrameBackground (int width,int height,boolean vertical){
		this.width = width;
		this.height = height;
		this.vertical = vertical;
	}
	
	/**
	 * @param width 宽
	 * @param height 高
	 * @param vertical 框架的方向，true为垂直,false为水平
	 * @param isFoucs 是否为当前日期
	 */
	public FrameBackground (int width,int height,boolean vertical,boolean isFoucs){
		this.width = width;
		this.height = height;
		this.vertical = vertical;
		this.isFocus = isFoucs;
	}
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint paint = new Paint();
		paint.setColor(All.stoneColor);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(5);
		if(isFocus){
			Rect rect = new Rect(0, 0, width, height);
			canvas.drawRect(rect, paint);
		}else{
			paint.setStyle(Style.STROKE);
			if(vertical){
				canvas.drawLine(width, 0, width, height, paint);
				canvas.drawLine(0, height, width, height, paint);
			}else{
				canvas.drawLine(width, 0, width, height, paint);
				canvas.drawLine(0, height, width , height, paint);
			}
		}
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}

}
