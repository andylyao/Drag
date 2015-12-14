package com.andy.drag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DragActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag);
		findViewById(R.id.normal).setOnClickListener(this);
		findViewById(R.id.list).setOnClickListener(this);
		findViewById(R.id.grid).setOnClickListener(this);
		findViewById(R.id.view_pager).setOnClickListener(this);
		findViewById(R.id.web_view).setOnClickListener(this);
		findViewById(R.id.horizontal_scroll_view).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.normal:
				startActivity(new Intent(DragActivity.this, NormalWidgetActivity.class));
				break;
			case R.id.list:
				startActivity(new Intent(DragActivity.this, ListActivity.class));
				break;
			case R.id.grid:
				startActivity(new Intent(DragActivity.this, GridActivity.class));
				break;
			case R.id.view_pager:
				startActivity(new Intent(DragActivity.this, ViewPagerActivity.class));
				break;
			case R.id.web_view:
				startActivity(new Intent(DragActivity.this, WebViewActivity.class));
				break;
			case R.id.horizontal_scroll_view:
				startActivity(new Intent(DragActivity.this, HorizontalScrollViewActivity.class));
				break;
		}
	}
}
