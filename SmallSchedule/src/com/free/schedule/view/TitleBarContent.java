package com.free.schedule.view;
/*获取新闻与构建新闻视图*/

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.free.schedule.R;
import com.free.schedule.service.WeatherService;

public class TitleBarContent extends RelativeLayout{
    private TextView[] newsShow = new TextView[2];
    private Animation downUp,upUp;
    private WeatherService weatherService;
    private CheckBox horn;
    private String[] info;
	public TitleBarContent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		weatherService = new WeatherService(context);
		//设置喇叭，用于打开，关闭新闻
		horn = new CheckBox(context);
		horn.setButtonDrawable(R.drawable.horn);
		horn.setId(1314);
//		horn.setChecked(sp.getBoolean(All.receviceNews, true));
		horn.setChecked(true);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ScheduleView.titleBarHeight,ScheduleView.titleBarHeight);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(horn,rlp);
		if(horn.isChecked()){
			getWeather();
		}else{
			handlerx.postDelayed(timeTicker, 3000);
		}
		//设置新闻文本属性
		newsShow[0] = new TextView(context);
		newsShow[1] = new TextView(context);
		newsShow[0].setMaxLines(2);
		newsShow[0].setEllipsize(TextUtils.TruncateAt.END);
		newsShow[0].setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		newsShow[0].setAutoLinkMask(1);
		newsShow[0].setTextColor(0xFFFFFFFF);
		newsShow[1].setEllipsize(TextUtils.TruncateAt.END);
		newsShow[1].setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		newsShow[1].setTextColor(0xFFFFFFFF);
		rlp = new RelativeLayout.LayoutParams(-1,-2);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		rlp.addRule(RelativeLayout.RIGHT_OF, horn.getId());
		addView(newsShow[0],rlp);
		addView(newsShow[1],rlp);
		
		//加载动画
		downUp = AnimationUtils.loadAnimation(context, R.anim.up);
        upUp = AnimationUtils.loadAnimation(context, R.anim.up_miss);
        
//        newsShow[0].setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(v.getTag() != null){
//					Uri url = Uri.parse(v.getTag().toString());
//					Intent intent = new Intent(Intent.ACTION_VIEW,url);
//					cx.startActivity(intent);
//				}
//			}
//		});
        
//        horn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				// TODO Auto-generated method stub
//				SharedPreferences.Editor spe = sp.edit();
//				spe.putBoolean(All.receviceNews, isChecked).commit();
//				if(!isChecked){
//					newsShow[1].setText("");
//					newsShow[0].setText("");
//					newsShow[0].setClickable(false);
//				}else{
//					 getWeather();
////					 played = true;
//					 newsShow[0].setClickable(true);
//				}
//			}
//		});
        setVisibility(View.GONE);
	}
	
	
	private Handler handlerx =new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.i("Message", msg.what+"");
			handlerx.postDelayed(timeTicker, 3000);
			super.handleMessage(msg);
		}
    	
    };
    private int timer = 0;
//    private boolean played = true;
    private Runnable timeTicker = new Runnable(){//计时器，每5秒滚动一条新闻
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("played","playing");
			if(info != null){
				if(info.length > 0){
					if(TitleBarContent.this.getVisibility() == 8){
						TitleBarContent.this.setVisibility(0);
						TitleBarContent.this.startAnimation(downUp);
					}
					newsShow[1].setText(newsShow[0].getText().toString());
					newsShow[0].setText(info[timer]);
//					newsShow[0].setTag(newsUrl[timer]);
					newsShow[0].startAnimation(downUp);
					newsShow[1].startAnimation(upUp);
					Log.i("timerIndex", timer+"");
					timer++ ;
					if(timer >= info.length){
						timer = 0;
						TitleBarContent.this.startAnimation(upUp);
					}else{
						handlerx.postDelayed(timeTicker, 8000);
					}
				}
				}
			}
//			if(horn.isChecked()){
//				if(played){
//					if(info.length > 0){
//						newsShow[1].setText(newsShow[0].getText().toString());
//						newsShow[0].setText(info[timer]);
////					newsShow[0].setTag(newsUrl[timer]);
//						newsShow[0].startAnimation(downUp);
//						newsShow[1].startAnimation(upUp);
//						Log.i("timerIndex", timer+"");
//						timer++ ;
//						if(timer >= info.length){
//							played = false;
//							timer = 0;
//							TitleBarContent.this.startAnimation(upUp);
//						}
//					}else{
//						played = false;
//					}
//				}else{
//					TitleBarContent.this.startAnimation(upUp);
//				}
////				handlerx.postDelayed(timeTicker, 8000);
//			}else{
//				if(played){
//					played = false;
//					handlerx.postDelayed(timeTicker, 8000);
//				}else{
//					TitleBarContent.this.startAnimation(upUp);
//				}
//			}
//		}
	};
	
	private void getWeather(){
		
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				info = weatherService.getWeather();
				Log.i("weatherInfo", info+"--");
				handlerx.sendEmptyMessage(0);
			}
			
		}.start();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		handlerx.removeCallbacks(timeTicker);
		super.onDetachedFromWindow();
	}
	
	
	
	
}
