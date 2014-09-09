//using System;
//using System.Collections;
//using System.Collections.Generic;
//using System.Drawing;
//using System.Drawing.Text;
//using System.IO;
//using System.Linq;
//using System.Security.Cryptography.X509Certificates;
//using System.Xml.XPath;
//using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
//using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

//namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
//{
//    public partial class MyStrategy : IStrategy
//    {
//        private const int xParts = 40, yParts = 20, angleParts = 12, speedDelta = 3;
//        private const int Inf = 0x3f3f3f3f;

//        private int[, ,] d = new int[xParts, yParts, angleParts];
//        private int[, , ,] dBack = new int[xParts, yParts, angleParts, 3];

//        int AngleEncode(double x)
//        {
//            if (x < 0)
//                x += 2 * Math.PI; // ??
//            return (int)(x * angleParts / (2 * Math.PI));
//        }

//        double AngleDecode(int x)
//        {
//            return x * (2 * Math.PI) / angleParts;
//        }

//        int XEncode(double x)
//        {
//            return (int)(x * xParts / width);
//        }

//        int YEncode(double y)
//        {
//            return (int)(y * yParts / height);
//        }

//        double XDecode(int x)
//        {
//            return x*width/xParts;
//        }

//        double YDecode(int y)
//        {
//            return y * height / yParts;
//        }

//        double fmod(double a)
//        {
//            while (a > 2*Math.PI)
//                a -= 2*Math.PI;
//            return a;
//        }

//        void FindShortestPath(int startX, int startY, int startAngle, double angleMaxChange)
//        {
//            var q = new Queue<int>();
//            for(int i = 0; i < xParts; i++)
//                for(int j = 0; j < yParts; j++)
//                    for (int k = 0; k < angleParts; k++)
//                        d[i, j, k] = Inf;
//            d[startX, startY, startAngle] = 0;
//            q.Enqueue(startX);
//            q.Enqueue(startY);
//            q.Enqueue(startAngle);
//            while (q.Count != 0)
//            {
//                var x = q.Dequeue();
//                var y = q.Dequeue();
//                var angle = q.Dequeue();

//                for (int dx = -speedDelta; dx <= speedDelta; dx++)
//                {
//                    for (int dy = -speedDelta; dy <= speedDelta; dy++)
//                    {
//                        int nx = x + dx;
//                        int ny = y + dy;
//                        double nnAngle = Math.Atan2(YDecode(ny) - YDecode(y), XDecode(nx) - XDecode(x));
//                        if (new Point(x, y).GetDistanceTo(nx, ny) <= speedDelta 
//                            && fmod(Math.Abs(nnAngle - AngleDecode(angle))) < angleMaxChange)
//                        {
//                            int nAngle = AngleEncode(nnAngle);
//                            if (nx >= 0 && ny >= 0 && nx < xParts && ny < yParts && d[nx, ny, nAngle] == Inf)
//                            {
//                                d[nx, ny, nAngle] = d[x, y, angle] + 1;
//                                q.Enqueue(nx);
//                                q.Enqueue(ny);
//                                q.Enqueue(nAngle);
//                                dBack[nx, ny, nAngle, 0] = x;
//                                dBack[nx, ny, nAngle, 1] = y;
//                                dBack[nx, ny, nAngle, 2] = nAngle;
//                            }
//                        }
//                    }
//                }
//            }
//        }

//        void GetDiscreteParams(Point p, double angle, out int x, out int y, out int ang)
//        {
//            x = (int) (p.X*xParts/width);
//            y = (int) (p.Y*yParts/height);
//            if (angle < 0)
//                angle += 2*Math.PI; // ??
//            ang = (int) (angle*angleParts/(2*Math.PI));
//        }

//        public double GetShortestPathAngle(Point from, double fromAngle, Point to)
//        {
//            double toAngle = Math.PI; // TODO:
//            int fromX, fromY, fromAng, toX, toY, toAng;
//            GetDiscreteParams(from, fromAngle, out fromX, out fromY, out fromAng);
//            GetDiscreteParams(to, toAngle, out toX, out toY, out toAng);
//            FindShortestPath(fromX, fromY, fromAng, Math.PI / 4);
//            if (d[toX, toY, toAng] == Inf)
//                return Inf;
//            while (!(dBack[toX, toY, toAng, 0] == fromX && dBack[toX, toY, toAng, 1] == fromY))
//            {
//                int nx = dBack[toX, toY, toAng, 0];
//                int ny = dBack[toX, toY, toAng, 1];
//                int na = dBack[toX, toY, toAng, 2];
//                drawPathQueue.Enqueue(new Point(width * nx / xParts, height * ny / yParts));
//                toX = nx;
//                toY = ny;
//                toAng = na;
//            }
//            int an = dBack[toX, toY, toAng, 2];
//            return AngleDecode((an - fromAng + angleParts)%angleParts);
//        }
//    }
//}
