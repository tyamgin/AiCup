using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        double getTeamRadius(long delId = -1, Point p = null)
        {
            double radius = Inf;
            for (int i = 0; i < Width; i++)
            {
                for (int j = 0; j < Height; j++)
                {
                    double maxV = p == null ? -Inf : p.GetDistanceTo(i, j);
                    foreach(Trooper tr in Team)
                        if (delId != tr.Id)
                            maxV = Math.Max(maxV, tr.GetDistanceTo(i, j));
                    radius = Math.Min(radius, maxV);
                }
            }
            return radius;
        }
    }
}
