package com.example.falldownforever;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBitmapFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Will Hagen on 3/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PhysicsUnitTest {
    float screenWidth = 800;
    float screenHeight = 600;
    float largeNumber = 99999;
    float x = 10;
    float y = 10;
    Ball ball;
    ArrayList<Blocker> blockers;
    Physics physics;

    @Before
    public void setup() {
        ball = new Ball(ShadowBitmapFactory.decodeResource(RuntimeEnvironment.application.getResources(), R.drawable.face));
        physics=new Physics(ball,ShadowBitmapFactory.decodeResource(RuntimeEnvironment.application.getResources(), R.drawable.brick));
        // we can setup the ball and physics because the shadow canvas is instantly available
        // unlike the real canvas
        ball.setup(screenWidth, screenHeight);
        physics.setup(screenWidth,screenHeight,true);
    }

    @Test
    public void alwaysSucceeds() throws Exception {
        assertTrue("true is true!", true);
    }

    @Test
    public void ball_setup_correctly() throws Exception {
        assertTrue(ball.canDraw());
        assertFalse(ball.isCrushed());
    }

    @Test
    public void physics_setup_correctly() throws Exception{
        assertTrue(physics.blockersLength() > 0);
    }

    public boolean near(double x,double y){
        return Math.abs(x-y)<1.5;
    }
    /*
    @Test
    public void locations_of_bricks() throws Exception {
        for(int i=0;i<physics.blockersLength();i++){
            System.out.println("blocker top"+physics.getBlocker(i).getTop());
        }
    }*/
    /*
    * build a world where the top blocker has no bricks
    * the second to top blocker has all bricks except 2 and 3
    * and place the ball at the bottom of the 0th blocker
    * and it's left point slightly to the right of the 1st brick of the 1st blocker
    */
    public void build_hypothetical_world() {
        physics.getBlocker(0).killAllBricks();
        physics.getBlocker(2).killAllBricks();
        Blocker testBlocker=physics.getBlocker(1);
        testBlocker.rebuildBricks(screenWidth, false);
        testBlocker.killBrick(2);
        testBlocker.killBrick(3);
        testBlocker.killBrick(4);
    }
    public void drop_ball(boolean outputCoordinates, Blocker testBlocker) {
        if(outputCoordinates)
            System.out.println("S_"+ball.getBottom() + ", " + testBlocker.getTop());
        int i=0;
        for(;i<20;i++){
            ball.moveUp(false, .1);
            physics.detectCollision();
            if(outputCoordinates)
                System.out.println(i + "_" + ball.getBottom() + ", " + testBlocker.getTop());
        }
        if(outputCoordinates)
            System.out.println("E_" + ball.getBottom()+", "+testBlocker.getTop());
    }
    @Test
    public void dropping_ball_on_brick_should_cause_the_ball_to_rest_on_the_brick() throws Exception {
        build_hypothetical_world();
        Blocker testBlocker = physics.getBlocker(1);
        ball.setLeft(testBlocker.getBrick(1).x + 1.);
        ball.setTop(physics.getBlocker(0).getBottom());
        drop_ball(false, testBlocker);
        assertTrue("ball should land on blocker",near(ball.getBottom(), testBlocker.getTop()));
    }
    @Test
    public void dropping_ball_on_gap_should_cause_ball_to_keep_falling() throws Exception {
        build_hypothetical_world();
        Blocker testBlocker = physics.getBlocker(1);
        ball.setLeft(testBlocker.getBrick(3).x);
        ball.setTop(physics.getBlocker(0).getBottom() + 1);
        drop_ball(false, testBlocker);
        assertFalse("ball should keep falling through the gap",near(ball.getBottom(), testBlocker.getTop()));
    }
    /*
    @Test
    public void physics_print_to_console() throws Exception{
        System.out.println("blockers length" + physics.blockersLength());
        for(int x=0;x<10;x++) {
            physics.setup(screenWidth,screenHeight);
            for (int i = 0; i < physics.blockersLength(); i++) {
                for (int j = 0; j < physics.blockerWidth(); j++) {
                    ///i+", "+j+" "
                    if (physics.brickAlive(i, j)) {
                        System.out.print("=");
                    } else {
                        System.out.print("-");
                    }

                }
                System.out.println();
            }
        }
    }*/
    /*
    @Test
    public void alwaysFails() throws Exception {
        assertFalse("false is true?",false)
    }*/
}