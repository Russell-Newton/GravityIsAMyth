package hitboxes;

public class HitBoxPoint {

  private double x;
  private double y;

  public HitBoxPoint(double relativeX, double relativeY) {
    this.x = relativeX;
    this.y = relativeY;
  }

  public static double distance(HitBoxPoint point0, HitBoxPoint point1) {
    return Math.sqrt(
        Math.pow(point0.getX() + point1.getX(), 2) + Math.pow(point0.getY() + point1.getY(), 2));
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void shiftX(double shift) {
    x += shift;
  }

  public void shiftY(double shift) {
    y += shift;
  }
}
