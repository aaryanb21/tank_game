package Tanks;

import processing.core.PApplet;

/**
 * The Explosion class represents an explosion effect at a specific location in the game.
 * It consists of expanding circles with different colors to simulate the explosion.
 */
public class Explosion {
    private int x;
    private int y;
    private int explosionRadius = 30;
    private float elapsedTime;
    private float animationDuration = 0.6f; // Animation duration

    // Radius variables for each circle
    private float redRadius;
    private float orangeRadius;
    private float yellowRadius;

    /**
     * Constructs a new Explosion object at the specified coordinates.
     *
     * @param x The X-coordinate of the explosion.
     * @param y The Y-coordinate of the explosion.
     */
    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.elapsedTime = 0;
        this.redRadius = 0;
        this.orangeRadius = 0;
        this.yellowRadius = 0; 
    }

    /**
     * Updates the state of the explosion.
     * Calculates the current radius of each circle based on the elapsed time.
     */
    public void tick() {
        elapsedTime += 0.2;

        // Calculate the current radius of each circle based on the elapsed time
        redRadius = PApplet.map(elapsedTime, 0, animationDuration, 0, explosionRadius);
        orangeRadius = PApplet.map(elapsedTime, 0, animationDuration, 0, explosionRadius * 0.5f);
        yellowRadius = PApplet.map(elapsedTime, 0, animationDuration, 0, explosionRadius * 0.2f);
    }

    /**
     * Draws the explosion on the screen.
     *
     * @param app The PApplet instance.
     */
    public void draw(PApplet app) {

        // Draw the red circle
        app.stroke(255, 0, 0);
        app.fill(255,0,0);
        app.ellipse(this.x, this.y, redRadius * 2, redRadius * 2);

        // Draw the orange circle
        app.stroke(255, 165, 0);
        app.fill(255, 165, 0);
        app.ellipse(this.x, this.y, orangeRadius * 2, orangeRadius * 2);

        // Draw the yellow circle
        app.stroke(255, 255, 255);
        app.fill(255, 255, 0);
        app.ellipse(this.x, this.y, yellowRadius * 2, yellowRadius * 2);
        app.noStroke();
    }

    public boolean isFinished() {
        return elapsedTime >= animationDuration;
    }

    public float getRedRadius(){
        return this.redRadius;
    }

    public float getOrangeRadius(){
        return this.orangeRadius;
    }

    public float getYellowRadius(){
        return this.yellowRadius;
    }
}
