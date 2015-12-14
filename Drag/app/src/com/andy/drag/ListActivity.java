package com.andy.drag;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BaseActivity {

	private ListView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		mListView = (ListView) findViewById(R.id.list);
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mListView.setAdapter(listAdapter);
		List<String> array = new ArrayList<String>();
		for(int i = 0; i < 100; i ++) {
			array.add("这是第" + i + "个item");
		}
		listAdapter.addAll(array);
	}
}
