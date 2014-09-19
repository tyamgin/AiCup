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
        public static readonly double PenetrationCoeff = 0.2;
        public static readonly double BoundSpeedChangeCoeff = -0.25;
        public static readonly double FrictionCoeff = 0.999;

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

        // TODO: не в том порядке обрабатывается отскок
        public int Move(int ticks, bool goalCheck = false)
        {
            var mayGoal = -1;
            var breakCount = 0;

            var top = MyStrategy.game.RinkTop + MyStrategy.PuckRadius;
            var bottom = MyStrategy.game.RinkBottom - MyStrategy.PuckRadius;
            var right = MyStrategy.game.RinkRight - MyStrategy.PuckRadius;
            var left = MyStrategy.game.RinkLeft + MyStrategy.PuckRadius;

            for (var tick = 1; tick <= ticks && (!goalCheck || breakCount < 1); tick++)
            {
                Speed = Speed * APuck.FrictionCoeff;
                X += Speed.X;
                Y += Speed.Y;
                if (Y < top)
                {
                    var penetration = top - Y;
                    Y = top + PenetrationCoeff * penetration;
                    Speed.Y *= BoundSpeedChangeCoeff;
                    mayGoal = tick;
                    breakCount++;
                }
                if (Y > bottom)
                {
                    var penetration = Y - bottom;
                    Y = bottom - PenetrationCoeff * penetration;
                    Speed.Y *= BoundSpeedChangeCoeff;
                    mayGoal = tick;
                    breakCount++;
                }
                if (X > right)
                {
                    var penetration = 0.0;//X - right;
                    X = right - PenetrationCoeff * penetration;
                    Speed.X *= BoundSpeedChangeCoeff;
                    mayGoal = tick;
                    breakCount++;
                }
                if (X < left)
                {
                    var penetration = 0.0;//left - X;
                    X = left + PenetrationCoeff * penetration;
                    Speed.X *= BoundSpeedChangeCoeff;
                    mayGoal = tick;
                    breakCount++;
                }

                var opp = IsDefend ? MyStrategy.my : MyStrategy.opp;

                if (IntersectPuckAngGoalie())
                    return 0;
                var dx = Math.Abs(X - opp.NetFront);
                // TODO: не правильно: Y + dx * (Speed.Y / Speed.X) (а может и правильно)
                if (Math.Abs((opp.NetFront < opp.NetBack ? (opp.NetFront - MyStrategy.PuckRadius) : (opp.NetFront + MyStrategy.PuckRadius)) - X) < 0.01 // (это стена ворот)
                    && MyStrategy.IsBetween(MyStrategy.game.GoalNetTop + MyStrategy.PuckRadius, Y + dx * (Speed.Y / Speed.X), MyStrategy.game.GoalNetTop + MyStrategy.game.GoalNetHeight - MyStrategy.PuckRadius) // (в воротах)
                    && (mayGoal == -1 || (mayGoal == tick && breakCount <= 1)) // (не от борта)
                    )
                    return 1; // Goal !!

                MyStrategy.GoalieMove(Goalie, 1, this);
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
