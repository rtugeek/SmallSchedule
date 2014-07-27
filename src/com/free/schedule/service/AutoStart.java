package com.free.schedule.service;


import com.free.schedule.All;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**现实开机自动启动功能
 * @author Administrator
 *
 */
public class AutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences sp = context.getSharedPreferences(All.sharedName, 0);
		if(sp.getBoolean(All.isAutoMute, true)|| sp.getBoolean(All.everyDayRemind, true)){
			Intent scheduleService = new Intent();
			scheduleService.setClass(context, ScheduleService.class);
			context.startService(scheduleService);
		}
	}

}
