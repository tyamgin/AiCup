package com.a.a.b;

import com.codeforces.commons.geometry.Vector2D;

public class b
  extends d
{
  private Vector2D a;
  private Vector2D b;
  private Vector2D c;
  private double d;
  private double e;
  private double f;
  
  public b()
  {
    this.a = new Vector2D(0.0D, 0.0D);
    this.b = new Vector2D(0.0D, 0.0D);
    this.c = new Vector2D(0.0D, 0.0D);
  }
  
  public b(b paramb)
  {
    super(paramb);
    this.a = paramb.a.copy();
    this.c = paramb.c.copy();
    this.d = paramb.d;
    this.f = paramb.f;
  }
  
  public Vector2D a()
  {
    return this.a;
  }
  
  public void a(Vector2D paramVector2D)
  {
    this.a = paramVector2D.copy();
  }
  
  public Vector2D b()
  {
    return this.b;
  }
  
  public void b(Vector2D paramVector2D)
  {
    this.b = paramVector2D;
  }
  
  public Vector2D c()
  {
    return this.c;
  }
  
  public void c(Vector2D paramVector2D)
  {
    this.c = paramVector2D.copy();
  }
  
  public double d()
  {
    return this.d;
  }
  
  public void a(double paramDouble)
  {
    this.d = paramDouble;
  }
  
  public double e()
  {
    return this.e;
  }
  
  public void b(double paramDouble)
  {
    this.e = paramDouble;
  }
  
  public double f()
  {
    return this.f;
  }
  
  public void c(double paramDouble)
  {
    this.f = paramDouble;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */