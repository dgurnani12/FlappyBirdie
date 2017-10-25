package com.dgurnani.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class FlappyBirdie extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture background;

    private boolean isGameOn = false; // game state

    private Texture[] birds; // holds 3 bird image states to create bird motion
    private int flapState = 0; // index for image state for flappy bird
    private int imageUpdateCounter = 0; // images are only update after every 10 renders() calls

    private float Y_Position = 0; // Y-axis position
    private float Y_Velocity = 0; // Y-axis velocity (+ is downward)
    private float Y_Gravity = 2; // Y-axis gravitational acceleration (+ is downward)

    private Texture topTube;
    private Texture bottomTube;

    private float gap = 400;
    private float maxTubeDisplacement;
    private Random randomGenerator;
    private float tubeVelocity = 4; // (+ is leftward)
    private int numberOfTubesSets = 4;
    private float distanceBetweenTubes;

    private float[] tubeX = new float[numberOfTubesSets]; // All tube sets positions on X axis
    private float[] tubeOffset = new float[numberOfTubesSets]; // Y axis offsets of the tube sets

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

        // Tube setup
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        maxTubeDisplacement = Gdx.graphics.getHeight() / 2 - gap / 2 - 100; // 100 for the rim height + some extra

        randomGenerator = new Random();

        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

        // Setting the X-axis position of all tubes and there respective offsets
        for (int i = 0; i < numberOfTubesSets; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 2 * maxTubeDisplacement;
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i * distanceBetweenTubes;
        }
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

            // update the 4 sets of tubes
            for (int i = 0; i < numberOfTubesSets; i++) {
                if (tubeX[i] < - topTube.getWidth()) {
                    // off screen and moving away case
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 2 * maxTubeDisplacement;
                    tubeX[i] += numberOfTubesSets * distanceBetweenTubes;
                } else {
                    // on screen or moving towards case
                    tubeX[i] = tubeX[i] - tubeVelocity;
                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
            }

            // position update system
            if (Y_Position > 0 || Y_Velocity < 0) {
                Y_Velocity = Y_Velocity + Y_Gravity; // acceleration * (unit of speed) = |acceleration| in unit of speed
                Y_Position -= Y_Velocity; // speed * (a single unit of distance) = |speed| in unit time of distance
            }

            // game over condition
            if(Y_Position <= 0) {
                isGameOn = false;
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
        // Do nothing
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