package com.dgurnani.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBirdie extends ApplicationAdapter {

	private SpriteBatch batch;
    private Texture background;

    private Texture[] birds; // holds 3 birds image states to create motion
    private int flapState = 0; // index for image state for flappy bird
    private int imageUpdateCounter = 0;

    private float Y_Position = 0; // Y-axis position
    private float Y_Velocity = 0; // Y-axis velocity (+ is downward)
    private float Y_Gravity = 2; // Y-axis gravitational acceleration (+ is downward)

    private boolean isGameOn = false;

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

		if(isGameOn) {
            if (Gdx.input.justTouched()) {
                Y_Velocity = -30; // speed in upward direction (against gravity)
            }

            if (Y_Position > 0 || Y_Velocity < 0) {
                Y_Velocity = Y_Velocity + Y_Gravity;
                Y_Position -= Y_Velocity;
            }
        } else {
            if (Gdx.input.justTouched()) {
                isGameOn = true;
            }
        }

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
