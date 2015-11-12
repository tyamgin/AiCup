package com.a.b.a.a.c;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class j
{
  private final long id;
  private final k type;
  private final int tick;
  private final Long affectedUnitId;
  private final Double x;
  private final Double y;
  private final Double angle;
  private final Map attributes;
  
  public j(long paramLong, k paramk, int paramInt, Long paramLong1, Double paramDouble1, Double paramDouble2, Double paramDouble3)
  {
    this.id = paramLong;
    this.type = paramk;
    this.tick = paramInt;
    this.affectedUnitId = paramLong1;
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.angle = paramDouble3;
    this.attributes = null;
  }
  
  public j(long paramLong, k paramk, int paramInt, Long paramLong1, Double paramDouble1, Double paramDouble2, Double paramDouble3, Map paramMap)
  {
    this.id = paramLong;
    this.type = paramk;
    this.tick = paramInt;
    this.affectedUnitId = paramLong1;
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.angle = paramDouble3;
    this.attributes = new HashMap(paramMap);
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public k getType()
  {
    return this.type;
  }
  
  public int getTick()
  {
    return this.tick;
  }
  
  public Long getAffectedUnitId()
  {
    return this.affectedUnitId;
  }
  
  public Double getX()
  {
    return this.x;
  }
  
  public Double getY()
  {
    return this.y;
  }
  
  public Double getAngle()
  {
    return this.angle;
  }
  
  public Map getAttributes()
  {
    return this.attributes == null ? null : Collections.unmodifiableMap(this.attributes);
  }
  
  public Object getAttribute(String paramString)
  {
    return this.attributes == null ? null : this.attributes.get(paramString);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\j.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */