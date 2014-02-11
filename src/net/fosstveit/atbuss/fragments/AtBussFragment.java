package net.fosstveit.atbuss.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.fosstveit.atbuss.BusStopActivity;
import net.fosstveit.atbuss.MainActivity;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.interfaces.CompassCallback;
import net.fosstveit.atbuss.interfaces.GPSCallback;
import net.fosstveit.atbuss.managers.CompassManager;
import net.fosstveit.atbuss.managers.GPSManager;
import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.BusStopAdapter;
import net.fosstveit.atbuss.utils.Utils;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;

public class AtBussFragment extends SherlockFragment implements GPSCallback,
		CompassCallback {
	private GPSManager gpsManager = null;
	private double currentLon = 0;
	private double currentLat = 0;

	private long lastCompassUpdate = System.currentTimeMillis();

	private Location currentLocation;

	private CompassManager compassManager = null;
	private int currentDirection = 0;

	private ListView listSelectStop;
	private BusStopAdapter busStopAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
				Boolean.TRUE);

		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.fragment_at_buss, container, false);

		listSelectStop = (ListView) rl.findViewById(R.id.listSelectStop);
		listSelectStop.setOnItemClickListener(busStopSelected);

		busStopAdapter = new BusStopAdapter(getSherlockActivity());
		listSelectStop.setAdapter(busStopAdapter);

		return rl;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startGPS();
		startCompass();
	}

	@Override
	public void onStart() {
		super.onStart();
		startGPS();
		startCompass();
	}

	@Override
	public void onStop() {
		super.onStop();
		stopGPS();
		stopCompass();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopGPS();
		stopCompass();
	}

	@Override
	public void onGPSUpdate(Location location) {
		currentLon = location.getLongitude();
		currentLat = location.getLatitude();

		currentLocation = location;

		Utils.executeAsyncTask(new GetBusStops());
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

	private OnItemClickListener busStopSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
			Intent intent = new Intent(getSherlockActivity(),
					BusStopActivity.class);
			BusStop b = (BusStop) listSelectStop.getItemAtPosition(i);

			b.setNumUsed(b.getNumUsed() + 1);
			MainActivity.sqliteManager.updateBusStop(b);

			intent.putExtra(MainActivity.BUS_STOP_ID, b.getId());
			intent.putExtra(MainActivity.BUS_STOP_NAME, b.getName());
			startActivity(intent);
		}
	};

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

			gpsManager.startListening(getSherlockActivity());
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

			compassManager.startListening(getSherlockActivity());
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
		List<BusStop> tmpList = new ArrayList<BusStop>();

		@Override
		protected void onPreExecute() {
			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
					Boolean.TRUE);
		}

		@Override
		protected String doInBackground(String... params) {
			if (MainActivity.busStops != null) {
				for (BusStop b : MainActivity.busStops) {
					double distance = calcGeoDistance(currentLat, currentLon,
							b.getLatitude(), b.getLongitude());

					b.setDistance((int) distance);
				}

				double distanceLimit = Integer
						.parseInt(MainActivity.sharedPrefs.getString(
								"Distance", "500"));

				for (BusStop b : MainActivity.busStops) {
					if (b.getDistance() < distanceLimit) {
						tmpList.add(b);
					}
				}
			}
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			Collections.sort(tmpList, new Comparator<BusStop>() {
				@Override
				public int compare(BusStop lhs, BusStop rhs) {
					return (int) lhs.getDistance() - (int) rhs.getDistance();
				}
			});

			busStopAdapter.updateBusStops(tmpList);

			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
					Boolean.FALSE);
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
}
