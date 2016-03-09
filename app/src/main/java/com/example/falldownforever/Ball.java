package com.example.falldownforever;

import android.graphics.Bitmap;
/**
 * Created by Will Hagen on 3/1/16.
 */
public class Ball {
    private Bitmap bitmap;
    private double x;
    private double y;
    private double horizontalSpeed;
    private double upwardSpeed;
    private boolean setup;
    private double screenWidth;
    private double screenHeight;
    private boolean crushed;

    public Ball(Bitmap bitmap) {
        this.bitmap = bitmap;
        setLeft(-getWidth());// this hides the bitmap off the user's screen
        setTop(-getHeight());
        horizontalSpeed=1;// these speed values are reset every time the ball moves
        upwardSpeed=1;// but set some dummy value at setup for unit testing
        setup = false;// the size of the screen is not available yet,
        // so setup must be split into two different functions
    }

    public void setup(double screenWidth, double screenHeight) {// the canvas size is now ready
        setup = true;//  the ball can finally be fully constructed
        setCenter(screenWidth / 2, screenHeight / 2);// the ball starts in the center of the screen
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        crushed=false;
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    public double getSpread(){
        return bitmap.getHeight()*2.25;
    }
    public double getHeight() {
        return bitmap.getHeight();
    }

    public double getWidth() {
        return bitmap.getWidth();
    }

    public double getLeft() {
        return x;
    }

    public double getTop() {
        return y;
    }

    public double getRight() {
        return x+bitmap.getWidth();
    }

    public double getBottom() {
        return getTop() + getHeight();
    }

    public double getHorizontalCenter() {
        return (getLeft() + getRight()) / 2.0;
    }


    public boolean canDraw() {
        return setup && bitmap != null && getWidth() > 0 && getHeight() > 0;
    }

    public void setCenter(double x, double y) {
        this.x = x - getWidth() / 2;
        this.y = y - getHeight() / 2;
    }
    public void setLeft(double left) {
        x = left;
    }

    public void setRight(double right) {
        x = right - getWidth();
    }

    public void setTop(double top) {
        y = top;
    }

    public void setBottom(double bottom) {
        y = bottom - getHeight();
    }

    public boolean isCrushed(){
        return crushed;
    }

    public double getHorizontalSpeed(){
        return horizontalSpeed;
    }
    public void moveLeft(boolean left, double speedMultiplier) {
        horizontalSpeed=(getWidth()/3.*speedMultiplier);
        if (left) {
            x -= horizontalSpeed;
        } else {
            x += horizontalSpeed;
        }
        if (x < 0) {
            x = 0;
        }
        if (getRight() > screenWidth) {
            setRight(screenWidth);
        }
    }
    public double getUpwardSpeed(){
        return upwardSpeed;
    }
    public double getDownwardSpeed() { return upwardSpeed*1.5;}
    public void moveUp(boolean up, double speedMultiplier) {
        upwardSpeed=getHeight() / 5. * speedMultiplier;
        if (up) {
            y -= upwardSpeed;
        } else {
            y += getDownwardSpeed();// falling down is faster than moving up
        }
        if (getBottom() <= 0) {
            setBottom(1);
            crushed=true;
        }
        if (getBottom() > screenHeight) {// make sure ball doesn't fall off the edge of the screen
            setBottom(screenHeight);
        }
    }
}
