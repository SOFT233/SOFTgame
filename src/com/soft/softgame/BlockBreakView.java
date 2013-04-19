package com.soft.softgame;

import java.util.ArrayList;

import com.soft.model.Block;
import com.soft.model.SpriteObject;
import com.soft.util.GameView;
import com.soft.util.InputObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

public class BlockBreakView extends GameView {
	
	private SpriteObject brt;
	private SpriteObject ball;
	private SpriteObject back;
	private SpriteObject win;
	private SpriteObject loose;
	private ArrayList<Block> blocks;
	private int id_ball_noise;
	private int id_block_noise;
	private int id_win_noise;
	private int id_loose_noise;
	private boolean gameEnd = false;
	private double ballVelocityPos = 0.5;
	private double ballVelocityNeg = -0.5;
	private MediaPlayer mp;
	
	public BlockBreakView(Context context) {
		super(context);
		
		id_ball_noise = this.getSoundPool().load(context, R.raw.ball, 1);
		id_block_noise = this.getSoundPool().load(context, R.raw.block, 1);
		id_win_noise = this.getSoundPool().load(context, R.raw.win, 1);
		id_loose_noise = this.getSoundPool().load(context, R.raw.loose, 1);
		
		blocks = new ArrayList<Block>();
		
		// block grid params
		int initposy = 50;
		int initposx = 100;
		int rows = 2;
		int bins = 7;
		
		// initialise blocks
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < bins; j++) {
				Block block = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.bin), 
										BitmapFactory.decodeResource(getResources(), R.drawable.deadbin), 
										initposx, 
										initposy);
				blocks.add(block);
				initposy += 100;
			}
			initposy = 40;
			initposx += 100;
		}
		
		brt = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.brt), 0, 0);
		ball = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.ball), 0, 0);
		back = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.background), 0, 0);
		win = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.win), 0, 0);
		loose = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.loose), 0, 0);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		
		win.setState(SpriteObject.DEAD);
		loose.setState(SpriteObject.DEAD);
		
		// set win loose position
		win.setX(super.getGame_width()/2 - (win.getBitmap().getWidth()/2));
		win.setY(super.getGame_height()/2 - (win.getBitmap().getHeight()/2));
		loose.setX(super.getGame_width()/2 - (loose.getBitmap().getWidth()/2));
		loose.setY(super.getGame_height()/2 - (loose.getBitmap().getHeight()/2));
		
		// centre to screen
		brt.setX(super.getGame_width() - 130);
		brt.setY(super.getGame_height()/2 - (brt.getBitmap().getHeight()/2));
		
		// centre to brt
		ball.setX(brt.getX() - 50);
		ball.setY(brt.getY() + ((brt.getBitmap().getHeight()/2) - (ball.getBitmap().getHeight()/2)));
		
		// run background music loop
		mp = MediaPlayer.create(getContext(), R.raw.breaktheme);
		mp.setLooping(true);
		mp.setVolume(1f, 1f);
		mp.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		
		// TODO surface destroyed not working correctly
		
		if(gameEnd == true) {
			mp.stop();
			mp.release();
		}
		else {
			if(mp.isPlaying()) {
				mp.stop();
				mp.release();
			}
		}
	}
	
	public void processMotionEvent(InputObject input) {
		//brt.setX(input.x);
		if(brt.getState() == SpriteObject.ALIVE) {
			// TODO stop player from leaving screen bounds
			brt.setY(input.y);
			
			// if ball not moving start ball
			if(ball.getMoveX() == 0 && ball.getMoveY() == 0) {
				ball.setMoveX(ballVelocityNeg);
				ball.setMoveY(ballVelocityNeg);
			}
		}
		
		// user selects end game image, return to home screen
		if(win.getState() == SpriteObject.ALIVE || loose.getState() == SpriteObject.ALIVE) {
			if(input.eventType == InputObject.ACTION_TOUCH_UP) {
				super.endGame();
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		back.draw(canvas);

		for(int i = 0; i < blocks.size(); i++) {
			blocks.get(i).draw(canvas);
		}
		
		ball.draw(canvas);
		brt.draw(canvas);
		
		if(win.getState() == SpriteObject.ALIVE) {
			win.draw(canvas);
		}
		
		if(loose.getState() == SpriteObject.ALIVE) {
			loose.draw(canvas);
		}
	}
	
	public void update(int adj_mov) {
		// check sprite state
		if(brt.getState() == SpriteObject.DEAD) {
			ball.setMoveX(0);
			ball.setMoveY(0);
			
			// stop the music
			mp.stop();
		}
		else if(checkBlocks() == false) {
			ball.setMoveX(0);
			ball.setMoveY(0);
			
			brt.setState(SpriteObject.DEAD);
			
			// stop the music
			mp.stop();
			
			// set win screen
			win.setState(SpriteObject.ALIVE);
			// play win sound
			if(gameEnd == false) {
				playSound(id_win_noise);
				gameEnd = true;
			}
		}
		else {
			// check for object collisions
			collisionChecks();
			
			// perform specific updates
			for(int i = 0; i < blocks.size(); i++) {
				blocks.get(i).update(adj_mov);
			}
			ball.update(adj_mov);
			brt.update(adj_mov);
		}
	}
	
	public boolean checkBlocks() {
		// check for alive blocks
		for(int i=0; i < blocks.size(); i++) {
			if(blocks.get(i).getState() == SpriteObject.ALIVE) {
				return true;
			}
		}
		return false;
	}

	public void collisionChecks() {
		// collisions with walls
		if(ball.getX() >= super.getGame_width()) {
			// if it goes off the back of the screen set to dead
			brt.setState(SpriteObject.DEAD);
			
			// set loose screen
			loose.setState(SpriteObject.ALIVE);
			// play loose sound
			if(gameEnd == false) {
				playSound(id_loose_noise);
				gameEnd = true;
			}
		}
		else if(ball.getX() <= 0) {
			ball.setMoveX(ballVelocityPos);
			playSound(id_ball_noise);
		}
		
		if(ball.getY() >= super.getGame_height()) {
			ball.setMoveY(ballVelocityNeg);
			playSound(id_ball_noise);
		}
		else if(ball.getY() <= 0) {
			ball.setMoveY(ballVelocityPos);
			playSound(id_ball_noise);
		}
		
		// get ball and player hit boxes
		Rect brtRect = brt.getBounds();
		Rect ballRect = ball.getBounds();
		
		// check player ball collisions
		if(brtRect.intersects(ballRect.left, ballRect.top, ballRect.right, ballRect.bottom)) {
			ball.setMoveX(ballVelocityNeg);
			playSound(id_ball_noise);
			//Log.i("collision:Brt", "hit by ball");
		}
		
		// check block ball collisions
		for(int i=0; i < blocks.size(); i++) {
			Rect blockRect = blocks.get(i).getBounds();
			
			if(ballRect.intersects(blockRect.left, blockRect.top, blockRect.right, blockRect.bottom) 
					&& blocks.get(i).getState() != SpriteObject.DEAD) {
				blocks.get(i).setState(SpriteObject.DEAD);
				playSound(id_block_noise);
				
				ball.setMoveX(-ball.getMoveX());
				ball.setMoveY(-ball.getMoveY());
				
				//Log.i("collision:Block", "hit by ball");
			}
		}
	}
}
