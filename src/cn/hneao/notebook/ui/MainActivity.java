package cn.hneao.notebook.ui;

import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.hneao.notebook.R;
import cn.hneao.notebook.adapter.*;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	private static final String TAG = "myTest";
	private ActionBar actionBar;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		initViewPager();
		// 为ActionBar添加Tab并设置Tab的事件
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 使用菜单填充Action Bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		// getMenuInflater().inflate(R.menu.main, menu);
		// 获取SearchView
		MenuItem menuItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(menuItem);
		if (searchView == null) {
			Log.e("searchView", "获取SearchView失败！");
			return true;
		}
		// 获取并使用searchable
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		ComponentName cn = new ComponentName(this, SearchActivity.class);
		SearchableInfo info = searchManager.getSearchableInfo(cn);
		if (info == null) {
			Log.e("info", "获取searchable失败！");
			return true;
		}
		// 将searchable 与 searchView绑定
		searchView.setSearchableInfo(info);

		searchView.setSubmitButtonEnabled(true);// 显示查询按钮
		searchView.setIconifiedByDefault(true);// 搜索图标的位置
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Intent intent = new Intent();
		if (id == R.id.action_settings) {
			// 系统设置页面
			intent.setClass(MainActivity.this, SetActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_add) {
			// 添加考生页面
			intent.setClass(MainActivity.this, AddStudentActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		//切换ViewPager的页面
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		// 为ViewPager创建一个适配器，该适配器返回一个Fragment供ViewPager使用
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), getApplicationContext());
		// 获取ViewPager
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		// 设置适配器
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 当ViewPager页面切换时更改显示ActionBar的Tab
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
