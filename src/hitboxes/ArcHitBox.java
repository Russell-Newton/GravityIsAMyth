package hitboxes;

import static java.lang.Math.PI;
import static hitboxes.HitBoxPoint.distance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ArcHitBox extends HitBox {

  private final double radius;
  private final double arcAngle;
  private final HitBoxPoint centerPoint;
  private double arcAngleStart;

  public ArcHitBox(double centerX, double centerY, double radius, double arcAngle,
      double arcAngleStart) {
    super(centerX, centerY);
    this.radius = radius;
    this.arcAngle = arcAngle;
    this.arcAngleStart = arcAngleStart;
    this.centerPoint = new HitBoxPoint(centerX, centerY);
  }

  @Override
  public Double getDeflectionAngle(HitBox testHitBox) {
    for (HitBoxPoint testPoint : testHitBox.getHitBoxPoints(this)) {
      if (underArc(testPoint)) {
        return angleToCenter(testPoint) + PI / 2;
      }
    }
    return null;
  }

  @Override
  public ObservableList<HitBoxPoint> getHitBoxPoints(HitBox testHitBox) {
    double centersAngle = StrictMath.atan2(this.getCenterY() - testHitBox.getCenterY(),
        this.getCenterX() - testHitBox.getCenterX());
    if (centersAngle >= arcAngleStart && centersAngle <= arcAngleStart + arcAngle) {
      HitBoxPoint hitBoxPoint = new HitBoxPoint(this.getCenterX() + radius * Math.cos(centersAngle),
          this.getCenterY() + radius * Math.sin(centersAngle));
      return FXCollections.observableArrayList(hitBoxPoint);
    }
    return FXCollections.observableArrayList(getCenterPoint());
  }

  private boolean underArc(HitBoxPoint testPoint) {
    return angleToCenter(testPoint) >= Math.min(arcAngleStart, arcAngleStart + arcAngle) &&
        angleToCenter(testPoint) <= Math.min(arcAngleStart, arcAngleStart + arcAngle) &&
        distance(getCenterPoint(), testPoint) <= radius;
  }

  private double angleToCenter(HitBoxPoint testPoint) {
    return StrictMath.atan2(this.getCenterY() - testPoint.getY(),
        this.getCenterX() - testPoint.getX());
  }

  private HitBoxPoint getCenterPoint() {
    return new HitBoxPoint(this.getCenterX(), this.getCenterY());
  }

  public void setArcAngleStart(double angle) {
    arcAngleStart = angle % (2 * PI);
  }
}
