package com.a.b.a.a.c;

import com.google.gson.annotations.Until;
import java.util.Arrays;

public class v
{
  private final int tick;
  @Until(1.0D)
  private final int tickCount;
  private final int lastTickIndex;
  @Until(1.0D)
  private final int width;
  @Until(1.0D)
  private final int height;
  private final o[] players;
  private final c[] cars;
  private final q[] projectiles;
  private final a[] bonuses;
  private final n[] oilSlicks;
  @Until(1.0D)
  private final String mapName;
  @Until(1.0D)
  private final t[][] tilesXY;
  @Until(1.0D)
  private final int[][] waypoints;
  @Until(1.0D)
  private final i startingDirection;
  
  public v(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, o[] paramArrayOfo, c[] paramArrayOfc, q[] paramArrayOfq, a[] paramArrayOfa, n[] paramArrayOfn, String paramString, t[][] paramArrayOft, int[][] paramArrayOfInt, i parami)
  {
    this.tick = paramInt1;
    this.tickCount = paramInt2;
    this.lastTickIndex = paramInt3;
    this.width = paramInt4;
    this.height = paramInt5;
    this.players = ((o[])Arrays.copyOf(paramArrayOfo, paramArrayOfo.length));
    this.cars = ((c[])Arrays.copyOf(paramArrayOfc, paramArrayOfc.length));
    this.projectiles = ((q[])Arrays.copyOf(paramArrayOfq, paramArrayOfq.length));
    this.bonuses = ((a[])Arrays.copyOf(paramArrayOfa, paramArrayOfa.length));
    this.oilSlicks = ((n[])Arrays.copyOf(paramArrayOfn, paramArrayOfn.length));
    this.mapName = paramString;
    this.tilesXY = new t[paramInt4][];
    for (int i = 0; i < paramInt4; i++) {
      this.tilesXY[i] = ((t[])Arrays.copyOf(paramArrayOft[i], paramArrayOft[i].length));
    }
    this.waypoints = new int[paramArrayOfInt.length][2];
    for (i = 0; i < paramArrayOfInt.length; i++)
    {
      this.waypoints[i][0] = paramArrayOfInt[i][0];
      this.waypoints[i][1] = paramArrayOfInt[i][1];
    }
    this.startingDirection = parami;
  }
  
  public int getTick()
  {
    return this.tick;
  }
  
  public int getTickCount()
  {
    return this.tickCount;
  }
  
  public int getLastTickIndex()
  {
    return this.lastTickIndex;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public o[] getPlayers()
  {
    return (o[])Arrays.copyOf(this.players, this.players.length);
  }
  
  public c[] getCars()
  {
    return (c[])Arrays.copyOf(this.cars, this.cars.length);
  }
  
  public q[] getProjectiles()
  {
    return (q[])Arrays.copyOf(this.projectiles, this.projectiles.length);
  }
  
  public a[] getBonuses()
  {
    return (a[])Arrays.copyOf(this.bonuses, this.bonuses.length);
  }
  
  public n[] getOilSlicks()
  {
    return (n[])Arrays.copyOf(this.oilSlicks, this.oilSlicks.length);
  }
  
  public String getMapName()
  {
    return this.mapName;
  }
  
  public t[][] getTilesXY()
  {
    t[][] arrayOft = new t[this.tilesXY.length][];
    for (int i = 0; i < this.tilesXY.length; i++) {
      arrayOft[i] = ((t[])Arrays.copyOf(this.tilesXY[i], this.tilesXY[i].length));
    }
    return arrayOft;
  }
  
  public int[][] getWaypoints()
  {
    int[][] arrayOfInt = new int[this.waypoints.length][2];
    for (int i = 0; i < this.waypoints.length; i++)
    {
      arrayOfInt[i][0] = this.waypoints[i][0];
      arrayOfInt[i][1] = this.waypoints[i][1];
    }
    return arrayOfInt;
  }
  
  public i getStartingDirection()
  {
    return this.startingDirection;
  }
  
  public o getMyPlayer()
  {
    for (int i = this.players.length - 1; i >= 0; i--)
    {
      o localo = this.players[i];
      if (localo.isMe()) {
        return localo;
      }
    }
    return null;
  }
  
  public o[] getPlayersUnsafe()
  {
    return this.players;
  }
  
  public c[] getCarsUnsafe()
  {
    return this.cars;
  }
  
  public q[] getProjectilesUnsafe()
  {
    return this.projectiles;
  }
  
  public a[] getBonusesUnsafe()
  {
    return this.bonuses;
  }
  
  public n[] getOilSlicksUnsafe()
  {
    return this.oilSlicks;
  }
  
  public t[][] getTilesXYUnsafe()
  {
    return this.tilesXY;
  }
  
  public int[][] getWaypointsUnsafe()
  {
    return this.waypoints;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\v.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */