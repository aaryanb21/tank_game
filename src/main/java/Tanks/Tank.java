package Tanks;

import java.util.ArrayList;
import processing.core.PApplet;

/**
 * The Tank class represents a tank object in a game environment.
 * Tanks have attributes such as position, velocity, health, fuel, power, and score,
 * and can perform actions like movement, shooting, and airstrikes.
 */
public class Tank implements Comparable<Tank>{
    public int x;
    public int y;
    private int xVel;
    private int fuel;
    private float power;
    private int health;
    public float rotateAngle;
    private int rotateAngleVel;
    public ArrayList<Bullet> bullets;
    private int[] terrain_new;
    private String color;
    private ArrayList<Tank> tanks;
    private int score;
    private int tankID;
    private Explosion e;
    private int wind;
    private int parachutes;
    private boolean parachuteDeployed;
    private int fallDamage;
    private int scoreToBeUpdated;
    private char name;
    private Tank fellBy;

    /**
     * Constructs a new Tank object with the specified parameters.
     *
     * @param x          The initial X-coordinate of the tank.
     * @param y          The initial Y-coordinate of the tank.
     * @param terrain_new The terrain information.
     * @param color      The color of the tank.
     * @param tanks      The list of all tanks in the game.
     * @param tankID     The unique ID of the tank.
     * @param wind       The wind speed.
     * @param name       The name of the tank.
     */
    public Tank(int x, int y, int[] terrain_new, String color, ArrayList<Tank> tanks, int tankID, int wind, char name){
        this.x = x;
        this.y = y;
        this.xVel = 0;
        this.fuel = 250;
        this.power = 50;
        this.health = 100;
        this.rotateAngle = 0;
        this.rotateAngleVel = 0;
        this.bullets = new ArrayList<Bullet>();
        this.terrain_new = terrain_new;
        this.color = color;
        this.tanks = tanks;
        this.score = 0;
        this.tankID = tankID;
        this.wind = wind; 
        this.parachutes = 3;
        this.parachuteDeployed = false;
        this.fallDamage = 0;
        this.name = name;
    }

    public int compareTo(Tank t){
        return Integer.compare(this.score, t.getScore());
    }

    /**
     * Updates the state of the tank for each game tick.
     *
     * @param app The PApplet instance.
     */
    public void tick(PApplet app){
        if(this.y >= 640){
            this.explode();
        }

        if (this.power > this.health){
            this.power = this.health;
        }
        
        if (this.x >= 2 && this.x <= 864){
            if (this.fuel >= 1){
                this.x += this.xVel;
            }
            else {
                this.stop();
            }
            if (this.xVel != 0){
                this.fuel -= 1;
            }
        }
        else if (this.x < 2){
            this.x = 2;
        }
        else if (this.x > 864){
            this.x = 864;
        }

        if (this.rotateAngle >= -90 && this.rotateAngle <= 90){
            this.rotateAngle += this.rotateAngleVel;
        }

        if (this.rotateAngle > 90){
            this.rotateAngle = 90;
        }
        if (this.rotateAngle < -90){
            this.rotateAngle = -90;
            }
        if (this.y < terrain_new[this.x] && this.xVel == 0){
            if (this.parachutes >= 1 || this.parachutes == 0 && this.parachuteDeployed == true){
                this.y += 2;
                if (this.parachuteDeployed == false){
                    this.parachutes--;
                }
                this.parachuteDeployed = true;
                Parachute p = new Parachute(this.x - 30, this.y - 70, app);
                p.tick();
                p.draw(app);
            }
            else{ 
                this.parachuteDeployed = false;
                this.y += 4;
                this.fallDamage += 4;
            }
        }
        else{
            this.parachuteDeployed = false;
            this.y = this.terrain_new[this.x];
            if (this.fallDamage > this.health){
                this.fallDamage = this.health;
            }
            this.health -= this.fallDamage;
            if (this.fellBy != null && this.fellBy.getID() != this.tankID){
                this.fellBy.setScore(this.fellBy.getScore() + fallDamage);
            }
            this.fallDamage = 0;
        }       
    }

    /**
     * Initiates an explosion by creating a bullet at the tank's position.
     * The explosion occurs when the tank's y-coordinate exceeds a certain threshold,
     * typically indicating that the tank has fallen off the terrain.
     * The bullet created represents the explosion effect.
     */
    public void explode(){
        bullets.add(new Bullet(this.x + 9 - 5, this.y - 5, this.power, Math.abs(this.rotateAngle - 90 + 3), this.terrain_new, this.color, this.tanks, this.tankID, this.wind));
        bullets.get(bullets.size() - 1).explode(this.x, this.y);
    }

    /**
     * Draws the tank on the screen.
     *
     * @param app The PApplet instance.
     */
    public void draw(PApplet app){

        //Tank
        String[] arr = this.color.split(",");
        
        int a = Integer.parseInt(arr[0]);
        int b = Integer.parseInt(arr[1]);
        int c = Integer.parseInt(arr[2]);

        app.fill(a, b, c);
        app.rect(this.x - 7, this.y - 1, 18, 6);
        app.rect(this.x+4 - 7,this.y-6 - 1, 9, 6);
        
        //Turret
        app.translate(this.x+9 - 7, this.y-3 - 1);
        app.rotate(PApplet.radians(this.rotateAngle));
        app.fill(0,0,0);
        app.rect(-2,-15,4,15);
        app.resetMatrix();
        
        //Bullet
        updateBullets(app);
        displayBullets(app);
        
        //Explosion
        if (e != null && e.isFinished() == false){
            e.tick();
            e.draw(app);
        }
    }

