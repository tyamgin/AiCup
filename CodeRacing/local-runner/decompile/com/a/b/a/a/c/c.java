package com.a.b.a.a.c;

import com.codeforces.commons.reflection.Name;
import com.google.gson.annotations.Until;

public class c
  extends s
{
  @Until(1.0D)
  private final long playerId;
  @Until(1.0D)
  private final int teammateIndex;
  @Until(1.0D)
  private final boolean teammate;
  @Until(1.0D)
  private final d type;
  private final int projectileCount;
  private final int nitroChargeCount;
  private final int oilCanisterCount;
  private final int remainingProjectileCooldownTicks;
  private final int remainingNitroCooldownTicks;
  private final int remainingOilCooldownTicks;
  private final int remainingNitroTicks;
  private final int remainingOiledTicks;
  private final double durability;
  private final double enginePower;
  private final double wheelTurn;
  private final int nextWaypointX;
  private final int nextWaypointY;
  private final boolean finishedTrack;
  
  public c(@Name("id") long paramLong1, @Name("mass") double paramDouble1, @Name("x") double paramDouble2, @Name("y") double paramDouble3, @Name("speedX") double paramDouble4, @Name("speedY") double paramDouble5, @Name("angle") double paramDouble6, @Name("angularSpeed") double paramDouble7, @Name("width") double paramDouble8, @Name("height") double paramDouble9, @Name("playerId") long paramLong2, @Name("teammateIndex") int paramInt1, @Name("teammate") boolean paramBoolean1, @Name("type") d paramd, @Name("projectileCount") int paramInt2, @Name("nitroChargeCount") int paramInt3, @Name("oilCanisterCount") int paramInt4, @Name("remainingProjectileCooldownTicks") int paramInt5, @Name("remainingNitroCooldownTicks") int paramInt6, @Name("remainingOilCooldownTicks") int paramInt7, @Name("remainingNitroTicks") int paramInt8, @Name("remainingOiledTicks") int paramInt9, @Name("durability") double paramDouble10, @Name("enginePower") double paramDouble11, @Name("wheelTurn") double paramDouble12, @Name("nextWaypointX") int paramInt10, @Name("nextWaypointY") int paramInt11, @Name("finishedTrack") boolean paramBoolean2)
  {
    super(paramLong1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9);
    this.playerId = paramLong2;
    this.teammateIndex = paramInt1;
    this.teammate = paramBoolean1;
    this.type = paramd;
    this.projectileCount = paramInt2;
    this.nitroChargeCount = paramInt3;
    this.oilCanisterCount = paramInt4;
    this.remainingProjectileCooldownTicks = paramInt5;
    this.remainingNitroCooldownTicks = paramInt6;
    this.remainingOilCooldownTicks = paramInt7;
    this.remainingNitroTicks = paramInt8;
    this.remainingOiledTicks = paramInt9;
    this.durability = paramDouble10;
    this.enginePower = paramDouble11;
    this.wheelTurn = paramDouble12;
    this.nextWaypointX = paramInt10;
    this.nextWaypointY = paramInt11;
    this.finishedTrack = paramBoolean2;
  }
  
  public long getPlayerId()
  {
    return this.playerId;
  }
  
  public int getTeammateIndex()
  {
    return this.teammateIndex;
  }
  
  public boolean isTeammate()
  {
    return this.teammate;
  }
  
  public d getType()
  {
    return this.type;
  }
  
  public int getProjectileCount()
  {
    return this.projectileCount;
  }
  
  public int getNitroChargeCount()
  {
    return this.nitroChargeCount;
  }
  
  public int getOilCanisterCount()
  {
    return this.oilCanisterCount;
  }
  
  public int getRemainingProjectileCooldownTicks()
  {
    return this.remainingProjectileCooldownTicks;
  }
  
  public int getRemainingNitroCooldownTicks()
  {
    return this.remainingNitroCooldownTicks;
  }
  
  public int getRemainingOilCooldownTicks()
  {
    return this.remainingOilCooldownTicks;
  }
  
  public int getRemainingNitroTicks()
  {
    return this.remainingNitroTicks;
  }
  
  public int getRemainingOiledTicks()
  {
    return this.remainingOiledTicks;
  }
  
  public double getDurability()
  {
    return this.durability;
  }
  
  public double getEnginePower()
  {
    return this.enginePower;
  }
  
  public double getWheelTurn()
  {
    return this.wheelTurn;
  }
  
  public int getNextWaypointX()
  {
    return this.nextWaypointX;
  }
  
  public int getNextWaypointY()
  {
    return this.nextWaypointY;
  }
  
  public boolean isFinishedTrack()
  {
    return this.finishedTrack;
  }
  
  public static boolean areFieldEquals(c paramc1, c paramc2)
  {
    return (paramc1 == paramc2) || ((paramc1 != null) && (paramc2 != null) && (s.areFieldEquals(paramc1, paramc2)) && (paramc1.playerId == paramc2.playerId) && (paramc1.teammateIndex == paramc2.teammateIndex) && (paramc1.teammate == paramc2.teammate) && (paramc1.type == paramc2.type) && (paramc1.projectileCount == paramc2.projectileCount) && (paramc1.nitroChargeCount == paramc2.nitroChargeCount) && (paramc1.oilCanisterCount == paramc2.oilCanisterCount) && (paramc1.remainingProjectileCooldownTicks == paramc2.remainingProjectileCooldownTicks) && (paramc1.remainingNitroCooldownTicks == paramc2.remainingNitroCooldownTicks) && (paramc1.remainingOilCooldownTicks == paramc2.remainingOilCooldownTicks) && (paramc1.remainingNitroTicks == paramc2.remainingNitroTicks) && (paramc1.remainingOiledTicks == paramc2.remainingOiledTicks) && (Double.compare(paramc1.durability, paramc2.durability) == 0) && (Double.compare(paramc1.enginePower, paramc2.enginePower) == 0) && (Double.compare(paramc1.wheelTurn, paramc2.wheelTurn) == 0) && (paramc1.nextWaypointX == paramc2.nextWaypointX) && (paramc1.nextWaypointY == paramc2.nextWaypointY) && (paramc1.finishedTrack == paramc2.finishedTrack));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */