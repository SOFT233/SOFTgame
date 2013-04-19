package com.soft.util;

import java.util.concurrent.ArrayBlockingQueue;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private GameLogic mGameLogic;
	private ArrayBlockingQueue<InputObject> inputObjectPool;
	private SoundPool soundPool;
	private int game_width;
	private int game_height;
	
	public GameView(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		createInputObjectPool();
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		
		setFocusable(true);
	}
	
	public SoundPool getSoundPool() {
		return soundPool;
	}

	public int getGame_width() {
		return game_width;
	}

	public void setGame_width(int game_width) {
		this.game_width = game_width;
	}

	public int getGame_height() {
		return game_height;
	}

	public void setGame_height(int game_height) {
		this.game_height = game_height;
	}
	
	public GameLogic getmGameLogic() {
		return mGameLogic;
	}

	public Rect getBounds() {
		return new Rect(0, 0, game_width, game_height);
	}

	private void createInputObjectPool() {
		inputObjectPool = new ArrayBlockingQueue<InputObject>(20);
		
		for(int i = 0; i < 20; i++) {
			inputObjectPool.add(new InputObject(inputObjectPool));
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// add update functionality here
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// create functionality here
		
		// created gameloop thread
		mGameLogic = new GameLogic(getHolder(), this);
		
		Canvas c = holder.lockCanvas();
		game_width = c.getWidth();
		game_height = c.getHeight();
		holder.unlockCanvasAndPost(c);
		
		mGameLogic.setGameState(GameLogic.RUNNING);
		mGameLogic.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// add close functionality here
		soundPool.release();
		mGameLogic.killThread();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		try {
			int hist = event.getHistorySize();
			if(hist > 0) {
				for(int i = 0; i < hist; i++) {
					InputObject input = inputObjectPool.take();
					input.useEventHistory(event, i);
					mGameLogic.feedInput(input);
				}
			}
			
			InputObject input = inputObjectPool.take();
			input.useEvent(event);
			mGameLogic.feedInput(input);
		} catch(InterruptedException e) {
			// nothing
		}
		
		try {
			Thread.sleep(16);
		} catch(InterruptedException e) {
			// nothing
		}
		
		return true;
	}
	
	public void processMotionEvent(InputObject input) {
		// for touch devices
	}
	
	public void processKeyEvent(InputObject input) {
		// for keyboard devices
	}
	
	public void playSound(int sound_id) {
		soundPool.play(sound_id, 1.0f, 1.0f, 10, 0, 1f);
		
		sound_id++;
		
		if(sound_id == 2) {
			sound_id = 0;
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
	}
	
	public void update(int adj_mov) {
		// adj_mov allows for processor speed scaling
	}

	public void endGame() {
		mGameLogic.setGameState(GameLogic.STOPPED);
		
		// TODO end activity and return to select activity
	}
}
