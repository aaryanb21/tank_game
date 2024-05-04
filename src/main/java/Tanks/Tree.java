package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

public class Tree{

    public int x;
    public int y;
    public PImage sprite;
    private int[] terrain;
    private int CELLHEIGHT = 32;

    public Tree(int x, int y, PImage sprite, int[] terrain){
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.terrain = terrain;
    }

    public void tick(){
        this.y = terrain[this.x + CELLHEIGHT/2] - CELLHEIGHT;
    }

    public void draw(PApplet app){
        app.image(this.sprite, this.x, this.y, 32, 32);
    }

    public void setTerrain(int[] terrain){
        this.terrain = terrain;
    }
}

