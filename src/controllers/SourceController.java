package controllers;

import static java.lang.Math.PI;
import static utilities.Run.sceneController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import utilities.Controller;
import utilities.GravityParticle;
import utilities.GravityParticle.EdgeHandling;
import utilities.Vector;

public class SourceController extends Controller {

  public Circle gravitySource;
  public ComboBox touchModeChoices;
  public Text sourceMassText;
  public Slider sourceMassSlider;
  public ToggleButton collisionButton;
  public Button clearButton;
  public Button switchButton;
  private double sourceMass;
  private double particleRadius = 15;       //Magic number that looked good
  private TouchMode touchMode;
  private boolean doCollisions;
  private double gravityConstant = 6.674;   //Based off of real G
  private double maxVelocity = 7.25;        //Magic number that looked good

  public SourceController() {
  }

  @Override
  public double getMaxVelocity() {
    return maxVelocity;
  }

  @Override
  public double getFriction() {
    return 0;
  }

  /**
   * g = (GM / r^2)<b>r</b>.
   *
   * @param target the target particle
   * @return the acceleration from gravity on target
   */
  @Override
  public Vector getGravityVector(GravityParticle target) {
    Vector sourceCenter = Vector.vectorFromXandY(gravitySource.getCenterX(),
        gravitySource.getCenterY());
    double dX = sourceCenter.getxComponent() - target.position().getxComponent();
    double dY = sourceCenter.getyComponent() - target.position().getyComponent();
    Vector direction = Vector.vectorFromXandY(dX, -dY);
    double scalar = gravityConstant * sourceMass / Math.pow(direction.getMagnitude(), 2);
    return direction.normalize().scale(scalar);
  }

  @Override
  protected void initialize() {
    touchModeChoices.getItems().clear();
    touchModeChoices.getItems().setAll("Add Particles", "Move Source");
    touchModeChoices.setValue("Add Particles");
    reset();
  }

  @Override
  protected void createParticle(MouseEvent event) {
    double x = event.getSceneX();
    double y = event.getSceneY();
    GravityParticle particle = new GravityParticle(x, y, particleRadius,
        EdgeHandling.SUSTAIN, doCollisions
        , this);

    //Set the initial velocity to create a clockwise circular orbit
    particle.setVelocityVector(
        getGravityVector(particle).rotate(PI / 2).normalize().scale(orbitalVelocity(particle)));
    addGravityParticle(particle);
    particles.add(particle);
  }

  /**
   * v = sqrt(GM / r).
   *
   * @param particle the particle to find the orbital velocity of
   * @return the orbital velocity
   */
  private double orbitalVelocity(GravityParticle particle) {
    double distance =
        Math.sqrt(Math.pow(particle.getCenterX() - gravitySource.getCenterX(), 2) +
            Math.pow(particle.getCenterY() - gravitySource.getCenterY(), 2));
    return Math.sqrt(gravityConstant * sourceMass / distance);
  }

  @FXML
  private void updateSourceMass() {
    sourceMass = sourceMassSlider.getValue();
    sourceMassText.setText(String.format("%.0f", sourceMass));
  }

  /**
   * Update what to do with on the cursor.
   */
  @FXML
  private void setTouchMode() {
    switch ((String) touchModeChoices.getValue()) {
      case "Add Particles":
        touchMode = TouchMode.ADD_PARTICLES;
        break;
      case "Move Source":
        touchMode = TouchMode.MOVE_SOURCE;
        break;
    }
  }

  @FXML
  private void toggleCollisions() {
    doCollisions = collisionButton.isSelected();
    for (GravityParticle particle : particles) {
      particle.setDoCollisions(doCollisions);
    }
  }

  @Override
  public void reset() {
    clearParticles();

    //Reset controls
    sourceMassSlider
        .setMax(Math.pow(maxVelocity, 2) * gravitySource.getRadius() / gravityConstant);
    sourceMassSlider.setValue(sourceMassSlider.getMax() / 2);
    gravitySource.setCenterX(960);
    gravitySource.setCenterY(525);

    //Run control methods for the first time
    updateSourceMass();
    setTouchMode();
    toggleCollisions();

  }

  /**
   * Depending on touchMode, perform an action at the cursor.
   *
   * @param event contains the cursor location, passed by the Application
   */
  @FXML
  private void handleTouch(MouseEvent event) {
    switch (touchMode) {
      case MOVE_SOURCE:
        gravitySource.setCenterX(event.getX());
        gravitySource.setCenterY(event.getY());
        break;
      case ADD_PARTICLES:
        createParticle(event);
        break;
    }
  }

  @FXML
  private void activateDirectionControl() {
    reset();
    sceneController.activate("Direction Control");
  }


  private enum TouchMode {
    ADD_PARTICLES,
    MOVE_SOURCE
  }
}
