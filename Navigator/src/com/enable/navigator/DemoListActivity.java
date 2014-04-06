package com.enable.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public abstract class DemoListActivity extends ListActivity {

	protected class IntentPair {
		String name;
		Intent intent;

		IntentPair(String name, Intent intent) {
			this.name = name;
			this.intent = intent;
		}
	}

	protected IntentPair[] intents;

	protected abstract void constructList();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		constructList();

		setListAdapter(new SimpleAdapter(this, getData(intents),
				android.R.layout.simple_list_item_1, new String[] { "title" },
				new int[] { android.R.id.text1 }));
		getListView().setTextFilterEnabled(true);
	}

	protected List<Map<String, Object>> getData(IntentPair[] intents) {

		List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

		for (IntentPair pair : intents) {
			addItem(myData, pair.name, pair.intent);
		}

		// Collections.sort(myData, sDisplayNameComparator);

		return myData;
	}

	protected Intent activityIntent(String pkg, String componentName) {
		Intent result = new Intent();
		result.setClassName(pkg, componentName);
		return result;
	}

	protected void addItem(List<Map<String, Object>> data, String name,
			Intent intent) {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("title", name);
		temp.put("intent", intent);
		data.add(temp);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) l
				.getItemAtPosition(position);

		Intent intent = (Intent) map.get("intent");
		startActivity(intent);
	}

}
