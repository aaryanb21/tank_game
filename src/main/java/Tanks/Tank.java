package Tanks;

import processing.core.PImage;

import java.util.ArrayList;

import processing.core.PApplet;

public class Tank{
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


    public Tank(int x, int y, int[] terrain_new, String color, ArrayList<Tank> tanks, int tankID, int wind){
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
        this.score = 50;
        this.tankID = tankID;
        this.wind = wind; 
        this.parachutes = 3;
        this.parachuteDeployed = false;
        this.fallDamage = 0;
    }

    public void setup(){
        ;
    }

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

            if (this.rotateAngle > 90){
                this.rotateAngle = 90;
            }
            if (this.rotateAngle < -90){
                this.rotateAngle = -90;
            }
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
            this.health -= this.fallDamage;
            this.fallDamage = 0;
        }
                
    }

    public void explode(){
        bullets.add(new Bullet(this.x + 9 - 5, this.y - 5, this.power, Math.abs(this.rotateAngle - 90 + 3), this.terrain_new, this.color, this.tanks, this.tankID, this.wind));
        bullets.get(bullets.size() - 1).explode(this.x, this.y);
    }

    public void draw(PApplet app){

        //Tank
        String[] arr = this.color.split(",");
        
        int a = Integer.parseInt(arr[0]);
        int b = Integer.parseInt(arr[1]);
        int c = Integer.parseInt(arr[2]);

        app.fill(a, b, c);
        app.rect(this.x - 5, this.y - 1, 18, 6);
        app.rect(this.x+4 - 5,this.y-6 - 1, 9, 6);
        
        //Turret
        app.translate(this.x+9 - 5, this.y-3 - 1);
        app.rotate(PApplet.radians(this.rotateAngle));
        app.fill(0,0,0);
        app.rect(-2,-15,4,15);
        app.resetMatrix();
        
        //Bullet
        updateBullets(app);
        displayBullets(app);
        
        //Explosion
        if (e != null && e.isFinished() == false){
            e.update();
            e.draw(app);
        }

    }


    public void moveRight(){
        if (this.fuel > 0){
            this.xVel = 2;
        }
        else{
            this.stop();
        }
        
    }

    public void moveLeft(){
        if (this.fuel > 0){
            this.xVel = -2;
        }
        else{
            this.stop();
        }
        
    }

    public void moveTurret(String direction){
        if (direction.equals("right")){
            this.rotateAngleVel = 3;
        }
        else{
            this.rotateAngleVel = -3;
        }
        
    }

    public void stop(){
        this.xVel = 0;
    }

    public void stopTurret(){
        this.rotateAngleVel = 0;
    }

    public int getFuel(){
        return this.fuel;
    }

    public int getHealth(){
        return this.health;
    }

    public void shoot(){
        bullets.add(new Bullet(this.x + 9 - 5, this.y - 5, this.power, Math.abs(this.rotateAngle - 90 + 3), this.terrain_new, this.color, this.tanks, this.tankID, this.wind));
    }
    public void airStrikePowerUp(int x){
        bullets.add(new Bullet(x, 5, 50, 270, this.terrain_new, this.color, this.tanks, this.tankID, this.wind));
    }

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



    public void displayBullets(PApplet app) {
        for (Bullet bullet : bullets) {
            bullet.draw(app);
        }
    }

    public void setTerrain(int[] terrain){
        this.terrain_new = terrain;
    }

    public void increasePower(){
        this.power += 1.2;
        if (this.power > this.health){
            this.power = this.health;
        }
    }

    public void decreasePower(){
        this.power -= 1.2;
        if (this.power < 0){
            this.power = 0;
        }
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

    public void resetValues(){
        this.health = 100;
        this.power = 50;
        this.rotateAngle = 0;
        this.rotateAngleVel = 0;
        this.fuel = 250;
    }

    public void incrementParachute(){
        this.parachutes++;
    }




}