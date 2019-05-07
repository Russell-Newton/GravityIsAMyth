package hitboxes;

import static java.lang.Math.PI;

public class CircleHitBox extends ArcHitBox {

  public CircleHitBox(double centerX, double centerY, double radius) {
    super(centerX, centerY, radius, 0, 2 * PI);
  }
}
