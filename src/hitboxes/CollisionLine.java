package hitboxes;

import static hitboxes.HitBoxPoint.distance;

public class CollisionLine {

  private final HitBoxPoint point0;
  private final HitBoxPoint point1;

  public CollisionLine(HitBoxPoint point0, HitBoxPoint point1) {
    this.point0 = point0;
    this.point1 = point1;
  }

  private static boolean onLine(CollisionLine line, HitBoxPoint testPoint) {
    HitBoxPoint point0 = line.getPoint0();
    HitBoxPoint point1 = line.getPoint1();

    return testPoint.getX() <= Math.max(point0.getX(), point1.getX()) &&
        testPoint.getX() >= Math.min(point0.getX(), point1.getX()) &&
        testPoint.getY() <= Math.max(point0.getY(), point1.getY()) &&
        testPoint.getY() >= Math.min(point0.getY(), point1.getY());
  }

  public HitBoxPoint getPoint0() {
    return point0;
  }

  public HitBoxPoint getPoint1() {
    return point1;
  }

  /**
   * For reference: go to https://www.geeksforgeeks.org/how-to-check-if-a-given-point-lies-inside-a-polygon/
   *
   * @param testPoint - the point to test against
   * @return whether or not the line is to the right of the testPoint
   */
  public boolean isRightOfPoint(HitBoxPoint testPoint) {
    HitBoxPoint extreme = new HitBoxPoint(testPoint.getX(), Double.POSITIVE_INFINITY);
    PointOrientation po1 = findOrientation(point0, point1, testPoint);
    PointOrientation po2 = findOrientation(point0, point1, extreme);
    PointOrientation po3 = findOrientation(testPoint, extreme, point0);
    PointOrientation po4 = findOrientation(testPoint, extreme, point1);

    if (po1 != po2 && po3 != po4) {
      return true;
    }
    if (onLine(testPoint)) {
      return true;
    } else if (onLine(new CollisionLine(testPoint, extreme), point0)) {
      return true;
    } else {
      return onLine(new CollisionLine(testPoint, extreme), point1);
    }
  }

  private PointOrientation findOrientation(HitBoxPoint p0, HitBoxPoint p1, HitBoxPoint p2) {
    double slope1 = (p1.getY() - p0.getY()) / (p1.getX() - p0.getX());
    double slope2 = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
    if (slope1 > slope2) {
      return PointOrientation.CLOCKWISE;
    } else if (slope1 < slope2) {
      return PointOrientation.COUNTERCLOCKWISE;
    } else {
      return PointOrientation.COLINEAR;
    }
  }

  private boolean onLine(HitBoxPoint testPoint) {
    return testPoint.getX() <= Math.max(point0.getX(), point1.getX()) &&
        testPoint.getX() >= Math.min(point0.getX(), point1.getX()) &&
        testPoint.getY() <= Math.max(point0.getY(), point1.getY()) &&
        testPoint.getY() >= Math.min(point0.getY(), point1.getY());
  }

  public double findDistanceRatio(HitBoxPoint testPoint) {
    double lineDistance = distance(point0, point1);
    double compositeDistance = distance(point0, testPoint) + distance(testPoint, point1);
    return compositeDistance / lineDistance;
  }

  /**
   * @return angle of this CollisionLine, modified for the screen
   */
  public double getWallAngle() {
    return StrictMath.atan2(point0.getY() - point1.getY(), point1.getX() - point0.getX());
  }

  public enum PointOrientation {
    COLINEAR,
    CLOCKWISE,
    COUNTERCLOCKWISE
  }
}
