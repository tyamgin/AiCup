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
        }

    }
}
