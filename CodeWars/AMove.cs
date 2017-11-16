using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
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

        public MyGroup MyGroup
        {
            set
            {
                if (value.Group != null)
                    Group = (int) value.Group;
                if (value.Type != null)
                    VehicleType = value.Type;
            }
        }
    }
}
