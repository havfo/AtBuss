package net.fosstveit.atbuss;

import net.fosstveit.atbuss.interfaces.OnLoadDataListener;
import net.fosstveit.atbuss.utils.AtBussViewPagerAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends ActionBarActivity implements
OnLoadDataListener {

	public final static String BUS_STOP_ID = "net.fosstveit.atbuss.BUSSTOPID";
	public final static String BUS_STOP_NAME = "net.fosstveit.atbuss.BUSSTOPNAME";
	public final static String BUS_ROUTE_NAME = "net.fosstveit.atbuss.BUSROUTE";
	public final static String BUS_ROUTE_TIME = "net.fosstveit.atbuss.BUSTIME";
	public final static String BUS_ROUTE_SCHED = "net.fosstveit.atbuss.BUSSCHED";

	private ActionBar mActionBar;
	private ViewPager mPager;
	private ActionBar.Tab tab;

	private AtBussApplication app = null;
	ProgressDialog progress = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		app = (AtBussApplication) getApplication();
		
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mPager = (ViewPager) findViewById(R.id.atbusspager);

		FragmentManager fm = getSupportFragmentManager();

		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				mActionBar.setSelectedNavigationItem(position);
			}
		};

		mPager.setOnPageChangeListener(ViewPagerListener);
		AtBussViewPagerAdapter viewpageradapter = new AtBussViewPagerAdapter(fm);
		mPager.setAdapter(viewpageradapter);

		// Capture tab button clicks
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
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
		
		if (!app.hasData()) {
			app.addDataListener(this);
			progress = new ProgressDialog(this);
			progress.setMessage("Laster ned nye stopp...");
			progress.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_at_buss, menu);
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

	@Override
	public void onLoadData() {
		progress.dismiss();
	}
}
