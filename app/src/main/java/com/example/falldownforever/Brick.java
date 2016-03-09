package com.example.falldownforever;

/**
 * Created by Will Hagen on 3/1/16.
 */
public class Brick { // plain old data structure representing the bricks in the blocker
    public double x;
    public boolean alive;
    public boolean anotherOnLeft;// is there a brick to the left of this brick?
    public boolean anotherOnRight;// these values could be calculated dynamically,
    // but by making them explicit it simplifies the game logic
}
