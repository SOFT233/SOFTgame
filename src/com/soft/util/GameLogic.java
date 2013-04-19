package com.soft.util;

import java.util.concurrent.ArrayBlockingQueue;


import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameLogic extends Thread{
	
	private SurfaceHolder surfaceHolder;
	private GameView mGameView;
	private int gameState;
	public static final int STOPPED = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	private ArrayBlockingQueue<InputObject> inputQueue = new ArrayBlockingQueue<InputObject>(20);
	private Object inputQueueMutex = new Object();
	
	public GameLogic(SurfaceHolder surfaceHolder, GameView mGameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.mGameView = mGameView;
	}

	public int getGameState() {
		return gameState;
	}

	public void setGameState(int gameState) {
		this.gameState = gameState;
	}

	@Override
	public void run() {
		Canvas canvas;
		long time_orig = System.currentTimeMillis();
		long time_interim;
		
		while(gameState == RUNNING) {
			canvas = null;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized(surfaceHolder) {
					try {
						Thread.sleep(10);
					} catch(InterruptedException e1) {
						// nothing
					}
					
					time_interim = System.currentTimeMillis();
					int adj_mov = (int)(time_interim - time_orig);
					
					mGameView.update(adj_mov);
					processInput();
					time_orig = time_interim;
					this.mGameView.onDraw(canvas);
				}
			}
			catch(NullPointerException e) {
				this.setGameState(GameLogic.STOPPED);
				Log.i("Game Suspended", "Reverting to home screen");
			}
			finally {
				if(canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
			
		}
	}
	
	public void feedInput(InputObject input) {
		synchronized(inputQueueMutex) {
			try {
				inputQueue.put(input);
			} catch(InterruptedException e) {
				// nothing
			}
		}
	}
	
	public void processInput() {
		synchronized(inputQueueMutex) {
			ArrayBlockingQueue<InputObject> inputQueue = this.inputQueue;
			
			while(!inputQueue.isEmpty()) {
				try {
					InputObject input = inputQueue.take();
					
					if(input.eventType == InputObject.EVENT_TYPE_KEY) {
						mGameView.processKeyEvent(input);
					}
					else if(input.eventType == InputObject.EVENT_TYPE_TOUCH) {
						mGameView.processMotionEvent(input);
					}
					
					input.returnToPool();
				} catch (InterruptedException e) {
					// nothing
				}
			}
		}
	}
	
	public void killThread() {
		this.gameState = STOPPED;
	}
}
