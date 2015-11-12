using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private void DrawMap()
        {
            if (_form == null)
            {
                _thread = new Thread(_ShowWindow);
                _thread.Start();
                Thread.Sleep(2000);
            }
        }

        private static MapForm _form;
        private static Thread _thread;
        private Graphics _graphics;

        private delegate void DrawDelegate();

        private void FillCircle(Brush brush, double x, double y, double radius)
        {
            _graphics.FillEllipse(brush, _X(x - radius), _Y(y - radius), _S(radius * 2), _S(radius * 2));
        }

        private static double _lookX = 0, _lookY = 0, _scale = 4;

        private static int _X(double x)
        {
            return (int)((x - _lookX) / _scale);
        }

        private static int _Y(double y)
        {
            return (int)((y - _lookY) / _scale);
        }

        private static int _S(double x)
        {
            return (int)(x / _scale);
        }

        private void FillRect(Brush brush, double x, double y, double w, double h)
        {
            _graphics.FillRectangle(brush, _X(x), _Y(y), _S(w), _S(h));
        }

        private void DrawLine(Brush brush, double x, double y, double X, double Y, float width = 0F)
        {
            _graphics.DrawLine(new Pen(brush, width), _X(x), _Y(y), _X(X), _Y(Y));
        }

        private void draw()
        {
            if (_form.InvokeRequired)
            {
                _form.BeginInvoke(new DrawDelegate(draw), new object[] { });
                return;
            }

            var panel = _form.panel;

            _form.tickLabel.Text = world.Tick + "";
            
            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            _graphics = Graphics.FromImage(drawArea);

            LookUp(new Point(world.Cars.FirstOrDefault(x => x.IsTeammate)));

            // Tiles
            for (var i = 0; i < world.Height; i++)
            {
                for (var j = 0; j < world.Width; j++)
                {
                    double x = j*game.TrackTileSize,
                        y = i*game.TrackTileSize;

                    if (waypoints.Any(cell => cell.Same(i, j)))
                    {
                        var brush = GetNextWayPoint().Same(i, j)
                            ? Brushes.Chartreuse
                            : Brushes.FloralWhite;
                        FillRect(brush, x, y, game.TrackTileSize, game.TrackTileSize);
                    }

                    double delta = game.TrackTileSize - game.TrackTileMargin;
                    FillRect(Brushes.Black, x, y, game.TrackTileMargin, game.TrackTileMargin);
                    FillRect(Brushes.Black, x, y + delta, game.TrackTileMargin, game.TrackTileMargin);
                    FillRect(Brushes.Black, x + delta, y + delta, game.TrackTileMargin, game.TrackTileMargin);
                    FillRect(Brushes.Black, x + delta, y, game.TrackTileMargin, game.TrackTileMargin);

                    var tt = tiles[i, j];

                    if (tt == TileType.Empty)
                    {
                        FillRect(Brushes.Black, x, y, game.TrackTileSize, game.TrackTileSize);
                    }
                    else
                    {
                        if (!_tileFreeLeft(tt))
                        {
                            FillRect(Brushes.Black, x, y, game.TrackTileMargin, game.TrackTileSize);
                        }
                        if (!_tileFreeTop(tt))
                        {
                            FillRect(Brushes.Black, x, y, game.TrackTileSize, game.TrackTileMargin);
                        }
                        if (!_tileFreeRight(tt))
                        {
                            FillRect(Brushes.Black, x + delta, y, game.TrackTileMargin, game.TrackTileSize);
                        }
                        if (!_tileFreeBottom(tt))
                        {
                            FillRect(Brushes.Black, x, y + delta, game.TrackTileSize, game.TrackTileMargin);
                        }
                    }
                }
            }

            // Cars
            foreach (var car in world.Cars)
            {
                var rect = new ACar(car).GetRect();
                for (var i = 0; i < 4; i++)
                {
                    DrawLine(car.IsTeammate ? Brushes.Green : Brushes.Red, rect[i].X, rect[i].Y, rect[(i + 1) % 4].X, rect[(i + 1) % 4].Y, 2);
                }
                var to = Point.ByAngle(car.Angle)*100 + new Point(car);
                DrawLine(Brushes.Black, car.X, car.Y, to.X, to.Y, 2);
            }

            // Oil
            foreach (var stick in world.OilSlicks)
                FillCircle(Brushes.Black, stick.X, stick.Y, stick.Radius);
        }

        public static double Zoom
        {
            get
            {
                return _scale;
            }
            set
            {
                if (value > 0)
                    _scale = value;
            }
        }

        public static bool Debug = false;
        public static bool Pause = false;

        public void LookUp(Point p, double scale = -1)
        {
            Zoom = scale;

            _lookY = p.Y - _scale*_form.panel.Height/2;
            if (_lookY < 0)
                _lookY = 0;
            if (_lookY > world.Height*game.TrackTileSize - _scale*_form.panel.Height/2)
                _lookY = world.Height*game.TrackTileSize - _scale*_form.panel.Height/2;

            _lookX = p.X - _scale*_form.panel.Width/2;
            if (_lookX < 0)
                _lookX = 0;
            if (_lookX > world.Width*game.TrackTileSize - _scale*_form.panel.Width/2)
                _lookX = world.Width*game.TrackTileSize - _scale*_form.panel.Width/2;
        }

        private static void _ShowWindow()
        {
            _form = new MapForm();
            _form.ShowDialog();
            _form.Focus();
        }
    }
}
