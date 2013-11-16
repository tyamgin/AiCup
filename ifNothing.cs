using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

// TODO:!!! радиус зависит от плотности местности
// нужно-ли?TODO: если я выигрываю и остался 1 противник, то залечь

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        void Reached(Point p)
        {
            if (BonusGoal != null && Equal(BonusGoal, p) && getBonusAt(p) == null)
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
            if (BonusGoal == null && (PointGoal == null || world.MoveIndex - PointGoal.profit > 4) && canMakeQuery())
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

        //bool IWin()
        //{
        //    if (alivePlayers.Count > 2)
        //        return false;
        //    int myScore = 0;
        //    int hisScore = 0;
        //    foreach(Player pl in world.Players)
        //    {
        //        if (pl.Id == self.PlayerId)
        //            myScore = pl.Score;
        //        else
        //            hisScore = Math.Max(hisScore, pl.Score);
        //    }
        //    return myScore > hisScore;
        //}

        void ProcessApproximation()
        {
            if (!isApproximationExist())
                return;
            alivePlayers = new ArrayList();
            Point nearestPoint = new Point(0, 0, Inf);
            foreach(Player pl in world.Players)
            {
                if (pl.ApproximateX != -1)
                {
                    alivePlayers.Add(pl);
                    if (pl.Id != self.PlayerId)
                    {
                        Point coordinate = getCoordinateByApproximation(pl.ApproximateX, pl.ApproximateY);
                        int path = getShoterPath(commander, coordinate, map, beginFree: true, endFree: true);
                        if (path < nearestPoint.profit)
                            nearestPoint = new Point(coordinate.X, coordinate.Y, path);
                    }
                }
            }
            if (nearestPoint.profit >= Inf)
                world = world;
            PointGoal = nearestPoint;
            PointGoal.profit = world.MoveIndex;
        }

        Point IfNothingCommander()
        {
            if (BonusGoal != null)
                return BonusGoal;
            if (PointGoal != null)
                return PointGoal;
            return null;
        }

        bool canShootSomeone(Point position, TrooperStance stance)
        {
            foreach (Trooper tr in opponents)
                if (world.IsVisible(self.ShootingRange, position.X, position.Y, stance, tr.X, tr.Y, tr.Stance))
                    return true;
            return false;
        }

        Point GoToEncircling(Trooper center, Point goal, bool needShootingPosition = false)
        {
            Point bestPoint = new Point(0, 0, Inf);
            for (int i = 0; i < width; i++)
            {   
                for (int j = 0; j < height; j++)
                {
                    if (map[i, j] == 0 || i == self.X && j == self.Y)
                    {
                        if (self.GetDistanceTo(i, j) > 10) // немного ускорить
                            continue;
                        if (needShootingPosition && !canShootSomeone(new Point(i, j), self.Stance))
                            continue;
                        // Нужно чтобы хватило ходов
                        int steps = getShoterPath(self, new Point(i, j), map, beginFree: true, endFree: true);
                        if (self.ActionPoints / getMoveCost() >= steps)
                        {
                            // и чтобы не закрывали кратчайший путь:
                            int before = goal == null ? Inf : getShoterPath(center, goal, map, beginFree:true, endFree: false);
                            map[i, j] = 1;
                            map[self.X, self.Y] = 0;
                            int after = goal == null ? Inf : getShoterPath(center, goal, map, beginFree: true, endFree: false);
                            map[i, j] = 0;
                            map[self.X, self.Y] = 1;

                            if (after <= before)
                            {
                                double sum = getShoterPath(center, new Point(i, j), notFilledMap, beginFree: true, endFree: true);
                                if (sum < bestPoint.profit)
                                    bestPoint = new Point(i, j, sum);
                            }
                        }
                    }
                }
            }
            if (bestPoint.profit >= Inf)
                return null;
            return bestPoint;
        }

        Point IfNothing()
        {
            if (commander.Id == self.Id)
                return IfNothingCommander();
            return GoToEncircling(commander, PointGoal);
        }
    }
}
