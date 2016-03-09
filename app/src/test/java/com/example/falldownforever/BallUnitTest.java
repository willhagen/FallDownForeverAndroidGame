package com.example.falldownforever;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBitmapFactory;


import static org.junit.Assert.*;

/**
 * Created by Will Hagen on 3/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class BallUnitTest {
    double screenWidth=800;
    double screenHeight=600;
    double largeNumber=99999;
    Ball ball;
    @Before
    public void setup(){
        ball = new Ball(ShadowBitmapFactory.decodeResource(RuntimeEnvironment.application.getResources(), R.drawable.face));
        ball.setup(screenWidth,screenHeight);
        assertTrue("ball should not be beyond the right of the screen",ball.getRight()<=screenWidth);
        assertTrue("ball should not be beyond the left of the screen",0<=ball.getLeft());
        assertTrue("ball should not be above the top of the screen",0<=ball.getTop());
        assertTrue("ball should not be below the bottom of the screen",ball.getBottom()<=screenHeight);
    }
    @Test
    public void moveLeft_should_not_move_the_ball_beyond_the_left_of_the_screen() throws Exception {
        ball.moveLeft(true, largeNumber);
        assertTrue("the ball should not be left of the left on the screen", ball.getLeft() >= 0);
    }
    @Test
    public void moveLeft_should_not_move_the_ball_beyond_the_right_of_the_screen() throws Exception {
        ball.moveLeft(false, largeNumber);
        assertTrue("the ball should not be beyond the right of the screen",ball.getRight() <= screenWidth);
    }
    @Test
    public void moveUp_should_not_move_the_ball_beyond_the_bottom_of_the_screen() throws Exception{
        ball.moveUp(false, largeNumber);
        assertTrue("the ball should not below the bottom of the screen",ball.getBottom() <= screenHeight);
    }
    @Test
    public void moveUp_should_not_move_the_ball_beyond_the_top_of_the_screen() throws Exception {
        ball.moveUp(true, largeNumber);
        assertTrue("the ball should not be above the top of the screen",ball.getBottom() >= 0);
    }
}