using System;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public abstract class Unit
    {
        private readonly long id;
        private readonly int x;
        private readonly int y;

        protected Unit(long id, int x, int y)
        {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public long Id
        {
            get { return id; }
        }

        public int X
        {
            get { return x; }
        }

        public int Y
        {
            get { return y; }
        }

        public double GetDistanceTo(int x, int y)
        {
            int xRange = x - this.x;
            int yRange = y - this.y;
            return Math.Sqrt(xRange*xRange + yRange*yRange);
        }

        public double GetDistanceTo(Unit unit)
        {
            return GetDistanceTo(unit.x, unit.y);
        }
    }
}