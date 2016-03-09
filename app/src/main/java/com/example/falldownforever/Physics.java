package com.example.falldownforever;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Will Hagen on 3/2/16.
 */
public class Physics {
    Ball ball;
    ArrayList<Blocker> blocker;
    Bitmap brickBitmap;
    double screenWidth;
    double screenHeight;

    public Physics(Ball ball, Bitmap brickBitmap){// the canvas may not ready to draw
        this.ball=ball;
        this.brickBitmap=brickBitmap;
    }

    public void setup(double screenWidth,double screenHeight, boolean onScreen){// the canvas is initialized
        this.screenWidth=screenWidth;
        this.screenHeight=screenHeight;
        blocker=new ArrayList<>();
        double buildProgress=0;
        while(buildProgress<screenHeight+ball.getSpread()){
            Blocker theBlock=new Blocker(brickBitmap);
            if(onScreen){
                theBlock.setup(screenWidth, buildProgress);
            }else {
                theBlock.setup(screenWidth, screenHeight + buildProgress);
            }
            blocker.add(theBlock);
            buildProgress+=ball.getSpread();
        }
    }
    public Blocker getBlocker(int i){
        return blocker.get(i);
    }
    public int blockersLength(){
        return blocker.size();
    }
    public int blockerWidth(){
        return blocker.get(0).getBrickLength();
    }
    public boolean brickAlive(int i, int j){
        return blocker.get(i).getBrick(j).alive;
    }
    public float brickX(int i,int j){
        return (float)blocker.get(i).getBrickX(j);
    }
    public float brickY(int i,int j){
        return (float)blocker.get(i).getTop();
    }
    public void moveWallsUp(){
        double greatestBlockerTop=0;
        for(Blocker block: blocker){
            if(block.getTop()>greatestBlockerTop){
                greatestBlockerTop=block.getTop();
            }
        }
        for(Blocker block: blocker){
            block.moveUp(ball.getUpwardSpeed(),greatestBlockerTop+ball.getSpread());
        }
    }
    public int ballRestingOnBlock(){
        int ballRestingOnBlock=-1;
        int i=0;
        for(Blocker block: blocker) {
            if(ball.getBottom()+1.0>=block.getTop()){
                if(ball.getBottom()+1.0<=block.getTop()+ball.getDownwardSpeed()*2.0){
                    ballRestingOnBlock=i;
                }
            }
            i++;
        }
        return ballRestingOnBlock;
    }
    public void detectCollision(){
        int ballOnBlock=ballRestingOnBlock();
        boolean blockBall=false;
        if(ballOnBlock>=0){
            Blocker bob=blocker.get(ballOnBlock);
            if(
                    bob.blockThere(ball.getLeft())||
                    bob.blockThere(ball.getHorizontalCenter())||
                    bob.blockThere(ball.getRight())
            ) {
                blockBall=true;
                //set bottom... unless left or right is near edge where something is missing
                if(!bob.blockThere(ball.getHorizontalCenter())){
                    if(bob.nearEdge(ball.getLeft(),ball.getHorizontalSpeed()*3)){
                        blockBall=false;
                    }else if(bob.nearEdge(ball.getRight(),ball.getHorizontalSpeed()*3)){
                        blockBall=false;
                    }
                }
                if(blockBall)
                    ball.setBottom(bob.getTop());
            }
        }
    }
}
