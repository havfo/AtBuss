package net.fosstveit.atbuss;

import java.util.ArrayList;

import net.fosstveit.atbuss.interfaces.OnLoadDataListener;
import net.fosstveit.atbuss.managers.AtBussDataManager;
import net.fosstveit.atbuss.utils.Utils;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class AtBussApplication extends Application {

	private ArrayList<OnLoadDataListener> dataListener;

	private AtBussDataManager dataManager = null;

	private boolean firstRun = false;

	// private ArrayList<BusStop> busStops;

	private SharedPreferences sharedPrefs;

	private boolean hasData = false;

	@Override
	public void onCreate() {
		dataListener = new ArrayList<OnLoadDataListener>();

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		dataManager = new AtBussDataManager(this);

		parseBusStops();
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		onDataUpdate();
	}

	private void onDataUpdate() {
		if (dataListener != null && dataListener.size() > 0) {
			for (OnLoadDataListener o : dataListener) {
				o.onLoadData();
			}
		}
	}

	public void addDataListener(OnLoadDataListener oldl) {
		dataListener.add(oldl);
	}

	private void parseBusStops() {
		if (sharedPrefs.getBoolean("my_first_time", true)) {
			firstRun = true;
			new PopulateBusStops().execute();
			sharedPrefs.edit().putBoolean("my_first_time", false).commit();
		} else {
			new PopulateBusStops().execute();
			onDataUpdate();
		}
	}

	private class PopulateBusStops extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(final String... args) {
			if (firstRun) {
				dataManager.clearBusStops();
				dataManager.addBusStops(Utils.getBusStops());
				dataManager.addVersion("" + Utils.getVersion());
				firstRun = false;
			} else {
				int version = dataManager.getLatestVersion();
				int latest = Utils.getVersion();
				
				if (latest != -1 && latest > version) {
					dataManager.clearBusStops();
					dataManager.addBusStops(Utils.getBusStops());
					dataManager.addVersion("" + Utils.getVersion());
				}
			}

			hasData = true;
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			onDataUpdate();
		}
	}

	public AtBussDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(AtBussDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public boolean isFirstRun() {
		return firstRun;
	}

	public void setFirstRun(boolean firstRun) {
		this.firstRun = firstRun;
	}

	// public ArrayList<BusStop> getBusStops() {
	// return busStops;
	// }
	//
	// public void setBusStops(ArrayList<BusStop> busStops) {
	// this.busStops = busStops;
	// }

	public SharedPreferences getSharedPrefs() {
		return sharedPrefs;
	}

	public void setSharedPrefs(SharedPreferences sharedPrefs) {
		this.sharedPrefs = sharedPrefs;
	}

	public boolean hasData() {
		return hasData;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

}
