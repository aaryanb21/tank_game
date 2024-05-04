package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.core.PVector;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*; 

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public static int INITIAL_TANK_COUNT;

    public String configPath;

    public static Random random = new Random();

    public PImage bg;
    public int level = 1;
    public char[][] terrain = new char[28][20];
    public PImage tree;
    public PImage tank;
    public PImage fuel;
    public PImage wind_left;
    public PImage wind_right;
    public PImage parachute;
    public String fg_color;
    public int turn;
    public long arrowDisplayTime;
    public int tankCount; 
    public boolean isGameOver;

    public int[] terrain_new = new int[896];
    public ArrayList<Tank> tankArr = new ArrayList<Tank>();
    public ArrayList<Tree> treeArr = new ArrayList<Tree>();
    public int[] scoreArr = new int[9];

    private int wind = random.nextInt(71) - 35;

    private long lastScoreDisplayTime = 0;
    private long scoreDisplayInterval = 700; // 0.7 seconds in milliseconds


	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    //Extract terrain info from txt file and puts it into an array
    public void loadTerrain(String fileName){

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            
            int y = 0;
            String line;

            while (y < 20){
                line = br.readLine();
                if (line == null){
                    ;
                }
                else{
                    for (int x = 0; x < line.length(); x++){
                        terrain[x][y] = line.charAt(x);
                    }
                }
                
                y++;

            }
            br.close();
    
        }
        

        catch (Exception e){
            e.printStackTrace();;
        }
        
    }

    public void transfer(){

   
        for (int x = 0; x < 28; x++){
            for (int y = 0; y < 20; y++){
                if (terrain[x][y] == 'X'){
                    int start = x*CELLSIZE;
                    int end = start + CELLSIZE;
                    for (int i = start; i < end; i++){
                        if (i < 896){
                            terrain_new[i] = y*CELLSIZE;
                        }
                    }
                } 
            }
        }

    }

    public void smoothTerrain(){
        for (int i = 0; i < 864; i++){
            int counter = 0;
            for (int j = i; j < i + 32; j++){
                counter += terrain_new[j];
            }
            terrain_new[i] = counter/32;
        }
    }

    public App() {
        this.configPath = "config.json";
    }
    

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {

        tankCount = 0;
        turn = 1;

        treeArr.clear();
        tankArr.clear();


        frameRate(FPS);

		//See PApplet javadoc:
		//loadJSONObject(configPath)

        JSONObject config = loadJSONObject(this.configPath);
        JSONArray levelArray = config.getJSONArray("levels");
        JSONObject current_level = levelArray.getJSONObject(level-1);

        this.bg = loadImage("src/main/resources/Tanks/" + current_level.getString("background"));
        this.fg_color = current_level.getString("foreground-colour");
        this.tree = loadImage("src/main/resources/Tanks/" + current_level.getString("trees", "tree1.png"));
        this.tank = loadImage("src/main/resources/Tanks/tank3.png");
        this.fuel = loadImage("src/main/resources/Tanks/fuel.png");
        this.wind_left = loadImage("src/main/resources/Tanks/wind-1.png");
        this.wind_left.resize(32, 32);
        this.wind_right = loadImage("src/main/resources/Tanks/wind.png");
        this.wind_right.resize(32, 32);
        this.parachute = loadImage("src/main/resources/Tanks/parachute.png");
        this.parachute.resize(32, 32);
        this.isGameOver = false;
        
        //INitializing array full of blank tanks
        



        loadTerrain(current_level.getString("layout"));    
        transfer();
        smoothTerrain();
        smoothTerrain();
        arrowDisplayTime = millis() + 2000;
        INITIAL_TANK_COUNT = this.tankCount;


        for (int i = 0; i < 9; i++){
            Tank t = null;
            switch (i) {
                case 0:
                    t = new Tank(0,0, terrain_new, "0,0,255", tankArr, 0, this.wind);
                    break;
                case 1:
                    t = new Tank(0,0, terrain_new, "255,0,0", tankArr, 0, this.wind);
                    break;
                case 2:
                    t = new Tank(0,0, terrain_new, "0,255,255", tankArr, 0, this.wind);
                    break;
                case 3:
                    t = new Tank(0,0, terrain_new, "255,255,0", tankArr, 0, this.wind);
                case 4:
                    t = new Tank(0,0, terrain_new, "0,255,0", tankArr, 0, this.wind);
                    break;
                
                default:
                    t = new Tank(0,0, terrain_new, "255,255,255", tankArr, 0, this.wind);
            }
            tankArr.add(t);
        }


        drawSprites();
        for (int i = 0; i < tankArr.size(); i++){
            if (tankArr.get(i).getID() != 0){
                tankCount++;
            }
        }
    
    }

    public void repairPowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 20){
            if (tankArr.get(turn - 1).getHealth() <= 80){
                tankArr.get(turn - 1).setScore(score - 20);
            }
            else {
                int change = 100 - tankArr.get(turn - 1).getHealth();
                tankArr.get(turn - 1).setScore(score - change);
            }
                
            tankArr.get(turn - 1).setHealth(tankArr.get(turn - 1).getHealth() + 20);
        }
    }

    public void fuelPowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 10){
            tankArr.get(turn - 1).setScore(score - 10);
            tankArr.get(turn - 1).setFuel(tankArr.get(turn - 1).getFuel() + 200);
        }

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (key == CODED){
            if (keyCode == RIGHT){
                tankArr.get(turn - 1).moveRight();
            }

            else if (keyCode == LEFT){
                tankArr.get(turn - 1).moveLeft();
            }

            else if (keyCode == UP){
                tankArr.get(turn - 1).moveTurret("right");
            }
            
            else if (keyCode == DOWN){
                tankArr.get(turn - 1).moveTurret("left");
            }

        
        }
        if (keyPressed && keyCode == 32){
            if (this.tankCount <= 1){
                // if (this.level == 3){
                //     endGameScreen();
                // }
    
                // else{
                //     this.level++;
                // }
                // setup();
                this.isGameOver = true;

            }
            else{
                tankArr.get(turn - 1).shoot();
                tankArr.get(turn - 1).stop();
                if (this.turn >= tankCount){
                    this.turn = 1;
                }
                else{
                    this.turn++;
                }
                arrowDisplayTime = millis() + 2000;

                int windChange = random.nextInt(11) - 5;
                this.wind += windChange;
            }
            

        }

        if (keyCode == 'W' || keyCode == 'w'){
            tankArr.get(turn - 1).increasePower();
        }

        if (keyCode == 'S' || keyCode == 's'){
            tankArr.get(turn - 1).decreasePower();
        }

        if (keyCode == 'N' || keyCode == 'n'){
            ;

        }

        if (keyCode == 'R' || keyCode == 'r'){
            repairPowerUp();
        }
        if (keyCode == 'F' || keyCode == 'f'){
            fuelPowerUp();
        }
        if (keyCode == 'T' || keyCode == 't'){
            ;
        }


        
    }

    public void removeTank(int i){
        scoreArr[i] = tankArr.get(i).getScore();
        tankArr.get(i).setID(0);
        tankCount--;
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        if (key == CODED){
            if (keyCode == RIGHT || keyCode == LEFT){
                tankArr.get(turn - 1).stop();
            }

            else if (keyCode == UP || keyCode == DOWN){
                tankArr.get(turn - 1).stopTurret();
            }

            
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO - powerups, like repair and extra fuel and teleport
        
    }

    public void tick(){
        // if (this.isGameOver){
        //     long currentTime = System.currentTimeMillis();
            
        //     if (currentTime - lastScoreDisplayTime >= scoreDisplayInterval) {
        //         lastScoreDisplayTime = currentTime;
                
        //         for (int i = 0; i < 4; i++) {
        //             if (tankArr.get(i).getID() != 0){
        //                 String x = tankArr.get(i).getColor();
        //                 String[] arr = x.split(",");
                
        //                 int a = Integer.parseInt(arr[0]);
        //                 int b = Integer.parseInt(arr[1]);
        //                 int c = Integer.parseInt(arr[2]);
                
        //                 this.fill(a,b,c);
        //                 textSize(20);
        //                 this.text("Player " + ((char) (tankArr.get(i).getID() + 64)) +" Wins!", 300, 100);
                        
        //                 textSize(12);
        //                 stroke(0);
        //                 strokeWeight(3);
        //                 this.fill(a-59, b-50, c+50);
        //                 rect(200, 150, 400, 200);
        //                 line(200, 200, 600, 200);
                
        //                 for (int j = 0; j < 4; j = j){
        //                     this.fill(0,0,0);
        //                     this.text("hey", 250, 200 + (j * 50));
        //                 }
                        
        //             }
        //         }
        //     }
        // }
    }

    public void endGameScreen(){
       
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    public void drawTerrain(String color){

        String[] arr = color.split(",");
        

        int a = Integer.parseInt(arr[0]);
        int b = Integer.parseInt(arr[1]);
        int c = Integer.parseInt(arr[2]);

        this.fill(a,b,c);
   
        for (int x = 0; x < 864; x++){
            if(terrain_new[x] != 0){
                rect(x, terrain_new[x], 1, HEIGHT - terrain_new[x]);
                noStroke();
            }
        }
    }

    public int[] getTerrain(){
        return terrain_new;
    }

    public void drawSprites(){

    

        //Yet to implement trees and tanks 
        
        for (int x = 0; x < BOARD_WIDTH; x++){
            for (int y = 0; y < BOARD_HEIGHT; y++){
               
                if (terrain[x][y] == 'T'){
                    int low = (x*CELLSIZE) - 15;
                    int high = (x*CELLSIZE) + 15;
                    int randomTreeLocation = random.nextInt(high-low) + low;
                    if (randomTreeLocation < 0){
                        randomTreeLocation = 0;
                    }
                    if (randomTreeLocation > 864){
                        randomTreeLocation = 864;
                    }
                    Tree t = new Tree(randomTreeLocation - CELLHEIGHT/2 , terrain_new[randomTreeLocation] - CELLHEIGHT, this.tree, terrain_new);
                    treeArr.add(t);
                }
                else if ((int) terrain[x][y] >= 65 && (int) terrain[x][y] <= 73){
                    int x_pixel = x*CELLSIZE;

                    //Adding tanks to tank array
                    tankArr.get((int) terrain[x][y] - 65).x = x_pixel;
                    tankArr.get((int) terrain[x][y] - 65).y = terrain_new[x_pixel];
                    tankArr.get((int) terrain[x][y] - 65).setID((int) terrain[x][y] - 65 + 1);
                    tankArr.get((int) terrain[x][y] - 65).setScore(scoreArr[(int) terrain[x][y] - 65]);
                }
               
            }
        }
        
    }

    // public void removeBlankTanks(){
    //     for (int i = 0; i < tankArr.size(); i ++){
    //         if (tankArr.get(i).getID() == 0){
    //             tankArr.remove(i);
    //         }
    //     }
    // }


    /**xs
     * Draw all elements in the game by current frame.
     */

     // Error here needs to be fixedddddd where it goes out of bounds
	@Override
    public void draw() {


        this.image(this.bg,0,0);
        drawTerrain(this.fg_color);

        

        for (int i = 0; i < tankArr.size(); i++){
            if (tankArr.get(i).getID() != 0){
                tankArr.get(i).tick(this);
                tankArr.get(i).draw(this);

                //Checking if tank is dead
                if (tankArr.get(i).getHealth() <= 0){
                    tankArr.get(i).explode();
                    removeTank(i);
                }
            }
            

            
        }
        
        // tankArr.get(turn-1).tick(this);
        tankArr.get(turn-1).setTerrain(terrain_new);
        
       
        

        for (int i = 0; i < treeArr.size(); i++){
            treeArr.get(i).setTerrain(terrain_new);
            treeArr.get(i).tick();
            treeArr.get(i).draw(this);
        }

        this.fill(0,0,0);

        //Which player's turn
        text("Player " + (char) (turn + 64) + "'s turn", 20, 30);

        //Fuel bar
        text(tankArr.get(turn - 1).getFuel(), 300,30);
        this.image(this.fuel, 260, 10, 32, 32);

        //Parachute Count
        this.image(this.parachute, 150, 15);
        text(tankArr.get(turn - 1).getParachutes(), 190, 35);
        
        
        //Health Bar
        text("Health:", 400, 30);
        text(tankArr.get(turn - 1).getHealth(), 450, 30);

        //Power Bar
        text("Power:", 500, 30);
        // text(tankArr.get(turn - 1).getPower(), 550, 30);
        text((int)tankArr.get(turn - 1).getPower(), 550, 30);

        //Wind Speed
        if (this.wind < 0){
            this.image(this.wind_left, 600, 10);
        }
        else{
            this.image(this.wind_right, 600, 10);
        }
        text(this.wind, 650, 30);

        //ScoreBoard
        stroke(0);
        strokeWeight(3);
        line(700, 30, 850, 30);
        line(700, 150, 850, 150);
        line(700, 30, 700, 150);
        line(850, 30, 850, 150);
        text("Scores", 710, 50);
        line(700, 60, 850, 60);

        for (int i = 0; i < 4; i++){

            String x = tankArr.get(i).getColor();
            String[] arr = x.split(",");
        

            int a = Integer.parseInt(arr[0]);
            int b = Integer.parseInt(arr[1]);
            int c = Integer.parseInt(arr[2]);
    
            this.fill(a,b,c);

            text("Player " + (i+1), 710, 80 + i * 20);
            this.fill(0,0,0);
            text(tankArr.get(i).getScore(), 800, 80 + i * 20);
        }


        // text("Player 1", 710, 80);
        // text(tankArr.get(0).getScore(), 800, 80);
        
        // text("Player 2", 710, 100);
        // text(tankArr.get(1).getScore(), 800, 100);
        
        // text("Player 3", 710, 120);
        // text(tankArr.get(2).getScore(), 800, 120);
        
        // text("Player 4", 710, 140);
        // text(tankArr.get(3).getScore(), 800, 140);

        //Arrow Over Tank
        if (millis() < arrowDisplayTime){
            int arrowStartX = tankArr.get(turn - 1).x + 9; 
            int arrowStartY = tankArr.get(turn - 1).y - 200; 
            int arrowEndX = tankArr.get(turn - 1).x + 9; 
            int arrowEndY = tankArr.get(turn - 1).y - 100; 
            stroke(0);
            strokeWeight(2);
            line(arrowStartX, arrowStartY, arrowEndX, arrowEndY);
            line(arrowEndX-20, arrowEndY -20, arrowEndX, arrowEndY);
            line(arrowEndX+20, arrowEndY -20, arrowEndX, arrowEndY);
        }

        
        this.tick();


        
        //----------------------------------
        //display HUD:
        //----------------------------------s
        //TODO

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
    }




    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}