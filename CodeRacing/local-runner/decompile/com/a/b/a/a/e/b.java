package com.a.b.a.a.e;

import com.a.b.a.a.c.c;
import com.a.b.a.a.c.l;
import com.a.b.a.a.c.m;
import com.a.b.a.a.c.v;

public class b
  implements d
{
  public void a(c paramc, v paramv, l paraml, m paramm)
  {
    double d1 = (paramc.getNextWaypointX() + 0.5D) * paraml.getTrackTileSize();
    double d2 = (paramc.getNextWaypointY() + 0.5D) * paraml.getTrackTileSize();
    double d3 = 0.25D * paraml.getTrackTileSize();
    switch (c.a[paramv.getTilesXY()[paramc.getNextWaypointX()][paramc.getNextWaypointY()].ordinal()])
    {
    case 1: 
      d1 += d3;
      d2 += d3;
      break;
    case 2: 
      d1 -= d3;
      d2 += d3;
      break;
    case 3: 
      d1 += d3;
      d2 -= d3;
      break;
    case 4: 
      d1 -= d3;
      d2 -= d3;
      break;
    }
    double d4 = paramc.getAngleTo(d1, d2);
    double d5 = StrictMath.hypot(paramc.getSpeedX(), paramc.getSpeedY());
    if (d5 * d5 * StrictMath.abs(d4) > 19.634954084936208D) {
      paramm.setBrake(true);
    }
    paramm.setWheelTurn(d4 * 32.0D / 3.141592653589793D);
    paramm.setEnginePower(0.75D);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */