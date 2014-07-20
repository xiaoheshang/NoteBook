package cn.hneao.notebook.ui;

import cn.hneao.notebook.R;
import cn.hneao.notebook.R.id;
import cn.hneao.notebook.R.layout;
import cn.hneao.notebook.R.menu;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class TabActivity extends ActionBarActivity implements TabListener {
	
	private static final String SELECTED_ITEM = "selected_item";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab);
		// ActionBar Tab导航
		final ActionBar actionBar = getSupportActionBar();
		actionBar.addTab(actionBar.newTab().setText("全部").setTabListener(this));
		actionBar
				.addTab(actionBar.newTab().setText("未录取").setTabListener(this));
		actionBar
				.addTab(actionBar.newTab().setText("已录取").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("关注").setTabListener(this));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(SELECTED_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(SELECTED_ITEM));

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(SELECTED_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
		
	}
	
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
		// TODO Auto-generated method stub
		Fragment fragment=new DummyFragment();
		Bundle args=new Bundle();
		args.putInt(DummyFragment.ARG_SECTION_NUMBER, tab.getPosition()+1);
		
		fragment.setArguments(args);
		FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.container, fragment);
		ft.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

}
