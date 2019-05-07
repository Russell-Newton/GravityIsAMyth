package controllers;

import static utilities.Run.sceneController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import utilities.Controller;
import utilities.GravityParticle;
import utilities.Vector;

public class DirectionController extends Controller {

  public static double windowWidth = 1920;
  public static double windowHeight = 1050;
  private static double gravityScale = 1000;
  private static double maxVelocity;        //Caps the particles' speed, determines color
  private static Vector gravityVector;      //Used to calculate particles' velocities
  private static double friction;           //Used as a percent "energy loss" on collisions

  //Setup objects used by DirectionController.fxml
  public Circle gravityBounds;
  public Circle lockRing;
  public Circle gravityCenterPoint;
  public Line gravityLine;
  public Circle gravityEndPoint;
  public Slider maxVelocitySlider;
  public Slider frictionSlider;
  public Slider radiusSlider;
  public Text gravityText;
  public Text velocityText;
  public Text frictionText;
  public Text radiusText;
  public ComboBox borderModeChoices;
  public ToggleButton funButton;
  public ToggleButton collisionButton;
  public Button clearButton;
  public Button switchButton;
  private double defaultGravityX;
  private double defaultGravityY;
  private double dragStartX;
  private double dragStartY;
  private double particleRadius;
  private GravityParticle.EdgeHandling edgeHandling;
  private boolean doCollisions;

  public DirectionController() {
  }

  @Override
  protected void initialize() {
    borderModeChoices.getItems().clear();
    borderModeChoices.getItems().addAll("Bounce", "Wrap", "Bound");
    borderModeChoices.setValue("Bounce");

    reset();
  }

  @Override
  public Vector getGravityVector(GravityParticle particle) {
    return gravityVector;
  }

  @Override
  public double getMaxVelocity() {
    return maxVelocity;
  }

  @Override
  public double getFriction() {
    return friction;
  }

  /**
   * Upon clicking inside gravityBounds, modify gravityLine and gravityVector representing gravity.
   *
   * @param event contains the cursor location, passed by the Application
   */
  @FXML
  private void moveGravityLine(MouseEvent event) {
    double eX = event.getSceneX();
    double eY = event.getSceneY();
    if (Math.pow(eX - gravityLine.getLayoutX(), 2) + Math.pow(eY - gravityLine.getLayoutX(), 2)
        > Math.pow(gravityBounds.getRadius(), 2)) {
      double angle = StrictMath.atan2(eY - gravityLine.getLayoutX(), eX - gravityLine.getLayoutX());
      eX = gravityBounds.getRadius() * Math.cos(angle) + gravityLine.getLayoutX();
      eY = gravityBounds.getRadius() * Math.sin(angle) + gravityLine.getLayoutY();
    }
    gravityLine.setEndX(eX - gravityLine.getLayoutX());
    gravityLine.setEndY(eY - gravityLine.getLayoutY());
    gravityEndPoint.setLayoutX(eX);
    gravityEndPoint.setLayoutY(eY);
    updateGravityVector();
  }

  @FXML
  private void setMaxVelocity() {
    maxVelocity = maxVelocitySlider.getValue() / gravityScale;
    velocityText.setText(String.format("%.3f", maxVelocity));
  }

  @FXML
  private void setFriction() {
    friction = frictionSlider.getValue();
    if (funButton.isSelected()) {
      friction += -1;
    }
    frictionText.setText(String.format("%.3f%c", friction * 100, '%'));
  }

  @FXML
  private void setParticleRadius() {
    particleRadius = radiusSlider.getValue();
    radiusText.setText(String.format("%.0f", particleRadius));
    for (GravityParticle particle : particles) {
      particle.setRadius(particleRadius);
    }
  }

  @Override
  protected void createParticle(MouseEvent event) {
    //Don't place a particle if the drag started inside gravityBounds
    if (!(dragStartX > 90 && dragStartX < 240 && dragStartY > 90 && dragStartY < 240)) {
      double x = event.getSceneX();
      double y = event.getSceneY();
      GravityParticle particle = new GravityParticle(x, y, particleRadius,
          edgeHandling, doCollisions, this);
      addGravityParticle(particle);
      particles.add(particle);
    }
  }

  /**
   * Resets gravityLine and gravityVector.
   */
  @FXML
  private void resetGravity() {
    gravityLine.setEndX(defaultGravityX);
    gravityLine.setEndY(defaultGravityY);
    gravityEndPoint.setLayoutX(defaultGravityX + gravityLine.getLayoutX());
    gravityEndPoint.setLayoutY(defaultGravityY + gravityLine.getLayoutY());
    updateGravityVector();
  }

  /**
   * Sets gravityVector to 0.
   */
  @FXML
  private void zeroGravity() {
    gravityLine.setEndX(0);
    gravityLine.setEndY(0);
    gravityEndPoint.setLayoutX(gravityLine.getLayoutX());
    gravityEndPoint.setLayoutY(gravityLine.getLayoutY());
    updateGravityVector();
  }

  /**
   * Saves the position of the cursor at the beginning of an event.
   *
   * @param event contains the cursor location, passed by the Application
   */
  @FXML
  private void setDragStartPoint(MouseEvent event) {
    dragStartX = event.getSceneX();
    dragStartY = event.getSceneY();
    createParticle(event);
  }

  /**
   * Updates the edge-handling of particles.
   */
  @FXML
  private void setBorderMode() {
    switch ((String) borderModeChoices.getValue()) {
      case "Wrap":
        edgeHandling = GravityParticle.EdgeHandling.WRAP;
        break;
      case "Bound":
        edgeHandling = GravityParticle.EdgeHandling.BOUND;
        break;
      default:
        edgeHandling = GravityParticle.EdgeHandling.BOUNCE;
        break;
    }
    for (GravityParticle particle : particles) {
      particle.setEdgeHandling(edgeHandling);
    }
  }

  @FXML
  private void toggleCollisions() {
    doCollisions = collisionButton.isSelected();
    for (GravityParticle particle : particles) {
      particle.setDoCollisions(doCollisions);
    }
  }

  /**
   * Sets gravityVector from gravityLine.
   */
  private void updateGravityVector() {
    double dX = gravityLine.getEndX() - gravityLine.getStartX();
    double dY = gravityLine.getEndY() - gravityLine.getStartY();
    gravityVector = Vector.vectorFromXandY(dX, -dY).scale(1 / gravityScale);

    gravityText.setText(gravityVector.toString());
  }

  @Override
  public void reset() {
    clearParticles();

    //Reset controls
    gravityLine.setEndX(0);
    gravityLine.setEndY(50);
    maxVelocitySlider.setValue(12500);
    frictionSlider.setValue(0);
    radiusSlider.setValue(15);
    collisionButton.setSelected(true);
    defaultGravityX = gravityLine.getEndX();
    defaultGravityY = gravityLine.getEndY();

    //Run control methods for the first time
    updateGravityVector();
    setMaxVelocity();
    setFriction();
    setParticleRadius();
    toggleCollisions();
    setBorderMode();

  }

  @FXML
  private void activateSourceControl() {
    reset();
    sceneController.activate("Source Control");
  }
}
