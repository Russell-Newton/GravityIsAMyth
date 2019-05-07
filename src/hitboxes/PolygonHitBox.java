package hitboxes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PolygonHitBox extends HitBox {

  private final ObservableList<CollisionLine> collisionLines;

  public PolygonHitBox(double centerX, double centerY, ObservableList<HitBoxPoint> hitBoxPoints) {
    super(centerX, centerY, hitBoxPoints);

    collisionLines = FXCollections.observableArrayList();
    for (int i = 0; i < hitBoxPoints.size() - 2; i++) {
      collisionLines.add(new CollisionLine(hitBoxPoints.get(i), hitBoxPoints.get(i + 1)));
    }
    collisionLines
        .add(new CollisionLine(hitBoxPoints.get(hitBoxPoints.size() - 1), hitBoxPoints.get(0)));
  }

  public ObservableList<CollisionLine> getCollisionLines() {
    return collisionLines;
  }

  /**
   * @return the angle of deflection, if the polygons have intersected. null if they haven't
   */
  @Override
  public Double getDeflectionAngle(HitBox testHitBox) {
    int count = 0;
    CollisionLine closestLine = this.collisionLines.get(0);
    for (HitBoxPoint point : testHitBox.getHitBoxPoints(this)) {
      for (CollisionLine borderLine : this.collisionLines) {
        if (borderLine.isRightOfPoint(point)) {
          count++;
        }

        if (borderLine.findDistanceRatio(point) < closestLine.findDistanceRatio(point)) {
          closestLine = borderLine;
        }
      }
    }
    if (count % 2 == 1) {
      return closestLine.getWallAngle();
    }
    return null;
  }

  @Override
  public ObservableList<HitBoxPoint> getHitBoxPoints(HitBox testHitBox) {
    return hitBoxPoints;
  }
}
