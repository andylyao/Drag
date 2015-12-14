package com.andy.drag;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewPagerActivity extends BaseActivity {

	private ViewPager mViewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pager);
		mViewPager = (ViewPager)findViewById(R.id.view_pager);
		mViewPager.setAdapter(new ViewPagerAdapter());
	}

	class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TextView text = new TextView(getBaseContext());
			text.setText("项目" + position);
			text.setGravity(Gravity.CENTER);
			text.setBackgroundColor(Color.RED);
			container.addView(text);
			return text;
		}
	}
}
