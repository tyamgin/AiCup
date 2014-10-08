using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Windows.Forms.PropertyGridInternal;
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
            WayPoints = new ArrayList
            {
                new Point(284, 280),
                new Point(284, 368),
                new Point(444, 255),
                new Point(444, 339),
                new Point(624, 228),
                new Point(624, 328),
            };
            

            var len = WayPoints.Count;
            for (var i = 0; i < len; i++)
            {
                var a = WayPoints[i] as Point;
                WayPoints.Add(new Point(a.X, Game.RinkBottom - (a.Y - Game.RinkTop)));
            }
            len = WayPoints.Count;
            for (var i = 0; i < len; i++)
            {
                var a = WayPoints[i] as Point;
                WayPoints.Add(new Point(Game.RinkRight - (a.X - Game.RinkLeft), a.Y));
            }
        }

        public Point FindWayPoint(Hockeyist self)
        {
            var okDist = 5*HoRadius;

            var bestTime = Inf;
            Point sel = null;
            //TimerStart();
            var bot = World.Hockeyists.Count(x => !x.IsTeammate && IsInGame(x) && x.Y > RinkCenter.Y);
            var top = World.Hockeyists.Count(x => !x.IsTeammate && IsInGame(x) && x.Y <= RinkCenter.Y);
                
            foreach (Point p in WayPoints.ToArray().OrderBy(x => ((Point) x).GetDistanceTo(self)).Take(10))
            {
                var I = new AHock(self);
                if (p.GetDistanceTo2(I) <= okDist*okDist || MyRight() && I.X < p.X || MyLeft() && I.X > p.X)
                    continue;

                var cands = World.Hockeyists
                    .Where(x => !x.IsTeammate && IsInGame(x))
                    .Select(x => new AHock(x)).ToArray();

                var time = 0;
                var ok = true;
                while (p.GetDistanceTo2(I) > okDist*okDist && ok)
                {
                    I.MoveTo(p);
                    foreach (var c in cands)
                    {
                        c.MoveTo(I);
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
                if (ok)
                {
                    if (p.Y > RinkCenter.Y && bot > top || p.Y <= RinkCenter.Y && top > bot)
                        time *= 3;
                    if (time < bestTime)
                    {
                        bestTime = time;
                        sel = p.Clone();
                    }
                }
            }
            //Log("FindWayPoint " + TimerStop());
            return sel;
        }

        bool StrikeWithDev(AHock striker, double strikePower, Point goalie, ActionType actionType, double passAngle, double dev)
        {
            var result = false;
            if (actionType == ActionType.Strike)
                striker.Angle += dev;
            if (Strike(striker, strikePower, goalie, actionType, passAngle + dev))
                result = true;
            if (actionType == ActionType.Strike)
                striker.Angle -= dev;
            return result;
        }

        double StrikeProbability(AHock striker, double strikePower, Point goalie, int leftTime, ActionType actionType, double passAngle)
        {
            if (striker.CoolDown != 0)
                return 0.0;
            if (!Strike(striker, strikePower, goalie, actionType, passAngle))
                return 0.0;

            const int iters = 5;
            var deviation = (actionType == ActionType.Strike ? Game.StrikeAngleDeviation : Game.PassAngleDeviation)*100/striker.ADexterity;
            var range = deviation*2;

            double upL = 0, upR = range;
            for (var it = 0; it < iters; it++)
            {
                var c = (upL + upR)/2;
                if (StrikeWithDev(striker, strikePower, goalie, actionType, passAngle, c))
                    upL = c;
                else
                    upR = c;
            }
            double downL = -range, downR = 0;
            for (var it = 0; it < iters; it++)
            {
                var c = (downL + downR) / 2;
                if (StrikeWithDev(striker, strikePower, goalie, actionType, passAngle, c))
                    downR = c;
                else
                    downL = c;
            }
            double result = GaussIntegral(downL, upR, deviation);

            // Проверка что шайбу перехватят:
            if (leftTime != -1)
            {
                var pk = actionType == ActionType.Strike
                    ? GetStrikePuck(striker, strikePower, goalie)
                    : GetPassPuck(striker, 1, passAngle, goalie);
                var opps = World.Hockeyists
                    .Where(x => !x.IsTeammate && IsInGame(x))
                    .Select(x => new AHock(x)).ToArray();

                pk.Clone().Move(300, true);
                var time = APuck.PuckLastTicks;
                for (var t = -leftTime; t < time; t++)
                {
                    foreach (var opp in opps)
                    {
                        var hisTurn = TurnNorm(opp.GetAngleTo(pk), opp.AAgility);
                        opp.Move(0.0, hisTurn);
                        if (CanStrike(opp, pk))
                        {
                            var pTake = (75.0 + Math.Max(opp.ADexterity, opp.AAgility) -
                                         pk.Speed.Length/20*100)/100;
                            return result*(1 - pTake);
                        }
                    }
                    if (t >= 0)
                        pk.Move(1);
                }
            }
            return result;
        }

        APuck GetStrikePuck(AHock striker, double strikePower, Point goalie)
        {
            var strikerDirection = new Point(striker.Angle);
            var speedAngleStriker = striker.Speed.GetAngle();
            var puckSpeed = 20.0 * strikePower + striker.Speed.Length * Math.Cos(striker.Angle - speedAngleStriker);
            var puckSpeedDirection = strikerDirection * puckSpeed;
            return new APuck(striker.PuckPos(), puckSpeedDirection, goalie);
        }

        private bool StrikePrimitiveValidate(AHock striker)
        {
            var puckPos = striker.PuckPos();
            if (Math.Abs(puckPos.X - Opp.NetFront) > RinkWidth/3*2)
                return false;

            if (Math.Abs(puckPos.X - Opp.NetFront) < 3.5*HoRadius)
                return false;

            if (MyRight() && Math.Cos(striker.Angle) > 0)
                return false;
            if (MyLeft() && Math.Cos(striker.Angle) < 0)
                return false;

            return true;
        }

        bool PuckPrimitiveValidate(APuck pk)
        {
            if (pk.Goalie == null)
                return true;

            if (Math.Abs(pk.X - Opp.NetFront) > RinkWidth/3*2)
                return false;

            if (Math.Abs(pk.X - Opp.NetFront) < 3.0 * HoRadius)
                return false;

            if (MyRight() && pk.Speed.X > 0)
                return false;
            if (MyLeft() && pk.Speed.X < 0)
                return false;

            var dx = Math.Abs(pk.X - Opp.NetFront);
            var isLeft = MyRight();

            return IsBetween(Game.GoalNetTop, pk.Y - (isLeft ? 1 : -1) * dx * pk.Speed.Y / pk.Speed.X,
                Game.GoalNetTop + Game.GoalNetHeight); // летит в ворота
        }

        bool Strike(AHock striker, double strikePower, Point goalie, ActionType actionType, double passAngle)
        {
            var pk = actionType == ActionType.Strike
                ? GetStrikePuck(striker, strikePower, goalie)
                : GetPassPuck(striker, 1, passAngle, goalie);
            return PuckPrimitiveValidate(pk) && pk.Move(300, true) == 1;
        }

        bool Chase(IEnumerable<AHock> opps, AHock I)
        {
            foreach (var opp in opps)
            {
                opp.MoveTo(I);
                if (CanStrike(opp, I) || CanStrike(opp, I.PuckPos()))
                    return false;
            }
            return true;
        }

        double ProbabStrikeAfter(int swingTime, Hockeyist self, IEnumerable<MoveAction> actions, ActionType actionType)
        {
            var I = new AHock(self);

            if (Math.Abs(My.NetFront - I.X) < RinkWidth / 3)
                return 0.0;

            var power = GetPower(I, swingTime);
            var totalTime = 0;
            var opps = World.Hockeyists
                .Where(x => !x.IsTeammate && IsInGame(x))
                .Select(x => new AHock(x))
                .ToArray();

            var goalie = Get(OppGoalie);
            foreach (var action in actions)
            {
                for (var i = 0; i < action.Ticks; i++)
                {
                    GoalieMove(goalie, 1, I.PuckPos());
                    I.Move(action.SpeedUp, action.Turn);
                    if (!Chase(opps, I))
                        return 0.0;
                }
                totalTime += action.Ticks;
            }
            var passAngle = PassAngleNorm(I.GetAngleTo(GetStrikePoint()));
            return StrikeProbability(I, power, goalie, totalTime, actionType, passAngle);
        }

        public static double PassAngleNorm(double angle)
        {
            if (angle > Game.PassSector/2)
                return Game.PassSector/2;
            if (angle < -Game.PassSector/2)
                return -Game.PassSector/2;
            return angle;
        }

        public Point GetStrikePoint()
        {
            const double delta = 1;
            const double shift = 10;
            double x = Opp.NetFront,
                bestDist = 0,
                bestY = 0;
            var OppGoalie = Get(MyStrategy.OppGoalie) ?? Point.Zero;
            for (var y = Opp.NetTop + shift; y <= Opp.NetBottom - shift; y += delta)
            {
                if (OppGoalie.GetDistanceTo2(x, y) > bestDist * bestDist)
                {
                    bestDist = OppGoalie.GetDistanceTo(x, y);
                    bestY = y;
                }
            }
            return new Point(x, bestY);
        }

        public bool TryStrikeWithoutTakeIfSwinging(AHock _hock, APuck _pk)
        {
            var hock = _hock.Clone();
            var pk = _pk.Clone();
            var bestProbab = 0.0;
            var swTime = Inf;

            for (var sw = 0; sw <= Game.MaxEffectiveSwingTicks; sw++)
            {
                if (CanStrike(hock, pk))
                {
                    var pr = StrikeProbability(hock, GetPower(hock, sw + hock.Base.SwingTicks), Get(OppGoalie), -1, ActionType.Strike, 0);
                    if (pr - 0.01 > bestProbab)
                    {
                        bestProbab = pr;
                        swTime = sw;
                    }
                }
                hock.Move(0, 0);
                pk.Move(1);
            }

            if (swTime == Inf)
                return false;
            if (swTime == 0)
                move.Action = ActionType.Strike;
            return true;
        }

        public bool TryStrikeWithoutTake(AHock _hock, APuck _pk)
        {
            if (!StrikePrimitiveValidate(_hock))
                return false;

            TimerStart();

            var moveDir = MyRight() && _hock.Y > RinkCenter.Y || MyLeft() && _hock.Y < RinkCenter.Y ? 1 : -1;

            var bestTurn = 0.0;
            var bestSpUp = 0.0;
            var bestProbab = 0.0;
            var bestWait = Inf;
            var swTime = 0;
            var range = TurnRange(_hock.AAgility);

            const int turns = 9;
            const int spUps = 8;

            var goalie = Get(OppGoalie);
            
            for (var moveTurn = 0.0; moveTurn <= range; moveTurn += range / turns)
            {
                var turn = moveDir*moveTurn;

                for (var spUp = 0.0; spUp <= 1.0; spUp += 1.0/spUps)
                {
                    var hock = _hock.Clone();
                    var pk = _pk.Clone();
                    var ticksWait = 0;
                    for (var startDist2 = hock.GetDistanceTo2(pk); !CanStrike(hock, pk) && ticksWait < 150; ticksWait++)
                    {
                        var I = hock.Clone();
                        var p = pk.Clone();
                        for (var sw = 0; sw <= Game.MaxEffectiveSwingTicks; sw++)
                        {
                            if (sw >= Game.SwingActionCooldownTicks && CanStrike(I, p))
                            {
                                var pr = StrikeProbability(I, GetPower(I, sw), goalie, -1, ActionType.Strike, 0);
                                if (pr > bestProbab)
                                {
                                    bestProbab = pr;
                                    bestTurn = turn;
                                    bestSpUp = spUp;
                                    bestWait = ticksWait;
                                    swTime = sw;
                                }
                            }
                            I.Move(0, 0);
                            p.Move(1);
                        }

                        hock.Move(spUp, turn);
                        pk.Move(1);
                        var dist2 = hock.GetDistanceTo2(pk);
                        if (dist2 > startDist2)
                            break;
                        startDist2 = dist2;
                    }
                    if (CanStrike(hock, pk))
                    {
                        var p = StrikeProbability(hock, GetPower(hock, 0), goalie, -1, ActionType.Strike, 0);
                        if (p > bestProbab)
                        {
                            bestProbab = p;
                            bestTurn = turn;
                            bestSpUp = spUp;
                            bestWait = ticksWait;
                            swTime = 0;
                        }
                    }
                }
            }
            Log("SW " + TimerStop());

            if (bestWait == Inf)
                return false;
            move.Turn = bestTurn;
            move.SpeedUp = bestSpUp;

            if (bestWait == 0)
                move.Action = swTime == 0 ? ActionType.Strike : ActionType.Swing;
            return true;
        }
    }
}