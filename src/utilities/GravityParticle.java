package utilities;

import static controllers.DirectionController.windowHeight;
import static controllers.DirectionController.windowWidth;
import static java.lang.Math.PI;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A Circle that is forced to behave the rules we set for them
 */
public class GravityParticle extends Circle {

  private final Controller controller;
  private EdgeHandling edgeHandling;
  private boolean doCollisions;
  private Vector velocityVector;
  private boolean hasCollided = false;

  /**
   * Make a new particle. He will have some fun
   *
   * @param startX X value that the particle will start at
   * @param startY Y value that the particle will start at
   * @param radius size of the particle
   * @param edgeHandling how the particle will behave when hitting an edge
   * @param doCollisions whether or not to have it collide with other particles
   */
  public GravityParticle(double startX, double startY, double radius, EdgeHandling edgeHandling,
      boolean doCollisions, Controller controller, Vector initialVelocity) {
    super(startX, startY, radius);
    this.edgeHandling = edgeHandling;
    this.doCollisions = doCollisions;
    this.controller = controller;
    velocityVector = initialVelocity;

//    Timeline positionUpdater = new Timeline(new KeyFrame(Duration.millis(100), event -> {
//      updateVelocity();
//      handleCollision();
//      updatePosition();
//    }));
//    positionUpdater.setCycleCount(Timeline.INDEFINITE);
//    positionUpdater.play();
  }

  public GravityParticle(double startX, double startY, double radius, EdgeHandling edgeHandling,
      boolean doCollisions, Controller controller) {
    this(startX, startY, radius, edgeHandling, doCollisions, controller, new Vector(0, 0));
  }

  /**
   * Updates the position of the particle, handling the edges as determined by the EdgeHandling
   */
  public void updatePosition() {
    switch (this.edgeHandling) {
      case BOUND:
        this.setCenterX(boundX(this.getCenterX() + velocityVector.getxComponent()));
        this.setCenterY(boundY(this.getCenterY() - velocityVector.getyComponent()));
        break;
      case WRAP:
        this.setCenterX(wrapX(this.getCenterX() + velocityVector.getxComponent()));
        this.setCenterY(wrapY(this.getCenterY() - velocityVector.getyComponent()));
        break;
      case BOUNCE:
        this.setCenterX(this.getCenterX() + velocityVector.getxComponent());
        this.setCenterY(this.getCenterY() - velocityVector.getyComponent());
        if (this.getCenterX() + getRadius() > windowWidth || this.getCenterX() - getRadius() < 0) {
          this.velocityVector = this.velocityVector.scale(1 - controller.getFriction());
          this.velocityVector.negateXComponent();
          this.setCenterX(boundX(this.getCenterX() + velocityVector.getxComponent()));
          this.setCenterY(boundY(this.getCenterY() - velocityVector.getyComponent()));
        }
        if (this.getCenterY() + getRadius() > windowHeight || this.getCenterY() - getRadius() < 0) {
          this.velocityVector = this.velocityVector.scale(1 - controller.getFriction());
          this.velocityVector.negateYComponent();
          this.setCenterX(boundX(this.getCenterX() + velocityVector.getxComponent()));
          this.setCenterY(boundY(this.getCenterY() - velocityVector.getyComponent()));
        }
        break;
      case SUSTAIN:
        this.setCenterX(this.getCenterX() + velocityVector.getxComponent());
        this.setCenterY(this.getCenterY() - velocityVector.getyComponent());
        break;
    }
  }

