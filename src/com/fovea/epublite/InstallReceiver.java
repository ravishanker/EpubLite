package com.fovea.epublite;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class InstallReceiver extends BroadcastReceiver {
	private static final int NOTIFY_ID = 1337;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		NotificationCompat.Builder builder = 
				new NotificationCompat.Builder(context);
		Intent toLaunch = new Intent(context, EpubLiteActivity.class);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, toLaunch, 0);
		
		builder.setAutoCancel(true).setContentIntent(pi)
				.setContentTitle(context.getString(R.string.update_complete))
				.setContentText(context.getString(R.string.update_desc))
				.setSmallIcon(android.R.drawable.stat_sys_download_done)
				.setTicker(context.getString(R.string.update_complete))
				.setWhen(System.currentTimeMillis());
		
		NotificationManager mgr =
				((NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE));
		
		mgr.notify(NOTIFY_ID, builder.build());

	}

}
