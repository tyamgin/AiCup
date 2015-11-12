using System;
using System.Collections;
using System.Collections.Generic;
using System.Data.Common;
using System.Data.SqlTypes;
using System.Linq;
using System.Threading;
using System.Windows.Forms;
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
        public static double MapWidth, MapHeight;
        public static Point[,,] TileCorner;

        void Initialize()
        {
            MapWidth = game.TrackTileSize*world.Width;
            MapHeight = game.TrackTileSize*world.Height;

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

            if (TileCorner == null)
            {
                TileCorner = new Point[world.Height, world.Width, 4];
                for (var i = 0; i < world.Height; i++)
                    for (var j = 0; j < world.Height; j++)
                        for (var di = 0; di < 2; di++)
                            for (var dj = 0; dj < 2; dj++)
                                TileCorner[i, j, di*2 + dj]
                                    = new Point((j + dj)*game.TrackTileSize, (i + di)*game.TrackTileSize);
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

        private bool ModelMove(ACar car, double x, double y, bool z)
        {
            car.Move(x, y, z);
            return car.GetRect().All(p => !_intersectTail(p, 3.0));
        }

        private void _move()
        {


            var t = GetSegments();

            var pts = GetSegments();
            var to = pts[1] as Point;
            var to2 = pts[2] as Point;

            if (world.Tick < game.InitialFreezeDurationTicks)
            {
                move.EnginePower = 1;
            }
            else if (self.GetDistanceTo(to.X, to.Y) > game.TrackTileSize * 2)
            {
                move.EnginePower = 1;
                move.WheelTurn = self.GetAngleTo(to.X, to.Y);
            }
            else
            {
                int selDirectTime = -1;
                int selRotateTime = -1;
                int selBreakTime = -1;
                double selTurn = -1;
                int bestTime = Infinity;

                double needDist = Math.Max(10.0, to2.GetDistanceTo(to) - 1.5*game.TrackTileSize);

                var modelA = new ACar(self);

                CarMoveFunc(modelA, 100, 5, 1.0, 0, false, 0, (aCar, time1) =>
                {
                    if (time1 >= bestTime)
                        return;
                    var turn = TurnRound(aCar.GetAngleTo(to2));
                    CarMoveFunc(aCar, 15, 3, 0.5, turn, false, time1, (bCar, time2) =>
                    {
                        if (time2 >= bestTime)
                            return;
                        CarMoveFunc(bCar, 42, 3, 0.0, turn, true, time2, (cCar, time3) =>
                        {
                            if (time3 >= bestTime)
                                return;
                            var model = new ACar(cCar);
                            int tt = time3;
                            for (; tt < bestTime && to2.GetDistanceTo2(model) > needDist * needDist; tt++)
                            {
                                if (!ModelMove(model, 1, TurnRound(model.GetAngleTo(to2)), false))
                                    return;
                            }

                            if (tt < bestTime)
                            {
                                bestTime = tt;
                                selDirectTime = time1;
                                selRotateTime = time2 - time1;
                                selBreakTime = time3 - time2;
                                selTurn = turn;
                            }
                        }); 
                    });
                });         
                      
                if (bestTime < Infinity)
                {
                    if (selDirectTime != 0)
                    {
                        move.EnginePower = 1;
                    }
                    else if (selRotateTime != 0)
                    {
                        move.WheelTurn = selTurn;
                        move.EnginePower = 1;
                    }
                    else if (selBreakTime != 0)
                    {
                        move.IsBrake = true;
                        move.WheelTurn = selTurn;
                    }
                }
                else
                {
                    move.EnginePower = 1;
                    move.WheelTurn = self.GetAngleTo(to.X, to.Y);
                }
            }

            
        }

        delegate void CarCallback(ACar car, int time);

        private void CarMoveFunc(ACar _model, int to, int step, double power, double turn, bool isBreak, int time,
            CarCallback callback)
        {
            var model = new ACar(_model);
            for (var t = 0; t <= to; t += step)
            {
                callback(new ACar(model), time + t);
                for (var r = 0; r < step; r++)
                    if (!ModelMove(model, power, turn, isBreak))
                        return;
            }
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
            _move();
#if DEBUG
            draw();
            Thread.Sleep(8);
#endif
        }

        public double GetSpeed(Unit u)
        {
            return Math.Sqrt(u.SpeedX*u.SpeedX + u.SpeedY*u.SpeedY);
        }

        public double TurnRound(double x)
        {
            if (x < -1)
                return -1;
            if (x > 1)
                return 1;
            return x;
        }
    }
}