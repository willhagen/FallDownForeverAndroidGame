package com.example.falldownforever;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBitmapFactory;


import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Will Hagen on 3/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BlockerUnitTest {
    double screenWidth = 800;
    double largeNumber = 99999;
    double x = 25;
    double y = 25;
    Blocker blocker;
    @Before
    public void setup() {
        blocker = new Blocker(ShadowBitmapFactory.decodeResource(RuntimeEnvironment.application.getResources(), R.drawable.brick));
        assertFalse("setup should not be complete yet", blocker.isSetup());
        blocker.setup(screenWidth, y);
        assertTrue("setup should now be complete", blocker.isSetup());
    }

    @Test
    public void the_blocker_should_have_built_bricks() throws Exception {
        assertTrue(blocker.getBrickLength() > 0);
    }
    @Test
    public void killing_a_middle_brick_should_set_make_sure_the_connecting_bricks_change_their_states() throws Exception{
        blocker.rebuildBricks(screenWidth,false);
        blocker.killBrick(3);
        assertFalse(blocker.getBrick(4).anotherOnLeft);
        assertFalse(blocker.getBrick(2).anotherOnRight);
    }
    @Test
    public void killing_the_first_brick_should_only_change_the_state_of_one_brick_forward() throws Exception {
        blocker.rebuildBricks(screenWidth, false);
        blocker.killBrick(0);
        assertFalse(blocker.getBrick(1).anotherOnLeft);
    }
    @Test
    public void killing_the_final_brick_should_only_change_state_of_one_brick_previous() throws Exception{
        blocker.rebuildBricks(screenWidth,false);
        blocker.killBrick(blocker.getBrickLength() - 1);
        assertFalse(blocker.getBrick(blocker.getBrickLength() - 2).anotherOnRight);
    }
    // don't just test once
    public double rebuild_bricks_and_determine_percent_killed() throws Exception{
        blocker.rebuildBricks(true);
        List list=blocker.getKillList();
        double n=0;
        double d=0;
        for(int i=0;i<list.size();i++){
            //System.out.println(list.get(i));
            if((boolean)list.get(i))
                n+=1;
            d+=1;
        }
        //System.out.println();
        Brick[] brick=blocker.getBricks();
        return n/d;
    }
    @Test
    public void points_near_edge_of_brick_should_return_true() throws Exception{
        double brickWidth=blocker.getBrickWidth();
        double delta=2;
        double nearPointA=brickWidth-(delta/2);
        double nearPointB=brickWidth+(delta/2);
        double farPointA=brickWidth-(delta*2);
        double farPointB=brickWidth+(delta*2);
        assertTrue(blocker.nearEdge(nearPointA,delta));
        assertTrue(blocker.nearEdge(nearPointB,delta));
        assertFalse(blocker.nearEdge(farPointA,delta));
        assertFalse(blocker.nearEdge(farPointB,delta));
        assertTrue(blocker.nearEdge(brickWidth*2,delta));
        assertFalse(blocker.nearEdge(brickWidth*3.5,delta));
    }
    /*
    @Test
    public void rebuild_bricks_many_times_and_determine_percent_killed() throws Exception{
        ArrayList<Double> x=new ArrayList<>();
        for(int i=0;i<100;i++){
            x.add(rebuild_bricks_and_determine_percent_killed());
        }
        for(Double d:x){
            System.out.println(blocker.MIN_BRICK_PROBABILITY+" _ "+d+" _"+blocker.MAX_BRICK_PROBABILITY);
            //inclusive on minimum size, exclusive on maximum size
            assertTrue("there are not enough bricks killed", blocker.MIN_BRICK_PROBABILITY<=d);
            assertTrue("there are too many bricks killed",d<blocker.MAX_BRICK_PROBABILITY);
        }
    }*/
    /*
    @Test
    public void yet_another_wtf() throws  Exception{
        Random random=new Random();
        for(int i=0;i<100;i++){
            //System.out.println(random.nextDouble());
            System.out.println(blocker.randomNumberMissing(8));
        }
    }*/
}