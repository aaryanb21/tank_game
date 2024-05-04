package Tanks;

import processing.core.PImage;
import processing.core.PApplet;

public class Explosion {
    private int x;
    private int y;
    private int explosionRadius = 30;
    private float elapsedTime;
    private float animationDuration = 0.6f; // Animation duration in seconds

    // Radius variables for each circle
    private float redRadius;
    private float orangeRadius;
    private float yellowRadius;

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.elapsedTime = 0;
        this.redRadius = 0;
        this.orangeRadius = 0;
        this.yellowRadius = 0; 
    }

    public void update() {
        elapsedTime += 0.2;

        // Calculate the current radius of each circle based on the elapsed time
        redRadius = PApplet.map(elapsedTime, 0, animationDuration, 0, explosionRadius);
        orangeRadius = PApplet.map(elapsedTime, 0, animationDuration, 0, explosionRadius * 0.5f);
        yellowRadius = PApplet.map(elapsedTime, 0, animationDuration, 0, explosionRadius * 0.2f);
    }

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
}
