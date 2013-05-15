package com.fovea.epublite;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctxt, Intent i) {
		if (i.getAction() != null) {
			scheduleAlarm(ctxt);
		}

	}
	
	static void scheduleAlarm(Context ctxt) {
		AlarmManager mgr = 
				(AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctxt, UpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.HOUR_OF_DAY, 4);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		if (cal.getTimeInMillis() < System.currentTimeMillis()) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		mgr.setRepeating(AlarmManager.RTC_WAKEUP, 
				cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
		
		
	}

}
