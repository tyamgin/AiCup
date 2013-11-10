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
        void Reached(Point p)
        {
            if (BonusGoal != null && Equal(BonusGoal, p))
                BonusGoal = null;
            if (PointGoal != null && Equal(PointGoal, p))
                PointGoal = null;
            if (BonusGoal != null)
                return;
            SelectNewPointGoal();
        }

        void SelectNewPointGoal()
        {
            if (canMakeQuery())
            {
                
            }
            else
            {
                ArrayList places = new ArrayList();
                for (int x = 1; x < width; x += width - 3)
                    for (int y = 1; y < height; y += height - 3)
                        if (PointGoal == null || PointGoal.GetDistanceTo(x, y) > height / 3)
                            places.Add(new Point(x, y));
                var pl = places.ToArray();
                PointGoal = pl[random.Next(places.Count)] as Point;
            }
        }

        Point IfNothingCommander()
        {
            if (Goal == null)
                SelectNewGoal();
            return Goal != null ? Goal : new Point(self.X, self.Y);
        }

        bool canShootSomeone(Point position)
        {
            foreach (Trooper tr in opponents)
                if (world.IsVisible(self.ShootingRange, position.X, position.Y, self.Stance, tr.X, tr.Y, tr.Stance))
                    return true;
            return false;
        }

        Point GoToEncircling(Trooper center, bool needShootingPosition = false)
        {
            Point bestPoint = Point.Inf;
            foreach (Point n in getEncirclingPoints(center))
            {
                double quality = 1.0 / getShoterPath(self, new Point(n.X, n.Y), map, false);
                if (quality > bestPoint.profit && (!needShootingPosition || canShootSomeone(n)))
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
