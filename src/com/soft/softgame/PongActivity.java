package com.soft.softgame;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PongActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(new PongView(this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// return to home screen
		this.finish();
	}
}
