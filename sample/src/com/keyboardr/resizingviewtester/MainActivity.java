package com.keyboardr.resizingviewtester;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(new KittenAdapter());
	}
}
