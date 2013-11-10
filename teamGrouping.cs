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
            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    double maxV = p == null ? -Inf : p.GetDistanceTo(i, j);
                    foreach(Trooper tr in team)
                        if (delId != tr.Id)
                            maxV = Math.Max(maxV, tr.GetDistanceTo(i, j));
                    radius = Math.Min(radius, maxV);
                }
            }
            return radius;
        }

        bool isAdjanced(Trooper a, Trooper b)
        {
            return getShoterPath(a, b, notFilledMap, true) <= 2;
        }

        HashSet<long> used = new HashSet<long>();
        HashSet<Point> points = new HashSet<Point>();

        void dfs_getEncircling(Trooper trooper)
        {
            used.Add(trooper.Id);
            foreach (Point p in Nearest(trooper, map))
                points.Add(p);
            foreach(Trooper tr in team)
            {
                if (!used.Contains(tr.Id) && isAdjanced(trooper, tr))
                {
                    used.Add(tr.Id);
                    dfs_getEncircling(tr);
                }
            }
        }

        Point[] getEncirclingPoints(Trooper trooper, bool extended = false)
        {
            points.Clear();
            used.Clear();
            if (extended)
            {
                _i = new int[] { 0, 0, 1, -1, 1, 1, -1, -1 };
                _j = new int[] { 1, -1, 0, 0, 1, -1, 1, -1 };
            }
            dfs_getEncircling(trooper);
            if (extended)
            {
                _i = new int[] { 0, 0, 1, -1 };
                _j = new int[] { 1, -1, 0, 0 };
            }
            return points.ToArray();
        }

        Point ifGrouping()
        {
            Point[] points = getEncirclingPoints(commander);
            Point bestPoint = new Point(0, 0, Inf);
            foreach (Point p in points)
            {
                double dist = (double)p.GetDistanceTo(self);
                if (bestPoint.profit > dist && getShoterPath(self, p, map, false) < Inf)
                    bestPoint = new Point(p.X, p.Y, dist);
            }
            if (bestPoint.profit >= Inf - 1)
                return null;
            return bestPoint;
        }
    }
}
