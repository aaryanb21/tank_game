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
    public int level;
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

    public int[] terrain_new = new int[896];
    public ArrayList<Tank> tankArr = new ArrayList<Tank>();
    public ArrayList<Tree> treeArr = new ArrayList<Tree>();

    private int wind = random.nextInt(71) - 35;


    public boolean teleport_status = false;
    public boolean airStrikeStatus = false;
    public boolean isGameOver = false;
    public boolean showPowerUp = false;


	
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


    public void loadLevel(){
        tankCount = 0;
        turn = 1;

        for (int i = 0; i < tankArr.size(); i++){
            tankArr.get(i).resetValues();
            tankArr.get(i).stop();
        }

        treeArr.clear();

        JSONObject config = loadJSONObject(this.configPath);
        JSONArray levelArray = config.getJSONArray("levels");
        JSONObject current_level = levelArray.getJSONObject(level-1);


        this.bg = loadImage("src/main/resources/Tanks/" + current_level.getString("background"));
        this.fg_color = current_level.getString("foreground-colour");
        this.tree = loadImage("src/main/resources/Tanks/" + current_level.getString("trees", "tree1.png"));

        loadTerrain(current_level.getString("layout"));    
        transfer();
        smoothTerrain();
        smoothTerrain();
        arrowDisplayTime = millis() + 2000;
        INITIAL_TANK_COUNT = this.tankCount;

        drawSprites();
        for (int i = 0; i < tankArr.size(); i++){
            if (tankArr.get(i).getID() != 0){
                tankCount++;
            }
        }
    }


    
	@Override
    public void setup() {


        currentIndex = 0;
        tankCount = 0;
        turn = 1;
        this.level = 1;

        treeArr.clear();
        tankArr.clear();
        displayedTanks.clear();


        //Emptying old terrain array
        for (int i = 0; i < 28; i++){
            Arrays.fill(terrain[i], ' ');
        }
        


        frameRate(FPS);
 
		//See PApplet javadoc:
		//loadJSONObject(configPath)

        JSONObject config = loadJSONObject(this.configPath);
        JSONArray levelArray = config.getJSONArray("levels");
        JSONObject current_level = levelArray.getJSONObject(this.level-1);

        this.bg = loadImage("src/main/resources/Tanks/" + current_level.getString("background"));
        this.fg_color = current_level.getString("foreground-colour");
        this.tree = loadImage("src/main/resources/Tanks/" + current_level.getString("trees", "tree1.png"));
        this.fuel = loadImage("src/main/resources/Tanks/fuel.png");
        this.wind_left = loadImage("src/main/resources/Tanks/wind-1.png");
        this.wind_left.resize(32, 32);
        this.wind_right = loadImage("src/main/resources/Tanks/wind.png");
        this.wind_right.resize(32, 32);
        this.parachute = loadImage("src/main/resources/Tanks/parachute.png");
        this.parachute.resize(32, 32);
        
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
                    t = new Tank(0,0, terrain_new, "0,0,255", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                case 1:
                    t = new Tank(0,0, terrain_new, "255,0,0", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                case 2:
                    t = new Tank(0,0, terrain_new, "0,255,255", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                case 3:
                    t = new Tank(0,0, terrain_new, "255,255,0", tankArr, 0, this.wind, (char)(i + 65));
                case 4:
                    t = new Tank(0,0, terrain_new, "0,255,0", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                
                default:
                    t = new Tank(0,0, terrain_new, "255,255,255", tankArr, 0, this.wind, (char)(i + 65));
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

    public void parachutePowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 15){
            tankArr.get(turn - 1).incrementParachute();
            tankArr.get(turn - 1).setScore(score - 15);
        }
    }

    public void teleportPowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 15){
            tankArr.get(turn - 1).setScore(score - 15);
            teleport_status = true;
        }

    }

    public void airStrikePowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 50){
            tankArr.get(turn - 1).setScore(score - 50);
            airStrikeStatus = true;
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (isGameOver == false){
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
                    if (this.level == 3){
                        isGameOver = true;
                    }
                    else{
                        this.level++;
                        loadLevel();
                    }
                    
                }
                else{
                    tankArr.get(turn - 1).shoot();
                    tankArr.get(turn - 1).stopTurret();
                    tankArr.get(turn - 1).stop();
                    if (this.turn >= 4){
                        this.turn = 1;
                    }
                    else{
                        this.turn++;
                        while (tankArr.get(turn - 1).getID() == 0){
                            if (this.turn == 4){
                                this.turn = 1;
                            }
                            else{
                                this.turn++;
                            }
                            
                        }
                        
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
                this.level++;
                loadLevel();
            }
    
            if (keyCode == 'R' || keyCode == 'r'){
                repairPowerUp();
            }
            if (keyCode == 'F' || keyCode == 'f'){
                fuelPowerUp();
            }
            if (keyCode == 'P' || keyCode == 'p'){
                parachutePowerUp();
            }
            if (keyCode == 'T' || keyCode == 't'){
                teleportPowerUp();
            }
            if (keyCode == 'A' || keyCode == 'a'){
                airStrikePowerUp();
            }
        }

        if(isGameOver){
            if (keyCode == 'R' || keyCode == 'r'){
                isGameOver = false;
                setup();
            }
        }
    }

    public void removeTank(int i){
        tankArr.get(i).setID(0);
        tankCount--;
        if (turn == i + 1){
            turn++;
        }
        
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
        if (isGameOver == false){
            if (teleport_status){
                int x = mouseX;
    
                tankArr.get(turn - 1).x = x;
                tankArr.get(turn - 1).y = terrain_new[x];
                teleport_status = false;
            }
    
            if(airStrikeStatus){
                int x = mouseX;
    
                tankArr.get(turn - 1).airStrikePowerUp(x);
                airStrikeStatus = false;
            }
    
            if ((mouseX >= 20 && mouseX <= 160) && (mouseY >= 50 && mouseY <= 80)){
                this.showPowerUp = !this.showPowerUp;
            }
        }
        
    }

    public void tick(){
        
    }

    public void endGameScreen(){
       
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    public void drawTerrain(String color){

        setColor(color);
   
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
                }
               
            }
        }
        
    }

    int currentIndex = 0;
    long lastWordTime = 0;
    long wordInterval = 700; // Interval between words in milliseconds
    
    ArrayList<Tank> displayedTanks = new ArrayList<>();
    

    public void displayScore(){
        ArrayList<Tank> sortedTanks = new ArrayList<>();
        for (int i = 0; i < tankArr.size(); i++){
            if ((int)(tankArr.get(i).getName()) >= 65 && (int)(tankArr.get(i).getName()) <= 68){
                sortedTanks.add(tankArr.get(i));
            }

        }
        Collections.sort(sortedTanks);   
        this.textSize(24);
        this.text("Player " + sortedTanks.get(sortedTanks.size() - 1).getName() + " wins!", 270, 130);
        this.textSize(12);

        setColorLight(sortedTanks.get(sortedTanks.size() - 1).getColor()); 
        
        this.stroke(2);
        rect(200, 150, 400,180);
        this.fill(0,0,0);
        this.line(200, 190, 600, 190);
        this.textSize(24);
        text("Final Scores", 220, 180);

        

        if (currentIndex < 4){

            if (millis() - lastWordTime > wordInterval) {
                displayedTanks.add(sortedTanks.get(sortedTanks.size() - 1 - currentIndex));
                lastWordTime = millis();
                currentIndex++;
            }
        
        }
        fill(0);
        this.textSize(24);
        for (int i = 0; i < displayedTanks.size(); i++) {

            setColor(displayedTanks.get(i).getColor());
     
            text("Player " + (displayedTanks.get(i).getName()), 220, 220 + i * 30);
            this.fill(0,0,0);
            text(displayedTanks.get(i).getScore(), 550, 220 + i * 30);
        }

        text("Press R to restart game", 270, 370);

        this.textSize(12);
        this.noStroke();
    }

    public void setColor(String color){
        String[] arr = color.split(",");
    

        int a = Integer.parseInt(arr[0]);
        int b = Integer.parseInt(arr[1]);
        int c = Integer.parseInt(arr[2]);
    
        this.fill(a,b,c);
    }

    public void setColorLight(String color){
        String[] arr = color.split(",");
        

        int a = Integer.parseInt(arr[0]);
        int b = Integer.parseInt(arr[1]);
        int c = Integer.parseInt(arr[2]);
        
        this.fill(a - 50,b - 50,c + 50, 170);    
    } 

    

	@Override
    public void draw() {


        this.image(this.bg,0,0);
        drawTerrain(this.fg_color);
       

        if (teleport_status){
            textSize(20);
            fill(0,0,0);
            text("SELECT X VALUE TO TELEPORT TO", 270, 200);
            textSize(12);
        }

        if (airStrikeStatus){
            textSize(20);
            fill(0,0,0);
            text("SELECT X VALUE TO LAUNCH AIRSTRIKE AT", 220, 200);
            textSize(12);
        }

        

        for (int i = 0; i < tankArr.size(); i++){
            if (tankArr.get(i).getID() != 0){
                tankArr.get(i).setWind(this.wind);
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

        //PowerUp Box
        stroke(0);
        strokeWeight(2);
        line(20, 50, 165, 50);
        line(20, 80, 165, 80);
        line(20, 50, 20, 80);
        line(165, 50, 165, 80);
        
        if (this.showPowerUp){
            text("Click to hide powerups", 25, 70);
            text("Repair Tank (R) - 20 Points", 20, 100);
            text("Buy Fuel (F) - 10 Points", 20, 120);
            text("Buy Parachute (P) - 15 Points", 20, 140);
            text("Teleport (T) - 15 Points", 20, 160);
            text("Launch AirStrike (A) - 50 Points", 20, 180);
        }
        else{
            text("Click to see powerups", 25, 70);
        }


        //Fuel bar
        text(tankArr.get(turn - 1).getFuel(), 300,30);
        this.image(this.fuel, 260, 10, 32, 32);

        //Parachute Count
        this.image(this.parachute, 150, 15);
        text(tankArr.get(turn - 1).getParachutes(), 190, 35);
        
        
        //Health and power Bar
        text("Health:", 350, 30);
        setColor(tankArr.get(turn - 1).getColor());
        stroke(0,0,0);
        strokeWeight(2);
        line(400, 15, 500, 15);
        line(400, 35, 500, 35);
        line(400, 15, 400, 35); 
        line(500, 15, 500, 35);


  

        rect(400, 15, 100 * tankArr.get(turn - 1).getHealth() / 100, 20);
        line(400 + (tankArr.get(turn - 1).getPower()), 15, 400 + (tankArr.get(turn - 1).getPower()), 35);
        
        // line(400 + (tankArr.get(turn - 1).getPower()), 15, 400 + (tankArr.get(turn - 1).getPower()), 35);
        
        this.fill(0,0,0);
        text(tankArr.get(turn - 1).getHealth(), 510, 30);

        //Power Bar
        text("Power:", 350, 60);
        // text(tankArr.get(turn - 1).getPower(), 550, 30);
        text((int)tankArr.get(turn - 1).getPower(), 400, 60);

        //Wind Speed
        if (this.wind < 0){
            this.image(this.wind_left, 600, 10);
        }
        else{
            this.image(this.wind_right, 600, 10);
        }
        text(this.wind, 645, 30);


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

            setColor(tankArr.get(i).getColor());

            text("Player " + (char)(i+65), 710, 80 + i * 20);
            this.fill(0,0,0);
            text(tankArr.get(i).getScore(), 800, 80 + i * 20); 
        }



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

        if (isGameOver){
            displayScore();            
        }


        
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
