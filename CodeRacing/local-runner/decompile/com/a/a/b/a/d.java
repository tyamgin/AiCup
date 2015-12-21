package com.a.a.b.a;

import com.a.a.b.a;
import com.codeforces.commons.geometry.Point2D;

class d
  extends com.a.a.b.d.d
{
  d(c paramc, double paramDouble, a parama) {}
  
  public void a(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    if (this.a > c.a(this.c)) {
      return;
    }
    int i;
    int j;
    int k;
    int m;
    if ((this.b.a() >= 0L) && (this.b.a() <= 9999L))
    {
      int n = (int)this.b.a();
      Point2D localPoint2D1 = c.b(this.c)[n];
      Point2D localPoint2D2 = c.c(this.c)[n];
      Point2D localPoint2D3 = this.b.r();
      if ((localPoint2D3.getX() >= localPoint2D1.getX()) && (localPoint2D3.getY() >= localPoint2D1.getY()) && (localPoint2D3.getX() < localPoint2D2.getX()) && (localPoint2D3.getY() < localPoint2D2.getY())) {
        return;
      }
      i = c.a(this.c, paramPoint2D1.getX());
      j = c.b(this.c, paramPoint2D1.getY());
      k = c.a(this.c, paramPoint2D2.getX());
      m = c.b(this.c, paramPoint2D2.getY());
    }
    else
    {
      i = c.a(this.c, paramPoint2D1.getX());
      j = c.b(this.c, paramPoint2D1.getY());
      k = c.a(this.c, paramPoint2D2.getX());
      m = c.b(this.c, paramPoint2D2.getY());
      if ((i == k) && (j == m)) {
        return;
      }
    }
    c.a(this.c, this.b, i, j);
    c.b(this.c, this.b, k, m);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\a\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */