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
		// ΪActionBar���Tab������Tab���¼�
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// ʹ�ò˵����Action Bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		// getMenuInflater().inflate(R.menu.main, menu);
		// ��ȡSearchView
		MenuItem menuItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(menuItem);
		if (searchView == null) {
			Log.e("searchView", "��ȡSearchViewʧ�ܣ�");
			return true;
		}
		// ��ȡ��ʹ��searchable
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		ComponentName cn = new ComponentName(this, SearchActivity.class);
		SearchableInfo info = searchManager.getSearchableInfo(cn);
		if (info == null) {
			Log.e("info", "��ȡsearchableʧ�ܣ�");
			return true;
		}
		// ��searchable �� searchView��
		searchView.setSearchableInfo(info);

		searchView.setSubmitButtonEnabled(true);// ��ʾ��ѯ��ť
		searchView.setIconifiedByDefault(true);// ����ͼ���λ��
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
			// ϵͳ����ҳ��
			intent.setClass(MainActivity.this, SetActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_add) {
			// ��ӿ���ҳ��
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
		//�л�ViewPager��ҳ��
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

	/**
	 * ��ʼ��ViewPager
	 */
	private void initViewPager() {
		// ΪViewPager����һ����������������������һ��Fragment��ViewPagerʹ��
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), getApplicationContext());
		// ��ȡViewPager
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		// ����������
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// ��ViewPagerҳ���л�ʱ������ʾActionBar��Tab
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