  /**
   * <a href=https://williamecraver.wixsite.com/elastic-equations>Source for equations used</a>
   */
  public void handleCollision() {
    if (doCollisions) {
      for (GravityParticle particle : controller.getParticles()) {
        if (!particle.equals(this) && this.distance(particle) <= this.getRadius() * 2) {
          Vector dCenter = position().findResultant(particle.position().scale(-1));
          double contactAngle = dCenter.getAngle();
          double overlap = getRadius() - dCenter.getMagnitude() / 2;

          shiftByVector(dCenter.normalize().scale(overlap));
          particle.shiftByVector(dCenter.normalize().scale(-overlap));

          double v1 = velocityVector.getMagnitude();
          double v2 = particle.velocityVector.getMagnitude();
          double theta1 = velocityVector.getAngle();
          double theta2 = particle.velocityVector.getAngle();

          double v1fx = v2 * Math.cos(theta2 - contactAngle) * Math.cos(contactAngle) +
              v1 * Math.sin(theta1 - contactAngle) * Math.cos(contactAngle + PI / 2);
          double v1fy = v2 * Math.cos(theta2 - contactAngle) * Math.sin(contactAngle) +
              v1 * Math.sin(theta1 - contactAngle) * Math.sin(contactAngle + PI / 2);
          double v2fx = v1 * Math.cos(theta1 - contactAngle) * Math.cos(contactAngle) +
              v2 * Math.sin(theta2 - contactAngle) * Math.cos(contactAngle + PI / 2);
          double v2fy = v1 * Math.cos(theta1 - contactAngle) * Math.sin(contactAngle) +
              v2 * Math.sin(theta2 - contactAngle) * Math.sin(contactAngle + PI / 2);

          velocityVector = Vector.vectorFromXandY(v1fx, v1fy);
          particle.velocityVector = Vector.vectorFromXandY(v2fx, v2fy);

          updatePosition();
          particle.updatePosition();
        }
      }
    }
    //Set the fill of the boy to represent the speed
    this.setFill(speedColor(velocityVector.getMagnitude()));
  }

  /**
   * Modify the current velocity vector by the gravity vector
   */
  public void updateVelocity() {
    velocityVector = velocityVector.findResultant(controller.getGravityVector(this),
        controller.getMaxVelocity());
  }

  /**
   * Keep the x value in the window
   *
   * @return the bounded value
   */
  private double boundX(double x) {
    return Math.max(getRadius(), Math.min(x, windowWidth - getRadius()));
  }

  /**
   * The same as boundX, but for y
   *
   * @return the bounded value
   */
  private double boundY(double y) {
    return Math.max(getRadius(), Math.min(y, windowHeight - getRadius()));
  }

  /**
   * Wraps a ball like Pac-Man going over the sides of the screens
   *
   * @return the wrapped x value
   */
  private double wrapX(double x) {
    if (x > windowWidth + 4) {
      return -4;
    } else if (x < -4) {
      return windowWidth + 4;
    } else {
      return x;
    }
  }

  /**
   * Like wrapX, but for up and down
   *
   * @return the wrapped y value
   */
  private double wrapY(double y) {
    if (y > windowHeight + 4) {
      return -4;
    } else if (y < -4) {
      return windowHeight + 4;
    } else {
      return y;
    }
  }

  /**
   * Find a color hue based off of the speed
   *
   * @return the color that represents the speed
   */
  private Color speedColor(double speed) {
    //Find the hue out of 300 degrees to prevent repeats, and offset it
    double h = (speed / controller.getMaxVelocity()) * -300 - 60;

    return Color.hsb(h, 1, 1);
  }

  /**
   * Find the distance between two particles' centers
   *
   * @param particle the target particle
   */
  private double distance(GravityParticle particle) {
    return Math.sqrt(Math.pow(this.getCenterX() - particle.getCenterX(), 2) + Math
        .pow(this.getCenterY() - particle.getCenterY(), 2));
  }

  /**
   * Sets the EdgeHandling type
   */
  public void setEdgeHandling(EdgeHandling handling) {
    this.edgeHandling = handling;
  }

  /**
   * Sets whether or not to perform particle collisions
   */
  public void setDoCollisions(boolean doCollisions) {
    this.doCollisions = doCollisions;
  }

  /**
   * @return the position of this particle as a Vector
   */
  public Vector position() {
    return Vector.vectorFromXandY(getCenterX(), getCenterY());
  }

  /**
   * Shifts the position of this particle by a Vector
   *
   * @param shift the vector to shift the position by
   */
  private void shiftByVector(Vector shift) {
    setCenterX(getCenterX() + shift.getxComponent());
    setCenterY(getCenterY() + shift.getyComponent());
  }

  public String toString() {
    return String.format("x = %.0f y = %.0f", this.getCenterX(), this.getCenterY());
  }

  public void setVelocityVector(Vector velocityVector) {
    this.velocityVector = velocityVector;
  }

  /**
   * Tells particles how to handle the edge of the screen
   */
  public enum EdgeHandling {
    WRAP,
    BOUND,
    BOUNCE,
    SUSTAIN
  }
}
