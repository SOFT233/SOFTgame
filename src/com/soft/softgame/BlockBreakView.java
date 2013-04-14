package com.soft.softgame;

import java.util.ArrayList;

import com.soft.model.Background;
import com.soft.model.Block;
import com.soft.model.SpriteObject;
import com.soft.util.GameView;

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
	private Background back;
	private ArrayList<Block> blocks;
	private int id_ball_noise;
	private int id_block_noise;
	private double ballVelocity = -0.75;
	private MediaPlayer mp;
	
	public BlockBreakView(Context context) {
		super(context);
		
		id_ball_noise = this.getSoundPool().load(context, R.raw.ball, 1);
		id_block_noise = this.getSoundPool().load(context, R.raw.block, 1);
		
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
		back = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), 0, 0);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		
		brt.setX(super.getGame_width() - 130);
		brt.setY(super.getGame_height()/2 - (brt.getBitmap().getHeight()/2));
		
		ball.setX(brt.getX() - 50);
		ball.setY(brt.getY() + ((brt.getBitmap().getHeight()/2) - (ball.getBitmap().getHeight()/2)));
		
		// run background music loop
		mp = MediaPlayer.create(getContext(), R.raw.breaktheme);
		mp.setLooping(true);
		mp.setVolume(1f, 1f);
		mp.start();
		
		Log.i("canvas size", super.getGame_width() + " " + super.getGame_height());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		
		mp.stop();
		mp.release();
	}
	
	public void processMotionEvent(InputObject input) {
		//brt.setX(input.x);
		if(brt.getState() == SpriteObject.ALIVE) {
			brt.setY(input.y);
			
			if(ball.getMoveX() == 0 && ball.getMoveY() == 0) {
				ball.setMoveX(ballVelocity);
				ball.setMoveY(ballVelocity);
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
	}
	
	public void update(int adj_mov) {
		// check sprite state
		if(brt.getState() == SpriteObject.DEAD) {
			ball.setMoveX(0);
			ball.setMoveY(0);
			
			// stop the music
			mp.stop();
			
			// set loose screen
			// play loose sound
		}
		else if(checkBlocks() == false) {
			ball.setMoveX(0);
			ball.setMoveY(0);
			
			brt.setState(SpriteObject.DEAD);
			
			// stop the music
			mp.stop();
			
			// set win screen
			// play win sound
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
		}
		else if(ball.getX() <= 0) {
			ball.setMoveX(-ball.getMoveX());
			playSound(id_ball_noise);
		}
		
		if(ball.getY() >= super.getGame_height()) {
			ball.setMoveY(-ball.getMoveY());
			playSound(id_ball_noise);
		}
		else if(ball.getY() <= 0) {
			ball.setMoveY(-ball.getMoveY());
			playSound(id_ball_noise);
		}
		
		// get ball and player hit boxes
		Rect brtRect = brt.getBounds();
		Rect ballRect = ball.getBounds();
		
		// check player ball collisions
		if(brtRect.intersects(ballRect.left, ballRect.top, ballRect.right, ballRect.bottom)) {
			ball.setMoveX(-ball.getMoveX());
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
