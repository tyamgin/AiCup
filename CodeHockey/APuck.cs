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
        
        public int Move(int ticks)
        {
            var mayGoal = -1;
            var breakCount = 0;
            for (var tick = 1; tick <= ticks; tick++)
            {
                Speed = Speed.Mul(Global.FrictionPuckCoeff);
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

                var opp = IsDefend ? Global.my : Global.opp;

                if (IntersectPuckAngGoalie())
                    return 0;
                var dx = Math.Abs(X - opp.NetFront);
                if (Math.Abs((opp.NetFront < opp.NetBack ? (opp.NetFront - Global.PuckRadius) : (opp.NetFront + Global.PuckRadius)) - X) < 0.01 // (это стена ворот)
                    && MyStrategy.IsBetween(Global.game.GoalNetTop + Global.PuckRadius, Y + dx * (Speed.Y / Speed.X), Global.game.GoalNetTop + Global.game.GoalNetHeight - Global.PuckRadius) // (в воротах)
                    && (mayGoal == -1 || (mayGoal == tick && breakCount <= 1)) // (не от борта)
                    )
                    return 1; // Goal !!

                if (Goalie.Y > Y)
                    Goalie.Y -= Math.Min(Global.game.GoalieMaxSpeed, Goalie.Y - Y);
                else
                    Goalie.Y += Math.Min(Global.game.GoalieMaxSpeed, Y - Goalie.Y);

                var MinY = opp.NetTop + Global.HoRadius;
                var MaxY = opp.NetBottom - Global.HoRadius;
                if (Goalie.Y < MinY)
                    Goalie.Y = MinY;
                if (Goalie.Y > MaxY)
                    Goalie.Y = MaxY;
            }
            return 0;
        }
        bool IntersectPuckAngGoalie()
        {
            return GetDistanceTo(Goalie) < Global.HoRadius + Global.PuckRadius - /* костыль -> */2;
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
