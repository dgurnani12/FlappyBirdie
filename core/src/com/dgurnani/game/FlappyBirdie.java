package com.dgurnani.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBirdie extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;

	Texture[] birds; // holds 3 birds states to create motion
	int flapState = 0; // index for  image state for flappy bird

	int imageUpdateCounter = 0;

	float Y_Position = 0; // Y-axis position
	float Y_Velocity = 0; // Y-axis velocity
	float Y_Gravity = 2; // Y-axis gravitational acceleration

	int gameState = 0; // state of the game (did user touch the screen)

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.png");

		birds = new Texture[3];
		birds[0] = new Texture("Bird-1.png");
		birds[1] = new Texture("Bird-2.png");
		birds[2] = new Texture("Bird-3.png");

		// Gets the bird exactly at the center
		Y_Position = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
	}

	@Override
	public void render () {
		batch.begin();

		// draw background:
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Draw bird:
		if(imageUpdateCounter % 10 == 0) {
			flapStateConfig();
		}
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, Y_Position);
		imageUpdateCounter++;

		

		batch.end();
	}
	
	@Override
	public void dispose () {

	}

	// return the next flapState in the sequence 0,1,2 ...
	private void flapStateConfig() {
		if (flapState == 0) {
			flapState = 1;
		} else if(flapState == 1) {
			flapState = 2;
		} else {
			flapState = 0;
		}
	}
}
