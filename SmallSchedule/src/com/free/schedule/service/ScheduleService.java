package com.free.schedule.service;
/*判断是否有课，有则把手机调为静音模式
 * 
 * */

import java.util.Calendar;

import com.free.schedule.All;
import com.free.schedule.Main;
import com.free.schedule.R;
import com.free.schedule.databases.ClassManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class ScheduleService extends Service {
	private SharedPreferences sp;
	private SharedPreferences.Editor spe;
	private AudioManager audioManager;
	private Notification n;
	private NotificationManager nm;
	private PendingIntent goToMain;//点击通知后跳转到主界面
	//以下为储存天气信息的字符串
	private Context context;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		context = this;
		handler = new Handler();
		handler.post(runnable);
		audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		nm =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		sp = getSharedPreferences(All.sharedName, 0);
		spe = sp.edit();
		Intent main = new Intent(this, Main.class);
		goToMain = PendingIntent.getActivity(this, 0, main, 0);
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			All.refreshTodaysData(ScheduleService.this);
			//获取当前时间
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int nowTime = hour * 60 + minute;
			
			n = new Notification();
			n.icon = R.drawable.ic_launcher;
			n.defaults = Notification.DEFAULT_VIBRATE;
			n.flags = Notification.FLAG_AUTO_CANCEL;
			if(sp.getBoolean(All.isAutoMute, true)){//判断是否开启自动静音
				if(haveClass(ScheduleService.this,nowTime , sp.getInt(All.dayOfWeek, 0))){
					if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE){
						if(!sp.getBoolean("isMineSet", false)){//如果没有设置成静音
							audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
							spe.putBoolean("isMineSet", true).commit();
							n.setLatestEventInfo(ScheduleService.this, getString(R.string.app_name), getString(R.string.tickerTextUp), goToMain);
							n.tickerText = getString(R.string.tickerTextUp);
							nm.notify(0, n);
						}
					}
				}else{
					if(sp.getBoolean("isMineSet", false)){//如果是本软件将手机调为震动，则下课后，调回原状态
						if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){ 
							audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							n.setLatestEventInfo(ScheduleService.this,getString(R.string.app_name), getString(R.string.tickerTextDown), goToMain);
							n.tickerText = getString(R.string.tickerTextDown);
							nm.notify(0, n);
						}
						spe.putBoolean("isMineSet", false).commit();
					}
				}
			}
			
			if(sp.getBoolean(All.everyDayRemind, true)){//判断是否有开启每日提醒
				if(!sp.getBoolean(All.todayNotifiy, false)){//判断是否提醒过？
					if(nowTime - 420 > -4 && nowTime - 420 < 4){//是否为早上7.左右(7 * 60 = 420)
					try {
						getWeatherData.start();
						handler.postDelayed(runnable, 30000);
					} catch (Exception e) {
						// TODO: handle exception
						NotifiyHandler.sendEmptyMessage(1);
					}
					}
				}
			}
			handler.sendEmptyMessage(0);
			handler.postDelayed(runnable, 3000);
		}	
			
	};
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(runnable);
		if(sp.getBoolean(All.isAutoMute, true) || sp.getBoolean(All.everyDayRemind, true)){//设置服务不被销毁
			Intent i =new Intent(this, ScheduleService.class);
			startService(i);
		}
		super.onDestroy();
	}
		
	/**判断是否有课
	 * @param context
	 * @param nowTime 现在的时间
	 * @param dayOfWeek 星期几
	 * @return
	 */
	public static boolean haveClass(Context context,int nowTime,int dayOfWeek){
		int startTime = 0;
		int endTime = 0;
		boolean haveClass = false;
		ClassManager classManager = new ClassManager(context);
		SQLiteDatabase db = classManager.getReadableDatabase();
		Cursor cursor2 = db.query(All.classTableName,null, "dayOfWeek=?", new String[]{dayOfWeek +""}, null, null, null);
		Cursor cursor = db.query("classTime", null,null,null,null,null,null);
		while(cursor.moveToNext()){
			startTime = cursor.getInt(cursor.getColumnIndex("startTime"));
			endTime = cursor.getInt(cursor.getColumnIndex("endTime"));
			if(nowTime > startTime && nowTime <endTime){
	a:			while(cursor2.moveToNext()){
					for(int i = cursor2.getInt(cursor2.getColumnIndex("startSection"));i <= cursor2.getInt(cursor2.getColumnIndex("endSection"));i++){
						if(cursor.getPosition() == i){
							haveClass = true;
							break a;
						}
					}
				}
			}else if(nowTime <endTime){ 
				break;
			}
		}
		cursor.close();
		cursor2.close();
		db.close();
	return haveClass;
	}
	
	/**
	 *解析json数据
	 */
	private Thread getWeatherData = new Thread(){
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(All.getNetworkState(context) == null){
					NotifiyHandler.sendEmptyMessage(1);
				}else{
					WeatherService weatherService = new WeatherService(context);
					weatherInfo = weatherService.getWeather()[0];
					NotifiyHandler.sendEmptyMessage(0);
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				NotifiyHandler.sendEmptyMessage(-1);
			}
		}
	};
	private String weatherInfo;
	private Handler NotifiyHandler = new Handler(){//用于每日提醒

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Notification todayNotify = new Notification();
			todayNotify.icon = R.drawable.ic_launcher;
			todayNotify.flags = Notification.FLAG_AUTO_CANCEL;
			String contentText = "";
			int howManyClasses = sp.getInt(All.howManyClasses, 0);
			if(howManyClasses == 0){
				contentText = getString(R.string.noClasses);
			}else{
				contentText = getString(R.string.todayClasses1) + howManyClasses + getString(R.string.toadyClasses2);
			}
			if(msg.what == 0){//如果有成功接收到天气预报
				contentText += "\n" + weatherInfo;
        	}
			todayNotify.setLatestEventInfo(ScheduleService.this, getString(R.string.app_name), contentText,goToMain);
    		nm.notify(1,todayNotify);
    		spe.putBoolean(All.todayNotifiy, true).commit();//设置今天已经通知过了
			super.handleMessage(msg);
		}
		
	};

}
