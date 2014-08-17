package com.free.schedule.view;
//编辑栏视图

import com.free.schedule.All;
import com.free.schedule.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;


public class ClassEditBar extends LinearLayout{
	public ImageButton delete;//删除
	public ImageButton edit;//编辑
	public ImageButton cancel;//取消操作
	private TranslateAnimation show,dismiss;//显示与消失动画
	public boolean isShown;//是否已经显示
	private Context context;
	public ClassEditBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		setGravity(Gravity.RIGHT);
		delete = new ImageButton(context);
		edit = new ImageButton(context);
		cancel = new ImageButton(context);
		int wh = ScheduleView.titleBarHeight;//按钮的宽和高
		addView(edit,new LayoutParams(wh,wh));
		addView(delete,new LayoutParams(wh,wh));
		addView(cancel,new LayoutParams(wh,wh));
		edit.setBackgroundResource(R.drawable.edit);
		edit.setScaleType(ScaleType.CENTER);
		delete.setBackgroundResource(R.drawable.delete);
		delete.setScaleType(ScaleType.CENTER);
		cancel.setBackgroundResource(R.drawable.cancel);
		cancel.setScaleType(ScaleType.CENTER);
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ScheduleView.classDetail.setAction(0);
				dismiss();
			}
		});
		
		
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sp = ClassEditBar.this.context.getSharedPreferences(All.noteShared, 0);
				SharedPreferences.Editor spe = sp.edit();
				for(int weekOfDay = 0;weekOfDay < ScheduleView.list;weekOfDay++){
					for(int section = 0;section < ScheduleView.row;section++){
						if(ScheduleView.classText[weekOfDay][section].isSelected){
							if(ScheduleView.classText[weekOfDay][section].isClass){
								ScheduleView.classText[weekOfDay][section].animationDelete();
								String noteIndex = weekOfDay+"" +section;
								ScheduleView.classDetail.clearContentNote();
								for(int i = 0;i < 3;i++){
									spe.remove(i + noteIndex);
								}
								Log.i("deleteClass",noteIndex);
								spe.remove(noteIndex).commit();
							}
						}
					}
				}
				dismiss();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(int i = 0;i < ScheduleView.list;i++){
					for(int i2 = 0;i2 < ScheduleView.row ;i2++){
						if(ScheduleView.classText[i][i2].isClass){
							ScheduleView.classText[i][i2].clearAnimation();
							ScheduleView.classText[i][i2].setSelected(false);
						}else{
							ScheduleView.classText[i][i2].initClassText();
						}
					}
				}
				dismiss();
			}
		});
		
		show = new TranslateAnimation(0, 0, -200, 0);
		show.setFillAfter(true);
		show.setDuration(500);
		dismiss = new TranslateAnimation(0, 0, 0, -200);
		dismiss.setFillAfter(true);
		dismiss.setDuration(500);
		
		show.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				delete.setClickable(true);
				edit.setClickable(true);
				cancel.setClickable(true);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dismiss.setAnimationListener(new AnimationListener() {
			
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
				delete.setClickable(false);
				edit.setClickable(false);
				cancel.setClickable(false);
			}
		});
		startAnimation(dismiss);
		
	}
	
	
	/**设置编辑栏动作
	 * @param action 
	 * 0 :显示添加和取消 选项（只选中没课的课程格时）
	 * 1 :显示全部选项(只要有选中有课的课程格时)
	 * 
	 */
	public void setClassEditorAction(int action){
		switch(action){
		case 0 :
			delete.setVisibility(8);
			break;
		case 1 :
			delete.setVisibility(0);
			break;
		}
		if(!isShown){
			startAnimation(show);
			isShown = true;
		}
		
	}
	
	public void dismiss(){
		startAnimation(dismiss);
		isShown = false;
		ScheduleView.isEdit = false;
	}
	
}
