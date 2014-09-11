using System;
using System.Drawing;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Threading;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk 
{
    public partial class MyStrategy : IStrategy
    {
        //private const int Inf = 0x3f3f3f3f;

        private Puck puck;
        private Player opp, my;
        private Hockeyist oppGoalkipper;
        private World world;
        private Game game;
        private static double eps = 1e-9;
        private double HoRadius;
        private double RinkWidth, RinkHeight;
        private Point RinkCenter;
        private double StrikeZoneWidth;
        private double StrikeZoneWidthBesideNet;

        public Point GetStrikePoint()
        {
            double delta = 2;
            double shift = 10;
            double x = opp.NetFront;
            double bestDist = 0;
            double bestY = 0;
            double minY = Math.Min(opp.NetBottom, opp.NetTop);
            double maxY = Math.Max(opp.NetBottom, opp.NetTop);
            for (double y = minY + shift; y <= maxY - shift; y += delta)
            {
                if (oppGoalkipper.GetDistanceTo(x, y) > bestDist)
                {
                    bestDist = oppGoalkipper.GetDistanceTo(x, y);
                    bestY = y;
                }
            }
            return new Point(x, bestY);
        }

        Point GetStrikeFrom(Point myPositionPoint, Point mySpeed, double myAngle)
        {
            var x1 = game.RinkLeft + RinkWidth * 0.44;
            var x2 = game.RinkRight - RinkWidth * 0.44;
            var y1 = game.RinkTop + RinkHeight / 5;
            var y2 = game.RinkBottom - RinkHeight / 5;

            var a = new Point(MyRight() ? x1 : x2, y1);
            var b = new Point(MyRight() ? x1 : x2, y2);
            if (Math.Abs(myAngle) < Deg(20) || Math.Abs(myAngle) > Deg(160))
                return a.GetDistanceTo(myPositionPoint) < b.GetDistanceTo(myPositionPoint) ? a : b;
            return GetTicksTo(myPositionPoint, mySpeed, myAngle, a) < GetTicksTo(myPositionPoint, mySpeed, myAngle, b) ? a : b;
        }

        const int DangerC = 100000;
        const int DangerB = 100;
        const int DangerA = 1;

        int GetDangerByDist(Hockeyist self, double dist)
        {
            if (self.Id == puck.OwnerHockeyistId)
            {
                if (dist < game.StickLength)
                    return DangerC;
                if (dist < game.StickLength * 1.5)
                    return DangerB;
                return DangerA;
            }
            if (dist < HoRadius * 1.5)
                return DangerC;
            return DangerA;
        }

        int GetDangerAt(Hockeyist self, double x, double y)
        {
            if (x < game.RinkLeft || y < game.RinkTop || x > game.RinkRight || y > game.RinkBottom)
                return Inf;
            int danger = 0;
            foreach (Hockeyist ho in world.Hockeyists)
            {
                if (ho.IsTeammate)
                    continue;
                if (ho.Type == HockeyistType.Goalie)
                    continue;
                danger += GetDangerByDist(self, ho.GetDistanceTo(x, y));
            }
            return danger;
        }

        void FillDangerMap(Hockeyist self)
        {
            for(int i = 0; i < xParts; i++)
            {
                for(int j = 0; j < yParts; j++)
                {
                    double x = XDecode(i), y = YDecode(j);
                    map[i, j] = GetDangerAt(self, x, y);
                    //drawDangerQueue.Enqueue(new Pair<Point, int>(new Point(x, y), map[i, j]));
                }
            }
        }

        public void GoTo(Point myPositionPoint, Point to)
        {
            GetShortestPathAngle(myPositionPoint, to);
        }

        public double GetTicksTo(Point myPosition, Point mySpeed, double myAngle, Point to)
        {
            var totalSpeed = mySpeed.Length;
            var an = Point.GetAngleBetween(to, myPosition, new Point(myPosition.X + mySpeed.X, myPosition.Y + mySpeed.Y));
            var speed = totalSpeed * Math.Cos(an);
            return Math.Sqrt(speed*speed + 2*myPosition.GetDistanceTo(to.X, to.Y)) - speed;
        }

        public Point GoToPuck(Point myPosition, Point mySpeed, double myAngle, out int ticks)
        {
            double best = Inf;
            var result = new Point(myPosition);
            for (ticks = 0; ticks < 400; ticks++)
            {
                var puckPosition = PuckMove(ticks, new Point(puck), new Point(puck.SpeedX, puck.SpeedY));
                var needTicks = GetTicksTo(myPosition, mySpeed, myAngle, puckPosition);

                if (needTicks <= ticks && ticks - needTicks < best)
                {
                    best = ticks - needTicks;
                    result = puckPosition;
                }
            }
            return result;   
        }

        public Point GoToPuck(Point myPosition, Point mySpeed, double myAngle)
        {
            int ticks;
            return GoToPuck(myPosition, mySpeed, myAngle, out ticks);
        }
        public int GetTicksToPuck(Point myPosition, Point mySpeed, double myAngle)
        {
            int ticks;
            GoToPuck(myPosition, mySpeed, myAngle, out ticks);
            return ticks;
        }

        Point GetDefendPos2(Point myPosition)
        {
            var a = new Point(game.RinkLeft + RinkWidth * 0.15, game.RinkTop + RinkHeight / 2);
            var b = new Point(game.RinkRight - RinkWidth * 0.15, game.RinkTop + RinkHeight / 2);
            if (Math.Abs(my.NetFront - a.X) < Math.Abs(my.NetFront - b.X))
                return a;
            return b;
        }

        public void Move(Hockeyist self, World world, Game game, Move move) 
        {
            drawPathQueue.Clear();
            drawGoalQueue.Clear();
            drawGoal2Queue.Clear();
            drawDangerQueue.Clear();

#if DEBUG
            if (form == null)
            {
                thread = new Thread(ShowWindow);
                thread.Start();
                Thread.Sleep(2000);
            }
#endif

            this.world = world;
            this.game = game;
            this.puck = world.Puck;
            this.opp = world.GetOpponentPlayer();
            this.my = world.GetMyPlayer();
            this.RinkWidth = game.RinkRight - game.RinkLeft;
            this.RinkHeight = game.RinkBottom - game.RinkTop;
            this.oppGoalkipper = world.Hockeyists.FirstOrDefault(x => !x.IsTeammate && x.Type == HockeyistType.Goalie);
            this.HoRadius = self.Radius;
            this.RinkCenter = new Point(RinkWidth / 2, RinkHeight / 2);
            this.StrikeZoneWidth = RinkHeight*0.3;
            this.StrikeZoneWidthBesideNet = RinkWidth*0.16;
            var friend = world.Hockeyists.FirstOrDefault(x => x.IsTeammate && x.Id != self.Id && x.Type != HockeyistType.Goalie);

            if (null == oppGoalkipper)
                return;

            FillDangerMap(self);

            var net = GetStrikePoint();
            var angleToNet = self.GetAngleTo(net.X, net.Y);
            var power = 1 + 0.25*(Math.Min(game.MaxEffectiveSwingTicks, self.SwingTicks) - game.MaxEffectiveSwingTicks);

            if (self.State == HockeyistState.Swinging && (!IsInStrikeZone(new Point(self)) || self.Id != puck.OwnerHockeyistId))
            {
                move.Action = ActionType.CancelStrike;
            }
            else if (puck.OwnerHockeyistId == self.Id)
            {
                move.Turn = angleToNet;

                if (!IsInStrikeZone(new Point(self)))
                {
                    var to = GetStrikeFrom(new Point(self), GetSpeed(self), self.Angle);
                    move.Turn = self.GetAngleTo(to.X, to.Y);
                }
                else if (Math.Abs(self.X - net.X) <= RinkWidth * 0.5 // <-- temp fix
                    && Strike(new Point(puck), new Point(self.SpeedX, self.SpeedY), power, self.Angle))
                {
                    move.Action = ActionType.Strike;
                }
                else if (self.State != HockeyistState.Swinging
                    && IsBetween(RinkWidth * 0.4, self.GetDistanceTo(net.X, net.Y), RinkWidth * 0.5) 
                    && Math.Abs(angleToNet) < Deg(4))
                {
                    move.Action = ActionType.Swing;
                }
                else if (self.GetDistanceTo(net.X, net.Y) > RinkWidth * 0.6)
                {
                    var to = GetStrikeFrom(new Point(self), GetSpeed(self), self.Angle);
                    drawGoal2Queue.Enqueue(to);
                    move.Turn = self.GetAngleTo(to.X, to.Y);
                }
            }
            else
            {
                if (puck.OwnerPlayerId != my.Id
                    && self.GetDistanceTo(net.X, net.Y) < 0.3 * RinkWidth
                    && Strike(new Point(puck), new Point(self.SpeedX, self.SpeedY), power, self.Angle))
                {
                    move.Action = ActionType.Strike;
                }
                else if (game.StickLength >= self.GetDistanceTo(puck) && puck.OwnerPlayerId != self.PlayerId)
                {
                    move.Action = ActionType.TakePuck;
                }
                else
                {
                    Point to;
                    if (puck.OwnerPlayerId == my.Id ||
                        GetTicksToPuck(new Point(self), GetSpeed(self), self.Angle) > GetTicksToPuck(new Point(friend), GetSpeed(friend), friend.Angle))

                        to = GetDefendPos2(new Point(self));
                    else
                        to = GoToPuck(new Point(self), new Point(self.SpeedX, self.SpeedY), self.Angle); 
                    move.Turn = self.GetAngleTo(to.X, to.Y);
                    drawGoalQueue.Enqueue(new Point(to));
                }
            }
            move.SpeedUp = 1;
#if DEBUG
            draw();
            Thread.Sleep(20);
#endif
        }

        public double Deg(double deg)
        {
            return Math.PI/180*deg;
        }

        public Point GetSpeed(Hockeyist ho)
        {
            return new Point(ho.SpeedX, ho.SpeedY);
        }

        public bool MyLeft()
        {
            return !MyRight();
        }

        public bool MyRight()
        {
            return opp.NetFront < RinkCenter.X;
        }

        public bool IsInStrikeZone(Point pos)
        {
            return (IsBetween(game.RinkTop, pos.Y, game.RinkTop + StrikeZoneWidth) || IsBetween(game.RinkBottom - StrikeZoneWidth, pos.Y, game.RinkBottom)) 
                && (MyRight() ? !IsBetween(game.RinkLeft, pos.X, game.RinkLeft + StrikeZoneWidthBesideNet) : !IsBetween(game.RinkRight - StrikeZoneWidthBesideNet, pos.X, game.RinkRight));
        }
    }
}