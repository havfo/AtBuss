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

		if (!((AtBussApplication) (getSherlockActivity()).getApplicationContext()).hasData()) {
			((AtBussApplication) (getSherlockActivity()).getApplicationContext()).addDataListener(this);
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
			((AtBussApplication) (getSherlockActivity()).getApplicationContext()).getDataManager().updateBusStop(b);

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
		List<BusStop> tmpList = new ArrayList<BusStop>();

		@Override
		protected void onPreExecute() {
			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
					Boolean.TRUE);
		}

		@Override
		protected String doInBackground(String... params) {
			if (((AtBussApplication) (getSherlockActivity()).getApplicationContext()).getBusStops() != null && ((AtBussApplication) (getSherlockActivity()).getApplicationContext()).getBusStops().size() > 10) {
				ArrayList<BusStop> mostUsed = new ArrayList<BusStop>();
				
				for (int i = 0; i < 10; i++) {
					mostUsed.add(((AtBussApplication) (getSherlockActivity()).getApplicationContext()).getBusStops().get(i));
				}
			}
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			busStopAdapter.updateBusStops(tmpList);

			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
					Boolean.FALSE);
		}
	}
}
