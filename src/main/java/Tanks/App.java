package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import java.io.*;
import java.util.*; 

/**
 * The main class representing the Tank game application.
 * This class extends the PApplet class provided by the Processing library.
 */

public class App extends PApplet {

    // Constants defining game parameters
    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 3;
    public static final int FPS = 30;


    // Attributes representing game state and data
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
    public String fgColor;
    public int turn;
    public long arrowDisplayTime;
    public int tankCount; 
    public int initialTankCount;
    public boolean scoreDisplayed;
    public int[] terrainNew = new int[896];
    public ArrayList<Tank> tankArr = new ArrayList<Tank>();
    public ArrayList<Tree> treeArr = new ArrayList<Tree>();
    public int wind = random.nextInt(71) - 35;
    public boolean teleportStatus = false;
    public boolean airStrikeStatus = false;
    public boolean isGameOver = false;
    public boolean showPowerUp = false;
    public Explosion e = null;

    //Variables for final scoreboard
    public int currentIndex = 0;
    public long lastWordTime = 0;
    public long wordInterval = 700; // Interval between words in milliseconds
    public ArrayList<Tank> displayedTanks = new ArrayList<>();

    /**
     * Main constructor of the App class.
     * Initializes the configuration file path.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Settings method to initialize the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Setup method to initialize game resources and elements.
     */
	@Override
    public void setup() {

        currentIndex = 0;
        tankCount = 0;
        initialTankCount = 0;
        turn = 1;
        this.level = 1;
        this.scoreDisplayed = false;
        treeArr.clear();
        tankArr.clear();
        displayedTanks.clear();

        //Emptying old terrain array
        for (int i = 0; i < 28; i++){
            Arrays.fill(terrain[i], ' ');
        }
        frameRate(FPS);
 
        JSONObject config = loadJSONObject(this.configPath);
        JSONArray levelArray = config.getJSONArray("levels");
        JSONObject current_level = levelArray.getJSONObject(this.level-1);

        this.bg = loadImage("src/main/resources/Tanks/" + current_level.getString("background"));
        this.fgColor = current_level.getString("foreground-colour");
        this.tree = loadImage("src/main/resources/Tanks/" + current_level.getString("trees", "tree1.png"));
        this.fuel = loadImage("src/main/resources/Tanks/fuel.png");
        this.wind_left = loadImage("src/main/resources/Tanks/wind-1.png");
        this.wind_left.resize(32, 32);
        this.wind_right = loadImage("src/main/resources/Tanks/wind.png");
        this.wind_right.resize(32, 32);
        this.parachute = loadImage("src/main/resources/Tanks/parachute.png");
        this.parachute.resize(32, 32);
        
        //Initializing array full of blank tanks

        loadTerrain(current_level.getString("layout"));    
        transfer();
        smoothTerrain();
        smoothTerrain();
        arrowDisplayTime = millis() + 2000;

        for (int i = 0; i < 9; i++){
            Tank t = null;
            switch (i) {
                case 0:
                    t = new Tank(0,0, terrainNew, "0,0,255", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                case 1:
                    t = new Tank(0,0, terrainNew, "255,0,0", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                case 2:
                    t = new Tank(0,0, terrainNew, "0,255,255", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                case 3:
                    t = new Tank(0,0, terrainNew, "255,255,0", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                case 4:
                    t = new Tank(0,0, terrainNew, "0,255,0", tankArr, 0, this.wind, (char)(i + 65));
                    break;
                
                default:
                    int randomR = random.nextInt(256);
                    int randomG = random.nextInt(256);
                    int randomB = random.nextInt(256);
                    String c = Integer.toString(randomR) + "," + Integer.toString(randomG) + "," + Integer.toString(randomB);
                    t = new Tank(0,0, terrainNew, c, tankArr, 0, this.wind, (char)(i + 65));
            }
            tankArr.add(t);
        }

        drawSprites();
        for (int i = 0; i < tankArr.size(); i++){
            if (tankArr.get(i).getID() != 0){
                tankCount++;
                initialTankCount++;
            }
        }
    }

    /**
     * Loads terrain data from a text file and populates the terrain array.
     * Each character in the text file represents a cell in the terrain array.
     * 'X' represents a filled cell, while other characters represent empty cells.
     * The terrain array is a 2D grid representing the game's terrain layout.
     * @param fileName The path to the text file containing terrain data.
     */
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

    /**
     * Transfers terrain data from the 2D char array to a 1D integer array.
     * The 1D array stores the height of each column in the terrain grid.
     * Each filled cell ('X') in the terrain grid contributes to the height of the corresponding column.
     * The method assumes that the terrain array has already been populated with data.
     */
    public void transfer(){
        for (int x = 0; x < 28; x++){
            for (int y = 0; y < 20; y++){
                if (terrain[x][y] == 'X'){
                    int start = x*CELLSIZE;
                    int end = start + CELLSIZE;
                    for (int i = start; i < end; i++){
                        if (i < 896){
                            terrainNew[i] = y*CELLSIZE;
                        }
                    }
                } 
            }
        }
    }

    /**
     * Smooths the terrain data to create a more visually appealing terrain.
     * This method calculates the moving average height of each 32-cell segment.
     * It iterates over the terrain data and replaces each segment with its moving average.
     * The method assumes that the terrain data has been transferred to the terrainNew array.
     */
    public void smoothTerrain(){
        for (int i = 0; i < 864; i++){
            int counter = 0;
            for (int j = i; j < i + 32; j++){
                counter += terrainNew[j];
            }
            terrainNew[i] = counter/32;
        }
    }

    /**
     * Method to load next level.
     * Loads level resources and terrain layout from the configuration file.
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
        this.fgColor = current_level.getString("foreground-colour");
        this.tree = loadImage("src/main/resources/Tanks/" + current_level.getString("trees", "tree1.png"));

        loadTerrain(current_level.getString("layout"));    
        transfer();
        smoothTerrain();
        smoothTerrain();
        arrowDisplayTime = millis() + 2000;

        drawSprites();
        for (int i = 0; i < tankArr.size(); i++){
            if (tankArr.get(i).getID() != 0){
                tankCount++;
            }
        }
    }

    /**
     * Provides a repair power-up to the current tank player if they have enough score points.
     * If the player's tank health is below 80%, it increases the health by 20 points.
     * If the health is already 80% or more, it increases the health to 100%.
     * Deducts the required score points from the player's score.
     */

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

    /**
     * Provides a fuel power-up to the current tank player if they have enough score points.
     * Increases the player's tank fuel by 200 units.
     * Deducts 10 score points from the player's score.
     */

    public void fuelPowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 10){
            tankArr.get(turn - 1).setScore(score - 10);
            tankArr.get(turn - 1).setFuel(tankArr.get(turn - 1).getFuel() + 200);
        }
    }

    /**
     * Provides a parachute power-up to the current tank player if they have enough score points.
     * Increments the number of parachutes available to the player.
     * Deducts 15 score points from the player's score.
     */

    public void parachutePowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 15){
            tankArr.get(turn - 1).incrementParachute();
            tankArr.get(turn - 1).setScore(score - 15);
        }
    }

