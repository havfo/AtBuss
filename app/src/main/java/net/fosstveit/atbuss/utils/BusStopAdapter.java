package net.fosstveit.atbuss.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.fosstveit.atbuss.AtBussApplication;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.objects.BusStop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Håvar Aambø Fosstveit on 03.02.14.
 */
public class BusStopAdapter extends BaseAdapter implements Filterable {

	private List<BusStop> busStops = Collections.emptyList();
	private final Context context;
	private Filter filter;

	public BusStopAdapter(Context context) {
		this.context = context;
	}

	public void updateBusStops(List<BusStop> busStops) {
		this.busStops = busStops;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return busStops.size();
	}

	@Override
	public BusStop getItem(int i) {
		return busStops.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//		ImageView arrowView;
		TextView titleView;
		TextView subTitleView;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.at_bus_list_item, parent, false);
//			arrowView = (ImageView) convertView.findViewById(R.id.arrowImage);
			titleView = (TextView) convertView.findViewById(R.id.bus_stop_name);
			subTitleView = (TextView) convertView
					.findViewById(R.id.bus_stop_distance);
			convertView.setTag(new ViewHolder(/* arrowView, */ titleView,
					subTitleView));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//			arrowView = viewHolder.arrowView;
			titleView = viewHolder.titleView;
			subTitleView = viewHolder.subTitleView;
		}

		BusStop busStop = getItem(position);
		titleView.setText(busStop.getName());
		subTitleView.setText(busStop.getDistance() + " meter unna");

//		Matrix matrix = new Matrix();
//		arrowView.setScaleType(ImageView.ScaleType.MATRIX);
//		matrix.postRotate((float) busStop.getDirection(), arrowView
//				.getDrawable().getBounds().width() / 2, arrowView.getDrawable()
//				.getBounds().height() / 2);
//		arrowView.setImageMatrix(matrix);

		return convertView;
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new BusStopNameFilter();
		return filter;
	}

	private class BusStopNameFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<BusStop> filt = new ArrayList<BusStop>();
				ArrayList<BusStop> lItems = new ArrayList<BusStop>();
				synchronized (this) {
					lItems.addAll(((AtBussApplication)context.getApplicationContext()).getDataManager().getAllBusStops());
				}
				for (BusStop b : lItems) {
					if (b.getName().toLowerCase(Locale.ENGLISH)
							.contains(constraint))
						filt.add(b);
				}
				result.count = filt.size();
				result.values = filt;
			} else {
				synchronized (this) {
					result.values = ((AtBussApplication)context.getApplicationContext()).getDataManager().getAllBusStops();
					result.count = ((AtBussApplication)context.getApplicationContext()).getDataManager().getAllBusStops().size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			busStops = (List<BusStop>) results.values;
			List<BusStop> filtered = new ArrayList<BusStop>();
			notifyDataSetChanged();
			for (int i = 0, l = busStops.size(); i < l; i++) {
				filtered.add(busStops.get(i));
			}

			updateBusStops(filtered);

			notifyDataSetInvalidated();
		}
	}

	private static class ViewHolder {
//		public final ImageView arrowView;
		public final TextView titleView;
		public final TextView subTitleView;

		public ViewHolder(/* ImageView arrowView, */ TextView titleView,
				TextView subTitleView) {
//			this.arrowView = arrowView;
			this.titleView = titleView;
			this.subTitleView = subTitleView;
		}
	}
}
