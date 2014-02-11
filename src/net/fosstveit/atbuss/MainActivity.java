package net.fosstveit.atbuss;

import java.util.ArrayList;
import net.fosstveit.atbuss.managers.SQLiteManager;
import net.fosstveit.atbuss.objects.BusStop;
import net.fosstveit.atbuss.utils.Utils;
import net.fosstveit.atbuss.utils.ViewPagerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class MainActivity extends SherlockFragmentActivity {

	public final static String BUS_STOP_ID = "net.fosstveit.atbuss.BUSSTOPID";
	public final static String BUS_STOP_NAME = "net.fosstveit.atbuss.BUSSTOPNAME";
	public final static String BUS_ROUTE_NAME = "net.fosstveit.atbuss.BUSROUTE";
	public final static String BUS_ROUTE_TIME = "net.fosstveit.atbuss.BUSTIME";
	public final static String BUS_ROUTE_SCHED = "net.fosstveit.atbuss.BUSSCHED";

	private ActionBar mActionBar;
	private ViewPager mPager;
	private Tab tab;

	public static SQLiteManager sqliteManager = null;

	public static boolean firstRun = false;

	public static ArrayList<BusStop> busStops;

	public static SharedPreferences sharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		sqliteManager = new SQLiteManager(this);
		parseBusStops();

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mPager = (ViewPager) findViewById(R.id.pager);

		FragmentManager fm = getSupportFragmentManager();

		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				mActionBar.setSelectedNavigationItem(position);
			}
		};

		mPager.setOnPageChangeListener(ViewPagerListener);
		ViewPagerAdapter viewpageradapter = new ViewPagerAdapter(fm);
		mPager.setAdapter(viewpageradapter);

		// Capture tab button clicks
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};

		// Create first Tab
		tab = mActionBar.newTab().setText("I nærheten")
				.setTabListener(tabListener);
		mActionBar.addTab(tab);

		// Create second Tab
		tab = mActionBar.newTab().setText("Mest brukte")
				.setTabListener(tabListener);
		mActionBar.addTab(tab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_at_buss, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			search();
			return true;
		case R.id.menu_oracle:
			askOracle();
			return true;
		case R.id.menu_settings:
			Intent intent = new Intent(MainActivity.this,
					SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void askOracle() {
		Intent intent = new Intent(MainActivity.this, AskOracleActivity.class);
		startActivity(intent);
	}

	public void search() {
		Intent intent = new Intent(MainActivity.this, SearchActivity.class);
		startActivity(intent);
	}

	private void parseBusStops() {
		if (sharedPrefs.getBoolean("my_first_time", true)) {
			firstRun = true;
			new PopulateBusStops().execute();
			sharedPrefs.edit().putBoolean("my_first_time", false).commit();
		} else {
			new PopulateBusStops().execute();
		}
	}

	private class PopulateBusStops extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(
				MainActivity.this);

		@Override
		protected void onPreExecute() {
			if (firstRun) {
				dialog.setMessage("Henter data...");
				dialog.show();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
			}
		}

		@Override
		protected String doInBackground(final String... args) {
			if (firstRun) {
				sqliteManager.clearBusStops();
				Utils.getBusStops();
				Utils.getVersion();
			}

			busStops = sqliteManager.getAllBusStops();
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			if (firstRun) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				firstRun = false;
			}
		}
	}
}
