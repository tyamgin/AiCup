using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
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

        private static void DrawCircle(Color color, double x, double y, double radius, float width = 0)
        {
            var pen = width > 0 ? new Pen(color, width) : new Pen(color);
            _graphics.DrawEllipse(pen, _X(x - radius), _Y(y - radius), _S(radius * 2), _S(radius * 2));
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
        public static List<Tuple<Point, double>> DangerPoints;

        public class Color01
        {
            public double R, G, B;

            public Color01(double r, double g, double b)
            {
                R = r;
                G = g;
                B = b;
            }

            public Color ToColor()
            {
                return Color.FromArgb((int)(255 * R), (int)(255 * G), (int)(255 * B));
            }
        }

        static private Color01 _grad2(Color01 col1, Color01 col2, double x)
        {
            return new Color01(
                (col2.R - col1.R) * x + col1.R,
                (col2.G - col1.G) * x + col1.G,
                (col2.B - col1.B) * x + col1.B
            );
        }

        public static Color01[] BadColors = new[] {
            new Color01(0x8B / 255.0, 0, 0),// red!!
            new Color01(1, 0, 0),// red
            new Color01(1, 69 / 255.0, 0),// orange
            new Color01(1, 1, 0),// yellow
            new Color01(1, 1, 1),// white
        };

        public static Color01[] GoodColors = new[] {
            new Color01(1, 1, 1),// white
            new Color01(0, 1, 0),// green
        };

        static Color01 _grad(Color01[] colors, double x)
        {
            var delta = 1.0 / (colors.Length - 1);
            for (var i = 0; i < colors.Length - 1; i++)
            {
                var left = delta*i;
                var right = delta * (i + 1);
                if (left <= x && x <= right)
                {
                    return _grad2(colors[i], colors[i + 1], (x - left) * (colors.Length - 1));
                }
            }
            throw new Exception("wrong x ranges");
        }

        public static int DrawSince = 0;

        public static void Draw()
        {
            if (_form.InvokeRequired)
            {
                _form.BeginInvoke(new DrawDelegate(Draw), new object[] {});
                return;
            }

            if (MyStrategy.World.TickIndex >= DrawSince)
                _draw();
            SegmentsDrawQueue.Clear();
        }

        private static void _draw()
        {
            var panel = _form.panel;

            _form.tickLabel.Text = MyStrategy.World.TickIndex + "";
            
            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            _graphics = Graphics.FromImage(drawArea);

            foreach(var seg in MyStrategy.Roads)
                DrawLine(Color.Khaki, seg.A.X, seg.A.Y, seg.B.X, seg.B.Y);

            var maxDanger = DangerPoints.Max(x => x.Item2);
            var minDanger = DangerPoints.Min(x => x.Item2);

            if (maxDanger > Const.Eps)
            {
                foreach (var t in DangerPoints)
                {
                    var pt = t.Item1;
                    var danger = t.Item2;
                    var color = (danger >= 0 ? _grad(BadColors, 1 - danger/maxDanger) : _grad(GoodColors, danger / minDanger)).ToColor();
                    FillCircle(color, pt.X, pt.Y, 4);
                }
            }

            // wizards
            foreach(var wizard in MyStrategy.Wizards)
            {
                var w = MyStrategy.World.Wizards.FirstOrDefault(x => x.Id == wizard.Id);
                var color = w.IsMe ? Color.Red : (wizard.Faction == MyStrategy.Self.Faction ? Color.Blue : Color.DarkOrange);

                DrawCircle(color, w.X, w.Y, wizard.Radius);
                var d = 7;
                for(var i = 0; i < d; i++)
                    if (wizard.RemainingStaffCooldownTicks >= MyStrategy.Game.StaffCooldownTicks - d)
                        DrawCircle(color, w.X, w.Y, wizard.Radius - (d - i));

                DrawText(w.Life + "", 15, Brushes.Red, wizard.X - 20, wizard.Y - 35);
                DrawText(w.Mana + "", 15, Brushes.Blue, wizard.X - 20, wizard.Y - 15);
                DrawText(w.Xp + "", 15, Brushes.Green, wizard.X - 20, wizard.Y + 5);

                DrawPie(color, wizard.X, wizard.Y, MyStrategy.Game.StaffRange, -MyStrategy.Game.StaffSector / 2.0 + wizard.Angle, MyStrategy.Game.StaffSector / 2.0 + wizard.Angle);
                DrawPie(color, wizard.X, wizard.Y, wizard.CastRange, -MyStrategy.Game.StaffSector / 2.0 + wizard.Angle, MyStrategy.Game.StaffSector / 2.0 + wizard.Angle);


            }

            // minions
            foreach (var minion in MyStrategy.Minions)
            {
                var color = minion.IsTeammate ? Color.Blue : (minion.Faction == Faction.Neutral ? Color.Fuchsia : Color.DarkOrange);

                DrawCircle(color, minion.X, minion.Y, minion.Radius);

                var to = Point.ByAngle(minion.Angle) * minion.Radius + minion;
                DrawLine(color, minion.X, minion.Y, to.X, to.Y, 2);

                if (minion is AOrc)
                {
                    DrawCircle(Color.Black, minion.X, minion.Y, MyStrategy.Game.OrcWoodcutterAttackRange);
                }

                DrawText(minion.Life + "", 15, Brushes.Red, minion.X - 10, minion.Y - 30);

            }

            // projectiles
            foreach (var projectile in MyStrategy.World.Projectiles)
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

            // buildings
            foreach (var building in BuildingsObserver.Buildings)
            {
                FillCircle(building.IsTeammate ? Color.Blue : Color.DarkOrange, building.X, building.Y, building.Radius);
                DrawText(building.Life + "", 15, Brushes.Red, building.X - 10, building.Y - 30);
                if (building.IsBesieded)
                    DrawText("rush", 13, Brushes.Black, building.X - 10, building.Y);
            }

            // bonuses
            foreach (var bonus in BonusesObserver.Bonuses)
            {
                var color = bonus.Type == BonusType.Empower
                    ? Color.Blue
                    : bonus.Type == BonusType.Haste 
                        ? Color.Aquamarine 
                        : Color.MidnightBlue;
                FillCircle(color, bonus.X, bonus.Y, bonus.Radius);
            }

            // map ranges
            DrawLine(Color.Black, 1, 1, 1, Const.MapSize - 1);
            DrawLine(Color.Black, 1, Const.MapSize - 1, Const.MapSize - 1, Const.MapSize - 1);
            DrawLine(Color.Black, Const.MapSize - 1, 1, Const.MapSize - 1, Const.MapSize - 1);
            DrawLine(Color.Black, Const.MapSize - 1, 1, 1, 1);

            // minions spawns
            foreach (var pt in MagicConst.MinionAppearencePoints)
            {
                FillCircle(Color.Khaki, pt.X, pt.Y, 20);
                FillCircle(Color.Khaki, Const.MapSize - pt.X, Const.MapSize - pt.Y, 20);
            }

            foreach (var pt in MyStrategy.GetFreePoints())
            {
                FillCircle(Color.Bisque, pt.X, pt.Y, 2);
            }

            try
            {
                foreach (var seg in SegmentsDrawQueue)
                {
                    var points = seg[0] as List<Point>;
                    var pen = seg[1] as Pen;
                    float width = seg.Length > 2 ? Convert.ToSingle(seg[2]) : 0F;
                    for (var i = 1; i < points.Count; i++)
                        DrawLine(pen.Color, points[i].X, points[i].Y, points[i - 1].X, points[i - 1].Y, width);
                }
            }
            catch (Exception)
            {
                // TODO: Падает если не успевает отрисоваться. Необходима синхронизация потоков.
            }
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
            if (_lookY > Const.MapSize - _scale * _form.panel.Height)
                _lookY = Const.MapSize - _scale * _form.panel.Height;

            _lookX = p.X - _scale*_form.panel.Width/2;
            if (_lookX < 0)
                _lookX = 0;
            if (_lookX > Const.MapSize - _scale * _form.panel.Width)
                _lookX = Const.MapSize - _scale * _form.panel.Width;
        }
    }
}
