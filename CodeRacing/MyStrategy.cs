using System;
using System.Collections;
using System.Collections.Generic;
using System.Data.Common;
using System.Data.SqlTypes;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    public partial class MyStrategy : IStrategy
    {
        public static World world;
        public static Game game;
        public Move move;
        public Car self;
        public TileType[,] tiles;
        public Cell[] waypoints;
        public int waypointIterator = 1;

        void Initialize()
        {
            // intialize tiles
            this.tiles = new TileType[world.Height, world.Width];
            var t = world.TilesXY;
            for(var i = 0; i < world.Height; i++)
                for (var j = 0; j < world.Width; j++)
                    tiles[i, j] = t[j][i];

            // intialize waypoints
            var wp = world.Waypoints;
            this.waypoints = new Cell[wp.Length];
            for(var i = 0; i < waypoints.Length; i++)
                this.waypoints[i] = new Cell(wp[i][1], wp[i][0]);

            if (waypoints[waypointIterator].I != self.NextWaypointY ||
                waypoints[waypointIterator].J != self.NextWaypointX)
            {
                waypointIterator = (waypointIterator + 1)%waypoints.Length;
            }
        }

        public Cell GetNextWayPoint(int delta = 1)
        {
            return waypoints[(waypointIterator + delta - 1)%waypoints.Length];
        }

        public Cell GetCell(double x, double y)
        {
            return new Cell((int)(y / game.TrackTileSize), (int)(x / game.TrackTileSize));
        }

        public Point GetCenter(Cell cell)
        {
            return new Point((cell.J + 0.5)*game.TrackTileSize, (cell.I + 0.5)*game.TrackTileSize);
        }

        public int Infinity = 0x3f3f3f3f;

        public ACar _car;

        public void Move(double power, double turn)
        {
            move.EnginePower = power;
            move.WheelTurn = turn;
            if (_car != null)
                _car.Move(power, turn);
        }

        public void Move(Car self, World world, Game game, Move move)
        {
            MyStrategy.world = world;
            MyStrategy.game = game;
            this.move = move;
            this.self = self;
            Initialize();
#if DEBUG
            while (Pause)
            {
                // pass
            }
            if (Debug)
            {
                Debug = false;
            }
            DrawMap();
#endif

            double turn = 0;

            if (_car == null)
            {
                _car = new ACar(self);
            }
            if (_car != null)
            {
                _car.Original = self;
            }

            if (world.Tick >= 200)
            {
                turn = 1;
                world = world;
            }
            Move(0.5, turn);

#if DEBUG
            draw();
            Thread.Sleep(8);
#endif
            return;

            var t = GetSegments();

            move.EnginePower = 1.0;

            var pts = GetSegments();
            var to = pts[1] as Point;
            
            if (self.GetDistanceTo(to.X, to.Y) < 1.6*game.TrackTileSize)
            {
                move.EnginePower = 0.8;
            }
            Console.WriteLine(self.WheelTurn);
            if (self.GetDistanceTo(to.X, to.Y) < 1.0*game.TrackTileSize)
            {
                if (GetSpeed(self) > 11)
                    move.IsBrake = true;
            }
            move.WheelTurn = self.GetAngleTo(to.X, to.Y);

            if (world.Tick > game.InitialFreezeDurationTicks)
            {
                move.IsThrowProjectile = true;
                move.IsSpillOil = true;

                if (to.GetDistanceTo(self) >= 7*game.TrackTileSize && Math.Abs(self.GetAngleTo(to.X, to.Y)) < Math.PI / 6)
                {
                    move.IsUseNitro = true;
                }
            }
            
        }

        public double GetSpeed(Unit u)
        {
            return Math.Sqrt(u.SpeedX*u.SpeedX + u.SpeedY*u.SpeedY);
        }
    }
}