import java.awt.Color;
import java.util.Random;
import javalib.funworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;

//SHIP
class Ship extends AGamePiece {

  int velocity; // in pixels/tick
  Color color;
  Random rand;

  public Ship(int xPos, int yPos, int velocity, int radius, Color color, OutlineMode fill) {
    super(xPos, yPos, radius, fill);
    this.velocity = velocity;
    this.radius = SHIP_RADIUS;
    this.color = SHIP_COLOR;
  }

  // moves ta ship along the y axis
  public Ship move() {
    return new Ship(this.xPos + this.velocity, this.yPos, this.velocity, this.radius, this.color,
        this.fill);
  }

  // draws a ship
  public WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new CircleImage(this.radius, this.fill, this.color), this.xPos,
        this.yPos);
  }
}

interface ILoShips {

  // removes a ship if a bullet and a ship collide
  ILoShips removeShips(ILoBullets bullets);

  // moves all Ships
  ILoShips moveShips();

  // removes all GamePieces that are offscreen
  ILoShips removeOffscreen();

  // draws the Ships fom the list onto the accumulted image of the scene so
  // far
  WorldScene draw(WorldScene acc);

  // determines whether a bullet hits a ship out of a list of ships
  boolean isHit(Bullet bullet);

  // returns a count of how many ships got hit
  public int countShips(ILoBullets bullets);

}

class MtLoShips implements ILoShips {

  // removes Ships if a bullet and a ship collide
  public ILoShips removeShips(ILoBullets bullets) {
    return this;
  }

  // moves all Ships
  public ILoShips moveShips() {
    return new MtLoShips();
  }

  // removes all Ships that are offscreen
  public ILoShips removeOffscreen() {
    return new MtLoShips();
  }

  // draws Ships from this list onto the accumulated
  // image of the scene so far
  public WorldScene draw(WorldScene acc) {
    return acc;
  }

  // determines if a Ship and a ship collide
  public boolean isHit(Bullet bullet) {
    return false;
  }

  // returns a count of how many ships got hit
  public int countShips(ILoBullets bullets) {
    return 0;
  }

}

class ConsLoShips implements ILoShips {
  Ship first;
  ILoShips rest;

  public ConsLoShips(Ship first, ILoShips rest) {
    this.first = first;
    this.rest = rest;
  }

  // draws Ships from this list onto the accumulated
  // image of the scene so far
  public WorldScene draw(WorldScene acc) {
    return this.rest.draw(this.first.draw(acc));
  }

  // determines whether a ship got hit by a bullet
  public boolean isHit(Bullet bullet) {
    if (bullet.isHitShip(this.first)) {
      return true;
    }
    return this.rest.isHit(bullet);
  }

  // removes Ships if a bullet and a ship collide
  public ILoShips removeShips(ILoBullets bullets) {
    if (bullets.isHitILoBullet(this.first)) {
      return this.rest.removeShips(bullets);
    }
    return new ConsLoShips(this.first, this.rest.removeShips(bullets));
  }

  // moves all Ships
  public ILoShips moveShips() {
    return new ConsLoShips(this.first.move(), this.rest.moveShips());
  }

  // removes all GamePieces that are offscreen
  public ILoShips removeOffscreen() {
    if (this.first.isOffScreen()) {
      return this.rest.removeOffscreen();
    }
    else {
      return new ConsLoShips(this.first, this.rest.removeOffscreen());
    }
  }

  // counts a hit ships if a bullet and ship collide
  public int countShips(ILoBullets bullets) {
    if (bullets.isHitILoBullet(this.first)) {
      return 1 + this.rest.countShips(bullets);
    }
    else {
      return this.rest.countShips(bullets);
    }
  }
}
