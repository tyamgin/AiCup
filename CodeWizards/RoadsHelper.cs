using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class RoadsHelper
    {
        public static LaneSegment[] Roads, AllowedForMiddle, AllowedForTop, AllowedForBottom;

        public static void Initialize()
        {
            if (Roads == null)
            {
                var dx = Const.BaseBuildingDistance / 2;
                var s = Const.MapSize;
                var a = new Point(dx, s - dx);
                var b = new Point(dx, dx);
                var c = new Point(s - dx, dx);
                var d = new Point(s - dx, s - dx);
                Roads = new[]
                {
                    new LaneSegment(a, b, ALaneType.Top),
                    new LaneSegment(b, c, ALaneType.Top),
                    new LaneSegment(c, d, ALaneType.Bottom),
                    new LaneSegment(d, a, ALaneType.Bottom),
                    new LaneSegment(a, c, ALaneType.Middle),
                    new LaneSegment(b, d, ALaneType.Middle2),
                };

                MagicConst.MinionAppearencePoints = new[]
                {
                    new Point(Const.BaseBuildingDistance * 2.5, Const.MapSize - Const.BaseBuildingDistance * 0.5),
                    new Point(Const.BaseBuildingDistance * 0.5, Const.MapSize - Const.BaseBuildingDistance * 2.5),
                    new Point(Const.BaseBuildingDistance * 2.0, Const.MapSize - Const.BaseBuildingDistance * 2.0),
                };
                foreach (var appPt in MagicConst.MinionAppearencePoints)
                {
                    appPt.X = Const.MapSize - appPt.X;
                    appPt.Y = Const.MapSize - appPt.Y;
                }
            }

            const int startTicksThreshold = 900;
            AllowedForMiddle = Roads
                .Where(r => r.LaneType == ALaneType.Middle || r.LaneType == ALaneType.Middle2 && MyStrategy.World.TickIndex > startTicksThreshold)
                .ToArray();
            AllowedForTop = Roads
                .Where(r => r.LaneType == ALaneType.Top || r.LaneType == ALaneType.Middle2 && MyStrategy.World.TickIndex > startTicksThreshold)
                .ToArray();
            AllowedForBottom = Roads
                .Where(r => r.LaneType == ALaneType.Bottom || r.LaneType == ALaneType.Middle2 && MyStrategy.World.TickIndex > startTicksThreshold)
                .ToArray();
        }

        public static ALaneType GetLane(Point self)
        {
            return Roads
                .Where(r => r.LaneType != ALaneType.Middle2)
                .ArgMin(r => r.GetDistanceTo(self)).LaneType;
        }

        public static ALaneType GetLaneEx(Point self)
        {
            return Roads
                .ArgMin(r => r.GetDistanceTo(self)).LaneType;
        }

        public static LaneSegment[] GetAllowedForLine(ALaneType lane)
        {
            switch (lane)
            {
                case ALaneType.Middle:
                    return AllowedForMiddle;
                case ALaneType.Bottom:
                    return AllowedForBottom;
                case ALaneType.Top:
                    return AllowedForTop;
                default:
                    throw new Exception("Invalid lane type");
            }
        }

        public static Point GetLaneCenter(ALaneType lane)
        {
            switch (lane)
            {
                case ALaneType.Top:
                    return Const.TopLeftCorner;
                case ALaneType.Middle:
                    return (Const.TopLeftCorner + Const.BottomRightCorner)/2;
                case ALaneType.Bottom:
                    return Const.BottomRightCorner;
                default:
                    throw new ArgumentOutOfRangeException(nameof(lane), lane, null);
            }
        }
    }

    public enum ALaneType
    {
        Top = LaneType.Top,
        Middle = LaneType.Middle,
        Bottom = LaneType.Bottom,
        Middle2,
    }

    public class LaneSegment : Segment
    {
        public ALaneType LaneType;

        public LaneSegment(Point a, Point b, ALaneType laneType) : base(a, b)
        {
            LaneType = laneType;
        }
    }
}
