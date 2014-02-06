package net.fosstveit.atbuss.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.objects.BusEvent;

public final class BusEventEntryAdapter extends ArrayAdapter<BusEvent> {

	private final int busEventItemLayoutResource;

	public BusEventEntryAdapter(final Context context,
			final int busEventItemLayoutResource) {
		super(context, 0);
		this.busEventItemLayoutResource = busEventItemLayoutResource;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final BusEvent entry = getItem(position);

		viewHolder.titleView.setText(entry.getRoute());
		final String dirTitle = entry.getDir();
		viewHolder.dirView.setText(dirTitle);
		final String subTitle = "Ca. " + entry.getTime() + "       "
				+ entry.getMinutes() + " min";
		viewHolder.subTitleView.setText(subTitle);

		return view;
	}

	private View getWorkingView(final View convertView) {
		View workingView = null;

		if (null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(busEventItemLayoutResource, null);
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
					.findViewById(R.id.bus_event_name);
			viewHolder.dirView = (TextView) workingView
					.findViewById(R.id.bus_event_dir);
			viewHolder.subTitleView = (TextView) workingView
					.findViewById(R.id.bus_event_details);
			workingView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) tag;
		}
		return viewHolder;
	}

	private static class ViewHolder {
		public TextView titleView;
		public TextView dirView;
		public TextView subTitleView;
	}

}