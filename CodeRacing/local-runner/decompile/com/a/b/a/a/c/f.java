package com.a.b.a.a.c;

import com.google.gson.annotations.Expose;

public final class f
{
  @Expose(serialize=false, deserialize=false)
  private final double distanceTraveled;
  @Expose(serialize=false, deserialize=false)
  private final Integer remainingHitRecoverTicks;
  private final Boolean brakes;
  private final double wheelDistanceChangePerTick;
  
  public f(double paramDouble1, Integer paramInteger, Boolean paramBoolean, double paramDouble2)
  {
    this.distanceTraveled = paramDouble1;
    this.remainingHitRecoverTicks = paramInteger;
    this.brakes = paramBoolean;
    this.wheelDistanceChangePerTick = paramDouble2;
  }
  
  public double getDistanceTraveled()
  {
    return this.distanceTraveled;
  }
  
  public Integer getRemainingHitRecoverTicks()
  {
    return this.remainingHitRecoverTicks;
  }
  
  public Boolean getBrakes()
  {
    return this.brakes;
  }
  
  public double getWheelDistanceChangePerTick()
  {
    return this.wheelDistanceChangePerTick;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\f.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */