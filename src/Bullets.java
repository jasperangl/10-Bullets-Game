import java.awt.Color;
import javalib.funworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;

// BULLET
class Bullet extends AGamePiece {

  Color color;
  int timesHit;
  double xvelocity;
  double yvelocity;

  // Cosntructor for all exploded Bullets
  public Bullet(int xPos, int yPos, int radius, Color color, OutlineMode fill, int timesHit,
      double xvelocity, double yvelocity) {
    super(xPos, yPos, radius, fill);
    this.color = BULLET_COLOR;
    this.radius = radius;
    this.timesHit = timesHit;
    this.xvelocity = xvelocity;
    this.yvelocity = yvelocity;
  }

  public Bullet(int xPos, int yPos, int radius) {
    this(xPos, yPos, radius, Color.pink, OutlineMode.SOLID, 1, 0, 8);
  }

  /*
   * TEMPLATE Bullet:
   * 
   * Fields: this.xPos -- int this.velocity -- int this.radius -- int this.color
   * -- Color this.fill -- OutlineMode
   * 
   * Methods:
   * 
   * this.move() -- Bullet this.draw(WorldScene) -- WorldScene
   * 
   */
  // moves a bullet in its determined x and y direction
  public Bullet move() {
    return new Bullet(this.xPos + (int) this.xvelocity, this.yPos - (int) this.yvelocity,
        this.radius, this.color, this.fill, this.timesHit, this.xvelocity, this.yvelocity);
  }

  // draws a bullet
  public WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new CircleImage(this.radius, this.fill, this.color), this.xPos,
        this.yPos);
  }

  // determines the distance between a ship and a bullet
  public double distanceToShip(Ship ship) {
    return Math.hypot(this.xPos - ship.xPos, this.yPos - ship.yPos);
  }

  // determines whether a bullet and a ship collide
  public boolean isHitShip(Ship ship) {
    return this.distanceToShip(ship) < (this.radius + ship.radius);
  }

  // creates the exploded bullets with a certain angle
  public ILoBullets makeExplodedBullet() {
    return new Utils().createsExplodedBullets(this.xPos, this.yPos, this.timesHit + 1,
        this.timesHit + 1, 360.0 / (double) (this.timesHit + 1.0));
  }

}

interface ILoBullets {

  // moves all bullets
  ILoBullets moveBullets();

  // removes all Bullets that are offscreen
  ILoBullets removeOffscreen();

  // draws the Bullets fom the list onto the accumulted image of the scene so
  // far
  WorldScene draw(WorldScene acc);

  // determines whether a Bullet collides with a ship
  boolean isHitILoBullet(Ship ship);

  // creates a ListOfBullets containing the exploded bullets
  ILoBullets createExplodedBullets(ILoShips ships);

  // appends a list to the front of another list
  ILoBullets append(ILoBullets bullets);

  // determines whether the list of bullets is empty
  boolean isEnd();
}

class MtLoBullets implements ILoBullets {

  // moves all Bullets
  public ILoBullets moveBullets() {
    return new MtLoBullets();
  }

  // removes all Bullets that are offscreen
  public ILoBullets removeOffscreen() {
    return this;
  }

  // draws Bullets from this list onto the accumulated
  // image of the scene so far
  public WorldScene draw(WorldScene acc) {
    return acc;
  }

  // determines if a Bullet and a ship collide
  public boolean isHitILoBullet(Ship ship) {
    return false;
  }

  // determines if a bullet and a ship collide
  public boolean isHitAcc(Bullet gp) {
    return false;
  }

  // if a ship gets hit the method appends a list of exploded bullets to the list
  // of bullets
  public ILoBullets createExplodedBullets(ILoShips ships) {
    return this;
  }

  // appends a list to end of another list
  public ILoBullets append(ILoBullets bullets) {
    return bullets;
  }

  // determines whether the list of bullets is empty
  public boolean isEnd() {
    return true;
  }
}

class ConsLoBullet implements ILoBullets {
  Bullet first;
  ILoBullets rest;

  public ConsLoBullet(Bullet first, ILoBullets rest) {
    this.first = first;
    this.rest = rest;
  }

  // draws Bullets from this list onto the accumulated
  // image of the scene so far
  public WorldScene draw(WorldScene acc) {
    return this.rest.draw(this.first.draw(acc));
  }

  // determines whether a Ship collides with a Bullet in the list of bullets
  public boolean isHitILoBullet(Ship ship) {
    if (this.first.isHitShip(ship)) {
      return true;
    }
    return this.rest.isHitILoBullet(ship);
  }

  // moves all Bullets
  public ILoBullets moveBullets() {
    return new ConsLoBullet(this.first.move(), this.rest.moveBullets());
  }

  // removes all Bullets that are offscreen
  public ILoBullets removeOffscreen() {
    if (this.first.isOffScreen()) {
      return this.rest.removeOffscreen();
    }
    else {
      return new ConsLoBullet(this.first, this.rest.removeOffscreen());
    }
  }

  // appends a list of bullets to the front of the existing list of bullets
  public ILoBullets append(ILoBullets bullets) {
    return new ConsLoBullet(this.first, this.rest.append(bullets));
  }

  // if a ship gets hit the method appends a list of exploded bullets to the list
  // of bullets
  public ILoBullets createExplodedBullets(ILoShips ships) {
    if (ships.isHit(this.first)) {
      return this.rest.createExplodedBullets(ships).append(this.first.makeExplodedBullet());
    }
    return new ConsLoBullet(this.first, this.rest.createExplodedBullets(ships));
  }

  // determines whether the list of bullets is empty
  public boolean isEnd() {
    return false;
  }
}
