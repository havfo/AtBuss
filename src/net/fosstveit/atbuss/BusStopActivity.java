package net.fosstveit.atbuss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import net.fosstveit.atbuss.objects.BusEvent;
import net.fosstveit.atbuss.utils.BusEventEntryAdapter;
import net.fosstveit.atbuss.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class BusStopActivity extends SherlockActivity {

	private ListView listSelectEvent;
	private BusEventEntryAdapter busEventEntryAdapter;
	private String stopName;
	private int stopId;
	private TimerTask doAsynchronousTask;
	private SharedPreferences sharedPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_bus_stop);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			stopName = (String) extras.get(AtBussActivity.BUS_STOP_NAME);
			setTitle(stopName);
			stopId = (int) extras.getInt(AtBussActivity.BUS_STOP_ID);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		listSelectEvent = (ListView) findViewById(R.id.listBusEvents);
		listSelectEvent.setOnItemClickListener(busEventSelected);

		busEventEntryAdapter = new BusEventEntryAdapter(this,
				R.layout.bus_stop_list_item);
		listSelectEvent.setAdapter(busEventEntryAdapter);

		getBusEvents();
	}

	private OnItemClickListener busEventSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
			Intent intent = new Intent(BusStopActivity.this,
					BusEventActivity.class);
			BusEvent b = (BusEvent) listSelectEvent.getItemAtPosition(i);

			intent.putExtra(AtBussActivity.BUS_STOP_NAME, stopName);
			intent.putExtra(AtBussActivity.BUS_STOP_ID, stopId);
			intent.putExtra(AtBussActivity.BUS_ROUTE_NAME, b.getRoute());
			intent.putExtra(AtBussActivity.BUS_ROUTE_TIME, b.getTime());
			intent.putExtra(AtBussActivity.BUS_ROUTE_SCHED, b.getSched());

			startActivity(intent);
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			doAsynchronousTask.cancel();
			doAsynchronousTask = null;
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getBusEvents() {
		final Handler handler = new Handler();
		Timer timer = new Timer();

		doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							GetBusEvents getBusEvents = new GetBusEvents();
							getBusEvents.execute();
						} catch (Exception e) {
						}
					}
				});
			}
		};

		timer.schedule(doAsynchronousTask, 0, 30000);
	}

	private class GetBusEvents extends AsyncTask<String, Void, String> {

		private BusEvent[] events;

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(Boolean.TRUE);
		}

		@Override
		protected String doInBackground(String... params) {
			int numStops = Integer.parseInt(sharedPrefs.getString(
					"Sanntidsdata", "15"));
			events = Utils.getBusTime(stopId, numStops);
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			busEventEntryAdapter.clear();

			if (events != null) {
				for (int i = 0; i < events.length; i++) {
					busEventEntryAdapter.add(events[i]);
				}
			} else {
				busEventEntryAdapter.add(new BusEvent("Ingen ruter", "", "",
						"", 0));
			}

			setProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
	}
}
