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
        public bool IsDefend = false;

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
        
        public int Move(int ticks, bool goalCheck = false)
        {
            var mayGoal = -1;
            var breakCount = 0;
            for (var tick = 1; tick <= ticks && breakCount < 1; tick++)
            {
                Speed = Speed * MyStrategy.FrictionPuckCoeff;
                X += Speed.X;
                Y += Speed.Y;
                if (Y < MyStrategy.game.RinkTop + MyStrategy.PuckRadius)
                {
                    Y = MyStrategy.game.RinkTop + MyStrategy.PuckRadius;
                    Speed.Y *= -1;
                    mayGoal = tick;
                    breakCount++;
                }
                if (Y > MyStrategy.game.RinkBottom - MyStrategy.PuckRadius)
                {
                    Y = MyStrategy.game.RinkBottom - MyStrategy.PuckRadius;
                    Speed.Y *= -1;
                    mayGoal = tick;
                    breakCount++;
                }
                if (X > MyStrategy.game.RinkRight - MyStrategy.PuckRadius)
                {
                    X = MyStrategy.game.RinkRight - MyStrategy.PuckRadius;
                    Speed.X *= -1;
                    mayGoal = tick;
                    breakCount++;
                }
                if (X < MyStrategy.game.RinkLeft + MyStrategy.PuckRadius)
                {
                    X = MyStrategy.game.RinkLeft + MyStrategy.PuckRadius;
                    Speed.X *= -1;
                    mayGoal = tick;
                    breakCount++;
                }

                var opp = IsDefend ? MyStrategy.my : MyStrategy.opp;

                if (IntersectPuckAngGoalie())
                    return 0;
                var dx = Math.Abs(X - opp.NetFront);
                // TODO: не правильно: Y + dx * (Speed.Y / Speed.X)
                if (Math.Abs((opp.NetFront < opp.NetBack ? (opp.NetFront - MyStrategy.PuckRadius) : (opp.NetFront + MyStrategy.PuckRadius)) - X) < 0.01 // (это стена ворот)
                    && MyStrategy.IsBetween(MyStrategy.game.GoalNetTop + MyStrategy.PuckRadius, Y + dx * (Speed.Y / Speed.X), MyStrategy.game.GoalNetTop + MyStrategy.game.GoalNetHeight - MyStrategy.PuckRadius) // (в воротах)
                    && (mayGoal == -1 || (mayGoal == tick && breakCount <= 1)) // (не от борта)
                    )
                    return 1; // Goal !!

                if (Goalie.Y > Y)
                    Goalie.Y -= Math.Min(MyStrategy.game.GoalieMaxSpeed, Goalie.Y - Y);
                else
                    Goalie.Y += Math.Min(MyStrategy.game.GoalieMaxSpeed, Y - Goalie.Y);

                var MinY = opp.NetTop + MyStrategy.HoRadius;
                var MaxY = opp.NetBottom - MyStrategy.HoRadius;
                if (Goalie.Y < MinY)
                    Goalie.Y = MinY;
                if (Goalie.Y > MaxY)
                    Goalie.Y = MaxY;
            }
            return 0;
        }
        bool IntersectPuckAngGoalie()
        {
            return GetDistanceTo2(Goalie) < Sqr(MyStrategy.HoRadius + MyStrategy.PuckRadius - /* костыль -> */2);
        }

        double Sqr(double x)
        {
            return x*x;
        }

        public APuck Clone()
        {
            var clone = new APuck(X, Y, Speed.X, Speed.Y, Goalie);
            clone.IsDefend = IsDefend;
            return clone;
        }

        public override string ToString()
        {
            return base.ToString() + ", " + Goalie.Y;
        }
    }
}
