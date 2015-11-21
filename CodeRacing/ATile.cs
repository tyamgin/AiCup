using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Security.Policy;
using System.Text;
using System.Windows.Forms.VisualStyles;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class ATile : Cell
    {
        public TileType Type;
        public TilePart[] Parts;

        public ATile(int i, int j, TileType type) : base(i, j)
        {
            Type = type;

            var margin = MyStrategy.game.TrackTileMargin;

            var sy = new Point(0, margin);
            var sx = new Point(margin, 0);
            var ly = new Point(0, MyStrategy.game.TrackTileSize);
            var lx = new Point(MyStrategy.game.TrackTileSize, 0);

            var res = new List<TilePart>();

            switch (type)
            {
                // Верхняя полоса
                case TileType.BottomHeadedT:
                case TileType.Horizontal:
                case TileType.LeftTopCorner:
                case TileType.RightTopCorner:
                    res.Add(TilePart.GetSegment(sy, lx + sy));
                    break;
            }
            switch (type)
            {
                // Нижняя полоса
                case TileType.TopHeadedT:
                case TileType.Horizontal:
                case TileType.LeftBottomCorner:
                case TileType.RightBottomCorner:
                    res.Add(TilePart.GetSegment(ly - sy, lx + ly - sy));
                    break;
            }
            switch (type)
            {
                // Левая полоса
                case TileType.RightHeadedT:
                case TileType.Vertical:
                case TileType.LeftBottomCorner:
                case TileType.LeftTopCorner:
                    res.Add(TilePart.GetSegment(sx, ly + sx));
                    break;
            }
            switch (type)
            {
                // Правая полоса
                case TileType.LeftHeadedT:
                case TileType.Vertical:
                case TileType.RightBottomCorner:
                case TileType.RightTopCorner:
                    res.Add(TilePart.GetSegment(lx - sx, lx + ly - sx));
                    break;
            }

            switch (type)
            {
                // Левый верхний угол
                case TileType.LeftHeadedT:
                case TileType.TopHeadedT:
                case TileType.RightBottomCorner:
                case TileType.Crossroads:
                    res.Add(TilePart.GetCircle(Point.Zero, margin));
                    break;
            }

            switch (type)
            {
                // Правый верхний угол
                case TileType.RightHeadedT:
                case TileType.TopHeadedT:
                case TileType.LeftBottomCorner:
                case TileType.Crossroads:
                    res.Add(TilePart.GetCircle(lx, margin));
                    break;
            }

            switch (type)
            {
                // Правый нижний угол
                case TileType.BottomHeadedT:
                case TileType.RightHeadedT:
                case TileType.LeftTopCorner:
                case TileType.Crossroads:
                    res.Add(TilePart.GetCircle(lx + ly, margin));
                    break;
            }

            switch (type)
            {
                // Левый нижний угол
                case TileType.BottomHeadedT:
                case TileType.LeftHeadedT:
                case TileType.RightTopCorner:
                case TileType.Crossroads:
                    res.Add(TilePart.GetCircle(ly, margin));
                    break;
            }

            var dx = MyStrategy.game.TrackTileSize*j;
            var dy = MyStrategy.game.TrackTileSize*i;
            foreach (var part in res)
            {
                if (part.Type == TilePartType.Circle)
                {
                    part.Circle.X += dx;
                    part.Circle.Y += dy;
                }
                else
                {
                    part.Start.X += dx;
                    part.Start.Y += dy;
                    part.End.X += dx;
                    part.End.Y += dy;

                    if (Geom.VectorProduct(part.Start, part.End, MyStrategy.GetCenter(i, j)) > 0)
                    {
                        var tmp = part.Start;
                        part.Start = part.End;
                        part.End = tmp;
                    }
                }
            }
            Parts = res.ToArray();
        }
    }

    public enum TilePartType
    {
        Circle,
        Segment
    }

    public class TilePart
    {
        public TilePartType Type;
        public Point Start, End;
        public ACircularUnit Circle;

        public Point GetIntersectionPoint(AProjectile proj)
        {
            if (Type == TilePartType.Segment)
            {
                var pts = Geom.LineCircleIntersect(Start, End, proj, proj.Radius);
                if (pts.Length == 0)
                    return null;
                var inter = pts.Length == 1 ? pts[0] : (pts[0] + pts[1])/2.0;
                var wallDir = (End - Start).Normalized();
                // TODO: убедиться что скорость и направление стены идут в нужном направлении
                var ang = Geom.GetAngleBetween(proj.Speed, wallDir);
                proj.Speed = proj.Speed.RotateClockwise(2*Math.PI - 2*ang);
            }
            else
            {
                
            }
            return null;
        }

        public static TilePart GetSegment(Point s, Point t)
        {
            return new TilePart
            {
                Type = TilePartType.Segment,
                Start = s,
                End = t
            };
        }
        public static TilePart GetCircle(Point cen, double r)
        {
            return new TilePart
            {
                Type = TilePartType.Circle,
                Circle = new ACircularUnit
                {
                    Radius = r,
                    X = cen.X,
                    Y = cen.Y
                }
            };
        }
    }
}
