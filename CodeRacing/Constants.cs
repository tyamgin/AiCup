using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    class Constants
    {
        private Dictionary<string, object> _m = new Dictionary<string, object>(); 

        private void _init(long paramLong, int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double paramDouble2, int paramInt4, int paramInt5, int paramInt6, double paramDouble3, int[] paramArrayOfInt, int paramInt7, double paramDouble4, double paramDouble5, int paramInt8, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12, double paramDouble13, double paramDouble14, double paramDouble15, int paramInt9, int paramInt10, int paramInt11, double paramDouble16, int paramInt12, int paramInt13, double paramDouble17, double paramDouble18, double paramDouble19, double paramDouble20, double paramDouble21, double paramDouble22, double paramDouble23, double paramDouble24, int paramInt14, double paramDouble25, double paramDouble26, double paramDouble27, double paramDouble28, double paramDouble29, double paramDouble30, double paramDouble31, double paramDouble32, double paramDouble33, double paramDouble34, double paramDouble35, double paramDouble36, int paramInt15, int paramInt16)
        {
            _m["randomSeed"] = paramLong;
            _m["tickCount"] = paramInt1;
            _m["worldWidth"] = paramInt2;
            _m["worldHeight"] = paramInt3;
            _m["trackTileSize"] = paramDouble1;
            _m["trackTileMargin"] = paramDouble2;
            _m["lapCount"] = paramInt4;
            _m["lapTickCount"] = paramInt5;
            _m["initialFreezeDurationTicks"] = paramInt6;
            _m["burningTimeDurationFactor"] = paramDouble3;
            _m["carRotationFrictionFactor"] = paramDouble15;
            _m["throwProjectileCooldownTicks"] = paramInt9;
            _m["useNitroCooldownTicks"] = paramInt10;
            _m["spillOilCooldownTicks"] = paramInt11;
            _m["nitroEnginePowerFactor"] = paramDouble16;
            _m["nitroDurationTicks"] = paramInt12;
            _m["carReactivationTimeTicks"] = paramInt13;
            _m["buggyMass"] = paramDouble17;
            _m["buggyEngineForwardPower"] = paramDouble18;
            _m["buggyEngineRearPower"] = paramDouble19;
            _m["jeepMass"] = paramDouble20;
            _m["jeepEngineForwardPower"] = paramDouble21;
            _m["jeepEngineRearPower"] = paramDouble22;
            _m["bonusSize"] = paramDouble23;
            _m["bonusMass"] = paramDouble24;
            _m["pureScoreAmount"] = paramInt14;
            _m["washerRadius"] = paramDouble25;
            _m["washerMass"] = paramDouble26;
            _m["washerInitialSpeed"] = paramDouble27;
            _m["washerDamage"] = paramDouble28;
            _m["sideWasherAngle"] = paramDouble29;
            _m["tireRadius"] = paramDouble30;
            _m["tireMass"] = paramDouble31;
            _m["tireInitialSpeed"] = paramDouble32;
            _m["tireDamageFactor"] = paramDouble33;
            _m["tireDisappearSpeedFactor"] = paramDouble34;
            _m["oilSlickInitialRange"] = paramDouble35;
            _m["oilSlickRadius"] = paramDouble36;
            _m["oilSlickLifetime"] = paramInt15;
            _m["maxOiledStateDurationTicks"] = paramInt16;
            //_m["finishTrackScores"] = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
            _m["finishLapScore"] = paramInt7;
            _m["lapWaypointsSummaryScoreFactor"] = paramDouble4;
            _m["carDamageScoreFactor"] = paramDouble5;
            _m["carEliminationScore"] = paramInt8;
            _m["carWidth"] = paramDouble6;
            _m["carHeight"] = paramDouble7;
            _m["carEnginePowerChangePerTick"] = paramDouble8;
            _m["carWheelTurnChangePerTick"] = paramDouble9;
            _m["carAngularSpeedFactor"] = paramDouble10;
            _m["carMovementAirFrictionFactor"] = paramDouble11;
            _m["carRotationAirFrictionFactor"] = paramDouble12;
            _m["carLengthwiseMovementFrictionFactor"] = paramDouble13;
            _m["carCrosswiseMovementFrictionFactor"] = paramDouble14;
        }

        public Constants()
        {
            _init(-1, -1, -1, -1, 800.0D, 80.0D, 4, -1, 180, 0.5D, new []{-1}, 1000, 0.5D, 100.0D, 100, 210.0D, 
                140.0D, 0.025D, 0.05D, 0.0017453292519943296D, 0.0075D, 0.0075D, 0.001D, 0.25D, 0.008726646259971648D, 
                60, 120, 120, 2.0D, 120, 300, 1250.0D, 312.5D, 234.375D, 1500.0D, 375.0D, 281.25D, 70.0D, 100.0D, 100, 
                20.0D, 10.0D, 60.0D, 0.1D, 0.03490658503988659D, 70.0D, 1000.0D, 60.0D, 0.25D, 0.25D, 10.0D, 150.0D, 600, 60);
        }
    }
}
