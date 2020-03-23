import tester.*; // The tester library
import javalib.worldimages.*; // images, like RectangleImage or OverlayImages
import javalib.funworld.*; // the abstract World class and the big-bang library
import java.awt.Color;
import java.util.Random;
import java.applet.*;

// represents the World  
class MyWorld extends World {
  int numBullets; // the number of bullets avaiable to shoot
  ILoBullets bullets; // the list of bullets represented in the world
  ILoShips ships;// the list of ships represented in the world
  int count;
  int shipshit;

  public MyWorld(int numBullets, ILoBullets bullets, ILoShips ships, int count, int shipshit) {
    this.numBullets = numBullets;
    this.bullets = bullets;
    this.ships = ships;
    this.count = count;
    this.shipshit = shipshit;

  }
  
  // creates background
  public WorldImage background() {
    return new OverlayImage(new LineImage(new Posn(-10, 50), Color.red), new RectangleImage(500, 300, "solid", Color.lightGray));
  }

  // creates the WorldScene
  public WorldScene makeScene() {
    return this.bullets.draw(this.ships.draw(new WorldScene(500, 300)
        .placeImageXY(this.background(), 250, 150)
        .placeImageXY(this.scoreBoard(), 50, 260)));
  }

  // creates the final scene of the world
  public WorldScene makeFinalScene() {
    return new WorldScene(500, 300).placeImageXY(this.scoreBoard(), 50, 260)
        .placeImageXY(gameOverText(), 250, 150);
  }

  // creates the score board image with bullets and ships hit
  public WorldImage scoreBoard() {
    return new AboveImage(this.bulletsLeftText(), this.shipsHitText());
  }

  // return the game over text for the end scene
  public WorldImage gameOverText() {
    return new AboveImage(new TextImage("Game Over", 40, Color.BLACK), this.shipsHitText());
  }

  // return the Image of the amount of bullets left to shoot
  public WorldImage bulletsLeftText() {
    return new TextImage("Bullets left: " + Integer.toString(this.numBullets), 15, Color.BLACK);
  }

  // return the Image of the amount of ships that have been hit
  public WorldImage shipsHitText() {
    return new TextImage("Ships hit: " + Integer.toString(this.shipshit), 15, Color.BLACK);
  }

  // counts the number of hit ships in the world
  public int countShipsW() {
    return this.shipshit + this.ships.countShips(this.bullets);
  }

  // moves the world on tick
  public World onTick() {
    if (this.count == 28) {
      return new MyWorld(this.numBullets, this.handleBullets(),
          new Utils().makeXShips(new Random().nextInt(3) + 1, this.ships).removeOffscreen()
              .removeShips(this.bullets).moveShips(),
          0, this.countShipsW());
    }
    else {
      return new MyWorld(this.numBullets, this.handleBullets(),
          this.ships.removeOffscreen().removeShips(this.bullets).moveShips(), this.count + 1,
          this.countShipsW());
    }
  }

  // handles all the on tick bullets movements
  public ILoBullets handleBullets() {
    return this.bullets.removeOffscreen().createExplodedBullets(this.ships).moveBullets();
  }

  // handles the world on key press
  // when "space" is pressed a bullet gets shot
  public World onKeyEvent(String key) {
    if (key.equals(" ") && this.numBullets > 0) {
      return new MyWorld(this.numBullets - 1,
          new ConsLoBullet(new Bullet(250, 300, 3), this.bullets), this.ships, this.count,
          this.shipshit);
    }
    else {
      return new MyWorld(this.numBullets, this.bullets, this.ships, this.count, this.shipshit);
    }
  }

  // determines whether no bullets are left to shoot and all bullets are off the
  // screen
  // initializes whether the world should end or not
  public boolean isEnd() {
    return this.numBullets == 0 && this.bullets.isEnd();
  }

  // returns the worldend
  public WorldEnd worldEnds() {
    if (this.isEnd()) {
      return new WorldEnd(true, this.makeFinalScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

class ExamplesMyWorldProgram extends Applet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  boolean testBigBang(Tester t) {
    MyWorld w = new MyWorld(10, new MtLoBullets(), new MtLoShips(), 0, 0);
    int worldWidth = 500;
    int worldHeight = 300;
    double tickRate = 1 / 28.0;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
  // EXAMPLES:

  Ship ship1 = new Ship(250, 100, 4, 10, Color.CYAN, OutlineMode.SOLID); // should be hit
  Ship ship2 = new Ship(240, 100, 4, 10, Color.CYAN, OutlineMode.SOLID); // should be hit
  Ship ship3 = new Ship(140, 230, 4, 10, Color.CYAN, OutlineMode.SOLID); // should not be hit
  Ship ship4 = new Ship(300, 150, 4, 10, Color.CYAN, OutlineMode.SOLID); // not hit
  Ship ship5 = new Ship(0, 15, 4, 10, Color.CYAN, OutlineMode.SOLID); // to test Utils methods
  Ship ship6 = new Ship(500, 15, -4, 10, Color.CYAN, OutlineMode.SOLID); // to test Utils methods
  Ship ship7 = new Ship(0, 200, 4, 10, Color.CYAN, OutlineMode.SOLID); // to test Utils methods
  Ship ship8 = new Ship(500, 200, -4, 10, Color.CYAN, OutlineMode.SOLID); // to test Utils methods

  // OffScreen Ships

  Bullet bullet1 = new Bullet(250, 100, 10);
  Bullet bullet2 = new Bullet(130, 100, 10);
  Bullet bullet3 = new Bullet(420, 100, 10);
  Bullet bullet4 = new Bullet(140, 230, 10);
  // OffScreen Bullets
  Bullet bulletOffScreen = new Bullet(600, 600, 10);
  Bullet bulletOffScreen2 = new Bullet(610, 600, 10);

  ILoShips loships0 = new MtLoShips();
  ILoShips loships1 = new ConsLoShips(this.ship1, this.loships0);
  ILoShips loships2 = new ConsLoShips(this.ship2, this.loships1);
  ILoShips loships3 = new ConsLoShips(this.ship3, this.loships2);
  ILoShips loships4 = new ConsLoShips(this.ship4, this.loships3);

  ILoShips loships1u = new ConsLoShips(this.ship5, this.loships0); 
  ILoShips loships2u = new ConsLoShips(this.ship5, this.loships1u); 
  ILoShips loships2uc = new ConsLoShips(this.ship5, this.loships2); 
  ILoShips loships2uc2 = new ConsLoShips(this.ship5, 
      new ConsLoShips(this.ship5, this.loships2)); 
  

  ILoBullets lobullet0 = new MtLoBullets();
  ILoBullets lobullet1 = new ConsLoBullet(this.bullet1, this.lobullet0);
  ILoBullets lobullet2 = new ConsLoBullet(this.bullet2, this.lobullet1);
  ILoBullets lobulletcb = new ConsLoBullet(this.bullet1, this.lobullet2);
  ILoBullets lobullet3 = new ConsLoBullet(this.bullet4, this.lobulletcb);
  ILoBullets lobullet4 = new ConsLoBullet(this.bullet3, this.lobullet3);

  // ILoBulletsMoved
  ILoBullets lobulletmove1 = new ConsLoBullet(new Bullet(250, 92, 10), this.lobullet0);
  ILoBullets lobulletmove2 = new ConsLoBullet(new Bullet(250, 92, 10), this.lobullet0);
  ILoBullets lobulletmove3 = new ConsLoBullet(new Bullet(130, 92, 10), this.lobulletmove1);

  // ILoBullet List for testing OffScreen
  ILoBullets lobulletOffS0 = new ConsLoBullet(new Bullet(250, 100, 10), new MtLoBullets());
  ILoBullets lobulletOffS1 = new ConsLoBullet(this.bulletOffScreen, this.lobullet2);
  ILoBullets lobulletsOffS2 = new ConsLoBullet(this.bulletOffScreen2, this.lobulletOffS1);
  ILoBullets lobulletsOff3 = new ConsLoBullet(new Bullet(130, 100, 10), this.lobulletOffS0);

  // ILoBullet for testing createExplodedBullet
  Bullet bulletexp1 = new Bullet(250, 100, 8);

  ILoBullets lobulletexp1 = new ConsLoBullet(new Bullet(250, 100, 10), this.lobullet0);
  ILoBullets lobulletexp2 = new ConsLoBullet(this.bulletexp1, this.lobullet0);
  ILoBullets lobulletexp3 = new ConsLoBullet(this.bulletexp1, this.lobulletexp2);
  ILoBullets lobulletexp4 = new ConsLoBullet(this.bullet2, this.lobulletexp3);

  // WorldScene Examples for testing draw
  CircleImage bullet = new CircleImage(10, OutlineMode.SOLID, Color.magenta);
  WorldScene ws1 = new WorldScene(500, 300)
      .placeImageXY(new RectangleImage(500, 300, "solid", Color.lightGray), 250, 150)
      .placeImageXY(new RectangleImage(200, 200, "solid", Color.pink), 50, 260);
  WorldScene ws2 = ws1.placeImageXY(bullet, 250, 100);
  TextImage bulletTxt = new TextImage("Bullets left: " + Integer.toString(30), 15, Color.BLACK);
  TextImage shipTxt = new TextImage("Ships hit: " + Integer.toString(0), 15, Color.BLACK);
  AboveImage scoreB = new AboveImage(this.bulletTxt, this.shipTxt);
  TextImage bulletTxt2 = new TextImage("Bullets left: " + Integer.toString(20), 15, Color.BLACK);
  TextImage shipTxt2 = new TextImage("Ships hit: " + Integer.toString(0), 15, Color.BLACK);
  AboveImage scoreB2 = new AboveImage(this.bulletTxt2, this.shipTxt2);
  AboveImage gameOverTxt = new AboveImage(new TextImage("Game Over", 40, Color.BLACK),
      this.shipTxt2);
  WorldScene scene1 = this.lobullet2.draw(this.loships1.draw(new WorldScene(500, 300)
      .placeImageXY(new RectangleImage(500, 300, "solid", Color.lightGray), 250, 150)
      .placeImageXY(this.scoreB, 50, 260)));

  // World Examples
  MyWorld w1 = new MyWorld(30, this.lobullet2, this.loships1, 3, 0);
  MyWorld w2 = new MyWorld(20, this.lobullet3, this.loships2, 3, 0);
  MyWorld w3 = new MyWorld(0, this.lobullet0, this.loships0, 3, 0);

  // TESTS:

  //Tests method isHit from ConsLoShips be
  boolean testIsHitShip(Tester t) {
    return t.checkExpect(this.bullet1.isHitShip(this.ship1), true)
        && t.checkExpect(this.bullet1.isHitShip(this.ship2), true);
  }

  boolean testIsHit(Tester t) {
    return t.checkExpect(this.loships2.isHit(this.bullet1), true)
        && t.checkExpect(this.loships2.isHit(this.bullet2), false);
  }

  boolean testCountShips(Tester t) {
    return t.checkExpect(this.loships0.countShips(this.lobullet0), 0)
        && t.checkExpect(this.loships2.countShips(this.lobullet1), 2)
        && t.checkExpect(this.loships1.countShips(this.lobullet1), 1)
        && t.checkExpect(this.loships3.countShips(this.lobullet1), 2)
        && t.checkExpect(this.loships4.countShips(this.lobullet4), 3);
  }

  boolean testRemoveShips(Tester t) {
    return t.checkExpect(this.loships2.removeShips(this.lobullet2), this.loships0)
        && t.checkExpect(this.loships3.removeShips(this.lobullet2),
            new ConsLoShips(this.ship3, this.loships0))
        && t.checkExpect(this.loships0.removeShips(this.lobullet2), new MtLoShips());
  }

  // Tests for bullets

  boolean testMoveBullets(Tester t) {
    return t.checkExpect(this.lobullet1.moveBullets(), this.lobulletmove1)
        && t.checkExpect(this.lobullet0.moveBullets(), this.lobullet0)
        && t.checkExpect(this.lobullet2.moveBullets(), this.lobulletmove3);

  }

  boolean testRemoveOffScreen(Tester t) {
    return t.checkExpect(this.lobullet0.removeOffscreen(), this.lobullet0)
        && t.checkExpect(this.lobulletOffS1.removeOffscreen(), this.lobullet2)
        && t.checkExpect(this.lobulletsOffS2.removeOffscreen(), this.lobulletsOff3);
  }

  boolean testIsHitILoBullet(Tester t) {
    return t.checkExpect(this.lobullet0.isHitILoBullet(this.ship1), false)
        && t.checkExpect(this.lobullet1.isHitILoBullet(this.ship2), true)
        && t.checkExpect(this.lobullet2.isHitILoBullet(this.ship3), false);

  }

  boolean testCreateExplodedBullet(Tester t) {
    return t.checkExpect(this.lobullet1.createExplodedBullets(this.loships0), this.lobulletexp1)
        && t.checkExpect(this.lobullet0.createExplodedBullets(this.loships1), this.lobullet0);

  }

  boolean testAppend(Tester t) {
    return t.checkExpect(this.lobullet1.append(this.lobullet2), this.lobulletcb)
        && t.checkExpect(this.lobullet0.append(this.lobullet1), this.lobullet1);

  }

  boolean testIsEnd(Tester t) {
    return t.checkExpect(this.lobullet1.isEnd(), false)
        && t.checkExpect(this.lobullet0.isEnd(), true);

  }

  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.w1.makeScene(),
        this.lobullet2.draw(this.loships1.draw(new WorldScene(500, 300)
            .placeImageXY(new RectangleImage(500, 300, "solid", Color.lightGray), 250, 150)
            .placeImageXY(this.scoreB, 50, 260))))
        && t.checkExpect(this.w2.makeScene(),
            this.lobullet3.draw(this.loships2.draw(new WorldScene(500, 300)
                .placeImageXY(new RectangleImage(500, 300, "solid", Color.lightGray), 250, 150)
                .placeImageXY(this.scoreB2, 50, 260))));
  }

  boolean testMakeFinalScene(Tester t) {
    return t.checkExpect(this.w1.makeFinalScene(),
        new WorldScene(500, 300).placeImageXY(this.scoreB, 50, 260).placeImageXY(this.gameOverTxt,
            250, 150))
        && t.checkExpect(this.w2.makeFinalScene(), new WorldScene(500, 300)
            .placeImageXY(this.scoreB2, 50, 260).placeImageXY(this.gameOverTxt, 250, 150));
  }

  boolean testScoreBoard(Tester t) {
    return t.checkExpect(this.w1.scoreBoard(), this.scoreB)
        && t.checkExpect(this.w2.scoreBoard(), this.scoreB2);
  }

  boolean testGameOverText(Tester t) {
    return t.checkExpect(this.w1.gameOverText(), this.gameOverTxt)
        && t.checkExpect(this.w2.gameOverText(), this.gameOverTxt);
  }

  boolean testBulletsLeftText(Tester t) {
    return t.checkExpect(this.w1.bulletsLeftText(), this.bulletTxt)
        && t.checkExpect(this.w2.bulletsLeftText(), this.bulletTxt2);
  }

  boolean testShipsLeftText(Tester t) {
    return t.checkExpect(this.w1.shipsHitText(), this.shipTxt)
        && t.checkExpect(this.w2.shipsHitText(), this.shipTxt2);
  }

  boolean testCountShipsW(Tester t) {
    return t.checkExpect(this.w1.countShipsW(), 1) && t.checkExpect(this.w2.countShipsW(), 2);
  }

  boolean testIsEndW(Tester t) {
    return t.checkExpect(this.w1.isEnd(), false) && t.checkExpect(this.w3.isEnd(), true);
  }

  /*
   * 
   * 
   */

  // TESTS FOR MAIN METHODS
  //  void testMakeXShips(Tester t) {
  //    t.checkExpect(new Utils(new Random(1), new Random(0)).makeXShips(0, this.loships0),
  //        this.loships0);
  //    t.checkExpect(new Utils(new Random(0), new Random(230)).makeXShips(0, this.loships0),
  //        this.loships0);
  //    t.checkExpect(new Utils(new Random(0), new Random(0)).makeXShips(1, this.loships0),
  //        this.loships1u);
  //    t.checkExpect(new Utils(new Random(0), new Random(0)).makeXShips(2, this.loships0),
  //        this.loships2u);
  //    t.checkExpect(new Utils(new Random(0), new Random(0)).makeXShips(1, this.loships2),
  //        this.loships2uc);
  //    t.checkExpect(new Utils(new Random(0), new Random(0)).makeXShips(2, this.loships2),
  //        this.loships2uc2);
  //  }
  //
  //  void testinitiallizeShip(Tester t) {
  //    t.checkExpect(new Utils(new Random(0), new Random(0)).intiallizeShip(0), this.ship5);
  //    t.checkExpect(new Utils(new Random(0), new Random(185)).intiallizeShip(0), this.ship7);
  //    t.checkExpect(new Utils(new Random(0), new Random(0)).intiallizeShip(1), this.ship6);
  //    t.checkExpect(new Utils(new Random(0), new Random(185)).intiallizeShip(1), this.ship8);
  //  }
}
