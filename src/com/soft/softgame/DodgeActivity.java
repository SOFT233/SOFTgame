package com.soft.softgame;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DodgeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(new DodgeView(this));
	}
}
