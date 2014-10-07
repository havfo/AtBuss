package net.fosstveit.atbuss.fragments;

import java.util.ArrayList;

import net.fosstveit.atbuss.BusStopActivity;
import net.fosstveit.atbuss.MainActivity;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.objects.BusRoute;
import net.fosstveit.atbuss.utils.BusRouteAdapter;
import net.fosstveit.atbuss.utils.Utils;
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

import com.actionbarsherlock.app.SherlockFragment;

public class BusStopInfoFragment extends SherlockFragment {
	private ListView listSelectRoutes;
	private BusRouteAdapter busRouteAdapter;
	private int stopId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
				Boolean.TRUE);

		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.fragment_bus_route, container, false);

		listSelectRoutes = (ListView) rl.findViewById(R.id.listBusRoutes);
		listSelectRoutes.setOnItemClickListener(busRouteSelected);

		busRouteAdapter = new BusRouteAdapter(getSherlockActivity());
		listSelectRoutes.setAdapter(busRouteAdapter);

		return rl;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getSherlockActivity().getIntent().getExtras();
		if (extras != null) {
			stopId = (int) extras.getInt(MainActivity.BUS_STOP_ID);
		}
		
		getBusRoutes();
	}

	private OnItemClickListener busRouteSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
//			Intent intent = new Intent(getSherlockActivity(),
//					BusStopActivity.class);
//			BusRoute b = (BusRoute) listSelectRoutes.getItemAtPosition(i);
//
//			intent.putExtra(MainActivity.BUS_STOP_ID, b.getId());
//			intent.putExtra(MainActivity.BUS_STOP_NAME, b.getName());
//			startActivity(intent);
		}
	};

	private void getBusRoutes() {
		new GetBusRoutes().execute();
	}

	private class GetBusRoutes extends AsyncTask<String, Void, String> {

		private ArrayList<BusRoute> routes;

		@Override
		protected void onPreExecute() {
			getSherlockActivity().setProgressBarIndeterminateVisibility(
					Boolean.TRUE);
		}

		@Override
		protected String doInBackground(String... params) {
			routes = Utils.getRoutes(stopId);
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			if (routes != null) {
				busRouteAdapter.updateBusRoutes(routes);
			} else {
				busRouteAdapter.updateBusRoutes(new ArrayList<BusRoute>());
			}

			getSherlockActivity().setProgressBarIndeterminateVisibility(
					Boolean.FALSE);
		}
	}
}
