using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

// TODO:!!! радиус зависит от плотности местности
// TODO: если я выигрываю и остался 1 противник, то залечь
// TODO: ходить за тимлидом, чтобы не обходить препятствия - искать кратчайший путь без учета юнитов
// TODO: поле опастности

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point Goal = null;

        void Reached(Point p)
        {
            if (Goal != null && Goal.X == p.X && Goal.Y == p.Y)
                Goal = null;
            if (Goal == null)
                SelectNewGoal();
        }

        void SelectNewGoal()
        {
            ArrayList places = new ArrayList();
            for (int x = 1; x < width; x += width - 3)
                for (int y = 1; y < height; y += height - 3)
                    if (Goal == null || Goal.GetDistanceTo(x, y) > height / 3)
                        places.Add(new Point(x, y));
            var pl = places.ToArray();
            Goal = pl[random.Next(places.Count)] as Point;
        }

        Point IfNothingCommander()
        {
            if (Goal == null || self.GetDistanceTo(Goal.X, Goal.Y) < height / 4)
                SelectNewGoal();
            return Goal != null ? Goal : new Point(self.X, self.Y);
        }

        Point GoToEncircling(Trooper center)
        {
            Point bestPoint = Point.Inf;
            foreach (Point n in getEncirclingPoints(center))
            {
                double quality = 1.0 / getShoterPath(new Point(n.X, n.Y), false);
                if (quality > bestPoint.profit)
                    bestPoint = new Point(n.X, n.Y, quality);
            }
            if (bestPoint.profit <= 0)
                bestPoint = null;
            return bestPoint;
        }

        Point IfNothing()
        {
            if (commander.Id == self.Id)
                return IfNothingCommander();
            return GoToEncircling(commander);
        }
    }
}
