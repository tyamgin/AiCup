package com.a.a.b.f;

import com.codeforces.commons.geometry.Line2D;
import com.codeforces.commons.geometry.Point2D;

public final class a
{
  public static double a(double paramDouble)
  {
    while (paramDouble > 3.141592653589793D) {
      paramDouble -= 6.283185307179586D;
    }
    while (paramDouble < -3.141592653589793D) {
      paramDouble += 6.283185307179586D;
    }
    return paramDouble;
  }
  
  public static boolean a(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    while (paramDouble3 < paramDouble2) {
      paramDouble3 += 6.283185307179586D;
    }
    while (paramDouble3 - 6.283185307179586D > paramDouble2) {
      paramDouble3 -= 6.283185307179586D;
    }
    while (paramDouble1 < paramDouble2) {
      paramDouble1 += 6.283185307179586D;
    }
    while (paramDouble1 - 6.283185307179586D > paramDouble2) {
      paramDouble1 -= 6.283185307179586D;
    }
    return (paramDouble1 >= paramDouble2) && (paramDouble1 <= paramDouble3);
  }
  
  public static boolean a(Point2D paramPoint2D, Point2D[] paramArrayOfPoint2D, double paramDouble)
  {
    int i = 0;
    int j = paramArrayOfPoint2D.length;
    while (i < j)
    {
      Point2D localPoint2D1 = paramArrayOfPoint2D[i];
      Point2D localPoint2D2 = paramArrayOfPoint2D[(i + 1)];
      Line2D localLine2D = Line2D.getLineByTwoPoints(localPoint2D1, localPoint2D2);
      if (localLine2D.getSignedDistanceFrom(paramPoint2D) >= paramDouble) {
        return true;
      }
      i++;
    }
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\f\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */