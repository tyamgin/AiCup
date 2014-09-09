using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private Queue<Point> drawPathQueue = new Queue<Point>();
        private Queue<Point> drawGoalQueue = new Queue<Point>(); 
#if DEBUG
        private static Window form;
#else
        private static Form form;
#endif
        private static Thread thread;
        private Graphics g;

        private delegate void DrawDelegate();

        private void drawThread()
        {
#if DEBUG
            draw();
#endif
        }

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

        private void DrawLine(Brush brush, double x, double y, double X, double Y)
        {
            g.DrawLine(new Pen(brush), (int)x, (int)y, (int)X, (int)Y);
        }

        private void draw()
        {
            if (form.InvokeRequired)
            {
                form.BeginInvoke(new DrawDelegate(draw), new object[] { });
                return;
            }
#if DEBUG
            var panel = form.panel;
#else
            var panel = new PictureBox();
#endif
            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            g = Graphics.FromImage(drawArea);

            // Хоккеисты
            foreach (var ho in world.Hockeyists)
            {
                var brush = ho.IsTeammate ? Brushes.Blue : Brushes.Red;
                if (ho.Type != HockeyistType.Goalie)
                {
                    DrawCircle(Brushes.Honeydew, ho.X, ho.Y, game.StickLength);
                    g.DrawLine(new Pen(brush), (int)ho.X, (int)ho.Y,
                        (int)(ho.X + Math.Cos(ho.Angle) * game.StickLength),
                        (int)(ho.Y + Math.Sin(ho.Angle) * game.StickLength)
                        );
                }
                var font = new Font(FontFamily.GenericMonospace, 10);
                if (ho.SwingTicks != 0)
                {
                    g.DrawString(ho.SwingTicks + "", new Font(FontFamily.GenericSansSerif, 14), Brushes.Chartreuse, (float)ho.X - 10, (float)ho.Y);
                }
                if (ho.RemainingKnockdownTicks != 0)
                {
                    g.DrawString(ho.RemainingKnockdownTicks + "", font, brush, (float)ho.X, (float)ho.Y);
                }
                DrawCircle(brush, ho.X, ho.Y, ho.Radius, width: ho.Id == puck.OwnerHockeyistId ? 4 : 1);
            }

            // Шайба
            DrawCircle(Brushes.Black, puck.X, puck.Y, puck.Radius, solid:true);

            // Ворота
            foreach (var player in world.Players)
            {
                var y1 = (int)player.NetBottom;
                var y2 = (int)player.NetTop;
                var x1 = (int)player.NetFront;
                var x2 = (int)player.NetBack;
                g.DrawLine(new Pen(Brushes.Black), x1, y1, x2, y1);
                g.DrawLine(new Pen(Brushes.Black), x1, y2, x2, y2);
                //g.DrawLine(new Pen(Brushes.Black), x1, y1, x1, y2);
                g.DrawLine(new Pen(Brushes.Black), x2, y1, x2, y2);
            }
            //g.DrawLine(new Pen(Brushes.Black), 0, 0, 0, (int)height);
            //g.DrawLine(new Pen(Brushes.Black), 0, 0, (int)width, 0);

            // Поле
            {
                var x1 = game.RinkLeft;
                var x2 = game.RinkRight;
                var y1 = game.RinkTop;
                var y2 = game.RinkBottom;
                DrawLine(Brushes.Black, x1, y1, x1, y2);
                DrawLine(Brushes.Black, x2, y1, x2, y2);
                DrawLine(Brushes.Black, x1, y1, x2, y1);
                DrawLine(Brushes.Black, x1, y2, x2, y2);
            }

            while (drawPathQueue.Count != 0)
            {
                var p = drawPathQueue.Dequeue();
                DrawCircleC(Brushes.BlueViolet, p.X, p.Y, 3);
            }
            while (drawGoalQueue.Count != 0)
            {
                var p = drawGoalQueue.Dequeue();
                DrawCircleC(Brushes.DarkGreen, p.X, p.Y, 3);
            }
        }

        private static void ShowWindow()
        {
#if DEBUG
            form = new Window();
#endif
            form.ShowDialog();
        }
    }
}
