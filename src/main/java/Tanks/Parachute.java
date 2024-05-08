package Tanks;

import processing.core.PApplet;

/**
 * The Parachute class represents a parachute object in the game.
 * Parachutes are used to slow the descent of tanks falling from the sky.
 */
public class Parachute{
    private int x;
    private int y;

    /**
     * Constructs a new Parachute object at the specified coordinates.
     *
     * @param x    The initial X-coordinate of the parachute.
     * @param y    The initial Y-coordinate of the parachute.
     * @param app  The PApplet instance.
     */
    public Parachute(int x, int y, PApplet app){
        this.x = x;
        this.y = y;
    }

    /**
     * Updates the position of the parachute for each game tick.
     * Moves the parachute upwards to simulate descent slowing.
     */

    public void tick(){
        this.y -= 2;
    }

    /**
     * Draws the parachute on the screen.
     *
     * @param app The PApplet instance.
     */

    public void draw(PApplet app){
        app.image( app.loadImage("src/main/resources/Tanks/parachute.png"), this.x, this.y);
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
}