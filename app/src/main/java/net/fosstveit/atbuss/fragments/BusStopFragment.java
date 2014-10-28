package net.fosstveit.atbuss.fragments;

import java.util.Timer;
import java.util.TimerTask;

import net.fosstveit.atbuss.AtBussApplication;
import net.fosstveit.atbuss.BusEventActivity;
import net.fosstveit.atbuss.MainActivity;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.objects.BusEvent;
import net.fosstveit.atbuss.utils.BusEventEntryAdapter;
import net.fosstveit.atbuss.utils.Utils;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class BusStopFragment extends Fragment {
	private ListView listSelectEvent;
	private BusEventEntryAdapter busEventEntryAdapter;
	private String stopName;
	private int stopId;
	private TimerTask doAsynchronousTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setProgressBarIndeterminateVisibility(
				Boolean.TRUE);

		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.fragment_bus_stop, container, false);

		listSelectEvent = (ListView) rl.findViewById(R.id.listBusEvents);
		listSelectEvent.setOnItemClickListener(busEventSelected);

		busEventEntryAdapter = new BusEventEntryAdapter(getActivity(),
				R.layout.bus_stop_list_item);
		listSelectEvent.setAdapter(busEventEntryAdapter);

		return rl;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			stopName = (String) extras.get(MainActivity.BUS_STOP_NAME);
			getActivity().setTitle(stopName);
			stopId = (int) extras.getInt(MainActivity.BUS_STOP_ID);
		}
		
		getBusEvents();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    doAsynchronousTask.cancel();
	}
	
	private OnItemClickListener busEventSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
			Intent intent = new Intent(getActivity(),
					BusEventActivity.class);
			BusEvent b = (BusEvent) listSelectEvent.getItemAtPosition(i);

			intent.putExtra(MainActivity.BUS_STOP_NAME, stopName);
			intent.putExtra(MainActivity.BUS_STOP_ID, stopId);
			intent.putExtra(MainActivity.BUS_ROUTE_NAME, b.getRoute());
			intent.putExtra(MainActivity.BUS_ROUTE_TIME, b.getTime());
			intent.putExtra(MainActivity.BUS_ROUTE_SCHED, b.getSched());

			startActivity(intent);
		}
	};

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
			getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
		}

		@Override
		protected String doInBackground(String... params) {
			int numStops = Integer.parseInt(((AtBussApplication) (getActivity()).getApplicationContext()).getSharedPrefs().getString(
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

			getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
	}
}
