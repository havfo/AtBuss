package net.fosstveit.atbuss;

import net.fosstveit.atbuss.interfaces.CompassCallback;
import net.fosstveit.atbuss.interfaces.GPSCallback;
import net.fosstveit.atbuss.managers.CompassManager;
import net.fosstveit.atbuss.managers.GPSManager;
import net.fosstveit.atbuss.managers.SQLiteManager;
import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.BusStopAdapter;
import net.fosstveit.atbuss.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class AtBussActivity extends SherlockActivity implements GPSCallback,
		CompassCallback {

	public final static String BUS_STOP_ID = "net.fosstveit.atbuss.BUSSTOPID";
	public final static String BUS_STOP_NAME = "net.fosstveit.atbuss.BUSSTOPNAME";
	public final static String BUS_ROUTE_NAME = "net.fosstveit.atbuss.BUSROUTE";
	public final static String BUS_ROUTE_TIME = "net.fosstveit.atbuss.BUSTIME";
	public final static String BUS_ROUTE_SCHED = "net.fosstveit.atbuss.BUSSCHED";

	private GPSManager gpsManager = null;
	private double currentLon = 0;
	private double currentLat = 0;

	private long lastCompassUpdate = System.currentTimeMillis();

	private Location currentLocation;

	private CompassManager compassManager = null;
	private int currentDirection = 0;

	public static SQLiteManager sqliteManager = null;
	private int busStopsVersion = 0;

	public static ArrayList<BusStop> busStops;

	private ListView listSelectStop;
	private BusStopAdapter busStopAdapter;

	private SharedPreferences sharedPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_at_buss);
		setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);

		sqliteManager = new SQLiteManager(this);

		checkIfFirstRun();
		parseBusStops();

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		listSelectStop = (ListView) findViewById(R.id.listSelectStop);
		listSelectStop.setOnItemClickListener(busStopSelected);

		busStopAdapter = new BusStopAdapter(this);
		listSelectStop.setAdapter(busStopAdapter);

		startGPS();
		startCompass();
	}

	@Override
	protected void onStart() {
		super.onStart();
		startGPS();
		startCompass();
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopGPS();
		stopCompass();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopGPS();
		stopCompass();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_at_buss, menu);
		return true;
	}

	@Override
	public void onGPSUpdate(Location location) {
		currentLon = location.getLongitude();
		currentLat = location.getLatitude();

		currentLocation = location;

		new GetBusStops().execute();
	}

	@Override
	public void onCompassUpdate(int direction) {
		currentDirection = direction;

		long time = System.currentTimeMillis();

		if (time - lastCompassUpdate > 500) {
			lastCompassUpdate = time;
			new RotateBusArrows().execute();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			search();
			return true;
		case R.id.menu_oracle:
			askOracle();
			return true;
		case R.id.menu_settings:
			Intent intent = new Intent(AtBussActivity.this,
					SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private OnItemClickListener busStopSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
			Intent intent = new Intent(AtBussActivity.this,
					BusStopActivity.class);
			BusStop b = (BusStop) listSelectStop.getItemAtPosition(i);
			intent.putExtra(BUS_STOP_ID, b.getId());
			intent.putExtra(BUS_STOP_NAME, b.getName());
			startActivity(intent);
		}
	};

	public void askOracle() {
		Intent intent = new Intent(AtBussActivity.this, AskOracleActivity.class);
		startActivity(intent);
	}

	public void search() {
		Intent intent = new Intent(AtBussActivity.this, SearchActivity.class);
		startActivity(intent);
	}

	private void checkIfFirstRun() {
		busStopsVersion = sqliteManager.getLatestVersion();

		if (busStopsVersion == -1) { // We don't have stops, need to get them
			new PopulateBusStops().execute();
		}
	}

	private void parseBusStops() {
		busStops = sqliteManager.getAllBusStops();
	}

	private double calcGeoDistance(final double lat1, final double lon1,
			final double lat2, final double lon2) {
		double distance = 0.0;

		try {
			final float[] results = new float[3];

			Location.distanceBetween(lat1, lon1, lat2, lon2, results);

			distance = results[0];
		} catch (final Exception ex) {
			distance = 0.0;
		}

		return distance;
	}

	private void startGPS() {
		if (gpsManager == null) {
			gpsManager = new GPSManager();

			gpsManager.startListening(this);
			gpsManager.setGPSCallback(this);
		}
	}

	private void stopGPS() {
		if (gpsManager != null) {
			gpsManager.stopListening();
			gpsManager.setGPSCallback(null);

			gpsManager = null;
		}
	}

	private void startCompass() {
		if (compassManager == null) {
			compassManager = new CompassManager();

			compassManager.startListening(this);
			compassManager.setCompassCallback(this);
		}
	}

	private void stopCompass() {
		if (compassManager != null) {
			compassManager.stopListening();
			compassManager.setCompassCallback(null);

			compassManager = null;
		}
	}

	private class GetBusStops extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		}

		@Override
		protected String doInBackground(String... params) {
			for (BusStop b : busStops) {
				double distance = calcGeoDistance(currentLat, currentLon,
						b.getLatitude(), b.getLongitude());

				b.setDistance((int) distance);
			}
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			double distanceLimit = Integer.parseInt(sharedPrefs.getString(
					"Distance", "500"));
			List<BusStop> tmpList = new ArrayList<BusStop>();

			for (BusStop b : busStops) {
				if (b.getDistance() < distanceLimit) {
					tmpList.add(b);
				}
			}

			Collections.sort(tmpList, new Comparator<BusStop>() {
				@Override
				public int compare(BusStop lhs, BusStop rhs) {
					return (int) lhs.getDistance() - (int) rhs.getDistance();
				}
			});

			busStopAdapter.updateBusStops(tmpList);

			setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
	}

	private class RotateBusArrows extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... strings) {

			for (int i = 0; i < busStopAdapter.getCount(); i++) {
				Location targetLocation = new Location("");
				targetLocation.setLatitude(busStopAdapter.getItem(i)
						.getLatitude());
				targetLocation.setLongitude(busStopAdapter.getItem(i)
						.getLongitude());

				float bearingTo = currentLocation.bearingTo(targetLocation);

				if (bearingTo < 0) {
					bearingTo = bearingTo + 360;
				}

				int direction = (int) (bearingTo - currentDirection);

				if (direction < 0) {
					direction = direction + 360;
				}

				busStopAdapter.getItem(i).setDirection(direction);
			}

			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			busStopAdapter.notifyDataSetChanged();
		}
	}

	private class PopulateBusStops extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(
				AtBussActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Henter data...");
			dialog.show();
			dialog.setCancelable(false);
	        dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected String doInBackground(final String... args) {
			sqliteManager.clearBusStops();
			Utils.getBusStops();
			Utils.getVersion();
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
}
