import javalib.worldimages.*; // images, like RectangleImage or OverlayImages
import javalib.funworld.*; // the abstract World class and the big-bang library
import java.awt.Color;
import java.util.Random;

interface IGamePiece {

  // World constants
  int SCREEN_HEIGHT = 600; // in pixels
  int SCREEN_WIDTH = 1000; // in pixels
  double TICK_RATE = 1.0 / 28.0;
  // Bullet constants
  Color BULLET_COLOR = Color.MAGENTA;
  // Ship constants
  int SHIP_RADIUS = 15;
  Color SHIP_COLOR = Color.YELLOW;
  int SHIP_SPEED = 6;

  // METHODS:
  // determines whether a GamePiece is offscreen
  boolean isOffScreen();

  // moves a game piece
  IGamePiece move();

  // draws a game piece
  WorldScene draw(WorldScene acc);
}

abstract class AGamePiece implements IGamePiece {
  int xPos;
  int yPos;
  int radius;
  OutlineMode fill;

  public AGamePiece(int xPos, int yPos, int radius, OutlineMode fill) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.radius = radius;
    this.fill = OutlineMode.SOLID;
  }

  // determines whether a game piece is off screen
  public boolean isOffScreen() {
    return this.xPos > SCREEN_WIDTH || this.xPos < 0 || this.yPos > SCREEN_HEIGHT || this.yPos < 0;
  }

}

class Utils {
  private static final int MAXIMUM_BULLET_RADIUS = 15;

  // creates between 1 to 3 ships randomly along the y-axis
  ILoShips makeXShips(int n, ILoShips ships) {
    if (n == 0) {
      return ships;
    }
    else {
      return new ConsLoShips(this.intiallizeShip(new Random().nextInt(2)),
          this.makeXShips(n - 1, ships));
    }

  }

  // spawns new ships randomly along the Y-axis on the left and right side on the
  // screen and moves them through the screen
  Ship intiallizeShip(int n) {
    if (n == 0) {
      return new Ship(0, 15 + new Random().nextInt(275), 4, 10, Color.CYAN, OutlineMode.SOLID);
    }
    return new Ship(500, 15 + new Random().nextInt(275), -4, 10, Color.CYAN, OutlineMode.SOLID);
  }

  // creates a list of bullets containing only new exploded bullets with an
  // increasing radius and number of bullets per hit and a changing angle
  ILoBullets createsExplodedBullets(int x, int y, int timesHit, int count, double angle) {
    if (count == 0) {
      return new MtLoBullets();
    }
    else {
      double newangle = angle * count;
      double newradian = Math.toRadians(newangle);

      return new ConsLoBullet(
          new Bullet(x, y, this.radiusRegulator(timesHit), Color.PINK, OutlineMode.SOLID, timesHit,
              Math.cos(newradian) * 8, Math.sin(newradian) * 8),
          this.createsExplodedBullets(x, y, timesHit, count - 1, angle));

    }
  }

  // to ensure the size of the bullet never exceeds the maximum bullet radius
  int radiusRegulator(int timesHit) {
    if (3 * timesHit > MAXIMUM_BULLET_RADIUS) {
      return MAXIMUM_BULLET_RADIUS;
    }
    else {
      return 3 * timesHit;
    }
  }
}