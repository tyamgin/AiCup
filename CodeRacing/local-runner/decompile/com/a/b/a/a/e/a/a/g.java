package com.a.b.a.a.e.a.a;

import com.a.b.a.a.a.b;
import com.a.b.a.a.c.a;
import com.a.b.a.a.c.c;
import com.a.b.a.a.c.e;
import com.a.b.a.a.c.m;
import com.a.b.a.a.c.n;
import com.a.b.a.a.c.o;
import com.a.b.a.a.c.p;
import com.a.b.a.a.c.q;
import com.a.b.a.a.c.s;
import com.a.b.a.a.c.u;
import com.a.b.a.a.c.v;
import java.io.File;

public class g
  extends f
{
  private final f.a a = new h(this);
  private final f.a b = new i(this);
  private final f.a c = new j(this);
  private final f.a d = new k(this);
  private final f.a e = new l(this);
  private int f;
  
  public g(b paramb, File paramFile)
  {
    super(paramb, paramFile);
  }
  
  public String b()
  {
    a((a)a(a.class), a.c);
    return g();
  }
  
  public void b(int paramInt)
  {
    a(a.d);
    c(paramInt);
    l();
  }
  
  public int c()
  {
    a((a)a(a.class), a.e);
    return this.f = i();
  }
  
  public void a(com.a.b.a.a.c.l paraml)
  {
    a(a.f);
    if (paraml == null)
    {
      a(false);
      return;
    }
    a(true);
    a(paraml.getRandomSeed());
    c(paraml.getTickCount());
    c(paraml.getWorldWidth());
    c(paraml.getWorldHeight());
    a(paraml.getTrackTileSize());
    a(paraml.getTrackTileMargin());
    c(paraml.getLapCount());
    c(paraml.getLapTickCount());
    c(paraml.getInitialFreezeDurationTicks());
    a(paraml.getBurningTimeDurationFactor());
    a(paraml.getFinishTrackScores(), true);
    c(paraml.getFinishLapScore());
    a(paraml.getLapWaypointsSummaryScoreFactor());
    a(paraml.getCarDamageScoreFactor());
    c(paraml.getCarEliminationScore());
    a(paraml.getCarWidth());
    a(paraml.getCarHeight());
    a(paraml.getCarEnginePowerChangePerTick());
    a(paraml.getCarWheelTurnChangePerTick());
    a(paraml.getCarAngularSpeedFactor());
    a(paraml.getCarMovementAirFrictionFactor());
    a(paraml.getCarRotationAirFrictionFactor());
    a(paraml.getCarLengthwiseMovementFrictionFactor());
    a(paraml.getCarCrosswiseMovementFrictionFactor());
    a(paraml.getCarRotationFrictionFactor());
    c(paraml.getThrowProjectileCooldownTicks());
    c(paraml.getUseNitroCooldownTicks());
    c(paraml.getSpillOilCooldownTicks());
    a(paraml.getNitroEnginePowerFactor());
    c(paraml.getNitroDurationTicks());
    c(paraml.getCarReactivationTimeTicks());
    a(paraml.getBuggyMass());
    a(paraml.getBuggyEngineForwardPower());
    a(paraml.getBuggyEngineRearPower());
    a(paraml.getJeepMass());
    a(paraml.getJeepEngineForwardPower());
    a(paraml.getJeepEngineRearPower());
    a(paraml.getBonusSize());
    a(paraml.getBonusMass());
    c(paraml.getPureScoreAmount());
    a(paraml.getWasherRadius());
    a(paraml.getWasherMass());
    a(paraml.getWasherInitialSpeed());
    a(paraml.getWasherDamage());
    a(paraml.getSideWasherAngle());
    a(paraml.getTireRadius());
    a(paraml.getTireMass());
    a(paraml.getTireInitialSpeed());
    a(paraml.getTireDamageFactor());
    a(paraml.getTireDisappearSpeedFactor());
    a(paraml.getOilSlickInitialRange());
    a(paraml.getOilSlickRadius());
    c(paraml.getOilSlickLifetime());
    c(paraml.getMaxOiledStateDurationTicks());
    l();
  }
  
  public void a(p paramp, boolean paramBoolean)
  {
    a(a.g);
    if (paramp == null)
    {
      a(false);
      return;
    }
    a(true);
    a(paramp.getCars());
    a(paramp.getWorld(), paramBoolean);
    l();
  }
  
  public m[] d()
  {
    a((a)a(a.class), a.h);
    int i = i();
    if (i < 0) {
      return null;
    }
    m[] arrayOfm = new m[i];
    for (int j = 0; j < i; j++) {
      if (h())
      {
        m localm = new m();
        arrayOfm[j] = localm;
        localm.setEnginePower(k());
        localm.setBrake(h());
        localm.setWheelTurn(k());
        localm.setThrowProjectile(h());
        localm.setUseNitro(h());
        localm.setSpillOil(h());
      }
    }
    return arrayOfm;
  }
  
  public void e()
  {
    try
    {
      a(a.b);
      l();
    }
    catch (RuntimeException localRuntimeException) {}
  }
  
  private void a(v paramv, boolean paramBoolean)
  {
    if (paramv == null)
    {
      a(false);
      return;
    }
    a(true);
    c(paramv.getTick());
    c(paramv.getTickCount());
    c(paramv.getLastTickIndex());
    c(paramv.getWidth());
    c(paramv.getHeight());
    a(paramv.getPlayersUnsafe());
    a(paramv.getCarsUnsafe());
    a(paramv.getProjectilesUnsafe());
    a(paramv.getBonusesUnsafe());
    a(paramv.getOilSlicksUnsafe());
    if (paramBoolean)
    {
      a(paramv.getMapName());
      a(paramv.getTilesXYUnsafe(), true);
      a(paramv.getWaypointsUnsafe(), true);
      a(paramv.getStartingDirection());
    }
  }
  
  private void a(o[] paramArrayOfo)
  {
    a(paramArrayOfo, this.a);
  }
  
  private void a(o paramo)
  {
    if (paramo == null)
    {
      a(false);
    }
    else
    {
      a(true);
      a(paramo.getId());
      a(paramo.isMe());
      a(paramo.getName());
      a(paramo.isStrategyCrashed());
      c(paramo.getScore());
    }
  }
  
  private void a(c[] paramArrayOfc)
  {
    a(paramArrayOfc, this.b);
  }
  
  private void a(c paramc)
  {
    if (paramc == null)
    {
      a(false);
    }
    else
    {
      a(true);
      a(paramc);
      a(paramc.getPlayerId());
      c(paramc.getTeammateIndex());
      a(paramc.isTeammate());
      a(paramc.getType());
      c(paramc.getProjectileCount());
      c(paramc.getNitroChargeCount());
      c(paramc.getOilCanisterCount());
      c(paramc.getRemainingProjectileCooldownTicks());
      c(paramc.getRemainingNitroCooldownTicks());
      c(paramc.getRemainingOilCooldownTicks());
      c(paramc.getRemainingNitroTicks());
      c(paramc.getRemainingOiledTicks());
      a(paramc.getDurability());
      a(paramc.getEnginePower());
      a(paramc.getWheelTurn());
      c(paramc.getNextWaypointX());
      c(paramc.getNextWaypointY());
      a(paramc.isFinishedTrack());
    }
  }
  
  private void a(q[] paramArrayOfq)
  {
    a(paramArrayOfq, this.c);
  }
  
  private void a(q paramq)
  {
    if (paramq == null)
    {
      a(false);
    }
    else
    {
      a(true);
      a(paramq);
      a(paramq.getCarId());
      a(paramq.getPlayerId());
      a(paramq.getType());
    }
  }
  
  private void a(a[] paramArrayOfa)
  {
    a(paramArrayOfa, this.d);
  }
  
  private void a(a parama)
  {
    if (parama == null)
    {
      a(false);
    }
    else
    {
      a(true);
      a(parama);
      a(parama.getType());
    }
  }
  
  private void a(n[] paramArrayOfn)
  {
    a(paramArrayOfn, this.e);
  }
  
  private void a(n paramn)
  {
    if (paramn == null)
    {
      a(false);
    }
    else
    {
      a(true);
      a(paramn);
      c(paramn.getRemainingLifetime());
    }
  }
  
  private void a(e parame)
  {
    a(parame);
    a(parame.getRadius());
  }
  
  private void a(s params)
  {
    a(params);
    a(params.getWidth());
    a(params.getHeight());
  }
  
  private void a(u paramu)
  {
    a(paramu.getId());
    a(paramu.getMass());
    a(paramu.getX());
    a(paramu.getY());
    a(paramu.getSpeedX());
    a(paramu.getSpeedY());
    a(paramu.getAngle());
    a(paramu.getAngularSpeed());
  }
  
  private static void a(a parama1, a parama2)
  {
    if (parama1 != parama2) {
      throw new com.a.b.a.a.e.a.g(String.format("Received wrong message [actual=%s, expected=%s].", new Object[] { parama1, parama2 }));
    }
  }
  
  private static enum a
  {
    a,  b,  c,  d,  e,  f,  g,  h;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\a\g.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */