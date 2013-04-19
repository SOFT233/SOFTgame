package com.soft.softgame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SelectionActivity extends Activity implements OnClickListener {

	Button pongGame = null;
	Button tennisDodger = null;
	Button blockBreak = null;
	private MediaPlayer mp;
	private boolean mpInit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_selection);
		
		pongGame = (Button)findViewById(R.id.pong);
		tennisDodger = (Button)findViewById(R.id.tennisDodge);
		blockBreak = (Button)findViewById(R.id.blockBreak);

		pongGame.setOnClickListener(this);
		tennisDodger.setOnClickListener(this);
		blockBreak.setOnClickListener(this);
		
		// run background music loop
		mp = MediaPlayer.create(this, R.raw.hometheme);
		mp.setLooping(true);
		mp.setVolume(1f, 1f);
		mpInit = true;
		mp.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		
		switch(id){
		
		case R.id.pong:
			intent = new Intent(v.getContext(), PongActivity.class);
			startActivity(intent);
			break;
			
		case R.id.tennisDodge:
			intent = new Intent(v.getContext(), DodgeActivity.class);
			startActivity(intent);
			break;
			
		case R.id.blockBreak:
			intent = new Intent(v.getContext(), BlockBreakActivity.class);
			startActivity(intent);
			break;
			
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		//stop music if playing
		if(mpInit) {
			mp.stop();
			mp.release();
			mpInit = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// TODO restart music (seems to get illegal state exception when trying to reset)
//		mp = MediaPlayer.create(this, R.raw.hometheme);
//		mp.setLooping(true);
//		mp.setVolume(1f, 1f);
//		mpInit = true;
//		mp.start();
	}
}
