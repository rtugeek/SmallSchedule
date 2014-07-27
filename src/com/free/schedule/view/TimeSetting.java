package com.free.schedule.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import com.free.schedule.All;
import com.free.schedule.R;
import com.free.schedule.databases.ClassManager;

public class TimeSetting extends PopupWindow{
	private ListView timeList;
	private ImageView cancel,confirm;
	private ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	private SimpleAdapter adapter;
	private TimePickerDialog tpd;
	private int[] hour = new int[16];
	private int[] min = new int[16];
	private int[] startTimeR = new int[16];
	private int[] endTimeR = new int[16];
	private Context context;
	private EditText timeLong;
	private int timeLongx = 45;
	private SharedPreferences sp;
	private SQLiteDatabase db;
	private int classCount = 0;
	public TimeSetting(Context context,int classCount) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.classCount = classCount;
		ClassManager classManager = new ClassManager(context);
		sp = context.getSharedPreferences(All.sharedName, 0);
		
		db = classManager.getWritableDatabase();
		adapter = new SimpleAdapter(context, list, R.layout.time_list, new String[]{"section","time"},new int[]{R.id.section,R.id.time});
		LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View timeLayout = li.inflate(R.layout.time_setting,null);
		setContentView(timeLayout);
		setAnimationStyle(R.style.popupStyle);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_full_bright));
		setWindowLayoutMode(-1, LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		
		timeLong = (EditText)timeLayout.findViewById(R.id.timeLong);
		timeList = (ListView)timeLayout.findViewById(R.id.listView1);
		cancel = (ImageView)timeLayout.findViewById(R.id.cancel);
		confirm = (ImageView)timeLayout.findViewById(R.id.confirm);
		
		timeLongx = sp.getInt(All.timeLong, 45);
		timeLong.setText(timeLongx+"");
		Cursor cursor = db.query("classTime", null,null,null,null,null,null);
		int startTime = 0;
		int endTime = 0;
		int count = 0;
		while(cursor.moveToNext() && count < classCount){
			count++;
			HashMap<String,String> map = new HashMap<String, String>();
			map.put("section", context.getString(R.string.di) + " " + count +" " +context.getString(R.string.section));
			startTime = cursor.getInt(cursor.getColumnIndex("startTime"));
			endTime = cursor.getInt(cursor.getColumnIndex("endTime"));
			startTimeR[count -1] = startTime;
			endTimeR[count - 1] = endTime;
			min[count - 1] = startTime % 60;
			hour[count - 1] =  (startTime - min[count - 1]) / 60;
			String m = min[count - 1] +"";
			String h = hour[count - 1] +"";
			if(m.length() < 2) m= "0"+ m;
			if(h.length() < 2) h= "0"+ h;
			String timeX = h + ":" + m;
			m =  endTime % 60+"";
			h = (endTime -  endTime % 60) / 60 +"";
			if(m.length() < 2) m= "0"+ m;
			if(h.length() < 2) h= "0"+ h;
			timeX = timeX + "~" + h+":"+m;
			map.put("time", timeX);
			list.add(map);
		}
		timeList.setAdapter(adapter);
		
		timeLong.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.length() > 0){
					timeLongx = Integer.parseInt(s.toString());
					if(Integer.parseInt(s.toString()) < 1){
						timeLong.setText("1");
						timeLong.setSelection(timeLong.getText().length());
						timeLongx = 1;
					}else if(Integer.parseInt(s.toString()) > 120){
						timeLong.setText("120");
						timeLong.setSelection(timeLong.getText().length());
						timeLongx = 120;
					}
					refreshList(timeLongx);
				}
			}
		});
		
		timeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				tpd = new TimePickerDialog(TimeSetting.this.context,new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// TODO Auto-generated method stub
						HashMap<String,String> map = new HashMap<String, String>();
						map.put("section",TimeSetting.this.context.getString(R.string.di) + " " + (position + 1) +" " + TimeSetting.this.context.getString(R.string.section));
						String result = "";
						hour[position] = hourOfDay;
						min[position] = minute;
						String m = minute +"";
						String h = hourOfDay +"";
						if(m.length() < 2) m= "0"+ m;
						if(h.length() < 2) h= "0"+ h;
						result =  h + ":" + m + "~";
						//计算结束时间
						int end = minute + timeLongx;
						m = end % 60 +"";
						h = hourOfDay + (end - end % 60) / 60 +"";
						if(m.length() < 2) m= "0"+ m;
						if(h.length() < 2) h= "0"+ h;
						result +=  h + ":" + m;
						startTimeR[position] = hourOfDay * 60 + minute;
						endTimeR[position] = startTimeR[position] + timeLongx;
						map.put("time",result);
						list.set(position, map);
						adapter = new SimpleAdapter(TimeSetting.this.context, list, R.layout.time_list, new String[]{"section","time"},new int[]{R.id.section,R.id.time});
						timeList.setAdapter(adapter);
					}
				}, hour[position], min[position], true);
				tpd.show();
			}
			
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TimeSetting.this.dismiss();
			}
		});
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor spe = sp.edit();
				spe.putInt(All.timeLong, timeLongx).commit();
				for(int i = 0; i < TimeSetting.this.classCount;i++){
					String whereClause = "section=?";
					String[] whereArgs = {i+""};
					ContentValues cv = new ContentValues();
					cv.put("section", i);
					cv.put("startTime", startTimeR[i]);
					cv.put("endTime",startTimeR[i] + timeLongx);
					db.update(All.timeTableName, cv, whereClause, whereArgs);
					TimeSetting.this.dismiss();
				}
			}
		} );
	}
	
	private void refreshList(int timeLong){
		int endTime = 0;
		int count = 0;
		while(count < classCount){
			HashMap<String,String> map = new HashMap<String, String>();
			map.put("section", context.getString(R.string.di) + " " + (count + 1) +" " +context.getString(R.string.section));
			endTime = startTimeR[count] + timeLong;
			String m = min[count] +"";
			String h = hour[count] +"";
			if(m.length() < 2) m= "0"+ m;
			if(h.length() < 2) h= "0"+ h;
			String timeX = h + ":" + m;
			m =  endTime % 60+"";
			h = (endTime - endTime % 60) / 60 +"";
			if(m.length() < 2) m= "0"+ m;
			if(h.length() < 2) h= "0"+ h;
			timeX = timeX + "~" + h+":"+m;
			map.put("time", timeX);
			list.set(count, map);
			count++;
		}
		adapter = new SimpleAdapter(TimeSetting.this.context, list, R.layout.time_list, new String[]{"section","time"},new int[]{R.id.section,R.id.time});
		timeList.setAdapter(adapter);
	}
				
	
}
