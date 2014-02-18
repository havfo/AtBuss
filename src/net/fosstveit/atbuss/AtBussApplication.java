package net.fosstveit.atbuss;

import java.util.ArrayList;

import net.fosstveit.atbuss.interfaces.OnLoadDataListener;
import net.fosstveit.atbuss.managers.AtBussDataManager;
import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.Utils;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class AtBussApplication extends Application {

	private ArrayList<OnLoadDataListener> dataListener;

	private AtBussDataManager dataManager = null;

	private boolean firstRun = false;

	private ArrayList<BusStop> busStops;

	private SharedPreferences sharedPrefs;

	private boolean hasData = false;

	@Override
	public void onCreate() {
		dataListener = new ArrayList<OnLoadDataListener>();

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		dataManager = new AtBussDataManager(this);

		parseBusStops();
	}
	
	public void updateBusStop() {
		
	}

	private void onDataUpdate() {
		if (dataListener != null && dataListener.size() > 0) {
			for (OnLoadDataListener o : dataListener) {
				o.onLoadData();
			}
		}

		hasData = true;
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
		}
	}

	private class PopulateBusStops extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(
				AtBussApplication.this);

		@Override
		protected void onPreExecute() {
			if (firstRun) {
				dialog.setMessage("Henter data...");
				dialog.show();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
			}
		}

		@Override
		protected String doInBackground(final String... args) {
			if (firstRun) {
				dataManager.clearBusStops();
				Utils.getBusStops();
				Utils.getVersion();
			}

			busStops = dataManager.getAllBusStops();
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			if (firstRun) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				firstRun = false;
			}

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

	public ArrayList<BusStop> getBusStops() {
		return busStops;
	}

	public void setBusStops(ArrayList<BusStop> busStops) {
		this.busStops = busStops;
	}

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
