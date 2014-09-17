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
        private string tooltipText = "";
        private static Queue<Point> drawPathQueue = new Queue<Point>();
        private static Queue<Point> drawGoalQueue = new Queue<Point>();
        private static Queue<Point> drawGoal2Queue = new Queue<Point>();
        private static Queue<string> drawInfo = new Queue<string>(); 
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

            var panel = form.panel;

            form.TickLabel.Text = world.Tick + "";
            form.ScoreLabel.Text = MyRight()
                ? opp.GoalCount + " : " + my.GoalCount
                : my.GoalCount + " : " + opp.GoalCount;

            var drawArea = new Bitmap(panel.Size.Width, panel.Size.Height);
            panel.Image = drawArea;
            g = Graphics.FromImage(drawArea);

            g.DrawLine(new Pen(Brushes.PeachPuff), (int) game.RinkLeft, (int) (game.RinkTop + StrikeZoneWidth),
                (int) game.RinkRight, (int) (game.RinkTop + StrikeZoneWidth));
            g.DrawLine(new Pen(Brushes.PeachPuff), (int)game.RinkLeft, (int)(game.RinkBottom - StrikeZoneWidth),
                (int)game.RinkRight, (int)(game.RinkBottom - StrikeZoneWidth));
            if (MyRight())
                g.DrawLine(new Pen(Brushes.PeachPuff), (int)(StrikeZoneWidthBesideNet + game.RinkLeft), (int)game.RinkTop,
                    (int)(StrikeZoneWidthBesideNet + game.RinkLeft), (int)game.RinkBottom);
            else
                g.DrawLine(new Pen(Brushes.PeachPuff), (int)(game.RinkRight - StrikeZoneWidthBesideNet), (int)game.RinkTop,
                    (int)(game.RinkRight - StrikeZoneWidthBesideNet), (int)game.RinkBottom);


            // Хоккеисты
            foreach (var ho in world.Hockeyists)
            {
                var brush = ho.IsTeammate ? Brushes.Blue : Brushes.Red;
                if (ho.Type != HockeyistType.Goalie)
                {
                    //DrawCircle(Brushes.Honeydew, ho.X, ho.Y, game.StickLength);
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
            while (drawGoal2Queue.Count != 0)
            {
                var p = drawGoal2Queue.Dequeue();
                DrawCircleC(Brushes.BlueViolet, p.X, p.Y, 5);
            }

            string ttip = "";
            while (drawInfo.Count != 0)
            {
                var p = drawInfo.Dequeue();
                ttip += p + "\n";
            }
            if (ttip != "")
                form.infoLabel.Text = ttip;
        }
        private static void ShowWindow()
        {
            form = new Window();
            form.ShowDialog();
            form.Focus();
        }
#endif

        private bool TK(int tick)
        {
            return tick == world.Tick;
        }
    }
}
