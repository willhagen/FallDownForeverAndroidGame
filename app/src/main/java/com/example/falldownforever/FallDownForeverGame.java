package com.example.falldownforever;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

/**
 * Created by Will Hagen on 3/1/16.
 */

public class FallDownForeverGame extends SurfaceView implements Runnable {
    private String version = "new";
    private boolean debug = false;
    private Thread thread;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint bitmapPaint;
    private Paint scoreFillPaint;
    private Paint scoreStrokePaint;
    private Paint debugPaint;
    private Paint gameOverPaint;
    private Paint instructionsPaint;
    private String scoreText;
    private Ball ball;
    private Bitmap ballBitmap;
    private Bitmap brickBitmap;
    private boolean fingerIsDown;
    private double mouseX;
    private double deBounceTimer;
    private double speedMultiplier;
    private double gameStartTime;
    private double score;
    private int textSize;
    private Random random;
    private boolean worldExists;
    private Timer timer;
    final Handler handler = new Handler();
    final Runnable runnable;
    private volatile boolean threadFocused;
    Physics physics;
    private GAME_STATE gameState;
    private enum GAME_STATE {INSTRUCTIONS, GAME_OVER, PLAYING}    ;

    private final static List<String> instructions = Arrays.asList(
            "FALL DOWN FOREVER",
            "Press on the left or right",
            "to move the ball.",
            "Try to keep falling down",
            "by falling through gaps",
            "in the bricks.",
            "Your ball will be crushed if",
            "you reach the top of the screen.",
            "(tap anywhere to begin)");


    public FallDownForeverGame(Context context) {
        super(context);
        thread = null;
        threadFocused=true;
        worldExists = false;
        gameState = GAME_STATE.INSTRUCTIONS;
        surfaceHolder = getHolder();
        bitmapPaint = new Paint();// this "paint" is nothing but drawing instructions for the bitmaps
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);

        scoreFillPaint = new Paint();
        scoreFillPaint.setAntiAlias(true);
        scoreFillPaint.setTextAlign(Paint.Align.LEFT);
        scoreFillPaint.setColor(Color.CYAN);

        scoreStrokePaint = new Paint();
        scoreStrokePaint.setStyle(Paint.Style.STROKE);
        scoreStrokePaint.setAntiAlias(true);
        scoreStrokePaint.setTextAlign(Paint.Align.LEFT);
        scoreStrokePaint.setColor(Color.BLACK);

