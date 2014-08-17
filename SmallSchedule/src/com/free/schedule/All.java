package com.free.schedule;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.free.schedule.databases.ClassManager;

public class All {
	//sharePreference key
	public static String sharedName = "settings";
	public static String everyDaySections = "everydaySections";//每日课程数
	public static String isAutoMute = "isAutoMute";//是否上课自动静音
	public static String everyDayRemind = "everydayRemind";//是否开启每日提醒
	public static String isMineSet = "isMineSet";//是否为本程序调为静音
	public static String colorIndex = "colorIndex";//颜色索引
	public static String dayOfWeek = "dayOfWeek";//今天为星期几
	public static String howManyClasses = "howManyClasses";//今天有多少节课
	public static String todayNotifiy = "todayNotifiy";//今天是否已经提醒过
	public static String dayOfMonth = "dateOfMonth";//今天为几号
	public static String userClassName = "className";//用户班级名
	public static String receviceNews = "receviceNews";//是否接受新闻
	public static String timeLong = "timeLong";//每节课时长
	//记事本 sharePreference key
	public static String noteShared = "note";//
	//数据库名
	public static String classTableName = "classes";//
	public static String databaseName = "schedule.db";//
	public static String timeTableName = "classTime";//
	//颜色
	public static int backgroundColor = 0xFFFFFFFF;
	public static int titleBarColor = 0xFFBEC3C7;
	public static int stoneColor = 0xFFECF0F1;
	public static int classTextColor = 0xFFFFFFFF;
	public static int dayOfWeekColor = 0xFF526375;
	//平台 
	public final static String APIKEY = "g6v0hGBimrs5fbh1f2yDOrUF";
	public static String classTextColors[] ={"#0080FF","#35C87A","#F78B26","#53C5E7","#48608E","#1D6478","#FEE79B","#91d101","#FE6553","#F6A0BB","#34BC98","#909E9F"};
	/**获取状态栏高度
	 * @param context
	 * @return 状态栏高度
	 */
	public static int getStatusBarHeight(Context context){
		
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        } 
        
        
        return statusBarHeight;
    }
	
	
	/**刷新数据(如今天为星期几 ,几号...),每日执行一次
	 * @param context
	 */
	public static void refreshTodaysData(Context context){
		Calendar calendar = Calendar.getInstance();
		SharedPreferences sp = context.getSharedPreferences(sharedName, 0);
		int date = calendar.get(Calendar.DAY_OF_MONTH);
		if(date != sp.getInt(All.dayOfMonth, 0)){//判断今天是否刷新过
			SharedPreferences.Editor spe = sp.edit();
			ClassManager classManager = new ClassManager(context);
			//获取今天星期几
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if(dayOfWeek == 1){
				dayOfWeek = 6;
			}else{
				dayOfWeek -= 2;
			}
			//获取今天有多少节课
			int howmayClassesToady = 0;
	    	SQLiteDatabase db = classManager.getReadableDatabase();
	    	Cursor cursor = db.query(All.classTableName, null, "dayOfWeek=?", new String[]{dayOfWeek+""}, null, null, null);
    		while(cursor.moveToNext()){
    			howmayClassesToady++;
    		}
	    	cursor.close();
	    	db.close();
	    	spe.putInt(All.dayOfWeek, dayOfWeek);
	    	spe.putInt(All.howManyClasses, howmayClassesToady);
	    	spe.putInt(All.dayOfMonth, date);
	    	spe.putBoolean(All.todayNotifiy, false).commit();
	    	spe.commit();
		}
	}
	
	/**获取网络状态
     * @return null则为没联网
     */ 
    public static NetworkInfo getNetworkState(Context context){
    	ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	if(cm == null) return null;
		return cm.getActiveNetworkInfo();
    }
    
}
