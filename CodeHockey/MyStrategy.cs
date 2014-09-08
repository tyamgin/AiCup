using System;
using System.Linq;
using System.Reflection;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk 
{
    public sealed class MyStrategy : IStrategy
    {
        private Puck puck;
        private Player opp;
        private double width, height;
        private Hockeyist oppGoalkipper;

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
            puck = world.Puck;
            opp = world.GetOpponentPlayer();
            width = world.Width;
            height = world.Height;
            oppGoalkipper = world.Hockeyists.FirstOrDefault(x => !x.IsTeammate && x.Type == HockeyistType.Goalie);

            var angleToPuck = self.GetAngleTo(puck);
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
                    move.Turn = angleToPuck;
                }
            }
            move.SpeedUp = 1;        
        }
    }
}