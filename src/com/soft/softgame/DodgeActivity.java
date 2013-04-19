package com.soft.softgame;

import android.os.Bundle;
import android.app.Activity;

public class DodgeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(new DodgeView(this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// return to home screen
		this.finish();
	}
	
}