    /**
     * Activates a teleportation power-up for the current tank player if they have enough score points.
     * Allows the player to select an X coordinate to teleport their tank to.
     * Deducts 50 score points from the player's score.
     */

    public void teleportPowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 15){
            tankArr.get(turn - 1).setScore(score - 15);
            teleportStatus = true;
        }

    }

    /**
     * Initiates an airstrike power-up for the current tank player if they have enough score points.
     * Allows the player to select an X coordinate to launch the airstrike at.
     * Deducts the required score points from the player's score.
     */

    public void airStrikePowerUp(){
        int score = tankArr.get(turn - 1).getScore();
        if (score >= 50){
            tankArr.get(turn - 1).setScore(score - 50);
            airStrikeStatus = true;
        }
    }

     /**
     * Method to handle keyboard inputs.
     * Responds to key presses for tank movement, shooting, power-ups, etc.
     * @param event The KeyEvent object representing the key press event.
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
                    if (this.turn >= initialTankCount){
                        this.turn = 1;
                    }
                    else{
                        this.turn++;
                        
                    }
                    while (tankArr.get(turn - 1).getID() == 0){
                        if (this.turn == initialTankCount){
                            this.turn = 1;
                        }
                        else{
                            this.turn++;
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
    * Method to handle key releases.
    * Responds to key releases for stopping tank movement, etc.
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

    /**
     * Method to handle mouse presses.
     * Responds to mouse clicks for using power-ups, etc.
     * @param e The MouseEvent object representing the mouse press event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO - powerups, like repair and extra fuel and teleport
        if (isGameOver == false){
            if (teleportStatus){
                int x = mouseX;
    
                tankArr.get(turn - 1).x = x;
                tankArr.get(turn - 1).y = terrainNew[x];
                teleportStatus = false;
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

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draws the terrain grid on the screen using the specified color.
     * Each filled cell in the terrain grid is represented as a rectangle.
     * The color parameter determines the fill color of the rectangles.
     * @param color The color in which the terrain grid should be drawn.
     */

    public void drawTerrain(String color){

        setColor(color);
   
        for (int x = 0; x < 864; x++){
            if(terrainNew[x] != 0){
                rect(x, terrainNew[x], 1, HEIGHT - terrainNew[x]);
                noStroke();
            }
        }
    }

    /**
     * Draws game sprites such as trees and tanks on the screen based on the terrain layout.
     * Trees and tanks are positioned according to their coordinates in the terrain grid.
     * Tree sprites are drawn at random x-coordinates within each terrain cell.
     */

    public void drawSprites(){
        
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
                    Tree t = new Tree(randomTreeLocation - CELLHEIGHT/2 , terrainNew[randomTreeLocation] - CELLHEIGHT, this.tree, terrainNew);
                    treeArr.add(t);
                }
                else if ((int) terrain[x][y] >= 65 && (int) terrain[x][y] <= 73){
                    int x_pixel = x*CELLSIZE;

                    //Adding tanks to tank array
                    tankArr.get((int) terrain[x][y] - 65).x = x_pixel;
                    tankArr.get((int) terrain[x][y] - 65).y = terrainNew[x_pixel];
                    tankArr.get((int) terrain[x][y] - 65).setID((int) terrain[x][y] - 65 + 1);
                }   
            }
        }
    }
    
    /**
     * Displays the final scores of all players at the end of the game.
     * The scores are displayed in a scoreboard format with player names and scores.
     * The winning player's name is also highlighted.
     */

    public void displayScore(){
        ArrayList<Tank> sortedTanks = new ArrayList<>();
        for (int i = 0; i < tankArr.size(); i++){
            if ((int)(tankArr.get(i).getName()) >= 65 && (int)(tankArr.get(i).getName()) <= 65 + initialTankCount - 1){
                sortedTanks.add(tankArr.get(i));
            }

        }
        Collections.sort(sortedTanks);   
        this.textSize(24);
        setColor(sortedTanks.get(sortedTanks.size() - 1).getColor());
        this.text("Player " + sortedTanks.get(sortedTanks.size() - 1).getName() + " wins!", 330, 130);
        this.textSize(12);

        setColorLight(sortedTanks.get(sortedTanks.size() - 1).getColor()); 
        
        this.stroke(2);
        rect(200, 150, 400,40 + (35 * initialTankCount));
        this.fill(0,0,0);
        this.line(200, 190, 600, 190);
        this.textSize(24);
        text("Final Scores", 220, 180);

        if (currentIndex < initialTankCount){

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

        text("Press R to restart game", 270, 220 + (35 * initialTankCount));

        this.textSize(12);
        this.noStroke();
        this.scoreDisplayed = true;
    }

    /**
     * Sets the fill color used for subsequent drawings on the screen.
     * The color parameter should be specified as a string in the format "R,G,B".
     * R, G, and B represent the red, green, and blue components of the color, respectively.
     * @param color The color string in the format "R,G,B".
     */

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

    /**
     * Method to update the game state and perform game logic for each frame.
     * This method manages tank movements, shooting, power-ups, terrain updates, etc.
     */
    public void tick(){

        //Checking if tank exploded
        if (e != null && e.isFinished() == false){
            e.tick();
            e.draw(this);
            }

        for (int i = 0; i < tankArr.size(); i++){
            if (tankArr.get(i).getID() != 0){
                tankArr.get(i).setWind(this.wind);
                tankArr.get(i).tick(this);
                tankArr.get(i).draw(this);

                //Checking if tank is dead
                if (tankArr.get(i).getHealth() <= 0){
                    tankArr.get(i).explode();
                    e = new Explosion(tankArr.get(i).x, tankArr.get(i).y);
                    removeTank(i);
                }
            }   
        }

        // tankArr.get(turn-1).tick(this);
        tankArr.get(turn-1).setTerrain(terrainNew);
        
        for (int i = 0; i < treeArr.size(); i++){
            treeArr.get(i).setTerrain(terrainNew);
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
        line(700, 30 + (30 * initialTankCount), 850, 30 + (30 * initialTankCount));
        line(700, 30, 700, 30 + (30 * initialTankCount));
        line(850, 30, 850, 30 + (30 * initialTankCount));
        text("Scores", 710, 50);
        line(700, 60, 850, 60);

        for (int i = 0; i < initialTankCount; i++){

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
        
        }


    /**
     * Method to draw game elements on the screen.
     * Renders terrain, tanks, trees, HUD, etc.
     */
	@Override
    public void draw() {

        this.image(this.bg,0,0);
        drawTerrain(this.fgColor);
       
        if (teleportStatus){
            textSize(20);
            fill(0,0,0);
            text("SELECT X VALUE TO TELEPORT TO", 270, 200);
            text("(CLICK ON SCREEN)", 330, 230);
            textSize(12);
        }

        if (airStrikeStatus){
            textSize(20);
            fill(0,0,0);
            text("SELECT X VALUE TO LAUNCH AIRSTRIKE AT", 220, 200);
            text("(CLICK ON SCREEN)", 330, 230);
            textSize(12);
        }

        this.tick();

        if (isGameOver){
            displayScore();
        }
    }

    public ArrayList<Tank> getTanks(){
        return this.tankArr;
    }
    
    public int[] getTerrain(){
        return terrainNew;
    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
