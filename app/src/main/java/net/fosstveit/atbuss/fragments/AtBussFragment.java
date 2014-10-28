package net.fosstveit.atbuss.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.fosstveit.atbuss.AtBussApplication;
import net.fosstveit.atbuss.BusStopActivity;
import net.fosstveit.atbuss.MainActivity;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.interfaces.GPSCallback;
import net.fosstveit.atbuss.managers.GPSManager;
import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.BusStopAdapter;
import net.fosstveit.atbuss.utils.Utils;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

public class AtBussFragment extends Fragment implements GPSCallback /*
																			 * ,
																			 * CompassCallback
																			 */{
	private GPSManager gpsManager = null;
	private double currentLon = 0;
	private double currentLat = 0;

	// private long lastCompassUpdate = System.currentTimeMillis();

	private Location currentLocation;

	// private CompassManager compassManager = null;
	// private int currentDirection = 0;

	private ArrayList<BusStop> stops;

	private ListView listSelectStop;
	private BusStopAdapter busStopAdapter;

	private AtBussApplication app = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.fragment_at_buss, container, false);

		listSelectStop = (ListView) rl.findViewById(R.id.listSelectStop);
		listSelectStop.setOnItemClickListener(busStopSelected);

		busStopAdapter = new BusStopAdapter(getActivity());
		listSelectStop.setAdapter(busStopAdapter);

		return rl;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActivity().setProgressBarIndeterminateVisibility(
				Boolean.TRUE);

		app = (AtBussApplication) getActivity().getApplication();

		startGPS();
		// startCompass();
	}

	@Override
	public void onStart() {
		super.onStart();
		startGPS();
		// startCompass();
	}

	@Override
	public void onStop() {
		super.onStop();
		stopGPS();
		// stopCompass();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopGPS();
		// stopCompass();
	}

	@Override
	public void onGPSUpdate(Location location) {
		currentLon = location.getLongitude();
		currentLat = location.getLatitude();

		currentLocation = location;

		if (app.hasData()) {
			Utils.executeAsyncTask(new GetBusStops());
		}
	}

	// @Override
	// public void onCompassUpdate(int direction) {
	// currentDirection = direction;
	//
	// long time = System.currentTimeMillis();
	//
	// if (time - lastCompassUpdate > 500 && app.hasData()) {
	// lastCompassUpdate = time;
	// new RotateBusArrows().execute();
	// }
	// }

	private OnItemClickListener busStopSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
			Intent intent = new Intent(getActivity(),
					BusStopActivity.class);
			BusStop b = (BusStop) listSelectStop.getItemAtPosition(i);

			b.setNumUsed(b.getNumUsed() + 1);
			app.getDataManager().updateBusStop(b);

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

			gpsManager.startListening(getActivity());
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

	// private int getLatitudeIndex(double latitude) {
	// double lats = 0.0045;
	// double latst = 62.50;
	//
	// int latind = (int) ((latitude - latst) / lats);
	//
	// return latind;
	// }
	//
	// private int getLongitudeIndex(double longitude) {
	// double lons = 0.01;
	// double lonst = 8.50;
	//
	// int lonind = (int) ((longitude - lonst) / lons);
	//
	// return lonind;
	// }

	// private void startCompass() {
	// if (compassManager == null) {
	// compassManager = new CompassManager();
	//
	// compassManager.startListening(getSherlockActivity());
	// compassManager.setCompassCallback(this);
	// }
	// }
	//
	// private void stopCompass() {
	// if (compassManager != null) {
	// compassManager.stopListening();
	// compassManager.setCompassCallback(null);
	//
	// compassManager = null;
	// }
	// }

	private class GetBusStops extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			getActivity().setProgressBarIndeterminateVisibility(
					Boolean.TRUE);
		}

		@Override
		protected String doInBackground(String... params) {
				double distanceLimit = Integer.parseInt(app.getSharedPrefs()
						.getString("Distance", "500"));

				stops = app.getDataManager().getBusStopsInRange(currentLat,
						currentLon, distanceLimit);

				for (BusStop b : stops) {
					double distance = calcGeoDistance(currentLat, currentLon,
							b.getLatitude(), b.getLongitude());
					b.setDistance((int) distance);
				}
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			Collections.sort(stops, new Comparator<BusStop>() {
				@Override
				public int compare(BusStop lhs, BusStop rhs) {
					return (int) lhs.getDistance() - (int) rhs.getDistance();
				}
			});

			busStopAdapter.updateBusStops(stops);

			getActivity().setProgressBarIndeterminateVisibility(
					Boolean.FALSE);
		}
	}

	// private class RotateBusArrows extends AsyncTask<String, Void, String> {
	// @Override
	// protected void onPreExecute() {
	// }
	//
	// @Override
	// protected String doInBackground(String... strings) {
	//
	// for (int i = 0; i < busStopAdapter.getCount(); i++) {
	// Location targetLocation = new Location("");
	// targetLocation.setLatitude(busStopAdapter.getItem(i)
	// .getLatitude());
	// targetLocation.setLongitude(busStopAdapter.getItem(i)
	// .getLongitude());
	//
	// float bearingTo = currentLocation.bearingTo(targetLocation);
	//
	// if (bearingTo < 0) {
	// bearingTo = bearingTo + 360;
	// }
	//
	// int direction = (int) (bearingTo - currentDirection);
	//
	// if (direction < 0) {
	// direction = direction + 360;
	// }
	//
	// busStopAdapter.getItem(i).setDirection(direction);
	// }
	//
	// return "Done";
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// busStopAdapter.notifyDataSetChanged();
	// }
	// }
}
