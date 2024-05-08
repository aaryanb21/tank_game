package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Tree class represents a tree object in the game.
 * Trees are static elements that are placed on the terrain.
 */
public class Tree{

    public int x;
    public int y;
    public PImage sprite;
    private int[] terrain;
    private int CELLHEIGHT = 32;

    /**
     * Constructs a new Tree object with the specified parameters.
     *
     * @param x      The X-coordinate of the tree.
     * @param y      The Y-coordinate of the tree.
     * @param sprite The sprite image of the tree.
     * @param terrain The terrain height map.
     */
    public Tree(int x, int y, PImage sprite, int[] terrain){
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.terrain = terrain;
    }

    /**
     * Updates the position of the tree based on the current terrain height.
     */
    public void tick(){
        this.y = terrain[this.x + CELLHEIGHT/2] - CELLHEIGHT;
    }

    /**
     * Draws the tree on the screen.
     *
     * @param app The PApplet instance.
     */
    public void draw(PApplet app){
        if (this.sprite != null){
            app.image(this.sprite, this.x, this.y, 32, 32);
        }
    }

    public void setTerrain(int[] terrain){
        this.terrain = terrain;
    }

}

