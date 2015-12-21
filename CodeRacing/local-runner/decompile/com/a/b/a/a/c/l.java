package com.a.b.a.a.c;

import java.util.Arrays;

public class l
{
  private final long randomSeed;
  private final int tickCount;
  private final int worldWidth;
  private final int worldHeight;
  private final double trackTileSize;
  private final double trackTileMargin;
  private final int lapCount;
  private final int lapTickCount;
  private final int initialFreezeDurationTicks;
  private final double burningTimeDurationFactor;
  private final int[] finishTrackScores;
  private final int finishLapScore;
  private final double lapWaypointsSummaryScoreFactor;
  private final double carDamageScoreFactor;
  private final int carEliminationScore;
  private final double carWidth;
  private final double carHeight;
  private final double carEnginePowerChangePerTick;
  private final double carWheelTurnChangePerTick;
  private final double carAngularSpeedFactor;
  private final double carMovementAirFrictionFactor;
  private final double carRotationAirFrictionFactor;
  private final double carLengthwiseMovementFrictionFactor;
  private final double carCrosswiseMovementFrictionFactor;
  private final double carRotationFrictionFactor;
  private final int throwProjectileCooldownTicks;
  private final int useNitroCooldownTicks;
  private final int spillOilCooldownTicks;
  private final double nitroEnginePowerFactor;
  private final int nitroDurationTicks;
  private final int carReactivationTimeTicks;
  private final double buggyMass;
  private final double buggyEngineForwardPower;
  private final double buggyEngineRearPower;
  private final double jeepMass;
  private final double jeepEngineForwardPower;
  private final double jeepEngineRearPower;
  private final double bonusSize;
  private final double bonusMass;
  private final int pureScoreAmount;
  private final double washerRadius;
  private final double washerMass;
  private final double washerInitialSpeed;
  private final double washerDamage;
  private final double sideWasherAngle;
  private final double tireRadius;
  private final double tireMass;
  private final double tireInitialSpeed;
  private final double tireDamageFactor;
  private final double tireDisappearSpeedFactor;
  private final double oilSlickInitialRange;
  private final double oilSlickRadius;
  private final int oilSlickLifetime;
  private final int maxOiledStateDurationTicks;
  
  public l(long paramLong, int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double paramDouble2, int paramInt4, int paramInt5, int paramInt6, double paramDouble3, int[] paramArrayOfInt, int paramInt7, double paramDouble4, double paramDouble5, int paramInt8, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12, double paramDouble13, double paramDouble14, double paramDouble15, int paramInt9, int paramInt10, int paramInt11, double paramDouble16, int paramInt12, int paramInt13, double paramDouble17, double paramDouble18, double paramDouble19, double paramDouble20, double paramDouble21, double paramDouble22, double paramDouble23, double paramDouble24, int paramInt14, double paramDouble25, double paramDouble26, double paramDouble27, double paramDouble28, double paramDouble29, double paramDouble30, double paramDouble31, double paramDouble32, double paramDouble33, double paramDouble34, double paramDouble35, double paramDouble36, int paramInt15, int paramInt16)
  {
    this.randomSeed = paramLong;
    this.tickCount = paramInt1;
    this.worldWidth = paramInt2;
    this.worldHeight = paramInt3;
    this.trackTileSize = paramDouble1;
    this.trackTileMargin = paramDouble2;
    this.lapCount = paramInt4;
    this.lapTickCount = paramInt5;
    this.initialFreezeDurationTicks = paramInt6;
    this.burningTimeDurationFactor = paramDouble3;
    this.carRotationFrictionFactor = paramDouble15;
    this.throwProjectileCooldownTicks = paramInt9;
    this.useNitroCooldownTicks = paramInt10;
    this.spillOilCooldownTicks = paramInt11;
    this.nitroEnginePowerFactor = paramDouble16;
    this.nitroDurationTicks = paramInt12;
    this.carReactivationTimeTicks = paramInt13;
    this.buggyMass = paramDouble17;
    this.buggyEngineForwardPower = paramDouble18;
    this.buggyEngineRearPower = paramDouble19;
    this.jeepMass = paramDouble20;
    this.jeepEngineForwardPower = paramDouble21;
    this.jeepEngineRearPower = paramDouble22;
    this.bonusSize = paramDouble23;
    this.bonusMass = paramDouble24;
    this.pureScoreAmount = paramInt14;
    this.washerRadius = paramDouble25;
    this.washerMass = paramDouble26;
    this.washerInitialSpeed = paramDouble27;
    this.washerDamage = paramDouble28;
    this.sideWasherAngle = paramDouble29;
    this.tireRadius = paramDouble30;
    this.tireMass = paramDouble31;
    this.tireInitialSpeed = paramDouble32;
    this.tireDamageFactor = paramDouble33;
    this.tireDisappearSpeedFactor = paramDouble34;
    this.oilSlickInitialRange = paramDouble35;
    this.oilSlickRadius = paramDouble36;
    this.oilSlickLifetime = paramInt15;
    this.maxOiledStateDurationTicks = paramInt16;
    this.finishTrackScores = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    this.finishLapScore = paramInt7;
    this.lapWaypointsSummaryScoreFactor = paramDouble4;
    this.carDamageScoreFactor = paramDouble5;
    this.carEliminationScore = paramInt8;
    this.carWidth = paramDouble6;
    this.carHeight = paramDouble7;
    this.carEnginePowerChangePerTick = paramDouble8;
    this.carWheelTurnChangePerTick = paramDouble9;
    this.carAngularSpeedFactor = paramDouble10;
    this.carMovementAirFrictionFactor = paramDouble11;
    this.carRotationAirFrictionFactor = paramDouble12;
    this.carLengthwiseMovementFrictionFactor = paramDouble13;
    this.carCrosswiseMovementFrictionFactor = paramDouble14;
  }
  
