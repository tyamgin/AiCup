using System;
using System.Linq;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public bool FindPath(Hockeyist self, Point to, Point lookAt)
        {
            if (lookAt == null)
                return StopOn(new AHock(self), to);

            var okDist = HoRadius * 1.5;

            var minTime = Inf;
            var selTurn = 0.0;
            var selSpUp = 0.0;
            for (var dir = -1; dir <= 1; dir += 2)
            {
                var hock = new AHock(self);
                for (var ticksDirect = 0; ticksDirect < 100; ticksDirect++)
                {
                    var curTime = ticksDirect;
                    var ho = hock.Clone();
                    while (Math.Abs(ho.GetAngleTo(lookAt)) > Deg(8))
                    {
                        ho.Move(0, TurnNorm(ho.GetAngleTo(lookAt), ho.AAgility));
                        curTime++;
                    }
                    if (curTime < minTime && ho.GetDistanceTo(to) < okDist)
                    {
                        minTime = curTime;
                        if (ticksDirect == 0)
                        {
                            selSpUp = 0.0;
                            selTurn = TurnNorm(ho.GetAngleTo(lookAt), hock.AAgility);
                        }
                        else if (dir > 0)
                        {
                            selTurn = self.GetAngleTo(to.X, to.Y);
                            selSpUp = GetSpeedTo(selTurn);
                        }
                        else
                        {
                            selTurn = RevAngle(self.GetAngleTo(to.X, to.Y));
                            selSpUp = -GetSpeedTo(selTurn);
                        }
                    }
                    if (dir > 0)
                        GetTicksToUpN(hock, to, 0, 1);
                    else
                        GetTicksToDownN(hock, to, 0, 1);
                }
            }
            move.SpeedUp = selSpUp;
            move.Turn = selTurn;
            return minTime != Inf;
        }

        bool StopOn(AHock _hock, Point to)
        {
            var minTime = Inf;
            var selTurn = 0.0;
            var selSpUp = 0.0;
            for (var dir = -1; dir <= 1; dir += 2)
            {
                var hock = _hock.Clone();
                for (var ticksDirect = 0; ticksDirect < 100; ticksDirect++)
                {
                    var curTime = ticksDirect;
                    var ho = hock.Clone();
                    var prevSpeed = ho.Speed.Length;
                    for(var _ = 0; _ < 100; _++)
                    {
                        var spUp = dir < 0 ? 1 : -1;
                        ho.Move(spUp, 0.0);
                        var curSpeed = ho.Speed.Length;
                        if (curSpeed > prevSpeed)
                            break;
                        prevSpeed = curSpeed;
                        curTime++;
                    }
                    if (curTime < minTime && prevSpeed < Game.MaxSpeedToAllowSubstitute && IsInSubstArea(ho))
                    {
                        minTime = curTime;
                        if (ticksDirect == 0)
                        {
                            selSpUp = dir < 0 ? 1 : -1;
                            selTurn = 0;
                        }
                        else if (dir > 0)
                        {
                            selTurn = _hock.GetAngleTo(to.X, to.Y);
                            selSpUp = GetSpeedTo(selTurn);
                        }
                        else
                        {
                            selTurn = RevAngle(_hock.GetAngleTo(to.X, to.Y));
                            selSpUp = -GetSpeedTo(selTurn);
                        }
                    }
                    hock.MoveTo(to, dir);
                }
            }
            move.Turn = selTurn;
            move.SpeedUp = selSpUp;
            return minTime < Inf;
        }

        bool TrySubstitute(AHock hock)
        {
            if (!IsFinal()
                || hock.Speed.Length > Game.MaxSpeedToAllowSubstitute 
                || !IsInSubstArea(hock)
                || hock.Base.RemainingCooldownTicks > 0
                || hock.Base.RemainingKnockdownTicks > 0
                )
                return false;
            
            var maxStamina = MyRest
                .Select(x => x.Stamina)
                .Max();
            var maxStrength = MyRest
                .Where(x => Eq(maxStamina, x.Stamina))
                .Select(x => x.Strength)
                .Max();

            var to = Hockeyists.FirstOrDefault(x => Eq(x.Stamina, maxStamina) && Eq(x.Strength, maxStrength));
            if (to == null || maxStamina < hock.Stamina)
                return false;
            move.Action = ActionType.Substitute;
            move.TeammateIndex = to.TeammateIndex;
            return true;
        }

        bool NeedTrySubstitute(AHock hock)
        {
            if (!IsFinal())
                return false;
            var maxStamina = Hockeyists
                .Where(x => x.State == HockeyistState.Resting && x.IsTeammate)
                .Select(x => x.Stamina)
                .Max();
            var to = Hockeyists.FirstOrDefault(x => Eq(x.Stamina, maxStamina));
            if (to == null || maxStamina*0.8 < hock.Stamina)
                return false;
            return true;
        }

        Pair<Point, int> GetSubstitutePoint(AHock hock)
        {
            Point bestPoint = null;
            var selDir = 0;
            var minTicks = Inf;
            for (var x = Game.RinkLeft; x <= Game.RinkRight; x += RinkWidth/100)
            {
                if (MyLeft() && x > RinkCenter.X - 100 || MyRight() && x < RinkCenter.X + 100)
                    continue;

                var to = new Point(x, Game.RinkTop);
                var up = GetTicksToUp(hock, to);
                var down = GetTicksToDown(hock, to);
                if (up < minTicks)
                {
                    minTicks = up;
                    selDir = 1;
                    bestPoint = to;
                }
                if (down < minTicks)
                {
                    minTicks = down;
                    selDir = -1;
                    bestPoint = to;
                }
            }
            return new Pair<Point, int>(bestPoint, selDir);
        }
    }
}
