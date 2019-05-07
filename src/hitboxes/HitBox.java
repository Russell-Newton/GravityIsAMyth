package hitboxes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class HitBox {

  protected final ObservableList<HitBoxPoint> hitBoxPoints;
  protected double centerX;
  protected double centerY;

  public HitBox(double centerX, double centerY, ObservableList<HitBoxPoint> hitBoxPoints) {
    this.hitBoxPoints = hitBoxPoints;
    setCenterX(centerX);
    setCenterY(centerY);
  }

  public HitBox(double centerX, double centerY) {
    this.hitBoxPoints = FXCollections.observableArrayList();
    setCenterX(centerX);
    setCenterY(centerY);

    //To counter potential errors from the setCenter methods
    this.hitBoxPoints.add(new HitBoxPoint(centerX, centerY));
  }

  public abstract Double getDeflectionAngle(HitBox testHitBox);

  public abstract ObservableList<HitBoxPoint> getHitBoxPoints(HitBox testHitBox);

  public double getCenterX() {
    return centerX;
  }

  public void setCenterX(double x) {
    double offsetDistance = x - this.centerX;
    this.centerX = x;
    for (HitBoxPoint point : hitBoxPoints) {
      point.shiftX(offsetDistance);
    }
  }

  public double getCenterY() {
    return centerY;
  }

  public void setCenterY(double y) {
    double offsetDistance = y - this.centerY;
    this.centerY = y;
    for (HitBoxPoint point : hitBoxPoints) {
      point.shiftY(offsetDistance);
    }
  }
}
