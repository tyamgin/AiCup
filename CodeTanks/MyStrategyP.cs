/*
 * 
 * Some utility methods and other.
 * 
 * 
*/

using System;
using Com.CodeGame.CodeTanks2012.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTanks2012.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private const double ShellStartSpeed = 16.7;
        private const double SelfSpeed = 3.5;
        private double Eps = 1e-7;
        private double[] Angle;
        private Random rnd = new Random();
        private World world;
        private Move move;
        private int start = -1;
        private int gameType;

        public MyStrategy()
        {
            Angle = new double[400];
            for (int i = 0; i < 400; i++)
                Angle[i] = Math.PI / 180.0 * i;
        }

        public TankType SelectTank(int tankIndex, int teamSize)
        {
            return TankType.Medium;
        }

        private bool InPolygon(double x0, double y0, double[] x, double[] y)
        {
            double[] v = new double[x.Length];
            for (int i = 0; i < x.Length; i++)
                v[i] = VectorProduct(x[i], y[i], x[(i + 1) % x.Length], y[(i + 1) % x.Length], x0, y0);
            bool ok = true;
            for (int i = 0; i < x.Length; i++)
                if (v[i] + Eps < 0)
                    ok = false;
            if (ok)
                return true;
            ok = true;
            for (int i = 0; i < x.Length; i++)
                if (v[i] - Eps > 0)
                    ok = false;
            return ok;
        }

        private void SetMove(double left, double right)
        {
            move.LeftTrackPower = left;
            move.RightTrackPower = right;
        }

        private static double GetSpeed(Unit unit)
        {
            return Math.Sqrt(unit.SpeedX * unit.SpeedX + unit.SpeedY * unit.SpeedY);
        }

        private double RotatedX(double x, double y, double angle)
        {
            return Math.Cos(angle) * x - Math.Sin(angle) * y;
        }

        private double RotatedY(double x, double y, double angle)
        {
            return Math.Sin(angle) * x + Math.Cos(angle) * y;
        }

        private double DistFromPointToLine(double x, double y, double x1, double y1, double x2, double y2)
        {
            double A = y1 - y2;
            double B = x2 - x1;
            double C = -x1 * A - y1 * B;
            return Math.Abs((A * x + B * y + C) / Math.Sqrt(A * A + B * B));
        }

        private bool OutOfRange(double x, double y)
        {
            return x < 0 || y < 0 || x > world.Width || y > world.Height;
        }

        private bool OutOfRange(Unit unit, double error = 1.0)
        {
            double[] X = new double[4];
            double[] Y = new double[4];
            GetCoordinates(unit, X, Y, error);
            return OutOfRange(X[0], Y[0]) && OutOfRange(X[1], Y[1]) || OutOfRange(X[2], Y[2]) && OutOfRange(X[3], Y[3]);
        }

        private double DistanseToBorder(Tank tank)
        {
            return Math.Min(Math.Min(tank.X, tank.Y), Math.Min(world.Width - tank.X, world.Height - tank.Y));
        }

        private bool IsAlive(Tank tank)
        {
            return tank.CrewHealth > 0 && tank.HullDurability > 0;
        }

        private bool IsNeed(Tank self, Bonus bonus)
        {
            return (self.CrewHealth < self.CrewMaxHealth && bonus.Type == BonusType.Medikit) ||
                   (self.HullDurability < self.HullMaxDurability && bonus.Type == BonusType.RepairKit) ||
                   (bonus.Type == BonusType.AmmoCrate && self.PremiumShellCount < 3);
        }

        public double GetTurretAngle(Tank tank)
        {
            return NormAngle(tank.TurretRelativeAngle + tank.Angle);
        }

        public double NormAngle(double angle)
        {
            if (angle >= Angle[180])
            {
                while (angle >= Angle[180])
                    angle -= 2 * Math.PI;
            }
            else
            {
                while (angle <= -Angle[180])
                    angle += 2 * Math.PI;
            }
            return angle;
        }

        private int CountAlive()
        {
            int cnt = 0;
            foreach (Tank tank in world.Tanks)
                if (IsAlive(tank))
                    cnt++;
            return cnt;
        }

        private double GetDistance(double x1, double y1, double x2, double y2)
        {
            return Math.Sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        }

        private bool PointInSegment(double x1, double y1, double x2, double y2, double x, double y)
        {
            return Math.Abs((x - x1) * (y1 - y2) - (y - y1) * (x1 - x2)) < Eps && Math.Min(x1, x2) - Eps < x && x < Eps + Math.Max(x1, x2) && Math.Min(y1, y2) - Eps < y && y < Eps + Math.Max(y1, y2);
        }

        private double Determinant(double a, double b, double c, double d)
        {
            return a * d - b * c;
        }

        private bool SegmentIntersected(double x1, double y1, double x2, double y2, double X1, double Y1, double X2, double Y2)
        {
            double A = y1 - y2;
            double B = x2 - x1;
            double C = -B * y1 - x1 * A;

            double a = Y1 - Y2;
            double b = X2 - X1;
            double c = -b * Y1 - X1 * a;

            double d = Determinant(A, B, a, b);
            double d1 = Determinant(-C, B, -c, b);
            double d2 = Determinant(A, -C, a, -c);
            double tmp;
            if (Math.Abs(d) < Eps)
            {
                if (Math.Abs(d1 - d2) < Eps)
                {
                    if (x2 < x1)
                    {
                        tmp = x1; x1 = x2; x2 = tmp;
                        tmp = y1; y1 = y2; y2 = tmp;
                    }
                    if (X2 < X1)
                    {
                        tmp = X1; X1 = X2; X2 = tmp;
                        tmp = Y1; Y1 = Y2; Y2 = tmp;
                    }
                    if (x1 > X1)
                    {
                        tmp = x1; x1 = X1; X1 = tmp;
                        tmp = x2; x2 = X2; X2 = tmp;
                        tmp = y1; y1 = Y1; Y1 = tmp;
                        tmp = y2; y2 = Y2; Y2 = tmp;
                    }
                    if (Math.Abs(x1 - x2) < Eps)
                    {
                        if (y2 < y1)
                        {
                            tmp = x1; x1 = x2; x2 = tmp;
                            tmp = y1; y1 = y2; y2 = tmp;
                        }
                        if (Y2 < Y1)
                        {
                            tmp = X1; X1 = X2; X2 = tmp;
                            tmp = Y1; Y1 = Y2; Y2 = tmp;
                        }
                        if (y1 > Y1)
                        {
                            tmp = x1; x1 = X1; X1 = tmp;
                            tmp = x2; x2 = X2; X2 = tmp;
                            tmp = y1; y1 = Y1; Y1 = tmp;
                            tmp = y2; y2 = Y2; Y2 = tmp;
                        }

                        return Y1 - Eps <= y2;
                    }
                    else
                    {
                        return X1 - Eps <= x2;
                    }
                }
                else
                {
                    return false;
                }
            }

            return PointInSegment(x1, y1, x2, y2, d1 / d, d2 / d) && PointInSegment(X1, Y1, X2, Y2, d1 / d, d2 / d);
        }

        private double VectorProduct(double x1, double y1, double x2, double y2, double x3, double y3)
        {
            return (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
        }

        private Unit FictiveTank(double x, double y, long id)
        {
            return new Tank(id, "", 0, x, y, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, TankType.Medium);
        }
    }
}
