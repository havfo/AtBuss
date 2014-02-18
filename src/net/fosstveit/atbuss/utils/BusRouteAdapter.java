package net.fosstveit.atbuss.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.objects.BusRoute;
import java.util.Collections;
import java.util.List;

/**
 * Created by Håvar Aambø Fosstveit on 03.02.14.
 */
public class BusRouteAdapter extends BaseAdapter {

	private List<BusRoute> busRoutes = Collections.emptyList();
	private final Context context;

	public BusRouteAdapter(Context context) {
		this.context = context;
	}

	public void updateBusRoutes(List<BusRoute> busRoute) {
		this.busRoutes = busRoute;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return busRoutes.size();
	}

	@Override
	public BusRoute getItem(int i) {
		return busRoutes.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView titleView;
		TextView detailView;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.bus_route_list_item, parent, false);
			titleView = (TextView) convertView
					.findViewById(R.id.bus_route_name);
			detailView = (TextView) convertView
					.findViewById(R.id.bus_route_tostop);
			convertView.setTag(new ViewHolder(titleView, detailView));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			titleView = viewHolder.titleView;
			detailView = viewHolder.detailView;
		}

		BusRoute busRoute = getItem(position);
		titleView.setText(busRoute.getName());
		detailView.setText(busRoute.getToStopName());

		return convertView;
	}

	private static class ViewHolder {
		public final TextView titleView;
		public final TextView detailView;

		public ViewHolder(TextView titleView, TextView subTitleView) {
			this.titleView = titleView;
			this.detailView = subTitleView;
		}
	}
}
