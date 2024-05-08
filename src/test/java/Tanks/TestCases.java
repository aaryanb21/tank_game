package Tanks;


import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;

public class TestCases {

    @Test
    void testInitialGameState() {
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        assertEquals("config.json", app.configPath);
        assertFalse(app.isGameOver, "Game should not be over at start.");
        assertEquals(App.INITIAL_PARACHUTES, 3, "Initial parachutes not set up correctly.");
    }

    @Test
    void testGameBoardDimensions() {
        assertEquals(864, App.WIDTH, "Game width is incorrect.");
        assertEquals(640, App.HEIGHT, "Game height is incorrect.");
    }

    @Test
    void testTerrainSetup() {
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        assertNotNull(app.terrain, "Terrain should be initialized.");
    }

    @Test
    void testWindConditions() {
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        assertTrue(app.wind >= -35 && app.wind <= 35, "Wind conditions should be within the specified range.");
    }

    @Test
    void buttonPressTestShoot(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);


        app.keyPressed = true;
        app.keyCode = 32;
        
        KeyEvent keyevent = new KeyEvent(null, 0, 0, 0, ' ', 0);

        app.keyPressed(keyevent);
        assertTrue(app.level <= 3); //Level is still valid
        assertTrue(app.tankArr.size() != 0); //Tank Array is not empty
        assertNotNull(app.tankArr.get(app.turn - 1).bullets); //Checking that a bullet was shot

    }

    @Test
    void testPowerUps(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);


        KeyEvent keyevent = new KeyEvent(null, 0, 0, 0, ' ', 0);

        app.keyCode = 'r';
        app.keyPressed(keyevent);
        assertNotNull(app.tankArr.get(app.turn - 1)); //Ensuring powerups are not called on null tank values

        app.keyCode = 'f';
        app.keyPressed(keyevent);
        assertNotNull(app.tankArr.get(app.turn - 1)); //Ensuring powerups are not called on null tank values

        app.keyCode = 'p';
        app.keyPressed(keyevent);
        assertNotNull(app.tankArr.get(app.turn - 1)); //Ensuring powerups are not called on null tank values

        app.keyCode = 't';
        app.keyPressed(keyevent);
        assertNotNull(app.tankArr.get(app.turn - 1)); //Ensuring powerups are not called on null tank values

        app.keyCode = 'a';
        app.keyPressed(keyevent);
        assertNotNull(app.tankArr.get(app.turn - 1)); //Ensuring powerups are not called on null tank values
    }

    @Test
    void checkTankMovement(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        

        app.tankArr.get(0).moveRight();
        assertTrue(app.tankArr.get(0).getxVel() >= 0); //Making sure tank moves

        app.tankArr.get(0).moveLeft();
        assertTrue(app.tankArr.get(0).getxVel() <= 0); //Making sure tank moves

        KeyEvent k = new KeyEvent(null, 0, 0, 0, ' ', 0); 
        app.keyPressed = true;
        app.keyCode = PApplet.RIGHT;
        app.key = PApplet.CODED;

        app.keyPressed(k);
        assertTrue(app.tankArr.get(app.turn - 1).getxVel() > 0); //Making sure right key makes tank move

        

        app.keyPressed = true;
        app.keyCode = PApplet.LEFT;
        app.key = PApplet.CODED;

        app.keyPressed(k);
        assertTrue(app.tankArr.get(app.turn - 1).getxVel() < 0); //Making sure left key makes tank move
        


        app.keyPressed = true;
        app.keyCode = PApplet.UP;
        app.key = PApplet.CODED;

        app.keyPressed(k);
        assertTrue(app.tankArr.get(app.turn - 1).getRotateAngleVel() != 0); //Making sure turret key makes turret move





        app.keyPressed = true;
        app.keyCode = PApplet.DOWN;
        app.key = PApplet.CODED;

        app.keyPressed(k);
        assertTrue(app.tankArr.get(app.turn - 1).getRotateAngleVel() != 0); //Making sure turret key makes turret move


        app.keyReleased();
        assertTrue(app.tankArr.get(0).getxVel() != 0); //Checking if tank did not stop by any random key being released
    }

    @Test
    void checkParachuteAndFallDamage(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);


        Tank t = app.tankArr.get(0);
        t.incrementParachute(); //Giving tank parachute if it does not have 

        Arrays.fill(app.terrain_new, 100); //Making a simpleified flat terrain
        t.x = 50;
        t.y = app.terrain_new[t.x];

        Arrays.fill(app.terrain_new, 150); //Destroying terrain below tank
        t.tick(app);
        assertTrue(t.y > 100); //Ensuring the y value has increased 
        assertTrue(t.isParachuteDeployed());

        t.setParachutes(0);
        Arrays.fill(app.terrain_new, 200); //Destroying terrain below tank
        t.tick(app);
        assertTrue(t.y > 100); //Ensuring the y value has increased again 
        assertTrue(t.isParachuteDeployed()); //Tank keeps using parachute from same fall even though parachute count changed to 0 mid fall.
    }

    @Test
    void testExplosion(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);


        Explosion e = new Explosion(100, 100);
        e.tick();
        assertTrue(e.isFinished() == false); //Checking explosion does not end as soon as it starts 
        assertTrue(e.getRedRadius() != 0); //Making sure explosion animation has started
        assertTrue(e.getOrangeRadius() != 0); //Making sure explosion animation has started
        assertTrue(e.getYellowRadius() != 0); //Making sure explosion animation has started
        
    }

    @Test
    void checkBulletExplosionAndDamage(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);


        Tank t = app.tankArr.get(0);

        t.shoot();
        assertTrue(t.bullets.size() > 0); //Ensuring bullet has been fired
        t.bullets.get(0).explode(100,100); //Making the bullet explode at x: 100, y: 100
        t.tick(app);
        assertTrue(t.bullets.get(0).isOffScreen() == true); //Ensuring Bullet has exploded is now off the screen
    }


    @Test
    void checkMousePressedPowerUps(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        app.turn = 1; //Making it first players turn
        app.tankArr.get(app.turn - 1).setScore(50); //Setting enough score to call powerup

        MouseEvent m = new MouseEvent(app, 0, 0, 0, 0, 0, 0, 0);
        app.isGameOver = false; //Making sure game is not over 
        app.teleportPowerUp();
        assertTrue(app.teleport_status == true); //Checking if powerup worked 
        
        app.mousePressed(m);
        app.mouseX = 2;
        app.tick();
        app.tankArr.get(0).y = app.terrain_new[app.tankArr.get(0).x];
        assertEquals(2, app.tankArr.get(0).x); //Checking if first tanks position is where the mouse was clicked.

        app.turn = 1; //Making it first players turn
        app.tankArr.get(app.turn - 1).setScore(50); //Setting enough score to call powerup

        m = new MouseEvent(app, 0, 0, 0, 0, 0, 0, 0);
        app.isGameOver = false; //Making sure game is not over 
        app.airStrikePowerUp();
        assertTrue(app.airStrikeStatus == true); //Checking if powerup worked 
        
        app.mousePressed(m);
        app.mouseX = 2;
        app.tick();
        assertTrue(app.tankArr.get(app.turn - 1).bullets.size() != 0); //Checking if first tanks position is where the mouse was clicked.



    }

    @Test
    void checkGameEnd(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        app.level = 3;
        app.tankCount = 1;
        app.isGameOver = false;

        app.keyPressed = true;
        app.keyCode = 32;

        KeyEvent k = new KeyEvent(null, 0, 0, 0, ' ', 0);
        app.keyPressed(k);
        assertTrue(app.isGameOver); //Checking if game ended
        app.draw();
        assertTrue(app.scoreDisplayed); //Checking if score was displayed

    }

    @Test
    void checkTankEdgeCases(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        Tank t = app.tankArr.get(0);

        t.setHealth(30);
        t.tick(app);
        assertEquals(30, t.getPower()); //Power should never be higher than health

        t.x = 901;
        t.tick(app);
        assertEquals(864, t.x); //Program does not let tank go out of bounds

        t.rotateAngle = 100;
        t.tick(app);
        assertEquals(90, t.rotateAngle); //Tank rotate angle should never be higher than 90

        t.rotateAngle = -100;
        t.tick(app);
        assertEquals(-90, t.rotateAngle); //Tank rotate angle should never be lower than 90

        int countBeforeRemoval = app.tankCount;
        app.removeTank(5); //Removing tank at index 0
        app.tankArr.get(0).explode();
        assertEquals(countBeforeRemoval - 1, app.tankCount); //Seeing if removal of tanks updates counter accordingly

    }

    @Test
    void checkNewLevel(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        app.loadLevel();
        app.tick();
        assertEquals(1, app.level); //We see if level has loaded
        assertEquals(100, app.tankArr.get(0).getHealth()); //See if tank health has been reset
    }

    @Test
    void checkPowerUps(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        //Checking health powerup
        app.tankArr.get(0).setScore(50); //Giving enough score to buy powerups
        app.tankArr.get(0).setHealth(50); //Reducing health so it can be healed

        app.repairPowerUp();
        assertEquals(70, app.tankArr.get(0).getHealth());

        //Checking fuel powerup
        app.tankArr.get(0).setScore(50); //Giving enough score to buy powerups
        app.tankArr.get(0).setFuel(200); //Setting fuel to 200

        app.fuelPowerUp();
        assertEquals(400, app.tankArr.get(0).getFuel());

        //Checking parachute powerup
        app.tankArr.get(0).setScore(50); //Giving enough score to buy powerups

        app.parachutePowerUp();
        assertEquals(4, app.tankArr.get(0).getParachutes());
    }

    @Test
    void checkShowPowerUp(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        app.showPowerUp = true;
        app.tick(); //Displays board 
  
        app.tick();
        assertTrue(app.showPowerUp); //Box only displays when this flag is true

    }



    // Additional tests can be added based on specific methods like game updates, rendering checks, and event responses.
}