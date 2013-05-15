package com.fovea.epublite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public class EpubLiteActivity extends SherlockFragmentActivity implements OnBackStackChangedListener {

	private ViewPager pager = null;
	private ContentsAdapter adapter = null;
	private static final String MODEL = "model";
	private SharedPreferences prefs = null;
	private static final String PREF_LAST_POSITION = "lastPosition";
	private static final String PREF_SAVE_LAST_POSITION = "saveLastPosition";
	private static final String PREF_KEEP_SCREEN_ON = "keepScreenOn";
	private ModelFragment model = null;
	
	private View sidebar = null;
	private View divider = null;
	
	private SimpleContentFragment help = null;
	private SimpleContentFragment about = null;
	
	private static final String HELP = "help";
	private static final String ABOUT = "about";
	private static final String FILE_HELP = 
			"file:///android_asset/misc/help.html";
	private static final String FILE_ABOUT =
			"file:///android_asset/misc/about.html";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getSupportFragmentManager().findFragmentByTag(MODEL) == null) {
			model = new ModelFragment();
			getSupportFragmentManager().beginTransaction()
				.add(model, MODEL).commit();
		} else {
			model = 
			  (ModelFragment)getSupportFragmentManager().findFragmentByTag(MODEL);
		}
		
		setContentView(R.layout.activity_epub_lite);
		
		pager = (ViewPager) findViewById(R.id.pager);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		sidebar = findViewById(R.id.sidebar);
		divider = findViewById(R.id.divider);
		
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			openSidebar();
		}
		
		help = (SimpleContentFragment)getSupportFragmentManager()
				.findFragmentByTag(HELP);
		about = (SimpleContentFragment)getSupportFragmentManager()
				.findFragmentByTag(ABOUT);
		
		
		UpdateReceiver.scheduleAlarm(this);
	}
	
	@Override
	public void onPause() {
		unregisterReceiver(onUpdate);
		
		if (prefs != null) {
			int position = pager.getCurrentItem();
			prefs.edit().putInt(PREF_LAST_POSITION, position).apply();
		}
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (prefs != null) {
			pager.setKeepScreenOn(prefs.getBoolean(PREF_KEEP_SCREEN_ON, false));
		}
		
		IntentFilter f = 
				new IntentFilter(DownloadInstallService.ACTION_UPDATE_READY);
		
		f.setPriority(1000);
		registerReceiver(onUpdate, f);
	}
	
	public void onBackStackChanged() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			LinearLayout.LayoutParams p =
					(LinearLayout.LayoutParams)sidebar.getLayoutParams();
			
			if (p.weight > 0 ) {
				p.weight = 0;
				sidebar.setLayoutParams(p);
				divider.setVisibility(View.GONE);
			}
		}
	}
	
	void openSidebar() {
		LinearLayout.LayoutParams p = 
				(LinearLayout.LayoutParams)sidebar.getLayoutParams();
		
		if (p.weight == 0) {
			p.weight = 3;
			sidebar.setLayoutParams(p);
		}
		divider.setVisibility(View.VISIBLE);
	}
	
	void showAbout() {
		if (sidebar != null) {
			openSidebar();
			
			if (about == null) {
				about = SimpleContentFragment.newInstance(FILE_ABOUT);
			}
			
			getSupportFragmentManager().beginTransaction()
										.addToBackStack(null)
										.replace(R.id.sidebar, about).commit();
		} else {
			Intent i = new Intent(this, SimpleContentActivity.class);
			i.putExtra(SimpleContentActivity.EXTRA_FILE, FILE_ABOUT);
			startActivity(i);
		}
	}
	
	void showHelp() {
		if (sidebar != null) {
			openSidebar();
			
			if (about == null) {
				about = SimpleContentFragment.newInstance(FILE_HELP);
			}
			
			getSupportFragmentManager().beginTransaction()
			.addToBackStack(null)
			.replace(R.id.sidebar, help).commit();
		} else {
			Intent i = new Intent(this, SimpleContentActivity.class);
			i.putExtra(SimpleContentActivity.EXTRA_FILE, FILE_HELP);
			startActivity(i);
		}
	}
	
	private BroadcastReceiver onUpdate = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent i) {
			model.updateBook();
			abortBroadcast();
		}
	};
	
	void setupPager(SharedPreferences prefs, BookContents contents) {
		this.prefs = prefs;
		
		if (prefs.getBoolean(PREF_SAVE_LAST_POSITION, false)) {
			pager.setCurrentItem(prefs.getInt(PREF_LAST_POSITION, 0));
		}
		
		adapter = new ContentsAdapter(this, contents);
		pager.setAdapter(adapter);
		pager.setKeepScreenOn(prefs.getBoolean(PREF_KEEP_SCREEN_ON, false));
		
		findViewById(R.id.progressBar1).setVisibility(View.GONE);
		findViewById(R.id.pager).setVisibility(View.VISIBLE);	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.epub_lite, menu);
		//return true;
		new MenuInflater(this).inflate(R.menu.epub_lite, menu);
		return(super.onCreateOptionsMenu(menu));
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			pager.setCurrentItem(0, false);
			return(true);
		case R.id.about:
			showAbout();
			return(true);
		case R.id.help:
			showHelp();
			return(true);
		case R.id.notes:
			Intent i = new Intent(this, NoteActivity.class);
			i.putExtra(NoteActivity.EXTRA_POSITION, pager.getCurrentItem());
			startActivity(i);
			return(true);
		case R.id.update:			
			startService(new Intent(this, DownloadCheckService.class));
			return(true);
		case R.id.settings:
			startActivity(new Intent(this, Preferences.class));
			return(true);

		}
		return(super.onOptionsItemSelected(item));
	}

}