  public long getRandomSeed()
  {
    return this.randomSeed;
  }
  
  public int getTickCount()
  {
    return this.tickCount;
  }
  
  public int getWorldWidth()
  {
    return this.worldWidth;
  }
  
  public int getWorldHeight()
  {
    return this.worldHeight;
  }
  
  public double getTrackTileSize()
  {
    return this.trackTileSize;
  }
  
  public double getTrackTileMargin()
  {
    return this.trackTileMargin;
  }
  
  public int getLapCount()
  {
    return this.lapCount;
  }
  
  public int getLapTickCount()
  {
    return this.lapTickCount;
  }
  
  public int getInitialFreezeDurationTicks()
  {
    return this.initialFreezeDurationTicks;
  }
  
  public double getBurningTimeDurationFactor()
  {
    return this.burningTimeDurationFactor;
  }
  
  public int[] getFinishTrackScores()
  {
    return Arrays.copyOf(this.finishTrackScores, this.finishTrackScores.length);
  }
  
  public int getFinishLapScore()
  {
    return this.finishLapScore;
  }
  
  public double getLapWaypointsSummaryScoreFactor()
  {
    return this.lapWaypointsSummaryScoreFactor;
  }
  
  public double getCarDamageScoreFactor()
  {
    return this.carDamageScoreFactor;
  }
  
  public int getCarEliminationScore()
  {
    return this.carEliminationScore;
  }
  
  public double getCarWidth()
  {
    return this.carWidth;
  }
  
  public double getCarHeight()
  {
    return this.carHeight;
  }
  
  public double getCarEnginePowerChangePerTick()
  {
    return this.carEnginePowerChangePerTick;
  }
  
  public double getCarWheelTurnChangePerTick()
  {
    return this.carWheelTurnChangePerTick;
  }
  
  public double getCarAngularSpeedFactor()
  {
    return this.carAngularSpeedFactor;
  }
  
  public double getCarMovementAirFrictionFactor()
  {
    return this.carMovementAirFrictionFactor;
  }
  
  public double getCarRotationAirFrictionFactor()
  {
    return this.carRotationAirFrictionFactor;
  }
  
  public double getCarLengthwiseMovementFrictionFactor()
  {
    return this.carLengthwiseMovementFrictionFactor;
  }
  
  public double getCarCrosswiseMovementFrictionFactor()
  {
    return this.carCrosswiseMovementFrictionFactor;
  }
  
  public double getCarRotationFrictionFactor()
  {
    return this.carRotationFrictionFactor;
  }
  
  public int getThrowProjectileCooldownTicks()
  {
    return this.throwProjectileCooldownTicks;
  }
  
  public int getUseNitroCooldownTicks()
  {
    return this.useNitroCooldownTicks;
  }
  
  public int getSpillOilCooldownTicks()
  {
    return this.spillOilCooldownTicks;
  }
  
  public double getNitroEnginePowerFactor()
  {
    return this.nitroEnginePowerFactor;
  }
  
  public int getNitroDurationTicks()
  {
    return this.nitroDurationTicks;
  }
  
  public int getCarReactivationTimeTicks()
  {
    return this.carReactivationTimeTicks;
  }
  
  public double getBuggyMass()
  {
    return this.buggyMass;
  }
  
  public double getBuggyEngineForwardPower()
  {
    return this.buggyEngineForwardPower;
  }
  
  public double getBuggyEngineRearPower()
  {
    return this.buggyEngineRearPower;
  }
  
  public double getJeepMass()
  {
    return this.jeepMass;
  }
  
  public double getJeepEngineForwardPower()
  {
    return this.jeepEngineForwardPower;
  }
  
  public double getJeepEngineRearPower()
  {
    return this.jeepEngineRearPower;
  }
  
  public double getBonusSize()
  {
    return this.bonusSize;
  }
  
  public double getBonusMass()
  {
    return this.bonusMass;
  }
  
  public int getPureScoreAmount()
  {
    return this.pureScoreAmount;
  }
  
  public double getWasherRadius()
  {
    return this.washerRadius;
  }
  
  public double getWasherMass()
  {
    return this.washerMass;
  }
  
  public double getWasherInitialSpeed()
  {
    return this.washerInitialSpeed;
  }
  
  public double getWasherDamage()
  {
    return this.washerDamage;
  }
  
  public double getSideWasherAngle()
  {
    return this.sideWasherAngle;
  }
  
  public double getTireRadius()
  {
    return this.tireRadius;
  }
  
  public double getTireMass()
  {
    return this.tireMass;
  }
  
  public double getTireInitialSpeed()
  {
    return this.tireInitialSpeed;
  }
  
  public double getTireDamageFactor()
  {
    return this.tireDamageFactor;
  }
  
  public double getTireDisappearSpeedFactor()
  {
    return this.tireDisappearSpeedFactor;
  }
  
  public double getOilSlickInitialRange()
  {
    return this.oilSlickInitialRange;
  }
  
  public double getOilSlickRadius()
  {
    return this.oilSlickRadius;
  }
  
  public int getOilSlickLifetime()
  {
    return this.oilSlickLifetime;
  }
  
  public int getMaxOiledStateDurationTicks()
  {
    return this.maxOiledStateDurationTicks;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\l.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */