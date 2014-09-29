using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Windows.Forms.PropertyGridInternal;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public ArrayList WayPoints;

        public int GetWayPointIndex(Point wayPoint)
        {
            for (var i = 0; i < WayPoints.Count; i++)
                if (wayPoint.Same(WayPoints[i] as Point))
                    return i;
            throw new Exception("Unknown waypoint");
        }

        public int GetWayPointPriority(Point wayPoint)
        {
            return GetWayPointIndex(wayPoint)%(WayPoints.Count/2);
        }

        Point GetNextWayPoint(Point wayPoint)
        {
            var size = WayPoints.Count / 2;
            var i = GetWayPointIndex(wayPoint);
            if (i == size - 1 || i == 2*size - 1)
                return null;
            return WayPoints[i + 1] as Point;
        }

        void FillWayPoints()
        {
            WayPoints = new ArrayList
            {
                //new Point(172, 438),
                //new Point(224, 384),
                //new Point(281, 339),
                //new Point(356, 308),
                //new Point(438, 284),
                //new Point(527, 255),
                //new Point(620, 246),
                //new Point(711, 263),
                //new Point(790, 291),
                //new Point(860, 325),
                //new Point(925, 353),
                //new Point(1002, 388),
                //new Point(1050, 446),
                
                new Point(286, 345),
                new Point(443, 278),
                new Point(620, 242),
                new Point(785, 289),
                new Point(917, 350),
            };
            if (MyRight())
                WayPoints.Reverse();

            var len = WayPoints.Count;
            for (var i = 0; i < len; i++)
            {
                var a = WayPoints[i] as Point;
                WayPoints.Add(new Point(a.X, Game.RinkBottom - (a.Y - Game.RinkTop)));
            }
        }

        public void MoveTo(AHock hock, Point p)
        {
            var turn = hock.GetAngleTo(p);
            var speedUp = GetSpeedTo(turn);
            hock.Move(speedUp, TurnNorm(turn, hock.BaseParams.Agility));
        }

        public Point _FindWayPoint(Hockeyist self)
        {
            double OkDist = 4 * HoRadius;

            var bestTime = Inf;
            Point sel = null;

            foreach (Point point in WayPoints)
            {
                var I = new AHock(self);
                if (I.GetDistanceTo2(point) <= OkDist*OkDist || MyRight() && I.X < point.X || MyLeft() && I.X > point.X)
                    continue;

                var cands = World.Hockeyists
                    .Where(x =>
                        x.Type != HockeyistType.Goalie
                        && !x.IsTeammate
                        && (x.State == HockeyistState.Active || x.State == HockeyistState.KnockedDown)
                    )
                    .Select(x => new AHock(x)).ToArray();

                int time = 0;
                bool ok = true;
                int fTime = -1;
                for (var p = point; p != null && ok; p = GetNextWayPoint(p))
                {
                    while (p.GetDistanceTo2(I) > OkDist * OkDist && ok)
                    {
                        MoveTo(I, p);
                        foreach (var c in cands)
                        {
                            MoveTo(c, I);
                            if (CanStrike(c, I.PuckPos()) // достанет шайбу
                                || CanStrike(c, I) // достанет меня
                                || I.GetDistanceTo2(c) <= 2*HoRadius*2*HoRadius // столкнется со мной
                                )
                            {
                                ok = false;
                                break;
                            }
                        }
                        time++;
                    }
                    if (fTime == -1)
                        fTime = time;
                }
                if (time > 20 || ok)
                {
                    if (fTime < bestTime)
                    {
                        bestTime = fTime;
                        sel = new Point(point);
                    }
                }
            }
            return sel;
        }

        public Point FindWayPoint(Hockeyist self)
        {
            double OkDist = 5 * HoRadius;

            var bestTime = Inf;
            Point sel = null;
            foreach (Point p in WayPoints)
            {
                var I = new AHock(self);
                if (p.GetDistanceTo2(I) <= OkDist * OkDist || MyRight() && I.X < p.X || MyLeft() && I.X > p.X)
                    continue;

                var cands = World.Hockeyists
                    .Where(x =>
                        x.Type != HockeyistType.Goalie
                        && !x.IsTeammate
                        && (x.State == HockeyistState.Active || x.State == HockeyistState.KnockedDown)
                    )
                    .Select(x => new AHock(x)).ToArray();

                int time = 0;
                bool ok = true;
                while (p.GetDistanceTo2(I) > OkDist * OkDist && ok)
                {
                    MoveTo(I, p);
                    foreach (var c in cands)
                    {
                        MoveTo(c, I);
                        if (CanStrike(c, I.PuckPos()) // достанет шайбу
                            || CanStrike(c, I) // достанет меня
                            || I.GetDistanceTo2(c) <= 2 * HoRadius * 2 * HoRadius // столкнется со мной
                            )
                        {
                            ok = false;
                            break;
                        }
                    }
                    time++;
                }
                if (ok)
                {
                    if (time < bestTime)
                    {
                        bestTime = time;
                        sel = new Point(p);
                    }
                }
            }
            return sel;
        }

        double StrikeProbability(Point puckPos, Point strikerSpeed, double strikePower, double dex, double angleStriker, Point goalie, int leftTime)
        {
            var deviation = Game.StrikeAngleDeviation*100/dex;
            double range = deviation * 2,
                dx = deviation / 20,
                result = 0;

            for (var L = -range; L + dx <= range; L += dx)
            {
                var x = L + dx;
                if (Strike(puckPos, strikerSpeed, strikePower, angleStriker + x, goalie))
                    result += dx * Gauss(x, 0, deviation);
            }
            var pk = GetStrikePuck(puckPos, strikerSpeed, strikePower, angleStriker, goalie);

            var opps = World.Hockeyists.Where(
                x => x.PlayerId == Opp.Id
                     && (x.State == HockeyistState.Active || x.State == HockeyistState.KnockedDown)
                     && x.Type != HockeyistType.Goalie
                ).Select(x => new AHock(x)).ToArray();

            pk.Clone().Move(300, true);
            var time = APuck.PuckLastTicks;
            for (int t = -leftTime; t < time; t++)
            {
                foreach (var opp in opps)
                {
                    var hisTurn = TurnNorm(opp.GetAngleTo(pk), opp.BaseParams.Agility);
                    opp.Move(0.0, hisTurn);
                    if (CanStrike(opp, pk))
                    {
                        var pTake = (75.0 + Math.Max(opp.BaseParams.Dexterity, opp.BaseParams.Agility) - pk.Speed.Length/20*100)/100;
                        return result*(1 - pTake);
                    }
                }
                if (t >= 0)
                    pk.Move(1);
            }
            return result;
        }

        APuck GetStrikePuck(Point puckPos, Point strikerSpeed, double strikePower, double angleStriker, Point goalie)
        {
            var strikerDirection = new Point(angleStriker);
            var speedStrikerAbs = strikerSpeed.Length;
            var speedAngleStriker = strikerSpeed.GetAngle();
            var puckSpeed = 20.0 * strikePower + speedStrikerAbs * Math.Cos(angleStriker - speedAngleStriker);
            var puckSpeedDirection = strikerDirection * puckSpeed;
            return new APuck(puckPos, puckSpeedDirection, goalie);
        }

        bool Strike(Point puckPos, Point strikerSpeed, double strikePower, double angleStriker, Point goalie)
        {
            if (Math.Abs(puckPos.X - Opp.NetFront) > RinkWidth / 2)
                return false;

            // TODO: временный костыль
            if (Math.Abs(puckPos.X - Opp.NetFront) < 2*HoRadius)
                return false;

            if (MyRight() && Math.Cos(angleStriker) > 0)
                return false;
            if (MyLeft() && Math.Cos(angleStriker) < 0)
                return false;

            
            var pk = GetStrikePuck(puckPos, strikerSpeed, strikePower, angleStriker, goalie);
            return pk.Move(300, true) == 1;
        }

        double ProbabStrikeAfter(int swingTime, Hockeyist self, IEnumerable<Tuple<int, double, double>> move)
        {
            var power = GetPower(self, swingTime);
            var I = new AHock(self);
            var totalTime = 0;
            var opps = World.Hockeyists.Where(
                x => x.PlayerId == Opp.Id
                     && (x.State == HockeyistState.Active || x.State == HockeyistState.KnockedDown)
                     && x.Type != HockeyistType.Goalie
                ).Select(x => new AHock(x)).ToArray();

            foreach (var action in move)
            {
                for (var i = 0; i < action.First; i++)
                {
                    I.Move(action.Second, action.Third);
                    foreach (var opp in opps)
                    {
                        var hisTurn = TurnNorm(opp.GetAngleTo(I), opp.BaseParams.Agility);
                        opp.Move(GetSpeedTo(hisTurn), hisTurn);
                        if (CanStrike(opp, I) || CanStrike(opp, GetPuckPos(I, I.Angle)))
                            return 0.0;
                    }
                }
                totalTime += action.First;
            }
            var pk = GetPuckPos(I, I.Angle);
            var goalie = Get(OppGoalie);
            GoalieMove(goalie, totalTime, pk);
            return I.CoolDown == 0
                ? StrikeProbability(pk, I.Speed, power, self.Dexterity, I.Angle, goalie, totalTime)
                : 0.0;
        }
        Point AttackPass(Hockeyist self)
        {
            var ho = new AHock(self);
            return null;
        }
    }
}