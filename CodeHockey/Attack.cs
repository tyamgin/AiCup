using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public ArrayList WayPoints;

        void FillWayPoints()
        {
            WayPoints = new ArrayList
            {
                new Point(620, 242),
                new Point(286, 345),
                new Point(917, 350),
                new Point(443, 278),
                new Point(785, 289)
            };
            var len = WayPoints.Count;
            for (var i = 0; i < len; i++)
            {
                var a = WayPoints[i] as Point;
                WayPoints.Add(new Point(a.X, Game.RinkBottom - (a.Y - Game.RinkTop)));
            }
        }

        public void MoveTo(AHo ho, Point p)
        {
            var turn = ho.GetAngleTo(p);
            var speedUp = GetSpeedTo(turn);
            ho.Move(speedUp, TurnNorm(turn));
        }

        public Point FindWayPoint(Hockeyist self)
        {
            double OkDist = 5*HoRadius;

            var bestTime = Inf;
            Point sel = null;
            foreach (Point p in WayPoints)
            {
                var I = new AHo(self);
                if (p.GetDistanceTo2(I) <= OkDist*OkDist || MyRight() && I.X < p.X || MyLeft() && I.X > p.X)
                    continue;

                var cands = World.Hockeyists
                    .Where(x => x.Type != HockeyistType.Goalie && !x.IsTeammate && x.State == HockeyistState.Active)
                    .Select(x => new AHo(x)).ToArray();                

                int time = 0;
                bool ok = true;
                while (p.GetDistanceTo2(I) > OkDist*OkDist && ok)
                {
                    MoveTo(I, p);
                    foreach (var c in cands)
                    {
                        MoveTo(c, I);
                        if (CanStrike(c, I.PuckPos()) || I.GetDistanceTo2(c) <= 2*HoRadius*2*HoRadius)
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

        double StrikeProbability(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker, Point goalie)
        {
            double range = Game.StrikeAngleDeviation * 2,
                dx = Game.StrikeAngleDeviation / 20,
                result = 0;

            for (double L = -range; L + dx <= range; L += dx)
            {
                double x = L + dx;
                if (Strike(puckPos, strikerSpeed, StrikePower, AngleStriker + x, goalie))
                    result += dx * Gauss(x, 0, Game.StrikeAngleDeviation);
            }
            return result;
        }

        bool Strike(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker, Point goalie)
        {
            if (Math.Abs(puckPos.X - Opp.NetFront) > RinkWidth / 2)
                return false;

            // TODO: временный костыль
            if (Math.Abs(puckPos.X - Opp.NetFront) < 2*HoRadius)
                return false;

            if (MyRight() && Math.Cos(AngleStriker) > 0)
                return false;
            if (MyLeft() && Math.Cos(AngleStriker) < 0)
                return false;

            var strikerDirection = new Point(AngleStriker);
            var SpeedStriker = strikerSpeed.Length;
            var SpeedAngleStriker = strikerSpeed.GetAngle();
            var puckSpeed = 20.0 * StrikePower + SpeedStriker * Math.Cos(AngleStriker - SpeedAngleStriker);
            var puckSpeedDirection = strikerDirection * puckSpeed;
            var pk = new APuck(puckPos, puckSpeedDirection, goalie);
            return pk.Move(300, true) == 1;
        }

        double ProbabStrikeAfter(int wait, int swingTime, Hockeyist self, IEnumerable<Tuple<int, double, double>> move)
        {
            var power = GetPower(swingTime);
            var I = new AHo(self);
            var totalTime = 0;
            var opps = World.Hockeyists.Where(
                x => x.PlayerId == Opp.Id
                     && x.State == HockeyistState.Active
                     && x.Type != HockeyistType.Goalie
                ).Select(x => new AHo(x)).ToArray();

            foreach (var action in move)
            {
                for (int i = 0; i < action.First; i++)
                {
                    I.Move(action.Second, action.Third);
                    foreach (var opp in opps)
                    {
                        opp.Move(1, TurnNorm(opp.GetAngleTo(I)));
                        if (CanStrike(opp, I) || CanStrike(opp, GetPuckPos(I, I.Angle)))
                            return 0.0;
                    }
                }
                totalTime += action.First;
            }
            var pk = GetPuckPos(I, I.Angle);
            var goalie = Get(OppGoalie);
            GoalieMove(goalie, totalTime, pk);
            return StrikeProbability(pk, I.Speed, power, I.Angle, goalie);
        }
    }
}