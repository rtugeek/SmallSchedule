package com.free.schedule.view;

import com.free.schedule.databases.ClassManager;

import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

/**课程格视图
 * @author Administrator
 *
 */
public class ClassTextView extends TextView{
	public String name;
	public String place;
	public String teacher;
	public int startSection;
	public int endSection;
	public int dayOfWeek;
	public int howManyClasses = 1;//几节课
	public boolean isClass = false;//是否有课
	public boolean isSelected =false;//是否被选定
	private ScaleAnimation selcetAnimation;//选中时的动画
	private ScaleAnimation deleteAnimation;//删除时的动画
	public boolean hasNote = false;//是否有笔记
	public static ClassManager classManager;
	public ClassTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		classManager = new ClassManager(context);
		
		selcetAnimation = new ScaleAnimation(1, 0.8f, 1, 0.8f, 1, 0.5f,1,0.5f);
		selcetAnimation.setDuration(500);
		selcetAnimation.setFillAfter(true);
		deleteAnimation = new ScaleAnimation(0.92f,0,0.92f,0, 1, 0.5f,1,0.5f);
		deleteAnimation.setDuration(500);
		deleteAnimation.setFillAfter(true);
		deleteAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				classManager.deleteClass(ClassTextView.this);
				ScheduleView.refreshSchedule();
			}
		});
		setGravity(Gravity.TOP);
		
	}
	
	/**
	 * 初始化课程格
	 */
	public void initClassText(){
		name = "";
		endSection = 0;
		isClass = false;
		isSelected = false;
		howManyClasses = 1;
		getLayoutParams().height = ScheduleView.classHeight;
		setText("");
		setBackground(0);
		clearAnimation();
	}

	/**设置ClassText没课时背景
	 * @param color 背景填充颜色 ,0则为没背景填充
	 */
	public void setBackground(int color){
		if(isClass){
			setBackgroundDrawable(new ClassBackground(color, ScheduleView.classWidth, ScheduleView.classHeight * (endSection - startSection + 1),hasNote));
		}else{
			if(isSelected){
				setBackgroundDrawable(new ClassBackground(ScheduleView.classWidth,ScheduleView.classHeight));
			}else{
				setBackgroundDrawable(null);
			}
			
		}
	}
	/**
	 * 设置选中并播放动画
	 */
	public void animationSelect(){
		startAnimation(selcetAnimation);
		setSelected(true);
	}
	
	/**
	 * 播放删除动画，动画结束后删除课程
	 */
	public void animationDelete(){
		startAnimation(deleteAnimation);
	}
	
	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;
		for(int i = startSection + 1;i <= endSection;i++){
			ScheduleView.classText[dayOfWeek][i].isSelected = isSelected;
		}
	}
	
}
