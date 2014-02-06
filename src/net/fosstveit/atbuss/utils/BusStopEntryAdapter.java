package net.fosstveit.atbuss.utils;

import java.util.ArrayList;
import java.util.Locale;

import net.fosstveit.atbuss.AtBussActivity;
import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.objects.BusStop;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public final class BusStopEntryAdapter extends ArrayAdapter<BusStop> {

	private final int busStopItemLayoutResource;
	private Filter filter;
	private ArrayList<BusStop> filtered;

	public BusStopEntryAdapter(final Context context,
			final int busStopItemLayoutResource) {
		super(context, 0);
		this.busStopItemLayoutResource = busStopItemLayoutResource;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {

		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final BusStop entry = getItem(position);

		viewHolder.titleView.setText(entry.getName());

		final String subTitle = (int) entry.getDistance() + " meter unna";

		viewHolder.subTitleView.setText(subTitle);

		return view;
	}

	private View getWorkingView(final View convertView) {
		View workingView = null;

		if (null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(busStopItemLayoutResource, null);
		} else {
			workingView = convertView;
		}

		return workingView;
	}

	private ViewHolder getViewHolder(final View workingView) {
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;

		if (null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();

			viewHolder.titleView = (TextView) workingView
					.findViewById(R.id.bus_stop_name);
			viewHolder.subTitleView = (TextView) workingView
					.findViewById(R.id.bus_stop_distance);

			workingView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) tag;
		}

		return viewHolder;
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
					lItems.addAll(AtBussActivity.busStops);
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
					result.values = AtBussActivity.busStops;
					result.count = AtBussActivity.busStops.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filtered = (ArrayList<BusStop>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filtered.size(); i < l; i++)
				add(filtered.get(i));
			notifyDataSetInvalidated();
		}
	}

	private static class ViewHolder {
		public TextView titleView;
		public TextView subTitleView;
	}

}