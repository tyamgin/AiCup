using System.Collections.Generic;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class ATile : Cell
    {
        public TileType Type;
        public TilePart[] Parts;

        public bool
            IsFreeLeft = true,
            IsFreeRight = true,
            IsFreeTop = true,
            IsFreeBottom = true;

        public ATile(int i, int j, TileType type) : base(i, j)
        {
            Type = type;

            IsFreeLeft = _tileFreeLeft(type);
            IsFreeRight = _tileFreeRight(type);
            IsFreeTop = _tileFreeTop(type);
            IsFreeBottom = _tileFreeBottom(type);

            var margin = MyStrategy.game.TrackTileMargin;

            var sy = new Point(0, margin);
            var sx = new Point(margin, 0);
            var ly = new Point(0, MyStrategy.game.TrackTileSize);
            var lx = new Point(MyStrategy.game.TrackTileSize, 0);

            var res = new List<TilePart>();

            switch (type)
            {
                    // Левый верхний угол
                case TileType.LeftHeadedT:
                case TileType.TopHeadedT:
                case TileType.RightBottomCorner:
                case TileType.Crossroads:
                case TileType.Unknown:
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
                case TileType.Unknown:
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
                case TileType.Unknown:
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
                case TileType.Unknown:
                    res.Add(TilePart.GetCircle(ly, margin));
                    break;
            }

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

        private static bool _tileFreeLeft(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Horizontal ||
                   type == TileType.RightBottomCorner ||
                   type == TileType.RightTopCorner ||
                   type == TileType.LeftHeadedT ||
                   type == TileType.TopHeadedT ||
                   type == TileType.BottomHeadedT ||
                   type == TileType.Unknown;
        }

        private static bool _tileFreeRight(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Horizontal ||
                   type == TileType.LeftBottomCorner ||
                   type == TileType.LeftTopCorner ||
                   type == TileType.RightHeadedT ||
                   type == TileType.TopHeadedT ||
                   type == TileType.BottomHeadedT ||
                   type == TileType.Unknown;
        }

        private static bool _tileFreeTop(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Vertical ||
                   type == TileType.LeftBottomCorner ||
                   type == TileType.RightBottomCorner ||
                   type == TileType.LeftHeadedT ||
                   type == TileType.TopHeadedT ||
                   type == TileType.RightHeadedT ||
                   type == TileType.Unknown;
        }

        private static bool _tileFreeBottom(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Vertical ||
                   type == TileType.LeftTopCorner ||
                   type == TileType.RightTopCorner ||
                   type == TileType.LeftHeadedT ||
                   type == TileType.BottomHeadedT ||
                   type == TileType.RightHeadedT ||
                   type == TileType.Unknown;
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
