using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Text;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public const int Infinity = 0x3f3f3f3f;
        public const double Eps = 1e-9;

        public ACircle GetOilSlick(ACar car)
        {
            var dist = game.OilSlickInitialRange + car.Original.Width/2 + game.OilSlickRadius;
            var slick = car - Point.ByAngle(car.Angle)*dist;
            return new ACircle{ X = slick.X, Y = slick.Y, Radius = game.OilSlickRadius};
        }

        public AProjectile[] GetProjectiles(ACar car)
        {
            if (car.Original.Type == CarType.Jeep)
            {
                return new[] { new AProjectile
                {
                    Radius = game.TireRadius,
                    Type = ProjectileType.Tire,
                    X = car.X,
                    Y = car.Y,
                    Speed = Point.ByAngle(car.Angle) * game.TireInitialSpeed
                }};
            }

            return new[] {0.0, -game.SideWasherAngle, game.SideWasherAngle}.Select(angle => new AProjectile
            {
                Radius = game.WasherRadius,
                Type = ProjectileType.Washer,
                X = car.X,
                Y = car.Y,
                Speed = Point.ByAngle(angle + car.Angle) * game.WasherInitialSpeed
            }).ToArray();
        }

        public double GetSpeed(Unit u)
        {
            return Math.Sqrt(u.SpeedX * u.SpeedX + u.SpeedY * u.SpeedY);
        }

        private static Dictionary<long, int> _waypointIterator;
        
        public Cell GetNextWayPoint(Car car, int delta = 1)
        {
            return waypoints[(_waypointIterator[car.Id] + delta - 1) % waypoints.Length];
        }

        public Cell GetCell(double x, double y)
        {
            return new Cell((int)(y / game.TrackTileSize), (int)(x / game.TrackTileSize));
        }

        public Point GetCenter(Cell cell)
        {
            return new Point((cell.J + 0.5) * game.TrackTileSize, (cell.I + 0.5) * game.TrackTileSize);
        }

        public static double TurnRound(double x)
        {
            if (x < -1)
                return -1;
            if (x > 1)
                return 1;
            return x;
        }


        private readonly List<Stopwatch> _timers = new List<Stopwatch>();

        public void TimerStart()
        {
#if DEBUG
            var timer = new Stopwatch();
            timer.Start();
            _timers.Add(timer);
#endif
        }

        public long TimerStop()
        {
#if DEBUG
            var res = _timers[_timers.Count - 1];
            res.Stop();
            _timers.RemoveAt(_timers.Count - 1);
            return res.ElapsedMilliseconds;
#else
            return 0;
#endif
        }

        public void TimeEndLog(string caption, long limit = TimerLogLimit)
        {
#if DEBUG
            var time = TimerStop();
            if (time > limit)
                Log(world.Tick + "> " + caption + ":" + time);
#endif
        }

        public void Log(object msg)
        {
#if DEBUG
            Console.WriteLine(msg);
#endif
        }
    }
}
