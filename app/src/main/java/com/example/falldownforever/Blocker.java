package com.example.falldownforever;

import android.graphics.Bitmap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Will Hagen on 3/1/16.
 */
public class Blocker {
    private Bitmap bitmap;
    private boolean setup;
    private double x;
    private double y;
    private double screenWidth;
    private int numberOfBricks;
    private Brick[] bricks;
    private List killList;// should we kill the brick?
    private Random random;
    private int killAmount;
    public final double MIN_BRICK_PROBABILITY=.20;
    public final double MAX_BRICK_PROBABILITY=.50;
    public Blocker(Bitmap bitmap){
        this.bitmap=bitmap;
        setup=false;
        random=new Random();
    }
    public void setup(double screenWidth,double y){// the drawing canvas is ready
        random.setSeed(System.currentTimeMillis());
        rebuildBricks(screenWidth, true);
        this.y=y;
        setup=true;
    }
    // due to rounding, this doesn't guarantee the integer percent is within the correct range
    /*
    public double randomRange(double low ,double high){//inclusive on low, exclusive on high
        return low + (high - low)*random.nextDouble();
    }*/
    // simply generating a random double and discarding it is slower, but more likely to be within
    // the correct range when rounded
    public double randomNumberMissing(int listLength){
        for(int i=0;i<100;i++){
            int value=(int)Math.round(random.nextDouble()*(double)listLength);
            // you need to cast the value and listLength to doubles before dividing
            double percent=(double)value/(double)listLength;
            //inclusive on minimum size, exclusive on maximum size
            if(MIN_BRICK_PROBABILITY<=percent&&percent<MAX_BRICK_PROBABILITY){
                return value;
            }
        }// if we can't get a random number within the range after 100 tries, just kill
        // a thrid of the bricks
        return (double)listLength/3.0;
    }
    public void rebuildBricks(double screenWidth, boolean killBricks) {
        this.screenWidth = screenWidth;
        rebuildBricks(killBricks);
    }
    public void rebuildBricks(boolean killBricks){
        numberOfBricks = 1+(int) (screenWidth/getBrickWidth());
        bricks = new Brick[numberOfBricks];
        for(int i=0;i<numberOfBricks;i++){
            bricks[i]=new Brick();
            bricks[i].alive=true;
            bricks[i].x=i*getBrickWidth();
            bricks[i].anotherOnLeft=true;
            bricks[i].anotherOnRight=true;
        }

        if(killBricks) {
            killList=Arrays.asList(new Boolean[numberOfBricks]);
            // there should be a random percent of bricks missing within the range specified
            killAmount=(int)Math.round(randomNumberMissing(numberOfBricks));
            // but make sure there is always at least two bricks missing
            if(killAmount<2){
                killAmount=2;
            }
            for(int i=0;i<killList.size();i++){
                killList.set(i,i<killAmount);
            }
            // shuffle which bricks to kill, then kill them
            Collections.shuffle(killList, new Random(System.nanoTime()));
            for (int i = 0; i < killList.size(); i++) {
                if((boolean)killList.get(i))
                    killBrick(i);
            }
        }
    }
    public void killAllBricks(){
        for(int i=0;i<bricks.length;i++){
            killBrick(i);
        }
    }
    public boolean nearEdge(double point, double delta){
        double distanceToEdge=point%getBrickWidth();
        return distanceToEdge<delta||distanceToEdge+delta>getBrickWidth();
    }
    public boolean blockThere(double point){
        int location=(int)Math.floor(point/getBrickWidth());
        if(location+1>=bricks.length)
            return true;
        if(location<0)
            return true;
        return (bricks[location].alive);
    }
    public List<Boolean> getKillList(){
        return killList;
    }
    public Brick[] getBricks(){
        return bricks;
    }
    public void moveUp(double amount,double jumpLocation){
        y-=amount;
        if(getBottom()<0){
            y=jumpLocation;
            rebuildBricks(true);
        }
    }
    public Brick getBrick(int i){
        return bricks[i];
    }
    public int getBrickLength(){
        return bricks.length;
    }
    public double getBrickX(int i){
        return i*bitmap.getWidth();
    }
    public void killBrick(int i){
        // instead of checking if there is a brick on the right or left on the fly,
        // these are predetermined, simplifying the main game logic,
        // and making it somewhat faster.
        bricks[i].alive=false;
        bricks[i].anotherOnLeft=false;
        bricks[i].anotherOnRight=false;
        if(i==0){
            bricks[i].anotherOnLeft=true;
        }
        if(i>0){
            bricks[i-1].anotherOnRight=false;
        }
        if(i+1<bricks.length){
            bricks[i+1].anotherOnLeft=false;
        }
        if(i+1==bricks.length){
            bricks[i].anotherOnRight=true;
        }
    }
    public boolean isSetup(){
        return setup;
    }
    public float getBrickWidth(){
        return bitmap.getWidth();
    }
    public double getTop(){
        return y;
    }
    public double getBottom() {
        return y+bitmap.getHeight();
    }
}
