package net.fosstveit.atbuss.managers;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import net.fosstveit.atbuss.interfaces.GPSCallback;

public class GPSManager {
	private static final int gpsMinTime = 20;
	private static final int gpsMinDistance = 20;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private GPSCallback gpsCallback = null;

	private Location currentBestLocation;

	public GPSManager() {
		locationListener = new LocationListener() {
			public void onProviderDisabled(final String provider) {
			}

			public void onProviderEnabled(final String provider) {
			}

			public void onStatusChanged(final String provider,
					final int status, final Bundle extras) {
			}

			public void onLocationChanged(final Location location) {
				if (location != null && gpsCallback != null) {
					if (isBetterLocation(location, currentBestLocation)) {
						currentBestLocation = location;
						gpsCallback.onGPSUpdate(location);
					}
				}
			}
		};
	}

	public void startListening(final Activity activity) {
		if (locationManager == null) {
			locationManager = (LocationManager) activity
					.getSystemService(Context.LOCATION_SERVICE);
		}

		Location lastGPS = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		currentBestLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (lastGPS != null) {
			if (isBetterLocation(lastGPS, currentBestLocation)) {
				currentBestLocation = lastGPS;
			}
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				GPSManager.gpsMinTime, GPSManager.gpsMinDistance,
				locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, GPSManager.gpsMinTime,
				GPSManager.gpsMinDistance, locationListener);
	}

	public void stopListening() {
		try {
			if (locationManager != null && locationListener != null) {
				locationManager.removeUpdates(locationListener);
			}
			locationManager = null;
		} catch (final Exception ex) {
		}
	}

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			return true;
		}

		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		if (isSignificantlyNewer) {
			return true;
		} else if (isSignificantlyOlder) {
			return false;
		}

		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public void setGPSCallback(final GPSCallback gpsCallback) {
		this.gpsCallback = gpsCallback;
	}

	public GPSCallback getGPSCallback() {
		return gpsCallback;
	}
}
