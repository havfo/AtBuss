package net.fosstveit.atbuss.fragments;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import net.fosstveit.atbuss.AtBussApplication;
import net.fosstveit.atbuss.BusStopActivity;
import net.fosstveit.atbuss.MainActivity;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.interfaces.OnLoadDataListener;
import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.BusStopAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class MostUsedFragment extends SherlockFragment implements
		OnLoadDataListener {
	private ListView listSelectStop;
	private BusStopAdapter busStopAdapter;
	
	private AtBussApplication app = null;
	
	private ArrayList<BusStop> stops;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		
		app = (AtBussApplication) getSherlockActivity().getApplication();

		if (!app.hasData()) {
			app.addDataListener(this);
		} else {
			createAdapterView();
		}
	}

	private OnItemClickListener busStopSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
			Intent intent = new Intent(getSherlockActivity(),
					BusStopActivity.class);
			BusStop b = (BusStop) listSelectStop.getItemAtPosition(i);

			b.setNumUsed(b.getNumUsed() + 1);
			app.getDataManager().updateBusStop(b);

			intent.putExtra(MainActivity.BUS_STOP_ID, b.getId());
			intent.putExtra(MainActivity.BUS_STOP_NAME, b.getName());
			startActivity(intent);
		}
	};

	private void createAdapterView() {
		new GetBusStops().execute();
	}

	@Override
	public void onLoadData() {
		createAdapterView();
	}
	
	private class GetBusStops extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			stops = app.getDataManager().getMostUsedBusStops(10);
			
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			busStopAdapter.updateBusStops(stops);
		}
	}
}
