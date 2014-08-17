package com.free.schedule.databases;

import com.free.schedule.All;
import com.free.schedule.view.ClassTextView;
import com.free.schedule.view.ScheduleView;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**管理课程表的数据库
 * @author Administrator
 *
 */
public class ClassManager extends SQLiteOpenHelper{
	private int[] startTime = {480,535,600,655,840,895,950,1005,1110,1165,1220,1275,1140,1195,1250,1305};//课程时间表的数据
	private int[] endTime = {525,580,645,700,885,940,995,1050,1155,1210,1265,1315,1185,1240,1295,1350};//
	private static int version = 1;
	private SQLiteDatabase db;
	private SharedPreferences sp;
	private Context context;
	public ClassManager(Context context) {
		super(context, All.databaseName, null, version);
		// TODO Auto-generated constructor stub
		this.context = context;
		sp = context.getSharedPreferences(All.sharedName, 0);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try {
			db.execSQL( "create table " + All.timeTableName+ "(section integer,startTime integer,endTime integer);");
			db.execSQL("create table " + All.classTableName + "(name varcahr(15),place varchar(15),dayOfWeek integer,startSection integer,endSection integer,teacher varchar(15),color varchar(15));");
			initTimeTable(db);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Database exist");
		}
	}

	
	/**添加课程
	 * @param values 课程内容
	 * @return
	 */
	public void insertClass(ContentValues values){
		//先设置课程背景颜色
		db = getWritableDatabase();
		Cursor cursor = db.query(All.classTableName, null, "name=?", new String[]{values.getAsString("name")}, null, null,null);
		if(cursor.moveToFirst()){
			values.put("color", cursor.getString(cursor.getColumnIndex("color")));
		}else{
			int index = sp.getInt(All.colorIndex, 0);
			values.put("color", All.classTextColors[index]);
			SharedPreferences.Editor spe = sp.edit();
			index++;
			if(index > 11) index = 0;
			spe.putInt(All.colorIndex, index).commit();
		}
		
		db.insert(All.classTableName, null, values);
		db.close();
	}
	
	/**删除课程
	 * @param whereClause 课程条件
	 * @param whereArgs 课程条件
	 * @param classToDelete 要删除的课程
	 * @return
	 */
	public void deleteClass(ClassTextView classToDelete){
		db = getWritableDatabase();
		String whereClause = "name=? and dayOfWeek=? and startSection=?";
		String[] whereArgs = {classToDelete.name,classToDelete.dayOfWeek+"",classToDelete.startSection+""};
		for(int i = classToDelete.startSection + 1;i <= classToDelete.endSection;i++){
			ScheduleView.classText[classToDelete.dayOfWeek][i].setClickable(true);
		}
		db.delete(All.classTableName, whereClause, whereArgs);
		db.close();
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	/**初始化时间表
	 * @param db
	 */
	private void initTimeTable(SQLiteDatabase db){
		for(int i = 0; i < startTime.length;i++){
			ContentValues cv = new ContentValues();
			cv.put("section", i);
			cv.put("startTime", startTime[i]);
			cv.put("endTime", endTime[i]);
			db.insert(All.timeTableName, null, cv);
		}
	}
	
	/**清空课表，并清空笔记
	 * @param context
	 */
	public void clearClasses(){
		db = getWritableDatabase();
		db.delete(All.classTableName, new String(), new String[]{});
		SharedPreferences sp = context.getSharedPreferences(All.noteShared, 0);
		SharedPreferences.Editor spe = sp.edit();
		spe.clear().commit();
	}
	
}
