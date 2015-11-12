package com.a.b.a.a.c;

import com.codeforces.commons.reflection.Name;

public class q
  extends e
{
  private final long carId;
  private final long playerId;
  private final r type;
  
  public q(@Name("id") long paramLong1, @Name("mass") double paramDouble1, @Name("x") double paramDouble2, @Name("y") double paramDouble3, @Name("speedX") double paramDouble4, @Name("speedY") double paramDouble5, @Name("angle") double paramDouble6, @Name("angularSpeed") double paramDouble7, @Name("radius") double paramDouble8, @Name("carId") long paramLong2, @Name("playerId") long paramLong3, @Name("type") r paramr)
  {
    super(paramLong1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8);
    this.carId = paramLong2;
    this.playerId = paramLong3;
    this.type = paramr;
  }
  
  public long getCarId()
  {
    return this.carId;
  }
  
  public long getPlayerId()
  {
    return this.playerId;
  }
  
  public r getType()
  {
    return this.type;
  }
  
  public static boolean areFieldEquals(q paramq1, q paramq2)
  {
    return (paramq1 == paramq2) || ((paramq1 != null) && (paramq2 != null) && (e.areFieldEquals(paramq1, paramq2)) && (paramq1.type == paramq2.type));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\q.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */