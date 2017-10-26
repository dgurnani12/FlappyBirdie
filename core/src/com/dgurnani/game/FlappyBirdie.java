package com.dgurnani.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBirdie extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture background;
    private Texture gameover;

    //private ShapeRenderer sr; // for debugging purposes (Do not Delete!)

    private boolean isGameOn = false; // game state
    private boolean isGameOver = false;

    private Texture[] birds; // holds 3 bird image states to create bird motion
    private int flapState = 0; // index for image state for flappy bird
    private int imageUpdateCounter = 0; // images are only update after every 10 renders() calls

    private float Y_Position = 0; // Y-axis position
    private float Y_Velocity = 0; // Y-axis velocity (+ is downward)
    private float Y_Gravity = 2; // Y-axis gravitational acceleration (+ is downward)

    private Circle birdShadow; // Circle - an easy way to shadow the bird

    private Texture topTube;
    private Texture bottomTube;

    private float gap = 400;
    private float maxTubeDisplacement;
    private Random randomGenerator;
    private float tubeVelocity = 4; // (+ is leftward)
    private int numberOfTubeSets = 4;
    private float distanceBetweenTubes;

    private float tubeXSync;
    private float[] tubeX = new float[numberOfTubeSets]; // All tube sets positions on X axis
    private float[] tubeOffset = new float[numberOfTubeSets]; // Y axis offsets of the tube sets

    private Rectangle[] topTubeShadows;
    private Rectangle[] bottomTubeShadows;

    private int score = 0;
    private int scoringTube = 0;
    private BitmapFont font;

    @Override
	public void create () {

        //sr = new ShapeRenderer();

        batch = new SpriteBatch();
		background = new Texture("background.png");
        gameover = new Texture("gameover.png");

        birdShadow = new Circle();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

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

        topTubeShadows = new Rectangle[numberOfTubeSets];
        bottomTubeShadows = new Rectangle[numberOfTubeSets];

        // Setting the X-axis position of all tubes and there respective offsets
        for (int i = 0; i < numberOfTubeSets; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 2 * maxTubeDisplacement;

            tubeXSync = (Gdx.graphics.getWidth() / 2) + (topTube.getWidth() / 2);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i * distanceBetweenTubes;
            tubeX[i] += tubeXSync; // Makes the tubes start much later as opposed to the middle

            topTubeShadows[i] = new Rectangle();
            bottomTubeShadows[i] = new Rectangle();
        }
	}

	@Override
	public void render () {

        if(isGameOver) {
            if (Gdx.input.justTouched()) {
                isGameOn = true;
                isGameOver = false; // get out of gamerover state
                score = 0;
                scoringTube = 0;
                Y_Velocity = 0;
                Restart();
            }
        } else {

            batch.begin();

            // draw background:
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            if (isGameOn) {

                if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                    score++;
                    Gdx.app.log("Score", String.valueOf(score));

                    scoringTube++;
                    scoringTube = scoringTube % numberOfTubeSets;
                }

                if (Gdx.input.justTouched()) {
                    Y_Velocity = -30; // speed in upward direction (against gravity)
                }

                // update the 4 sets of tubes
                for (int i = 0; i < numberOfTubeSets; i++) {
                    if (tubeX[i] < -topTube.getWidth()) {
                        // off screen and moving away case
                        tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 2 * maxTubeDisplacement;
                        tubeX[i] += numberOfTubeSets * distanceBetweenTubes;
                    } else {
                        // on screen or moving towards case
                        tubeX[i] = tubeX[i] - tubeVelocity;
                    }

                    batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                    batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                    // shadows
                    topTubeShadows[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                    bottomTubeShadows[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
                }

                // position update system
                if (Y_Position > 0 || Y_Velocity < 0) {
                    Y_Velocity = Y_Velocity + Y_Gravity; // acceleration * (unit of speed) = |acceleration| in unit of speed
                    Y_Position -= Y_Velocity; // speed * (a single unit of distance) = |speed| in unit time of distance
                }

                // game over condition
                if(Y_Position <= 0) {
                    Gameover();
                }

            } else {
                if (Gdx.input.justTouched()) {
                    isGameOn = true;
                }
            }

            // Draw bird:
            if (imageUpdateCounter % 10 == 0) {
                flapStateConfig();
                if (imageUpdateCounter == 20) {
                    imageUpdateCounter = 0; // keeps this counter from running out memory
                }

            }

            batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, Y_Position);
            imageUpdateCounter++;

            font.draw(batch, String.valueOf(score), 100, 200);

            birdShadow.set(Gdx.graphics.getWidth() / 2, Y_Position + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

            //sr.begin(ShapeRenderer.ShapeType.Filled);
            //sr.setColor(Color.RED);
            //sr.circle(birdShadow.x, birdShadow.y, birdShadow.radius);

            for (int i = 0; i < numberOfTubeSets; i++) {
                //sr.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                //sr.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

                // game over condition
                if (Intersector.overlaps(birdShadow, topTubeShadows[i]) || Intersector.overlaps(birdShadow, bottomTubeShadows[i])) {
                    Gameover();
                }
            }

            //sr.end();

            batch.end();
        }
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

	private void Restart() {
        Y_Position = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        // Setting the X-axis position of all tubes and there respective offsets
        for (int i = 0; i < numberOfTubeSets; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 2 * maxTubeDisplacement;

            tubeXSync = (Gdx.graphics.getWidth() / 2) + (topTube.getWidth() / 2);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i * distanceBetweenTubes;
            tubeX[i] += tubeXSync; // Makes the tubes start much later as opposed to the middle

            topTubeShadows[i] = new Rectangle();
            bottomTubeShadows[i] = new Rectangle();
        }
    }

    private  void Gameover() {
        isGameOn = false;
        isGameOver = true;
        batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);
    }

}