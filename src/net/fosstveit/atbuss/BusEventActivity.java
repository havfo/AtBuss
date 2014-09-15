package net.fosstveit.atbuss;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.R.id;
import net.fosstveit.atbuss.R.layout;
import net.fosstveit.atbuss.utils.BusEventAlarm;

import java.util.Calendar;
import java.util.Date;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class BusEventActivity extends SherlockActivity {

	private String stopName = "";
	private String routeName = "";
	private String busTime = "";
	private String busSched = "";
	private int stopId = 0;

	private Bundle extras;

	private TextView busStop = null;
	private TextView busRoute = null;
	private TextView realTime = null;
	private TextView schedTime = null;

	private EditText alarmMinutes = null;
	private Button setAlarm = null;

	private BusEventAlarm alarmEvent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_event);

		setTitle("Bussruteinfo");

		setAlarm = (Button) findViewById(R.id.alarmButton);
		setAlarm.setOnClickListener(setAlarmListener);
		alarmMinutes = (EditText) findViewById(R.id.alarmMinutes);

		extras = getIntent().getExtras();
		if (extras != null) {
			stopName = (String) extras.get("net.fosstveit.atbuss.BUSSTOPNAME");
			stopId = (int) extras.getInt("net.fosstveit.atbuss.BUSSTOPID");
			routeName = (String) extras
					.getString("net.fosstveit.atbuss.BUSROUTE");
			busTime = (String) extras.getString("net.fosstveit.atbuss.BUSTIME");
			busSched = (String) extras
					.getString("net.fosstveit.atbuss.BUSSCHED");
		}

		busStop = (TextView) findViewById(R.id.busStopName);
		busRoute = (TextView) findViewById(R.id.busRouteName);
		realTime = (TextView) findViewById(R.id.realTimeTimeName);
		schedTime = (TextView) findViewById(R.id.scheduledTimeName);

		busStop.setText(stopName);
		busRoute.setText(routeName);
		realTime.setText(busTime);
		schedTime.setText(busSched);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener setAlarmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setAlarm(alarmMinutes.getText().toString());
		}
	};

	private void setAlarm(String minutes) {
		Calendar cal = Calendar.getInstance();

		String[] t = busTime.split(":");

		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(t[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(t[1]));
		cal.set(Calendar.SECOND, 30);
		cal.set(Calendar.MILLISECOND, 0);

		cal.add(Calendar.MINUTE, -Integer.parseInt(minutes));

		Date alarm = cal.getTime();

		alarmEvent = new BusEventAlarm(this, extras, alarm);

		Toast.makeText(this, "Alarm satt!", Toast.LENGTH_SHORT).show();
	}
}
