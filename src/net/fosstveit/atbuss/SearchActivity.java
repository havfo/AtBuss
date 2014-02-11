package net.fosstveit.atbuss;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import net.fosstveit.atbuss.R;
import net.fosstveit.atbuss.R.id;
import net.fosstveit.atbuss.R.layout;
import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.BusStopAdapter;

public class SearchActivity extends SherlockActivity {

	private EditText filterText = null;
	private ListView searchList = null;
	private BusStopAdapter busStopAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);

		searchList = (ListView) findViewById(R.id.searchList);
		searchList.setOnItemClickListener(busStopSelected);

		busStopAdapter = new BusStopAdapter(this);

		searchList.setAdapter(busStopAdapter);
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			busStopAdapter.getFilter().filter(s);
		}
	};

	private OnItemClickListener busStopSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int i, long l) {
			Intent intent = new Intent(SearchActivity.this,
					BusStopActivity.class);
			BusStop b = (BusStop) searchList.getItemAtPosition(i);
			
			b.setNumUsed(b.getNumUsed() + 1);
			MainActivity.sqliteManager.updateBusStop(b);
			
			intent.putExtra(MainActivity.BUS_STOP_ID, b.getId());
			intent.putExtra(MainActivity.BUS_STOP_NAME, b.getName());
			startActivity(intent);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		filterText.removeTextChangedListener(filterTextWatcher);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
