using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Visualizer
{
    public class Visualizer
    {
        public static void CreateForm()
        {
            if (_form == null)
            {
                var thread = new Thread(_showWindow);
                thread.Start();
                Thread.Sleep(2000);
            }
        }

        private static void _showWindow()
        {
            _form = new VisualizerForm();
            _form.ShowDialog();
            _form.Focus();
        }

        private static VisualizerForm _form;
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

        private static void DrawRect(Color color, double x, double y, double w, double h, float width = 0)
        {
            _graphics.DrawRectangle(new Pen(color, width), _X(x), _Y(y), _S(w), _S(h));
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

        private static void FillPie(Color color, double x, double y, double r, double startAngle, double endAngle)
        {
            startAngle = Geom.ToDegrees(startAngle);
            endAngle = Geom.ToDegrees(endAngle);
            _graphics.FillPie(new SolidBrush(color), new Rectangle(_X(x - r), _Y(y - r), _S(2 * r), _S(2 * r)), (float)startAngle, (float)(endAngle - startAngle));
        }

        private static void DrawText(string text, double size, Brush brush, double x, double y)
        {
            var font = new Font("Comic Sans MS", _S(size));
            _graphics.DrawString(text, font, brush, _X(x), _Y(y));
        }

        public static List<object[]> SegmentsDrawQueue = new List<object[]>();

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

        public class RectangularSelection : Rect
        {
            public int Tick;
        }

        public class SelectionVector
        {
            public Point Start, End;
            public int Tick;
        }

        public static List<RectangularSelection> Selections = new List<RectangularSelection>();
        public static List<SelectionVector> Vectors = new List<SelectionVector>(); 

        public static int DrawSince { get; set; } = 0;

        public static bool Done;

        public static void Draw()
        {
            Done = false;

            if (_form.InvokeRequired)
            {
                _form.BeginInvoke(new DrawDelegate(Draw), new object[] {});
                return;
            }

            if (MyStrategy.World.TickIndex >= DrawSince)
                _draw();
            SegmentsDrawQueue.Clear();
            Done = true;
        }

        private static void _draw()
        {
            var panel = _form.panel;

            _form.tickLabel.Text = MyStrategy.World.TickIndex + "";
            
            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            _graphics = Graphics.FromImage(drawArea);

            // туман войны
            // TODO

            _form.scoreLabel.Text = MyStrategy.Me.Score + " : " + MyStrategy.Opp.Score;

            for (var j = 0; j < MyStrategy.TerrainType.Length; j++)
            {
                for (var i = 0; i < MyStrategy.TerrainType[0].Length; i++)
                {
                    var type = MyStrategy.TerrainType[i][j];
                    Color? color = null;
                    switch (type)
                    {
                        case TerrainType.Swamp:
                            color = Color.Bisque;
                            break;
                        case TerrainType.Forest:
                            color = Color.PaleGreen;
                            break;
                    }
                    if (color != null)
                        FillRect(color.Value, i*G.CellSize, j*G.CellSize, G.CellSize, G.CellSize);
                }
            }


            for (var j = 0; j < MyStrategy.WeatherType.Length; j++)
            {
                for (var i = 0; i < MyStrategy.WeatherType[0].Length; i++)
                {
                    var type = MyStrategy.WeatherType[i][j];

                    var size = G.CellSize;
                    var dx = size*0.5 + i * G.CellSize;
                    var dy = size*0.45 + j * G.CellSize;
                    var bigRad = size*0.25;
                    var smallRad = size*0.17;
                    var rainRad = size/20;

                    var cloudColor = Color.LightBlue;
                    if (type == WeatherType.Cloud || type == WeatherType.Rain)
                    {
                        FillPie(cloudColor, dx, dy, bigRad, Math.PI, Math.PI*2);
                        FillPie(cloudColor, dx - bigRad, dy, smallRad, 0, 2*Math.PI);
                        FillPie(cloudColor, dx + bigRad, dy, smallRad, 0, 2*Math.PI);
                        FillRect(cloudColor, dx - bigRad, dy, 2*bigRad, smallRad);
                    }
                    if (type == WeatherType.Rain)
                    {
                        FillCircle(cloudColor, dx + size * 0.2, dy + bigRad + size * 0.02, rainRad);
                        FillCircle(cloudColor, dx - size * 0.1, dy + bigRad + size * 0.02, rainRad);
                        FillCircle(cloudColor, dx + size * 0.05, dy + bigRad + size * 0.10, rainRad);
                        FillCircle(cloudColor, dx - size * 0.25, dy + bigRad + size * 0.10, rainRad);
                    }
                }
            }

            // selections
            if (MyStrategy.ResultingMove.Right > 0 && MyStrategy.ResultingMove.Bottom > 0)
            {
                Selections.Add(new RectangularSelection
                {
                    X = MyStrategy.ResultingMove.Left,
                    Y = MyStrategy.ResultingMove.Top,
                    X2 = MyStrategy.ResultingMove.Right,
                    Y2 = MyStrategy.ResultingMove.Bottom,
                    Tick = MyStrategy.World.TickIndex,
                });
            }
            if (MyStrategy.ResultingMove.Action == ActionType.Move)
            {
                var selectedVehicles = VehiclesObserver.Vehicles.Where(x => x.IsSelected).ToArray();
                if (selectedVehicles.Length == 0)
                    throw new Exception("Trying to move 0 vehicles");
                var start = MyStrategy.GetAvg(selectedVehicles);
                Vectors.Add(new SelectionVector
                {
                    Start = start,
                    End = start + new Point(MyStrategy.ResultingMove.X, MyStrategy.ResultingMove.Y),
                    Tick = MyStrategy.World.TickIndex,
                });
            }

            var selTicksWait = 10;
            for (var i = 0; i < Selections.Count; i++)
            {
                var rect = Selections[i];
                var tm = MyStrategy.World.TickIndex - rect.Tick;
                DrawRect(Color.Black, rect.X, rect.Y, rect.Width, rect.Height, selTicksWait - tm);

                if (tm + 1 >= selTicksWait)
                {
                    Selections.RemoveAt(i);
                    i--;
                }
            }

            var selTicksWait2 = 20;
            for (var i = 0; i < Vectors.Count; i++)
            {
                var vec = Vectors[i];
                var tm = MyStrategy.World.TickIndex - vec.Tick;
                DrawLine(Color.Black, vec.Start.X, vec.Start.Y, vec.End.X, vec.End.Y, selTicksWait2 - tm);

                if (tm + 1 >= selTicksWait2)
                {
                    Vectors.RemoveAt(i);
                    i--;
                }
            }

            foreach (var nuclear in MyStrategy.Environment.Nuclears)
            {
                DrawCircle(Color.OrangeRed, nuclear.X, nuclear.Y, nuclear.Radius, 1);
                FillPie(Color.OrangeRed, nuclear.X, nuclear.Y, nuclear.Radius, 0, 2 * Math.PI * (G.TacticalNuclearStrikeDelay - nuclear.RemainingTicks) / G.TacticalNuclearStrikeDelay);
                var veh = VehiclesObserver.Vehicles.FirstOrDefault(x => x.Id == nuclear.VehicleId);
                if (veh != null)
                    FillCircle(Color.Black, veh.X, veh.Y, veh.Radius * 2);
            }

            // clusters
            foreach (var cluster in MyStrategy.Environment.GetClusters(false, Const.ClusteringMargin))
            {
                var rect = MyStrategy.GetUnitsBoundingRect(cluster);
                DrawRect(Color.Maroon, rect.X, rect.Y, rect.Width, rect.Height);
            }

            // vehicles
            foreach (var veh in VehiclesObserver.Vehicles)
            {
                var color = Color.Black;
                switch (veh.Type)
                {
                    case VehicleType.Arrv:
                        color = Color.Blue;
                        break;
                    case VehicleType.Fighter:
                        color = Color.Green;
                        break;
                    case VehicleType.Helicopter:
                        color = Color.DarkOrchid;
                        break;
                    case VehicleType.Ifv:
                        color = Color.Red;
                        break;
                    case VehicleType.Tank:
                        color = Color.Orange;
                        break;
                }
                if (veh.RemainingAttackCooldownTicks > 0)
                    color = Color.FromArgb(150, color);

                DrawCircle(color, veh.X, veh.Y, veh.Radius, 1);
                FillPie(color, veh.X, veh.Y, veh.Radius, 0, Math.PI * 2 * veh.Durability / G.MaxDurability);

                if (veh.IsSelected)
                    DrawCircle(color,  veh.X, veh.Y, veh.Radius + 1, 1);

                if (!veh.IsMy)
                    FillCircle(Color.Black, veh.X, veh.Y, 0.6);
            }


            // map ranges
            DrawLine(Color.Black, 0, 0, 0, G.MapSize);
            DrawLine(Color.Black, 0, G.MapSize, G.MapSize, G.MapSize);
            DrawLine(Color.Black, G.MapSize, 0, G.MapSize, G.MapSize);
            DrawLine(Color.Black, G.MapSize, 0, 0, 0);

            foreach (var seg in SegmentsDrawQueue)
            {
                var points = seg[0] as List<Point>;
                var pen = seg[1] as Pen;
                float width = seg.Length > 2 ? Convert.ToSingle(seg[2]) : 0F;
                for (var i = 1; i < points.Count; i++)
                    DrawLine(pen.Color, points[i].X, points[i].Y, points[i - 1].X, points[i - 1].Y, width);
            }
        }

        public static double Zoom
        {
            get { return _scale; }
            set
            {
                if (value > 0)
                    _scale = value;
            }
        }

        public static bool Pause = false;

        private static Point _lootAt;

        public static Point LookAt
        {
            set
            {
                _lootAt = value;
                _lookY = value.Y - _scale*_form.panel.Height/2;
                if (_lookY < 0)
                    _lookY = 0;
                if (_lookY > G.MapSize - _scale*_form.panel.Height)
                    _lookY = G.MapSize - _scale*_form.panel.Height;

                _lookX = value.X - _scale*_form.panel.Width/2;
                if (_lookX < 0)
                    _lookX = 0;
                if (_lookX > G.MapSize - _scale*_form.panel.Width)
                    _lookX = G.MapSize - _scale*_form.panel.Width;
            }
            get { return _lootAt; }
        }

        private static double _lookX = 0, _lookY = 0, _scale = 1.5;

        private static int _X(double x)
        {
            if (_lookX > 0)
                x -= _lookX;
            
            return (int)(x / _scale);
        }

        private static int _Y(double y)
        {
            if (_lookY > 0)
                y -= _lookY;
            return (int)(y / _scale);
        }

        private static int _S(double x)
        {
            return (int)Math.Ceiling(x / _scale);
        }
    }
}
