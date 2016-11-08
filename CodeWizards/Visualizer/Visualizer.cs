﻿using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Threading;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Visualizer
{
    public class Visualizer
    {
        public static void CreateForm()
        {
            if (_form == null)
            {
                _thread = new Thread(_showWindow);
                _thread.Start();
                Thread.Sleep(2000);
            }
        }

        private static void _showWindow()
        {
            _form = new MainForm();
            _form.ShowDialog();
            _form.Focus();
        }

        private static MainForm _form;
        private static Thread _thread;
        private static Graphics _graphics;

        private delegate void DrawDelegate();

        private static void DrawCircle(Color color, double x, double y, double radius)
        {
            _graphics.DrawEllipse(new Pen(color), _X(x - radius), _Y(y - radius), _S(radius * 2), _S(radius * 2));
        }

        private static void FillCircle(Color color, double x, double y, double radius)
        {
            _graphics.FillEllipse(new SolidBrush(color), _X(x - radius), _Y(y - radius), _S(radius * 2), _S(radius * 2));
        }

        private static void FillRect(Color color, double x, double y, double w, double h)
        {
            _graphics.FillRectangle(new SolidBrush(color), _X(x), _Y(y), _S(w), _S(h));
        }

        private static void DrawLine(Color color, double x, double y, double X, double Y, float width = 0F)
        {
            _graphics.DrawLine(new Pen(color, width), _X(x), _Y(y), _X(X), _Y(Y));
        }

        private static void DrawPie(Color color, double x, double y, double r, double startAngle, double endAngle, float width = 0F)
        {
            startAngle = Geom.ToDegrees(startAngle);
            endAngle = Geom.ToDegrees(endAngle);
            _graphics.DrawPie(new Pen(color), new Rectangle(_X(x - r), _Y(y - r), _S(2*r), _S(2*r)), (float)startAngle, (float)(endAngle - startAngle));
        }

        private static void DrawText(string text, double size, Brush brush, double x, double y)
        {
            var font = new Font("Comic Sans MS", _S(size));
            _graphics.DrawString(text, font, brush, _X(x), _Y(y));
        }

        private static double _lookX = 0, _lookY = 0, _scale = 1.5;

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
            return (int)Math.Ceiling(x / _scale);
        }

        public static List<object[]> SegmentsDrawQueue = new List<object[]>();

        public static void Draw()
        {
            if (_form.InvokeRequired)
            {
                _form.BeginInvoke(new DrawDelegate(Draw), new object[] { });
                return;
            }

            var panel = _form.panel;

            _form.tickLabel.Text = MyStrategy.world.TickIndex + "";
            
            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            _graphics = Graphics.FromImage(drawArea);

            // wizards
            foreach(var wizard in MyStrategy.world.Wizards)
            {
                var color = wizard.IsMe ? Color.Red : (wizard.Faction == MyStrategy.self.Faction ? Color.Blue : Color.DarkOrange);

                DrawCircle(color, wizard.X, wizard.Y, wizard.Radius);

                var to = Point.ByAngle(wizard.Angle) * wizard.Radius + new Point(wizard);
                DrawLine(color, wizard.X, wizard.Y, to.X, to.Y, 2);

                DrawText(wizard.Life + "", 15, Brushes.Red, wizard.X - 10, wizard.Y - 30);
                DrawText(wizard.Mana + "", 15, Brushes.Blue, wizard.X - 10, wizard.Y - 10);

                DrawPie(color, wizard.X, wizard.Y, MyStrategy.game.StaffRange, -MyStrategy.game.StaffSector / 2.0 + wizard.Angle, MyStrategy.game.StaffSector / 2.0 + wizard.Angle);
                DrawPie(color, wizard.X, wizard.Y, wizard.CastRange, -MyStrategy.game.StaffSector / 2.0 + wizard.Angle, MyStrategy.game.StaffSector / 2.0 + wizard.Angle);
            }

            // minions
            foreach (var minion in MyStrategy.world.Minions)
            {
                var color = minion.Faction == MyStrategy.self.Faction ? Color.Blue : (minion.Faction == Faction.Neutral ? Color.Fuchsia : Color.DarkOrange);

                DrawCircle(color, minion.X, minion.Y, minion.Radius);

                var to = Point.ByAngle(minion.Angle) * minion.Radius + new Point(minion);
                DrawLine(color, minion.X, minion.Y, to.X, to.Y, 2);

                DrawText(minion.Life + "", 15, Brushes.Red, minion.X - 10, minion.Y - 30);

            }

            // projectiles
            foreach (var projectile in MyStrategy.world.Projectiles)
            {
                var color = projectile.Type == ProjectileType.MagicMissile
                    ? Color.Blue
                    : projectile.Type == ProjectileType.Dart
                        ? Color.Black
                        : Color.DarkOrange;
                FillCircle(color, projectile.X, projectile.Y, projectile.Radius);
            }

            // trees
            foreach (var tree in TreesObserver.Trees)
            {
                FillCircle(Color.Chartreuse, tree.X, tree.Y, tree.Radius);
            }

            foreach (var building in MyStrategy.world.Buildings)
            {
                FillCircle(building.Faction == MyStrategy.self.Faction ? Color.Blue : Color.DarkOrange, building.X, building.Y, building.Radius);
                DrawText(building.Life + "", 15, Brushes.Red, building.X - 10, building.Y - 30);
            }

            // map ranges
            DrawLine(Color.Black, 1, 1, 1, Const.Height - 1);
            DrawLine(Color.Black, 1, Const.Height - 1, Const.Width - 1, Const.Height - 1);
            DrawLine(Color.Black, Const.Width - 1, 1, Const.Width - 1, Const.Height - 1);
            DrawLine(Color.Black, Const.Width - 1, 1, 1, 1);

            foreach (var pt in MyStrategy.GetFreePoints())
            {
                FillCircle(Color.Aqua, pt.X, pt.Y, 2);
            }

            foreach (var seg in SegmentsDrawQueue)
            {
                var points = seg[0] as List<Point>;
                var pen = seg[1] as Pen;
                for(var i = 1; i < points.Count; i++)
                    DrawLine(pen.Color, points[i].X, points[i].Y, points[i - 1].X, points[i - 1].Y);
            }

            SegmentsDrawQueue.Clear();
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

        public static bool Pause = false;

        public static void LookUp(Point p, double scale = -1)
        {
            Zoom = scale;

            _lookY = p.Y - _scale*_form.panel.Height/2;
            if (_lookY < 0)
                _lookY = 0;
            if (_lookY > Const.Height - _scale * _form.panel.Height)
                _lookY = Const.Height - _scale * _form.panel.Height;

            _lookX = p.X - _scale*_form.panel.Width/2;
            if (_lookX < 0)
                _lookX = 0;
            if (_lookX > Const.Width - _scale * _form.panel.Width)
                _lookX = Const.Width - _scale * _form.panel.Width;
        }
    }
}
