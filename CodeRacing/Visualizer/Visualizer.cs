using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class Visualizer
    {
        private static int _myCarsCount;
        private static int _renderTick;

        public static void CreateForm(int myCarsCount)
        {
            if (_form == null)
            {
                _myCarsCount = myCarsCount;
                _renderTick = 0;

                _thread = new Thread(_showWindow);
                _thread.Start();
                Thread.Sleep(2000);
            }
        }

        private static void _showWindow()
        {
            _form = new MapForm();
            _form.ShowDialog();
            _form.Focus();
        }

        private static MapForm _form;
        private static Thread _thread;
        private static Graphics _graphics;

        private delegate void DrawDelegate();

        private static void DrawCircle(Pen pen, double x, double y, double radius)
        {
            _graphics.DrawEllipse(pen, _X(x - radius), _Y(y - radius), _S(radius * 2), _S(radius * 2));
        }

        private static void FillCircle(Brush brush, double x, double y, double radius)
        {
            _graphics.FillEllipse(brush, _X(x - radius), _Y(y - radius), _S(radius * 2), _S(radius * 2));
        }

        private static void FillRect(Brush brush, double x, double y, double w, double h)
        {
            _graphics.FillRectangle(brush, _X(x), _Y(y), _S(w), _S(h));
        }

        private static void DrawLine(Brush brush, double x, double y, double X, double Y, float width = 0F)
        {
            _graphics.DrawLine(new Pen(brush, width), _X(x), _Y(y), _X(X), _Y(Y));
        }

        private static void DrawText(string text, double size, Brush brush, double x, double y)
        {
            var font = new Font("Comic Sans MS", _S(size));
            _graphics.DrawString(text, font, brush, _X(x), _Y(y));
        }

        private static double _lookX = 0, _lookY = 0, _scale = 6;

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

        public static ArrayList SegmentsDrawQueue = new ArrayList();
        public static List<Tuple<Brush, ACircularUnit>> CircleFillQueue = new List<Tuple<Brush, ACircularUnit>>();

        public static void Draw()
        {
            if (_form.InvokeRequired)
            {
                _form.BeginInvoke(new DrawDelegate(Draw), new object[] { });
                return;
            }

            _renderTick++;
            if ((_renderTick - 1) % _myCarsCount != _myCarsCount - 1)
                return;

            var panel = _form.panel;

            _form.tickLabel.Text = MyStrategy.world.Tick + "";
            
            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            _graphics = Graphics.FromImage(drawArea);

            if (_myCarsCount == 1)
            {
                _form.jeepRadioButton.Enabled = false;
                _form.buggyRadioButton.Enabled = false;
                LookUp(new Point(MyStrategy.world.Cars.FirstOrDefault(x => x.IsTeammate)));
            }
            else
            {
                LookUp(
                    new Point(
                        MyStrategy.world.Cars.FirstOrDefault(
                            x =>
                                x.IsTeammate &&
                                (x.Type == CarType.Jeep && _form.jeepRadioButton.Checked ||
                                 x.Type == CarType.Buggy && _form.buggyRadioButton.Checked))));
            }

            //var myNextWp = MyStrategy.GetNextWayPoint(self);
            //FillRect(Brushes.Aqua, myNextWp.J * MyStrategy.game.TrackTileSize, myNextWp.I * MyStrategy.game.TrackTileSize, MyStrategy.game.TrackTileSize, MyStrategy.game.TrackTileSize);

            // tiles
            foreach (var tile in MyStrategy.MyTiles)
            {
                var margin = MyStrategy.game.TrackTileMargin;
                var tsize = MyStrategy.game.TrackTileSize;

                var dx = MyStrategy.game.TrackTileSize*tile.J;
                var dy = MyStrategy.game.TrackTileSize*tile.I;

                if (tile.Type == TileType.Unknown)
                    FillRect(Brushes.DarkGray, dx, dy, tsize, tsize);
                if (tile.Type == TileType.Empty)
                    FillRect(Brushes.Black, dx, dy, tsize, tsize);

                if (!tile.IsFreeLeft)
                    FillRect(Brushes.Black, dx, dy, margin, tsize);
                if (!tile.IsFreeTop)
                    FillRect(Brushes.Black, dx, dy, tsize, margin);
                if (!tile.IsFreeRight)
                    FillRect(Brushes.Black, dx + tsize - margin, dy, margin, tsize);
                if (!tile.IsFreeBottom)
                    FillRect(Brushes.Black, dx, dy + tsize - margin, tsize, margin);

                foreach (var part in tile.Parts)
                {
                    switch (part.Type)
                    {
                        case TilePartType.Circle:
                            FillCircle(Brushes.Black, part.Circle.X, part.Circle.Y, part.Circle.Radius);
                            break;
                        case TilePartType.Segment:
                            DrawLine(Brushes.DarkRed, part.Start.X, part.Start.Y, part.End.X, part.End.Y, 1);
                            break;
                        default:
                            throw new Exception("Unknown TilePartType");
                    }
                }
            }

            // Bonuses
            foreach (var bonus in MyStrategy.world.Bonuses)
            {
                var rect = new ABonus(bonus).GetRect();
                for (var i = 0; i < 4; i++)
                {
                    Brush brush;
                    if (bonus.Type == BonusType.PureScore)
                        brush = Brushes.OrangeRed;
                    else if (bonus.Type == BonusType.RepairKit)
                        brush = Brushes.LimeGreen;
                    else if (bonus.Type == BonusType.OilCanister)
                        brush = Brushes.DarkSlateGray;
                    else if (bonus.Type == BonusType.NitroBoost)
                        brush = Brushes.Blue;
                    else if (bonus.Type == BonusType.AmmoCrate)
                        brush = Brushes.DarkGoldenrod;
                    else
                        throw new Exception("Unknown BonusType");
                        
                    DrawLine(brush, rect[i].X, rect[i].Y, rect[(i + 1) % 4].X, rect[(i + 1) % 4].Y, 2);
                }
            }

            // Cars
            foreach (var car in MyStrategy.world.Cars)
            {
                var isAvtive = DurabilityObserver.IsActive(car);
                var rect = new ACar(car).GetRectEx(isAvtive ? 0 : 1);
                for (var i = 0; i < 4; i++)
                {
                    DrawLine(car.IsTeammate ? Brushes.Green : Brushes.Red, 
                        rect[i].X, rect[i].Y, rect[(i + 1)%4].X, rect[(i + 1)%4].Y, 
                        isAvtive ? 2 : 1);
                }
                var to = Point.ByAngle(car.Angle)*100 + new Point(car);
                DrawLine(Brushes.Black, car.X, car.Y, to.X, to.Y, car.Type == CarType.Buggy ? 2 : 6);
                DrawText("~" + car.ProjectileCount, 50, Brushes.Black, car.X, car.Y);
            }

            // Oil
            foreach (var stick in MyStrategy.world.OilSlicks)
                FillCircle(Brushes.Black, stick.X, stick.Y, stick.Radius);

            // Nitro
            foreach (var car in MyStrategy.world.Cars)
            {
                if (car.RemainingNitroTicks > 0)
                    FillCircle(Brushes.Blue, car.X, car.Y, 40);   
            }



            // Canisters
            foreach (var car in MyStrategy.world.Cars)
            {
                if (car.OilCanisterCount == 0)
                    continue;
                var canisterPen = car.RemainingOilCooldownTicks == 0 ? Pens.DarkSlateGray : Pens.Lavender;
                var slick = new AOilSlick(new ACar(car));
                DrawCircle(canisterPen, slick.X, slick.Y, slick.Radius);
                if (car.RemainingOiledTicks > 0)
                    FillCircle(Brushes.Black, car.X, car.Y, 30);
            }


            // Projectiles
            foreach (var pr in MyStrategy.world.Projectiles)
            {
                FillCircle(Brushes.OrangeRed, pr.X, pr.Y, pr.Radius);
            }

            // Segments
            try
            {
                foreach (var _el in SegmentsDrawQueue)
                {
                    var el = _el as object[];
                    var brush = el[0] as Brush;
                    var line = el[1] as Points;
                    var width = Convert.ToDouble(el[2]);
                    for (var i = 1; i < line.Count; i++)
                        DrawLine(brush, line[i - 1].X, line[i - 1].Y, line[i].X, line[i].Y, (float)width);
                }
            }
            catch (Exception)
            {
                var tmp = 0;
            }

            // Circles
            foreach (var el in CircleFillQueue)
            {
                var brush = el.Item1;
                var circle = el.Item2;
                FillCircle(brush, circle.X, circle.Y, circle.Radius);
            }

            CircleFillQueue.Clear();
            SegmentsDrawQueue.Clear();
        }
        public static void DrawWay(Car self, Moves stack, Brush brush, double width)
        {
            if (stack == null)
                return;
            stack = stack.Clone();
            var drawPts = new Points();
            var drawModel = new ACar(self);
            while (stack.Count > 0)
            {
                var m = stack[0];

                drawPts.Add(new Point(drawModel));
                AMove.ModelMove(drawModel, m, new PassedInfo(), MyStrategy.Bonuses, MyStrategy.OilSlicks, MyStrategy.Tires, MyStrategy.OpponentsCars);
                m.Times--;
                stack.Normalize();
            }
            SegmentsDrawQueue.Add(new object[] { brush, drawPts, width });
        }

        public static void DrawWays(Car self, Moves[] stacks, int sel)
        {
            if (stacks.Length > 0)
                DrawWay(self, stacks[0], Brushes.BlueViolet, sel == 0 ? 2 : 0);
            if (stacks.Length > 1)
                DrawWay(self, stacks[1], Brushes.Red, sel == 1 ? 2 : 0);
            if (stacks.Length > 2)
                DrawWay(self, stacks[2], Brushes.DeepPink, sel == 2 ? 2 : 0);
            if (stacks.Length > 3)
                DrawWay(self, stacks[3], Brushes.Black, sel == 3 ? 2 : 0);
            if (stacks.Length > 4)
                DrawWay(self, stacks[4], Brushes.SpringGreen, sel == 4 ? 2 : 0);
            if (stacks.Length > 5)
                DrawWay(self, stacks[5], Brushes.Coral, sel == 5 ? 2 : 0);
            if (stacks.Length > 6)
                throw new NotImplementedException("Please select color for this path");
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
            if (_lookY > MyStrategy.world.Height * MyStrategy.game.TrackTileSize - _scale * _form.panel.Height)
                _lookY = MyStrategy.world.Height * MyStrategy.game.TrackTileSize - _scale * _form.panel.Height;

            _lookX = p.X - _scale*_form.panel.Width/2;
            if (_lookX < 0)
                _lookX = 0;
            if (_lookX > MyStrategy.world.Width * MyStrategy.game.TrackTileSize - _scale * _form.panel.Width)
                _lookX = MyStrategy.world.Width * MyStrategy.game.TrackTileSize - _scale * _form.panel.Width;
        }
    }
}
