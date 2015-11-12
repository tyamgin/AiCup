package com.a.b.a.a.c;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Until;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class h
  extends v
{
  @Until(1.0D)
  private final Long randomSeed;
  @Until(1.0D)
  private final double speedFactor;
  private final Boolean lastTick;
  private final j[] effects;
  private final Map decoratedCarById;
  private final Map decoratedPlayerById;
  @Expose(serialize=false, deserialize=false)
  private final Object systemData;
  
  public h(v paramv, Long paramLong, double paramDouble, boolean paramBoolean, j[] paramArrayOfj, Map paramMap1, Map paramMap2, Object paramObject)
  {
    super(paramv.getTick(), paramv.getTickCount(), paramv.getLastTickIndex(), paramv.getWidth(), paramv.getHeight(), paramv.getPlayersUnsafe(), paramv.getCarsUnsafe(), paramv.getProjectilesUnsafe(), paramv.getBonusesUnsafe(), paramv.getOilSlicksUnsafe(), paramv.getMapName(), paramv.getTilesXYUnsafe(), paramv.getWaypointsUnsafe(), paramv.getStartingDirection());
    this.randomSeed = paramLong;
    this.speedFactor = paramDouble;
    this.lastTick = (paramBoolean ? Boolean.valueOf(true) : null);
    this.effects = ((j[])Arrays.copyOf(paramArrayOfj, paramArrayOfj.length));
    this.decoratedCarById = (paramMap1 == null ? null : new HashMap(paramMap1));
    this.decoratedPlayerById = (paramMap2 == null ? null : new HashMap(paramMap2));
    this.systemData = paramObject;
  }
  
  public Long getRandomSeed()
  {
    return this.randomSeed;
  }
  
  public double getSpeedFactor()
  {
    return this.speedFactor;
  }
  
  public boolean isLastTick()
  {
    return this.lastTick == null ? false : this.lastTick.booleanValue();
  }
  
  public j[] getEffects()
  {
    return (j[])Arrays.copyOf(this.effects, this.effects.length);
  }
  
  public Map getDecoratedCarById()
  {
    return this.decoratedCarById == null ? null : Collections.unmodifiableMap(this.decoratedCarById);
  }
  
  public Map getDecoratedPlayerById()
  {
    return this.decoratedPlayerById == null ? null : Collections.unmodifiableMap(this.decoratedPlayerById);
  }
  
  public Object getSystemData()
  {
    return this.systemData;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\h.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */