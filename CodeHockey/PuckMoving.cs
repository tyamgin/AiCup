using System;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point PuckMove(int ticks)
        {
            Point p = new Point(puck);
            Point sp = new Point(puck.SpeedX, puck.SpeedY);
            var angle = puck.Angle;
            for (int tick = 0; tick < ticks; tick++)
            {
                p.X += sp.X;
                p.Y += sp.Y;
                if (p.Y < game.RinkTop)
                {
                    p.Y = game.RinkTop;
                    sp.Y *= -1;
                }
                if (p.Y > game.RinkBottom)
                {
                    p.Y = game.RinkBottom;
                    sp.Y *= -1;
                }
                if (p.X > game.RinkRight)
                {
                    p.X = game.RinkRight;
                    sp.X *= -1;
                }
                if (p.X < game.RinkLeft)
                {
                    p.X = game.RinkLeft;
                    sp.X *= -1;
                }
                //if (puck.OwnerHockeyistId != -1)
                //    drawPathQueue.Enqueue(new Point(p));
            }
            return p;
        }
    }
}
