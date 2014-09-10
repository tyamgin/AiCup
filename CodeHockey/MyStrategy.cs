using System;
using System.Drawing;
using System.Linq;
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
        private double width, height;
        private Hockeyist oppGoalkipper;
        private World world;
        private Game game;
        private static double eps = 1e-9;
        private double HoRadius;

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

        Point GetStrikeFrom(Point p)
        {
            double x1 = game.RinkLeft + (game.RinkRight - game.RinkLeft) * 0.4;
            double x2 = game.RinkRight - (game.RinkRight - game.RinkLeft) * 0.4;
            double y1 = game.RinkTop + (game.RinkBottom - game.RinkTop) / 4;
            double y2 = game.RinkBottom - (game.RinkBottom - game.RinkTop) / 4;

            Point a = new Point(opp.NetFront < width / 2 ? x1 : x2, y1);
            Point b = new Point(opp.NetFront < width / 2 ? x1 : x2, y2);
            if (a.GetDistanceTo(p) < b.GetDistanceTo(p))
                return a;
            return b;
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
            else 
            {
                if (dist < HoRadius * 1.5)
                    return DangerC;
                return DangerA;
            }
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

        public void GoTo(Point p, Point to)
        {
            GetShortestPathAngle(p, to);
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
                //thread.Join();
                Thread.Sleep(2000);
            }
#endif

            this.world = world;
            this.game = game;
            puck = world.Puck;
            opp = world.GetOpponentPlayer();
            my = world.GetMyPlayer();
            width = world.Width;
            height = world.Height;
            oppGoalkipper = world.Hockeyists.FirstOrDefault(x => !x.IsTeammate && x.Type == HockeyistType.Goalie);
            HoRadius = self.Radius;

            if (null == oppGoalkipper)
                return;

            FillDangerMap(self);

            var net = GetStrikePoint();
            double angleToNet = self.GetAngleTo(net.X, net.Y);

            if (self.State == HockeyistState.Swinging && (puck.OwnerHockeyistId != self.Id || Math.Abs(self.X - opp.NetFront) < 60))
            {
                move.Action = ActionType.CancelStrike;
            }
            else if (puck.OwnerHockeyistId == self.Id)
            {
                move.Turn = angleToNet;
                if (self.State != HockeyistState.Swinging
                    && self.GetDistanceTo(net.X, net.Y) <= width * 0.4 && self.GetDistanceTo(net.X, net.Y) > width * 0.3 
                    && Math.Abs(angleToNet) < Math.PI / 180 * 4)
                {
                    move.Action = ActionType.Swing;
                }
                else if (self.GetDistanceTo(net.X, net.Y) <= width * 0.3
                    && Strike(new Point(puck), new Point(self.SpeedX, self.SpeedY), 0.75, self.Angle)
                    )
                {
                    move.Action = ActionType.Strike;
                }
                else if (self.GetDistanceTo(net.X, net.Y) > width * 0.55)
                {
                    var to = GetStrikeFrom(new Point(self));
                    drawGoal2Queue.Enqueue(to);
                    move.Turn = self.GetAngleTo(to.X, to.Y);
                    GoTo(new Point(self), to);
                }
            }
            else
            {
                if (game.StickLength >= self.GetDistanceTo(puck) && puck.OwnerPlayerId != self.PlayerId)
                {
                    move.Action = ActionType.TakePuck;
                }
                else
                {
                    double best = Inf;
                    Point to = null;
                    for (int ticks = 0; ticks < 400; ticks++)
                    {
                        var puckPos = PuckMove(ticks, new Point(puck), new Point(puck.SpeedX, puck.SpeedY));
                        var totalSpeed = self.SpeedX * Math.Cos(self.Angle) + self.SpeedY * Math.Sin(self.Angle);
                        double an = Point.GetAngleBetween(puckPos, new Point(self),
                            new Point(self.X + self.SpeedX, self.Y + self.SpeedY));
                        var speed = totalSpeed*Math.Cos(an);

                        var needTicks = Math.Sqrt(speed*speed + self.GetDistanceTo(puckPos.X, puckPos.Y)) - speed;

                        if (needTicks <= ticks && ticks - needTicks < best)
                        {
                            best = ticks - needTicks;
                            to = puckPos;
                        }
                    }

                    if (to != null)
                    {
                        move.Turn = self.GetAngleTo(to.X, to.Y);
                        drawGoalQueue.Enqueue(new Point(to));
                        GoTo(new Point(self), to);
                    }
                }
            }
            move.SpeedUp = 1;
#if DEBUG
            draw();
            Thread.Sleep(10);
#endif
        }
    }
}