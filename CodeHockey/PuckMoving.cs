using System;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point PuckMove(int ticks, Point _pos, Point _speed)
        {
            var pos = new Point(_pos);
            var speed = new Point(_speed);
            var angle = puck.Angle;
            for (int tick = 0; tick < ticks; tick++)
            {
                pos.X += speed.X;
                pos.Y += speed.Y;
                if (pos.Y < game.RinkTop)
                {
                    pos.Y = game.RinkTop;
                    speed.Y *= -1;
                }
                if (pos.Y > game.RinkBottom)
                {
                    pos.Y = game.RinkBottom;
                    speed.Y *= -1;
                }
                if (pos.X > game.RinkRight)
                {
                    pos.X = game.RinkRight;
                    speed.X *= -1;
                }
                if (pos.X < game.RinkLeft)
                {
                    pos.X = game.RinkLeft;
                    speed.X *= -1;
                }
                //if (puck.OwnerHockeyistId != -1)
                //    drawPathQueue.Enqueue(new Point(p));
            }
            return pos;
        }

        int PuckTicksToBorder(Point _pos, Point _speed)
        {
            var pos = new Point(_pos);
            var speed = new Point(_speed);
            const int limit = 1000;
            for (int tick = 0; tick < limit; tick++)
            {
                if (pos.Y < game.RinkTop || pos.Y > game.RinkBottom || pos.X > game.RinkRight || pos.X < game.RinkLeft)
                    return tick;
                pos.X += speed.X;
                pos.Y += speed.Y;
            }
            return limit;
        }

        bool IsBetween(double left, double x, double right)
        {
            return left <= x && x <= right;
        }

        bool Eq(double a, double b)
        {
            return Math.Abs(a - b) < eps;
        }

        bool Strike(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker)
        {
            var strikerDir = new Point(Math.Cos(AngleStriker), Math.Sin(AngleStriker));
            var SpeedStriker = strikerSpeed.Length;
            var SpeedAngleStriker = Math.Atan2(strikerSpeed.Y, strikerSpeed.X);
            var puckSpeed = 20.0 * StrikePower + SpeedStriker * Math.Cos(AngleStriker - SpeedAngleStriker);
            var puckSpeedDir = strikerDir.Mul(puckSpeed);
            int ticksToBorder = PuckTicksToBorder(puckPos, puckSpeedDir);
            Point p = PuckMove(ticksToBorder, puckPos, puckSpeedDir);
            if (Eq(p.X, opp.NetFront) && IsBetween(game.GoalNetTop, p.Y, game.GoalNetTop + game.GoalNetHeight))
            {
                return true;
            }
            return false;
        }
    }
}
