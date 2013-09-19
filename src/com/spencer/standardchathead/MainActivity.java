package com.spencer.standardchathead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.spencer.standardchathead.service.ChatheadService;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startService(new Intent(this, ChatheadService.class));
	}
}
