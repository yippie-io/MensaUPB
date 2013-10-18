package de.najidev.mensaupb.activities;

import java.sql.Date;
import java.util.*;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.*;
import com.actionbarsherlock.view.Menu;
import com.googlecode.androidannotations.annotations.*;

import de.najidev.mensaupb.*;
import de.najidev.mensaupb.adapter.*;
import de.najidev.mensaupb.dialog.*;
import de.najidev.mensaupb.entity.*;
import de.najidev.mensaupb.helper.*;
import de.najidev.mensaupb.helper.Context;

@EActivity(R.layout.main)
@OptionsMenu(R.menu.main)
public class MainActivity extends SherlockActivity implements
		OnPageChangeListener, TabListener {

	String actionBarTitle;
	ViewPager dayPager;
	DayPagerAdapter dayPagerAdapter;
	MenuRepository menuRepository;
	Context context;
	Configuration config;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);

		ServiceContainer container = ServiceContainer.getInstance();
		if (!container.isInitialized()) {
			try {
				container = ServiceContainer.getInstance().initialize(
						getApplicationContext());
			} catch (final Exception e) {
				e.printStackTrace();
				finish();
			}
		}

		context = container.getContext();
		menuRepository = container.getMenuRepository();
		config = container.getConfiguration();

	}

	@AfterViews
	void afterViews() {
		dayPagerAdapter = new DayPagerAdapter(this);
		dayPager = (ViewPager) findViewById(R.id.viewpager);
		dayPager.setOnPageChangeListener(this);
		dayPager.setAdapter(dayPagerAdapter);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		final String[] germanDays = new String[] { "Mo", "Di", "Mi", "Do", "Fr" };
		int i = 0;
		final Calendar calendar = Calendar.getInstance();
		for (final Date date : context.getAvailableDates()) {
			calendar.setTime(date);
			final ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText(germanDays[i++] + "\n"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "."
					+ (calendar.get(Calendar.MONTH) + 1) + ".");
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}

		if (menuRepository.dataIsNotLocallyAvailable()) {
			new PrepareMenuRepositoryTask(this, context, menuRepository)
					.execute();
		}

		final Date today = new Date(new java.util.Date().getTime());
		i = 0;
		for (final Date date : context.getAvailableDates()) {
			if (date.toString().equals(today.toString())) {
				dayPager.setCurrentItem(i);
				break;
			}

			i++;
		}

		String location;

		if (0 == i) {
			location = config.getMondayLocation();
		}
		else if (1 == i) {
			location = config.getTuesdayLocation();
		}
		else if (2 == i) {
			location = config.getWednesdayLocation();
		}
		else if (3 == i) {
			location = config.getThursdayLocation();
		}
		else {
			location = config.getFridayLocation();
		}

		context.setCurrentLocation(location);
		changedLocation();
	}

	@OptionsItem(R.id.ab_changeLocation)
	void abChangeLocationClicked() {
		final Intent i = new Intent(this, ChooseOnListDialog.class);

		i.putExtra("title", "Ort wählen");
		i.putExtra("list", context.getLocationTitle());

		this.startActivityForResult(i, 1);
	}

	@OptionsItem(R.id.ab_times)
	void abTimesClicked() {
		this.startActivity(new Intent().setClass(this, OpeningTimeDialog.class));
	}

	@OptionsItem(R.id.ab_refresh)
	void abRefreshClicked() {
		new PrepareMenuRepositoryTask(this, context, menuRepository).execute();
	}

	@OptionsItem(R.id.ab_settings)
	void abSettingsClicked() {
		this.startActivity(new Intent().setClass(this, SettingsActivity.class));
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (1 == requestCode && 1 == resultCode) {
			context.setCurrentLocation(context.getLocationTitle()[data
					.getIntExtra("chosen", 0)]);
			changedLocation();
		}
	}

	protected void changedLocation() {
		getSupportActionBar().setTitle(context.getCurrentLocationTitle());
		dayPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPageSelected(final int arg0) {
		getSupportActionBar().getTabAt(arg0).select();
	}

	@Override
	public void onTabSelected(final Tab tab, final FragmentTransaction ft) {
		dayPager.setCurrentItem(tab.getPosition());
	}

	public DayPagerAdapter getDayPagerAdapter() {
		return dayPagerAdapter;
	}

	@Override
	public void onTabUnselected(final Tab tab, final FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(final Tab tab, final FragmentTransaction ft) {
	}

	@Override
	public void onPageScrollStateChanged(final int arg0) {
	}

	@Override
	public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
	}
}