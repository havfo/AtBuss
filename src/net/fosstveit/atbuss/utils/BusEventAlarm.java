package net.fosstveit.atbuss.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import net.fosstveit.atbuss.BusStopActivity;

import java.util.Date;

public class BusEventAlarm extends BroadcastReceiver {

	public BusEventAlarm() {
	}

	public BusEventAlarm(Context context, Bundle extras, Date time) {
		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, BusEventAlarm.class);
		intent.putExtras(extras);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTime(), pendingIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// here you can get the extras you passed in when creating the alarm
		// intent.getBundleExtra(REMINDER_BUNDLE));

		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Exception e) {
		}

		Intent stop = new Intent(context, BusStopActivity.class);
		stop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		stop.putExtras(intent.getExtras());

		context.startActivity(stop);
	}
}