package Tanks;

import processing.core.PImage;
import processing.core.PVector;
import processing.core.PApplet;
import static java.lang.Math.*;
import java.util.ArrayList;



public class Bullet{

    public float x;
    public float y;
    private double xVel;
    private double yVel;
    private float angle;
    private int[] terrain;
    private float power;
    private String color;
    private ArrayList<Tank> tanks;
    private int bulletID;
    private int vel;
    private boolean offScreen;
    
    public int BLAST_RADIUS = 30;
    private int score = 0;
    private int wind;


    public Bullet (float x, float y, float power, float angle, int[] terrain, String color, ArrayList<Tank> tanks, int bulletID, int wind){
        this.x = x;
        this.y = y;
        this.power = power;
        this.vel = (int) round(1 + 0.08 * this.power);
        this.angle = angle;
        this.yVel = this.vel * sin(PApplet.radians(this.angle));
        this.xVel = this.vel * cos(PApplet.radians(this.angle)) - 0.261680;
        this.terrain = terrain;
        this.color = color;
        this.tanks = tanks;
        this.bulletID = bulletID;
        this.wind = wind;
        
    }

    public void tick(PApplet app) {

        this.x += this.xVel;
        this.y -= this.yVel;

        this.xVel += + ((this.wind * 0.03)/30);
        this.yVel -= 0.2;

        if (this.hasHitGround()){
            this.explode((int)this.x, (int)this.y);

        }

        
        
        
    }

    public void draw (PApplet app){

        String[] arr = this.color.split(",");
        
        int a = Integer.parseInt(arr[0]);
        int b = Integer.parseInt(arr[1]);
        int c = Integer.parseInt(arr[2]);

        app.fill(a, b, c);
        app.ellipse(this.x, this.y, 7, 7);  

    }


    public boolean isOffScreen(){
        if (this.offScreen){
            return true;
        }
        return (x < 0 || x > App.WIDTH || y < 0 || y > App.HEIGHT);
    }

    public boolean hasHitGround(){
        if (isOffScreen() == false && this.y >= this.terrain[(int)this.x]){
            return true;
        }
        else {
            return false;
        }
    }

    public void shoot(){
        ;
    }

    public void explode(int x, int y){
        

        for (int i = (int)this.x - BLAST_RADIUS; i < this.x + BLAST_RADIUS; i++) {
            for (int j = (int)this.y - BLAST_RADIUS; j < this.y + BLAST_RADIUS; j++) {
                // Ensure the indices are within the bounds of the terrain array
                if (i >= 0 && i < this.terrain.length && j >= 0 && j > this.terrain[i]) {
                    // Calculate distance from the explosion point
                    float distance = dist(x, y, i, j);
                    // If within blast radius, update terrain
                    if (distance <= BLAST_RADIUS) {
                        float depth = y - (int)sqrt(BLAST_RADIUS * BLAST_RADIUS - (x - i) * (x - i));
                        // Update the terrain height at position i
                        terrain[i] += BLAST_RADIUS - distance;
                    }
                }
            }
        }

        updateTankHealth((int)this.x, (int)this.y);
        

        //Makes isOffScreen true so bullet disappears
        this.offScreen = true;
    }

    public void updateTankHealth(int x, int y){
        for (Tank tanks : this.tanks){
            if (dist((int)this.x, (int)this.y, tanks.x, tanks.y) <= 30){
                int damage = 60 - (2 * (int)dist((int)this.x, (int)this.y, tanks.x + 4 , tanks.y));
                int newHealth = tanks.getHealth() - damage;
                tanks.setHealth(newHealth);
                if (tanks.getID() != this.bulletID){
                    this.score += damage;
                }
            }
            if (Math.abs(this.x - tanks.x) <= 30){
                Tank t = null;
                for (int i = 0; i < this.tanks.size(); i++){
                    if (this.bulletID == this.tanks.get(i).getID()){
                        t = this.tanks.get(i);
                    }
                }
                tanks.setFellBy(t);
            }
            
            
         }
    }


    public float dist(int x1, int y1, int x2, int y2) {
        // Calculate the horizontal and vertical differences between the points
        float dx = x2 - x1;
        float dy = y2 - y1;
        
        // Use the Pythagorean theorem to find the distance
        float distance = (float) sqrt(dx * dx + dy * dy);
        
        return distance;
    }

    public int getScore(){
        return this.score;
    }
    
}