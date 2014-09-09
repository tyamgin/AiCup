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
        private const int Inf = 0x3f3f3f3f;

        private Puck puck;
        private Player opp;
        private double width, height;
        private Hockeyist oppGoalkipper;
        private World world;
        private Game game;

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

        public void Move(Hockeyist self, World world, Game game, Move move) 
        {
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
            width = world.Width;
            height = world.Height;
            oppGoalkipper = world.Hockeyists.FirstOrDefault(x => !x.IsTeammate && x.Type == HockeyistType.Goalie);

            //var dr = new Thread(drawThread);
            //dr.Start();
            //dr.Join();

            if (null == oppGoalkipper)
                return;

            var net = GetStrikePoint();
            double angleToNet = self.GetAngleTo(net.X, net.Y);

            if (self.State == HockeyistState.Swinging && (self.SpeedX < 0.5 || puck.OwnerHockeyistId != self.Id))
            {
                if (puck.OwnerHockeyistId != self.Id || self.GetAngleTo(net.X, net.Y) > 10)
                    move.Action = ActionType.CancelStrike;
                else
                    move.Action = ActionType.Strike;
            }
            if (puck.OwnerHockeyistId == self.Id)
            {
                move.Turn = angleToNet;
                if (self.State != HockeyistState.Swinging
                    && self.GetDistanceTo(net.X, net.Y) <= width * 0.5 && self.GetDistanceTo(net.X, net.Y) > width * 0.3 
                    && Math.Abs(angleToNet) < Math.PI / 180 * 10)
                {
                    move.Action = ActionType.Swing;
                }
                else if (self.GetDistanceTo(net.X, net.Y) <= width * 0.3 && Math.Abs(angleToNet) < Math.PI / 180 * 10)
                {
                    move.Action = ActionType.Strike;
                }
            }
            else
            {
                if (game.StickLength >= self.GetDistanceTo(puck))
                {
                    move.Action = ActionType.TakePuck;
                }
                else
                {
                    double best = Inf;
                    Point to = null;
                    for (int ticks = 0; ticks < 400; ticks++)
                    {
                        var puckPos = PuckMove(ticks);
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

                    if (to == null)
                        to = null;
                    else
                    {
                        move.Turn = self.GetAngleTo(to.X, to.Y);
                        drawGoalQueue.Enqueue(new Point(to));
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