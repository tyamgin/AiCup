using System.Globalization;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class AMove : Move
    {
        public void ApplyTo(Move move)
        {
            move.Action = Action;
            move.Group = Group;
            move.Left = Left;
            move.Top = Top;
            move.Right = Right;
            move.Bottom = Bottom;
            move.X = X;
            move.Y = Y;
            move.Angle = Angle;
            move.MaxSpeed = MaxSpeed;
            move.MaxAngularSpeed = MaxAngularSpeed;
            move.VehicleType = VehicleType;
            move.FacilityId = FacilityId;
            move.Factor = Factor;
            move.VehicleId = VehicleId;
        }

        public Point Point
        {
            get { return new Point(X, Y); }
            set
            {
                X = value.X;
                Y = value.Y;
            }
        }

        public void SetVector(Point from, Point to)
        {
            X = to.X - from.X;
            Y = to.Y - from.Y;
        }

        public Rect Rect
        {
            get
            {
                return new Rect
                {
                    X = Left,
                    X2 = Right,
                    Y = Top,
                    Y2 = Bottom
                };
            }
            set
            {
                Left = value.X;
                Right = value.X2;
                Top = value.Y;
                Bottom = value.Y2;
            }
        }

        public override string ToString()
        {
            switch (Action)
            {
                case ActionType.Move:
                    return ActionType.Move + " (" + X.ToString(CultureInfo.InvariantCulture) + ", " + Y + ")";
                case ActionType.Rotate:
                    return ActionType.Rotate + " (" + X + ", " + Y + "; " + Angle + ")";
                case ActionType.Scale:
                    return ActionType.Scale + " (" + X + ", " + Y + "; " + Factor + ")";
                case ActionType.ClearAndSelect:
                    var groupStr = VehicleType?.ToString() ?? (Group != 0 ? Group.ToString() : "");
                    return ActionType.ClearAndSelect + " " + groupStr + Rect;
                case ActionType.TacticalNuclearStrike:
                    return ActionType.TacticalNuclearStrike + "(" + X + ", " + Y + ") " + VehicleId;
                case ActionType.Assign:
                    return ActionType.Assign + " " + Group;
                case ActionType.SetupVehicleProduction:
                    return ActionType.SetupVehicleProduction + " " + FacilityId + " " + VehicleType;
                default:
                    //TODO
                    return base.ToString();
            }
        }
    }

    public class AMovePresets
    {
        public static AMove ClearAndSelectType(VehicleType type, Rect rect = null)
        {
            return new AMove { Action = ActionType.ClearAndSelect, Rect = rect ?? G.MapRect, VehicleType = type };
        }

        public static AMove ClearAndSelectGroup(int group, Rect rect = null)
        {
            return new AMove { Action = ActionType.ClearAndSelect, Rect = rect ?? G.MapRect, Group = group };
        }

        public static AMove AssignGroup(int groupId)
        {
            return new AMove {Action = ActionType.Assign, Group = groupId};
        }

        public static AMove Scale(Point point, double factor)
        {
            return new AMove {Action = ActionType.Scale, Factor = factor, Point = point};
        }

        public static AMove Rotate(Point point, double angle)
        {
            return new AMove {Action = ActionType.Rotate, Point = point, Angle = angle};
        }

        public static AMove MoveTo(Point from, Point to, double maxSpeed = 0)
        {
            return new AMove {Action = ActionType.Move, Point = to - from, MaxSpeed = maxSpeed};
        }

        public static AMove Move(Point vector, double maxSpeed = 0)
        {
            return new AMove {Action = ActionType.Move, Point = vector, MaxSpeed = maxSpeed};
        }
    }
}
