using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
#if DEBUG
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Visualizer;
#endif
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private static Queue<Point> drawPathQueue = new Queue<Point>();
        private static Queue<Point> drawGoalQueue = new Queue<Point>();
        private static Queue<Point> drawGoal2Queue = new Queue<Point>();
        private static Queue<string> drawInfo = new Queue<string>();
        private static Queue<Point> needPassQueue = new Queue<Point>(); 

        private void ShowWindow()
        {
#if DEBUG
            if (form == null)
            {
                thread = new Thread(_ShowWindow);
                thread.Start();
                Thread.Sleep(2000);
            }
#endif
        }

#if DEBUG
        private static Window form;

        private static Thread thread;
        private Graphics g;

        private delegate void DrawDelegate();

        private void DrawCircle(Brush brush, double x, double y, double radius, float width = 1, bool solid = false)
        {
            if (solid)
                g.FillEllipse(brush, (int)(x - radius), (int)(y - radius), (int)radius * 2, (int)radius * 2);
            else
                g.DrawEllipse(new Pen(brush, width), (int)(x - radius), (int)(y - radius), (int)radius * 2, (int)radius * 2);
        }

        private void DrawCircleC(Brush brush, double x, double y, double radius, float width = 1, bool solid = false)
        {
            if (solid)
                g.FillEllipse(brush, (int)(x), (int)(y), (int)radius * 2, (int)radius * 2);
            else
                g.DrawEllipse(new Pen(brush, width), (int)(x), (int)(y), (int)radius * 2, (int)radius * 2);
        }

        private void DrawLine(Brush brush, double x, double y, double X, double Y, float width = 0F)
        {
            g.DrawLine(new Pen(brush, width), (int)x, (int)y, (int)X, (int)Y);
        }

        private void draw()
        {
            if (form.InvokeRequired)
            {
                form.BeginInvoke(new DrawDelegate(draw), new object[] { });
                return;
            }

            var panel = form.panel;

            form.TickLabel.Text = World.Tick + "";
            form.ScoreLabel.Text = MyRight()
                ? Opp.GoalCount + " : " + My.GoalCount
                : My.GoalCount + " : " + Opp.GoalCount;

            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            g = Graphics.FromImage(drawArea);

            // Хоккеисты
            foreach (var ho in Hockeyists)
            {
                var brush = ho.IsTeammate ? Brushes.Blue : Brushes.Red;
                if (ho.Type != HockeyistType.Goalie)
                {
                    g.DrawLine(new Pen(brush), 
                        (int)(ho.X + Math.Cos(ho.Angle) * HoRadius),
                        (int)(ho.Y + Math.Sin(ho.Angle) * HoRadius),
                        (int)(ho.X + Math.Cos(ho.Angle) * Game.StickLength),
                        (int)(ho.Y + Math.Sin(ho.Angle) * Game.StickLength)
                        );
                }
                if (ho.SwingTicks != 0)
                {
                    g.DrawString(ho.SwingTicks + "", new Font(FontFamily.GenericSansSerif, 14), Brushes.Chartreuse, (float)ho.X - 10, (float)ho.Y);
                }
                if (ho.RemainingKnockdownTicks != 0)
                {
                    g.DrawString(ho.RemainingKnockdownTicks + "", new Font(FontFamily.GenericMonospace, 14), brush, (float)ho.X, (float)ho.Y);
                    var a = Get(ho) + new Point(Deg(135)) * HoRadius;
                    var b = Get(ho) + new Point(Deg(-45)) * HoRadius;
                    var c = Get(ho) + new Point(Deg(45)) * HoRadius;
                    var d = Get(ho) + new Point(Deg(-135)) * HoRadius;
                    DrawLine(brush, a.X, a.Y, b.X, b.Y, 3);
                    DrawLine(brush, c.X, c.Y, d.X, d.Y, 3);
                }
                DrawCircle(brush, ho.X, ho.Y, ho.Radius, width: ho.Id == puck.OwnerHockeyistId ? 4 : 1);
                if (ho.RemainingCooldownTicks > 0)
                {
                    g.DrawString(ho.RemainingCooldownTicks + "", new Font(FontFamily.GenericMonospace, 9), brush, (float)ho.X, (float)ho.Y - 13);
                    var a = Get(ho) + new Point(Deg(135))*HoRadius;
                    var b = Get(ho) + new Point(Deg(-45))*HoRadius;
                    DrawLine(brush, a.X, a.Y, b.X, b.Y);
                }
            }

            // Шайба
            DrawCircle(Brushes.Black, puck.X, puck.Y, puck.Radius, solid:true);
            var puckCenter = new Point(puck);
            if (Math.Abs(puck.SpeedX) > Double.Epsilon || Math.Abs(puck.SpeedY) > Double.Epsilon)
            {
                if (puck.OwnerPlayerId == -1)
                {
                    var puckDirection = puckCenter + (new Point(puck.SpeedX, puck.SpeedY).Normalized() * puck.Radius);
                    g.DrawLine(new Pen(Brushes.White), (int) puckCenter.X, (int) puckCenter.Y, (int) puckDirection.X,
                        (int) puckDirection.Y);
                }
            }
            // Ворота
            foreach (var player in World.Players)
            {
                var y1 = (int)player.NetBottom;
                var y2 = (int)player.NetTop;
                var x1 = (int)player.NetFront;
                var x2 = (int)player.NetBack;
                g.DrawLine(new Pen(Brushes.Black), x1, y1, x2, y1);
                g.DrawLine(new Pen(Brushes.Black), x1, y2, x2, y2);
                g.DrawLine(new Pen(Brushes.Black), x2, y1, x2, y2);
            }

            // Поле
            {
                var x1 = Game.RinkLeft;
                var x2 = Game.RinkRight;
                var y1 = Game.RinkTop;
                var y2 = Game.RinkBottom;
                DrawLine(Brushes.Black, x1, y1, x1, y2);
                DrawLine(Brushes.Black, x2, y1, x2, y2);
                DrawLine(Brushes.Black, x1, y1, x2, y1);
                DrawLine(Brushes.Black, x1, y2, x2, y2);
            }

            while (drawPathQueue.Count != 0)
            {
                var p = drawPathQueue.Dequeue();
                DrawCircle(Brushes.BlueViolet, p.X, p.Y, 3);
            }
            while (drawGoalQueue.Count != 0)
            {
                var p = drawGoalQueue.Dequeue();
                DrawCircle(Brushes.DarkGreen, p.X, p.Y, 3);
            }
            while (drawGoal2Queue.Count != 0)
            {
                var p = drawGoal2Queue.Dequeue();
                DrawCircle(Brushes.BlueViolet, p.X, p.Y, 5);
            }
            while (needPassQueue.Count != 0)
            {
                var p = needPassQueue.Dequeue();
                DrawCircle(Brushes.Yellow, p.X, p.Y, 12, solid:true);
            }

            string info = "";
            while (drawInfo.Count != 0)
            {
                var p = drawInfo.Dequeue();
                info += p + "\n";
            }
            if (info != "")
                form.infoLabel.Text = info;
            foreach(Point p in WayPoints)
                DrawCircleC(Brushes.BlueViolet, p.X, p.Y, 2);
        }
        private static void _ShowWindow()
        {
            form = new Window();
            form.ShowDialog();
            form.Focus();
        }
#endif

        private bool TK(int tick)
        {
            return tick == World.Tick;
        }
    }
}
