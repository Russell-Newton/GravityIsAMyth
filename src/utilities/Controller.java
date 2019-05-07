package utilities;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


/**
 * Abstract class used by controllers to manage particles.
 */
public abstract class Controller {

  protected static ObservableList<GravityParticle> particles = FXCollections.observableArrayList();

  @FXML
  protected AnchorPane root;

  protected Controller() {
    //Create the task to update particles
    Timeline positionUpdater = new Timeline(new KeyFrame(Duration.millis(10),
        (event) -> updateParticles()));
    positionUpdater.setCycleCount(Timeline.INDEFINITE);
    positionUpdater.play();
  }

  /**
   * This is called by the Application when it creates the Controller. I use it to run reset methods
   * and methods that run only once.
   */
  @FXML
  protected abstract void initialize();

  public abstract double getMaxVelocity();

  public abstract double getFriction();

  /**
   * Used to reset the scene to the original format.
   */
  public abstract void reset();

  /**
   * Requires a parameter if gravity differs for each particle.
   *
   * @param target the target particle
   * @return the Vector representing the acceleration of gravity acting on target
   */
  public abstract Vector getGravityVector(GravityParticle target);

  ObservableList<GravityParticle> getParticles() {
    return particles;
  }

  /**
   * Used by to add a particle on the screen at the cursor location.
   *
   * @param event contains the cursor location, passed in by the Application
   */
  @FXML
  protected abstract void createParticle(MouseEvent event);

  private void updateParticles() {
    for (GravityParticle particle : particles) {
      particle.handleCollision();
      particle.updatePosition();
      particle.updateVelocity();
    }
  }


  /**
   * Adds particles for render on the window.
   */
  public void addGravityParticle(GravityParticle particle) {
    //Through DirectionController.fxml, this index will not change
    Group particleGroup = (Group) root.getChildren().get(0);

    particleGroup.getChildren().add(particle);
  }

  /**
   * Clears all currently rendered particles.
   */
  @FXML
  public void clearParticles() {
    //Through DirectionController.fxml, this index will not change
    particles.clear();
    Group particleGroup = (Group) root.getChildren().get(0);
    particleGroup.getChildren().clear();
  }
}
