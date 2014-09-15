using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public class APuck : AUnit
    {
        // На вратаря действует трение?

        public Point Goalie;

        public APuck(double x, double y, double speedX, double speedY, Point goalie) 
            : base(x, y, speedX, speedY, 0)
        {
            Goalie = new Point(goalie);
        }

        public APuck(Point pos, Point speed, Point goalie)
            : base(pos, speed, 0)
        {
            Goalie = new Point(goalie);
        }
        
        public int Move(int ticks)
        {
            var mayGoal = -1;
            var breakCount = 0;
            for (var tick = 1; tick < ticks; tick++)
            {
                X += Speed.X;
                Y += Speed.Y;
                if (Y < Global.game.RinkTop + Global.PuckRadius)
                {
                    Y = Global.game.RinkTop + Global.PuckRadius;
                    Speed.Y *= -1;
                    mayGoal = tick;
                    breakCount++;
                }
                if (Y > Global.game.RinkBottom - Global.PuckRadius)
                {
                    Y = Global.game.RinkBottom - Global.PuckRadius;
                    Speed.Y *= -1;
                    mayGoal = tick;
                    breakCount++;
                }
                if (X > Global.game.RinkRight - Global.PuckRadius)
                {
                    X = Global.game.RinkRight - Global.PuckRadius;
                    Speed.X *= -1;
                    mayGoal = tick;
                    breakCount++;
                }
                if (X < Global.game.RinkLeft + Global.PuckRadius)
                {
                    X = Global.game.RinkLeft + Global.PuckRadius;
                    Speed.X *= -1;
                    mayGoal = tick;
                    breakCount++;
                }
                Speed = Speed.Mul(Global.FrictionPuckCoeff);

                if (Goalie.Y > Y)
                    Goalie.Y -= Math.Min(Global.game.GoalieMaxSpeed, Goalie.Y - Y);
                else
                    Goalie.Y += Math.Min(Global.game.GoalieMaxSpeed, Y - Goalie.Y);

                var MinY = Global.opp.NetTop + Global.HoRadius;
                var MaxY = Global.opp.NetBottom - Global.HoRadius;
                if (Goalie.Y < MinY)
                    Goalie.Y = MinY;
                if (Goalie.Y > MaxY)
                    Goalie.Y = MaxY;
                if (IntersectPuckAngGoalie())
                    return 0;
                var dx = Math.Abs(X - Global.opp.NetFront);
                if (Math.Abs(Global.opp.NetFront + Global.PuckRadius - X) < 0.01
                    &&
                    MyStrategy.IsBetween(Global.game.GoalNetTop + Global.PuckRadius, Y + dx * (Speed.Y / Speed.X), Global.game.GoalNetTop + Global.game.GoalNetHeight - Global.PuckRadius)
                    && (mayGoal == -1 || (mayGoal == tick && breakCount <= 1))
                    )
                    return 1; // Goal !!
            }
            return 0;
        }

        //public int GetTicksToBorder()
        //{
        //    var clone = Clone();
        //    const int limit = 1000;
        //    for (int tick = 0; tick < limit; tick++)
        //    {
        //        if (clone.Y < Global.game.RinkTop + Global.PuckRadius
        //            || clone.Y > Global.game.RinkBottom - Global.PuckRadius
        //            || clone.X > Global.game.RinkRight - Global.PuckRadius
        //            || clone.X < Global.game.RinkLeft + Global.PuckRadius
        //            )
        //            return tick;
        //        clone.X += clone.Speed.X;
        //        clone.Y += clone.Speed.Y;
        //        clone.Speed = clone.Speed.Mul(Global.FrictionPuckCoeff);
        //    }
        //    return limit;
        //}

        bool IntersectPuckAngGoalie()
        {
            return GetDistanceTo(Goalie) < Global.HoRadius + Global.PuckRadius - 2;
        }

        public APuck Clone()
        {
            return new APuck(X, Y, Speed.X, Speed.Y, Goalie);
        }
    }
}
