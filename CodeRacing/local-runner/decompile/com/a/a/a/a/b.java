package com.a.a.a.a;

import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;

public class b
{
  public static Point2D[] a(Point2D paramPoint2D, Vector2D paramVector2D, double paramDouble)
  {
    double d1 = Math.sin(paramDouble);
    double d2 = Math.cos(paramDouble);
    double d3 = paramVector2D.getX() * 0.5D;
    double d4 = paramVector2D.getY() * 0.5D;
    double d5 = d2 * d3;
    double d6 = d1 * d3;
    double d7 = d1 * d4;
    double d8 = -d2 * d4;
    return new Point2D[] { new Point2D(paramPoint2D.getX() - d5 + d7, paramPoint2D.getY() - d6 + d8), new Point2D(paramPoint2D.getX() + d5 + d7, paramPoint2D.getY() + d6 + d8), new Point2D(paramPoint2D.getX() + d5 - d7, paramPoint2D.getY() + d6 - d8), new Point2D(paramPoint2D.getX() - d5 - d7, paramPoint2D.getY() - d6 - d8) };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\a\a\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */