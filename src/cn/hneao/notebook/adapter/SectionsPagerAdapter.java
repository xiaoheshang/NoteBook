package cn.hneao.notebook.adapter;

import java.util.Locale;

import cn.hneao.notebook.R;
import cn.hneao.notebook.ui.PlaceholderFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	private Context context;
	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public SectionsPagerAdapter(FragmentManager fm,Context context){
		super(fm);
		this.context=context;
	}

	@Override
	public Fragment getItem(int position) {
		return PlaceholderFragment.newInstance(position + 1);
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return context.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return context.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return context.getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
}
