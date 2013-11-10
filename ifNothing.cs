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
            if (BonusGoal != null || PointGoal != null)
                return;
            SelectNewPointGoal();
        }

        bool canMakeQuery()
        {
            if (self.Type != TrooperType.Commander || self.ActionPoints < game.CommanderRequestEnemyDispositionCost)
                return false;
            if (world.MoveIndex < 3) // TODO: Только для первого типа боя
                return false;
            return true;
        }

        bool IfMakeQuery()
        {
            if (BonusGoal == null && (PointGoal == null || world.MoveIndex - PointGoal.profit > 6) && canMakeQuery())
                return true;
            return false;
        }

        void SelectNewPointGoal()
        {
            if (!canMakeQuery())
            {
                ArrayList places = new ArrayList();
                for (int x = 1; x < width; x += width - 3)
                    for (int y = 1; y < height; y += height - 3)
                        if (PointGoal == null || PointGoal.GetDistanceTo(x, y) > height / 3)
                            places.Add(new Point(x, y));
                var pl = places.ToArray();
                PointGoal = pl[random.Next(places.Count)] as Point;
                PointGoal.profit = world.MoveIndex;
            }
        }

        bool isApproximationExist()
        {
            foreach (Player pl in world.Players)
                if (pl.ApproximateX != -1)
                    return true;
            return false;
        }

        Point getCoordinateByApproximation(int x, int y)
        {
            Point nearestPoint = new Point(0, 0, Inf);
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    if (notFilledMap[i, j] == 0 && new Point(x, y).GetDistanceTo(i, j) < nearestPoint.profit)
                        nearestPoint = new Point(i, j, new Point(x, y).GetDistanceTo(i, j));
            return nearestPoint;            
        }

        void ProcessApproximation()
        {
            if (!isApproximationExist())
                return;
            playersCount = 0;
            Point nearestPoint = new Point(0, 0, Inf);
            foreach(Player pl in world.Players)
            {
                if (pl.ApproximateX != -1)
                {
                    playersCount++;
                    if (pl.Id != self.PlayerId)
                    {
                        Point coordinate = getCoordinateByApproximation(pl.ApproximateX, pl.ApproximateY);
                        int path = getShoterPath(commander, coordinate, map, true);
                        if (path < nearestPoint.profit)
                            nearestPoint = new Point(coordinate.X, coordinate.Y, path);
                    }
                }
            }
            if (nearestPoint.profit >= Inf)
                throw new Exception("");
            PointGoal = nearestPoint;
            PointGoal.profit = world.MoveIndex;
        }

        Point IfNothingCommander()
        {
            if (BonusGoal != null)
                return new Point(BonusGoal);
            if (PointGoal != null)
                return PointGoal;
            return null;
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
