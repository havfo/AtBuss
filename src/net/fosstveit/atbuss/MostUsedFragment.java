package net.fosstveit.atbuss;

import com.actionbarsherlock.app.SherlockFragment;

import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.BusStopAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class MostUsedFragment extends SherlockFragment {
	private ListView listSelectStop;
	private BusStopAdapter busStopAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
				Boolean.TRUE);

		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.activity_at_buss, container, false);

		listSelectStop = (ListView) rl.findViewById(R.id.listSelectStop);
		listSelectStop.setOnItemClickListener(busStopSelected);

		busStopAdapter = new BusStopAdapter(getSherlockActivity());
		listSelectStop.setAdapter(busStopAdapter);
		
		busStopAdapter.updateBusStops(MainActivity.mostUsedBusStops);

		return rl;
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
}
