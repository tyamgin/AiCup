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
        void Reached(Point p)
        {
            if (BonusGoal != null && Equal(BonusGoal, p) && GetBonusAt(p) == null)
                BonusGoal = null;
            if (PointGoal != null && Equal(PointGoal, p))
                PointGoal = null;
            if (BonusGoal != null || PointGoal != null)
                return;
            SelectNewPointGoal();
        }

        bool IsCanMakeQuery()
        {
            if (self.Type != TrooperType.Commander || self.ActionPoints < game.CommanderRequestEnemyDispositionCost)
                return false;
            if (world.MoveIndex < 3) // TODO: Только для первого типа боя
                return false;
            return true;
        }

        bool IfMakeQuery()
        {
            if (BonusGoal == null && (PointGoal == null || world.MoveIndex - PointGoal.profit > 4) && IsCanMakeQuery())
                return true;
            return false;
        }

        void SelectNewPointGoal()
        {
            if (!IsCanMakeQuery())
            {
                ArrayList places = new ArrayList();
#if DEBUG
                places.Add(new Point(Width - 1, 1));
#else
                for (int x = 1; x < Width; x += Width - 3)
                    for (int y = 1; y < Height; y += Height - 3)
                        if (PointGoal == null || PointGoal.GetDistanceTo(x, y) > Height / 3)
                            places.Add(new Point(x, y));
#endif
                var pl = places.ToArray();
                PointGoal = pl[random.Next(places.Count)] as Point;
                PointGoal.profit = world.MoveIndex;
            }
        }

        bool IsApproximationExist()
        {
            return world.Players.Any(
                player => player.ApproximateX != -1
            );
        }

        Point GetCoordinateByApproximation(int x, int y)
        {
            var nearestPoint = new Point(0, 0, Inf);
            for (var i = 0; i < Width; i++)
                for (var j = 0; j < Height; j++)
                    if (notFilledMap[i, j] == 0 && new Point(x, y).GetDistanceTo(i, j) < nearestPoint.profit)
                        nearestPoint = new Point(i, j, new Point(x, y).GetDistanceTo(i, j));
            return nearestPoint;            
        }

        void ProcessApproximation()
        {
            if (!IsApproximationExist())
                return;
            AlivePlayers = new ArrayList();
            var nearestPoint = new Point(0, 0, Inf);
            foreach(var player in world.Players)
            {
                if (player.ApproximateX != -1)
                {
                    AlivePlayers.Add(player);
                    if (player.Id != self.PlayerId)
                    {
                        var coordinate = GetCoordinateByApproximation(player.ApproximateX, player.ApproximateY);
                        var path = GetShoterPath(commander, coordinate, map, beginFree: true, endFree: true);
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

        int HowManyCanShoot(Point position, TrooperStance stance)
        {
            return Opponents.Count(
                trooper => world.IsVisible(GetShootingRange(self, self.Stance), position.X, position.Y, stance, trooper.X, trooper.Y, trooper.Stance)
            );
        }

        Point GoToEncircling(Trooper center, Point goal, bool needShootingPosition)
        {
            var bestPoint = new Point(0, 0, Inf);
            double optDanger = self.Type == TrooperType.FieldMedic || self.Type == TrooperType.Sniper ? Inf : -Inf;
            for (var i = 0; i < Width; i++)
            {   
                for (var j = 0; j < Height; j++)
                {
                    if (map[i, j] == 0 || i == self.X && j == self.Y)
                    {
                        if (self.GetDistanceTo(i, j) > 10) // немного ускорить
                            continue;
                        if (needShootingPosition && HowManyCanShoot(new Point(i, j), self.Stance) == 0)
                            continue;
                        // Нужно чтобы хватило ходов
                        int steps = GetShoterPath(self, new Point(i, j), map, beginFree: true, endFree: true);
                        if (self.ActionPoints / GetMoveCost() >= steps)
                        {
                            // и чтобы не закрывали кратчайший путь:
                            int before = goal == null ? Inf : GetShoterPath(center, goal, map, beginFree:true, endFree: false);
                            map[self.X, self.Y] = 0;
                            map[i, j] = 1;
                            int after = goal == null ? Inf : GetShoterPath(center, goal, map, beginFree: true, endFree: false);
                            map[i, j] = 0;
                            map[self.X, self.Y] = 1;

                            if ((goal == null || after < Inf) && after <= before)
                            {
                                double sum = GetShoterPath(center, new Point(i, j), notFilledMap, beginFree: true, endFree: true);
                                double dang = danger[i, j] + (goal == null ? 0 : goal.GetDistanceTo(i, j) * 0.01);
                                if (sum < bestPoint.profit || sum == bestPoint.profit && (self.Type == TrooperType.FieldMedic || self.Type == TrooperType.Sniper ? (dang < optDanger) : ((dang > optDanger))))
                                {
                                    bestPoint = new Point(i, j, sum);
                                    optDanger = dang;
                                }
                            }
                        }
                    }
                }
            }
            if (bestPoint.profit >= Inf)
            {
                if (needShootingPosition)
                    return GoToEncircling(center, goal, false);
                return null;
            }
            return bestPoint;
        }

        Point IfNothing()
        {
            if (commander.Id == self.Id)
                return IfNothingCommander();
            return GoToEncircling(commander, PointGoal, needShootingPosition: false);
        }
    }
}
