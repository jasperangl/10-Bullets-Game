import tester.*;                
import javalib.worldimages.*;   
import javalib.funworld.*;      
import java.awt.Color;
import java.util.Random;     

class Dot {
  int radius;
  Color c;
  int x;
  int y;
  int velocity;

  Dot(int radius, Color c, int x, int y, int velocity) {
    this.radius = radius;
    this.c = c;
    this.x = x;
    this.y = y;
    this.velocity = velocity;
  }

  //draw this dot
  WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new CircleImage(this.radius, "solid", this.c), this.x, this.y);
  }
  //create a new dot that is shifted on the x-axis and the y-axis
  Dot move() {
      return new Dot(this.radius, this.c,this.x + this.velocity, this.y , this.velocity);
    }
}

class Utils1 {
  Dot intiallizedDot(int n) {
    if (n == 0) {
      return new Dot(25, Color.magenta, 0, new Random().nextInt(400), 5);
    }
    return new Dot(25, Color.magenta, 600, new Random().nextInt(400), -5);
  }
}

interface ILoDot {
  //draw the dots in this ILoDot onto the scene
  WorldScene draw(WorldScene acc);
  //move the dots in this ILoDot
  ILoDot move();
}

class MtLoDot implements ILoDot {

  //draws dots from this list onto the accumulated
  //image of the scene so far
  public WorldScene draw(WorldScene acc) {
    return acc;
  }

  //move the dots in this empty list
  public ILoDot move() {
    return this;
  }
}

class ConsLoDot implements ILoDot {
  Dot first;
  ILoDot rest;
  ConsLoDot(Dot first, ILoDot rest) {
    this.first = first;
    this.rest = rest;
  }
  //draws dots from this list onto the accumulated
  //image of the scene so far
  public WorldScene draw(WorldScene acc) {
    return this.rest.draw(this.first.draw(acc));
  }

  //move the dots in this non-empty list
  public ILoDot move() {
    return new ConsLoDot(this.first.move(), this.rest.move());
  }

}

class Dots extends World {
  ILoDot dots;
  Dots(ILoDot dots) {
    this.dots = dots;
  }
  //draws the dots onto the background
  public WorldScene makeScene() {
    return this.dots.draw(new WorldScene(600, 400));
  }

  //move the dots on the scene
  public World onTick() {
    return new Dots(new ConsLoDot(new Utils1().intiallizedDot(new Random().nextInt(2)), this.dots).move());
  }
}

class Examples {

  
  boolean testBigBang(Tester t) {
    Dots world = new Dots(new MtLoDot());
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = .0;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }

}