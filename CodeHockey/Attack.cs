using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms.VisualStyles;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public ArrayList WayPoints;

        void FillWayPoints()
        {
            WayPoints = new ArrayList();
            WayPoints.Add(new Point(620, 242));
            WayPoints.Add(new Point(286, 345));
            WayPoints.Add(new Point(917, 350));
            WayPoints.Add(new Point(443, 278));
            WayPoints.Add(new Point(785, 289));
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
                var I = new AHo(Get(self), GetSpeed(self), self.Angle, self.AngularSpeed, self);
                if (p.GetDistanceTo(I) <= OkDist || MyRight() && I.X < p.X || MyLeft() && I.X > p.X)
                    continue;

                var cands = World.Hockeyists
                    .Where(x => x.Type != HockeyistType.Goalie && !x.IsTeammate && x.State == HockeyistState.Active)
                    .Select(x => new AHo(Get(x), GetSpeed(x), x.Angle, x.AngularSpeed, x)).ToArray();                

                int time = 0;
                bool ok = true;
                while (p.GetDistanceTo(I) > OkDist && ok)
                {
                    MoveTo(I, p);
                    foreach (var c in cands)
                    {
                        MoveTo(c, I);
                        if (CanStrike(c, I.PuckPos()) || I.GetDistanceTo(c) <= 2*HoRadius)
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
    }
}