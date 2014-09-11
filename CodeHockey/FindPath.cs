using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Text;
using System.IO;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Xml.XPath;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private const int xParts = 120, yParts = 60;
        private const int Inf = 0x3f3f3f3f;

        private double[,] d = new double[xParts, yParts];
        private int[, ,] dBack = new int[xParts, yParts, 2];

        //int AngleEncode(double x)
        //{
        //    if (x < 0)
        //        x += 2 * Math.PI; // ??
        //    return (int)(x * angleParts / (2 * Math.PI));
        //}

        //double AngleDecode(int x)
        //{
        //    return x * (2 * Math.PI) / angleParts;
        //}
        int XEncode(double x)
        {
            return (int)(x * xParts / world.Width);
        }

        int YEncode(double y)
        {
            return (int)(y * yParts / world.Height);
        }

        double XDecode(int x)
        {
            return x * world.Width / xParts;
        }

        double YDecode(int y)
        {
            return y * world.Height / yParts;
        }

        //double fmod(double a)
        //{
        //    while (a > 2 * Math.PI)
        //        a -= 2 * Math.PI;
        //    return a;
        //}

        int[] dx = new int[] { 0, 0, 1, -1, -1, -1, 1, 1 };
        int[] dy = new int[] { 1, -1, 0, 0, 1, -1, 1, -1 };
        double[] dz = new double[] { 1.0, 1.0, 1.0, 1.0, Math.Sqrt(2.0), Math.Sqrt(2.0), Math.Sqrt(2.0), Math.Sqrt(2.0) };
        int[,] map = new int[xParts, yParts];

        void FindShortestPath(int startX, int startY)
        {
            var q = new PriorityQueue<Pair<double, int>>();
            for (int i = 0; i < xParts; i++)
                for (int j = 0; j < yParts; j++)
                    d[i, j] = Inf;
            d[startX, startY] = 0;
            q.Push(new Pair<double, int>(0.0, startX * yParts + startY));
            while (q.Count != 0)
            {
                var v = q.Top().second;
                var cur_d = -q.Top().first;
                q.Pop();
                int x = v / yParts, y = v % yParts;
                if (cur_d > d[x, y]) 
                    continue;

                for (var j = 0; j < dx.Count(); j++)
                {
                    int nx = dx[j] + x, 
                        ny = dy[j] + y;
                    if (nx >= 0 && ny >= 0 && nx < xParts && ny < yParts)
                    {
                        var to = nx * yParts + ny;
                        var len = map[nx, ny] * dz[j];
                        if (d[x, y] + len < d[nx, ny])
                        {
                            d[nx, ny] = d[x, y] + len;
                            dBack[nx, ny, 0] = x;
                            dBack[nx, ny, 1] = y;
                            q.Push(new Pair<double, int>(-d[nx, ny], to));
                        }
                    }
                }
            }
        }

        public double GetShortestPathAngle(Point _from, Point _to)
        {
            int fromX = XEncode(_from.X),
                fromY = YEncode(_from.Y),
                toX = XEncode(_to.X),
                toY = YEncode(_to.Y);
            FindShortestPath(fromX, fromY);
            if (d[toX, toY] == Inf || _from.Equals(_to))
                return 0.0;
            for (int x = toX, y = toY, cnt = 0; !(x == fromX && y == fromY); cnt++)
            {
                if (cnt > 1000)
                    break;
                drawPathQueue.Enqueue(new Point(XDecode(x), YDecode(y)));
                int _x = dBack[x, y, 0];
                int _y = dBack[x, y, 1];
                toX = x;
                toY = y;
                x = _x;
                y = _y;
            }

            return 0.0;
        }
    }
}
