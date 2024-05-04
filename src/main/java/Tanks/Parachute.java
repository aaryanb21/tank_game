package Tanks;

import processing.core.PImage;
import processing.core.PApplet;


public class Parachute{
    private int x;
    private int y;
    private PImage sprite;
    private PApplet app;


    public Parachute(int x, int y, PApplet app){
        this.x = x;
        this.y = y;
        this.sprite = app.loadImage("src/main/resources/Tanks/parachute.png");
    }

    public void tick(){
        this.y -= 2;
    }

    public void draw(PApplet app){
        app.image(this.sprite, this.x, this.y);
    }
}