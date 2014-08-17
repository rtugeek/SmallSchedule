package com.free.schedule.view;

import com.free.schedule.All;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**课程表格十字分格背景
 * @author Administrator
 *
 */
public class ClassRelativeBackground extends Drawable{
	public ClassRelativeBackground(){
		
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint whitePaint = new Paint();
		whitePaint.setColor(0xFFFFFFFF);
		canvas.drawPaint(whitePaint);//画白底
		int x,y;
		int lineLenght = ScheduleView.classWidth / 8;
		Paint crossPaint = new Paint();
		crossPaint.setAntiAlias(true);
		crossPaint.setStrokeWidth(1);
		crossPaint.setColor(All.titleBarColor);
		for(int i = 0;i < ScheduleView.list - 1;i++){
			x = ScheduleView.classWidth * (i + 1);
			for(int i2 = 0;i2 < ScheduleView.row - 1;i2++){
				y = ScheduleView.classHeight * (i2 + 1);
				//画十字
				canvas.drawLine(x - lineLenght, y,  x + lineLenght,y, crossPaint);
				canvas.drawLine(x, y - lineLenght,  x ,y + lineLenght, crossPaint);
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