    /**
     * Moves the tank to the right if fuel is available.
     * If fuel is depleted, the tank stops moving.
     */
    public void moveRight(){
        if (this.fuel > 0){
            this.xVel = 2;
        }
        else{
            this.stop();
        }
    }

    /**
     * Moves the tank to the left if fuel is available.
     * If fuel is depleted, the tank stops moving.
     */
    public void moveLeft(){
        if (this.fuel > 0){
            this.xVel = -2;
        }
        else{
            this.stop();
        }
    }

    /**
     * Rotates the turret of the tank either left or right.
     *
     * @param direction The direction to rotate the turret ("right" or "left").
     */
    public void moveTurret(String direction){
        if (direction.equals("right")){
            this.rotateAngleVel = 3;
        }
        else{
            this.rotateAngleVel = -3;
        }    
    }
   
    /**
     * Stops the horizontal movement of the tank.
     * The tank remains stationary until further movement commands are issued.
     */
    public void stop(){
        this.xVel = 0;
    }

    /**
     * Stops the rotation of the turret.
     * The turret remains in its current position until further rotation commands are issued.
     */
    public void stopTurret(){
        this.rotateAngleVel = 0;
    }

    /**
     * Initiates a shooting action by creating a new bullet at the tank's turret position.
     * The bullet inherits the tank's position, angle, and other properties.
     */
    public void shoot(){
        bullets.add(new Bullet(this.x + 9 - 7, this.y - 5, this.power, Math.abs(this.rotateAngle - 90 + 3), this.terrain_new, this.color, this.tanks, this.tankID, this.wind));
    }

    /**
     * Initiates an airstrike power-up by creating a bullet at the specified x-coordinate.
     * The airstrike bullet travels vertically downwards, causing damage upon impact.
     *
     * @param x The x-coordinate at which to initiate the airstrike.
     */
    public void airStrikePowerUp(int x){
        bullets.add(new Bullet(x, 5, 50, 270, this.terrain_new, this.color, this.tanks, this.tankID, this.wind));
    }

    /**
     * Updates the positions and states of bullets fired by the tank.
     * Removes bullets that have gone off-screen.
     *
     * @param app The PApplet instance.
     */
    public void updateBullets(PApplet app) {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.tick(app);
            if (bullet.isOffScreen()) {
                this.score += bullets.get(i).getScore();
                e = new Explosion((int)bullet.x, (int)bullet.y);
                bullets.remove(i);
            }
        }
    }

    /**
     * Renders all bullets fired by the tank on the screen.
     *
     * @param app The PApplet instance.
     */
    public void displayBullets(PApplet app) {
        for (Bullet bullet : bullets) {
            bullet.draw(app);
        }
    }

    public void setTerrain(int[] terrain){
        this.terrain_new = terrain;
    }

    /**
     * Increases the power of the tank's shots.
     * Power is capped at the tank's health level.
     */
    public void increasePower(){
        this.power += 1.2;
        if (this.power > this.health){
            this.power = this.health;
        }
    }

    /**
     * Decreases the power of the tank's shots.
     * Power cannot go below zero.
     */
    public void decreasePower(){
        this.power -= 1.2;
        if (this.power < 0){
            this.power = 0;
        }
    }

    /**
     * Resets the tank's attributes to their default values.
     * Used when respawning the tank or starting a new game.
     */
    public void resetValues(){  
        this.health = 100;
        this.power = 50;
        this.rotateAngle = 0;
        this.rotateAngleVel = 0;
        this.fuel = 250;
        this.bullets.clear();
        this.e = null;
    }

    public void incrementParachute(){
        this.parachutes++;
    }

    public char getName(){
        return this.name;
    }

    public void setFellBy(Tank t){
        this.fellBy = t;
    }

    public void setWind(int w){
        this.wind = w;
    }

    public void setxVel (int xVel){
        this.xVel = xVel;
    }

    public boolean isParachuteDeployed(){
        return this.parachuteDeployed;
    }

    public int getxVel (){
        return this.xVel;
    }

    public int getRotateAngleVel(){
        return this.rotateAngleVel;
    }

    public void setParachutes(int p){
        this.parachutes = p;
    }

    public int getFuel(){
        return this.fuel;
    }

    public int getHealth(){
        return this.health;
    }

    public void setFuel(int fuel){
        this.fuel = fuel;
    }

    public String getColor(){
        return this.color;
    }

    public int getParachutes(){
        return this.parachutes;
    }

    public int getScoreToBeUpdated(){
        return this.scoreToBeUpdated;
    }

    public void setScoreToBeUpdated(int x){
        this.scoreToBeUpdated = x;
    }

    public float getPower(){
        return this.power;
    }

    public void setHealth(int health){

        if (health <= 0){
            this.health = 0;
        }
        if (health >= 100){
            this.health = 100;
        }
        else {
            this.health = health;
        }
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getID(){
        return this.tankID;
    }

    public void setID(int id){
        this.tankID = id;
    }

}