        debugPaint = new Paint();// for onscreen debugging
        debugPaint.setColor(Color.argb(255, 249, 129, 0));
        debugPaint.setAntiAlias(true);
        gameOverPaint = new Paint();// you lose!
        gameOverPaint.setColor(Color.argb(255, 255, 0, 0));
        gameOverPaint.setAntiAlias(true);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);
        instructionsPaint = new Paint();// setup instructions
        instructionsPaint.setColor(Color.argb(255, 200, 200, 200));
        instructionsPaint.setAntiAlias(true);
        instructionsPaint.setTextAlign(Paint.Align.CENTER);
        // don't set the size of the paint here, the canvas may not be properly initialized
        random = new Random();
        mouseX = 0;
        score = 0;
        speedMultiplier = 0.3f;
        fingerIsDown = false;
        ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.face);
        brickBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.brick);
        ball = new Ball(ballBitmap);
        physics = new Physics(ball,brickBitmap);
        runnable = new Runnable() {
            @Override
            public void run() {// the main game loop... tick tock...
                update();
                draw();
            }
        };
        timer = new Timer();// 30 fps
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, 1000, 1000 / 30);
        // delay game loop by a second to compensate for image loading latency
    }

    private void tick() {// one game frame
        handler.post(runnable);
    }

    @Override
    public void run() {// use the run method of a timed runnable instead
    }

    public boolean bitmapValid(Bitmap bitmap) {  // make sure bitmap is loaded
        // if the file was not found getWidth will return 0
        return bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0;
    }

    public boolean everythingLoadedCorrectly() {
        // if getRight() or getBottom() is 0, the surface isn't fully loaded
        return getRight() > 0 &&
                getBottom() > 0 &&
                bitmapValid(ballBitmap) &&
                bitmapValid(brickBitmap);
    }

    public void setupWorld() { // this must be separate from constructor; the canvas must be loaded and initialized
        if (everythingLoadedCorrectly()) {
            ball.setup(getRight(), getBottom());
            physics.setup(getRight(),getBottom(),false);
            deBounceTimer = System.currentTimeMillis() + 1000;
            gameStartTime = System.currentTimeMillis()-1;
            gameState=GAME_STATE.INSTRUCTIONS;
            score = 0;
            speedMultiplier = 0.3f;
            worldExists = true;
        }
    }

    private void gameOver() {
        gameState=GAME_STATE.GAME_OVER;
        deBounceTimer = System.currentTimeMillis() + 1000;
    }

    private void update() {
        if (threadFocused) {
            if (!worldExists) {
                setupWorld();
            } else {
                if (gameState!=GAME_STATE.PLAYING) {
                    if (fingerIsDown) {
                        if (deBounceTimer < System.currentTimeMillis()) {
                            setupWorld();
                            gameState=GAME_STATE.PLAYING;
                        }
                    }
                }
                if (gameState==GAME_STATE.PLAYING) {
                    speedMultiplier=Math.log((System.currentTimeMillis()-gameStartTime)/1000.)*.15+.15;
                    score=Math.pow((System.currentTimeMillis()-gameStartTime)/25,1.5);
                    ball.setScreenSize(getRight(), getBottom());
                    if (fingerIsDown) {
                        ball.moveLeft(mouseX < getRight() / 2, speedMultiplier);
                    }
                    ball.moveUp(true, speedMultiplier);
                    ball.moveUp(false, speedMultiplier);
                    physics.moveWallsUp();
                    physics.detectCollision();
                    if (ball.isCrushed()) {
                        gameOver();
                    }
                }
            }
        }
    }
    private int getScore(){
        // pinball style where the score always ends in 000
        return ((int)Math.floor(score/1000))*1000;
    }
    private void draw() {
        if (worldExists && threadFocused && surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            textSize = (int) (getRight() / 30);// set font relative to screen size
            debugPaint.setTextSize(textSize);// this occurs every frame since the size of the game may have changed

            if (gameState==GAME_STATE.PLAYING) {
                canvas.drawColor(Color.DKGRAY);
                //gf4un7g5
                for(int i=0;i<physics.blockersLength();i++){
                    for(int j=0;j<physics.blockerWidth();j++){
                        if(physics.brickAlive(i,j)){
                            canvas.drawBitmap(brickBitmap,physics.brickX(i,j),physics.brickY(i,j),bitmapPaint);
                        }
                    }
                }
                canvas.drawBitmap(ballBitmap, (float) ball.getLeft(), (float) ball.getTop(), bitmapPaint);
                if (debug) {
                    canvas.drawText(version +"|"+random.nextInt(10) + "|", 10, textSize * 3, debugPaint);
                    canvas.drawText(ball.getLeft() + "_" + ball.getTop(), 10, textSize * 4, debugPaint);
                }

                //pinball style with scores always ending in 000
                scoreText="SCORE: "+getScore();
                scoreStrokePaint.setStrokeWidth(textSize / 8);
                // set the size
                scoreStrokePaint.setTextSize(textSize);
                scoreFillPaint.setTextSize(textSize);
                // draw the text with an outline
                canvas.drawText(scoreText,0,textSize,scoreStrokePaint);
                canvas.drawText(scoreText,0,textSize,scoreFillPaint);
            } else if (gameState==GAME_STATE.GAME_OVER) {
                canvas.drawColor(Color.BLACK);
                textSize = (int) (getRight() / 10);
                gameOverPaint.setTextSize(textSize);
                canvas.drawText("GAME OVER", getRight() / 2, textSize, gameOverPaint);
                gameOverPaint.setTextSize((int) (getRight() / 20));
                canvas.drawText("click anywhere on screen", getRight() / 2, (float) ((getBottom() + textSize) * .25), gameOverPaint);
                canvas.drawText("score: "+getScore(), getRight()/2,((getBottom() + textSize) * .50f), gameOverPaint);
                canvas.drawText("© Will Hagen 2016",getRight()/2,(getBottom()-textSize/2.0f),gameOverPaint);
            } else if (gameState==GAME_STATE.INSTRUCTIONS){
                canvas.drawColor(Color.DKGRAY);
                textSize = (int) (getRight() / 20);
                instructionsPaint.setTextSize(textSize);
                for (int i = 0; i < instructions.size(); i++) {
                    canvas.drawText(instructions.get(i), getRight() / 2, i * textSize, instructionsPaint);
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        gameState=GAME_STATE.INSTRUCTIONS;
        threadFocused=false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("error", "joining thread");
        }

    }

    public void resume() {
        gameState=GAME_STATE.INSTRUCTIONS;
        threadFocused=true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerIsDown = true;
                mouseX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                fingerIsDown = true;
                mouseX = event.getX();
                break;

            case MotionEvent.ACTION_UP:
                fingerIsDown = false;
                break;
        }
        return true;
    }
}
