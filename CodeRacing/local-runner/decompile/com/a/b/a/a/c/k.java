package com.a.b.a.a.c;

public enum k
{
  CAR_AND_BORDER_IMPACT(10),  CAR_AND_CAR_IMPACT(10),  CAR_AND_WASHER_IMPACT(10),  CAR_AND_TIRE_IMPACT(10),  DRIFTING(60),  CAR_CONDITION_CHANGE(60);
  
  private static final int DEFAULT_EFFECT_DURATION = 1;
  private final int duration;
  
  private k(int paramInt1)
  {
    if (paramInt1 < 1) {
      throw new IllegalArgumentException("Argument 'duration' is less than 1.");
    }
    if (paramInt1 > 32767) {
      throw new IllegalArgumentException("Argument 'duration' is greater than 32767.");
    }
    this.duration = paramInt1;
  }
  
  private k()
  {
    this(1);
  }
  
  public int getDuration()
  {
    return this.duration;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\k.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */