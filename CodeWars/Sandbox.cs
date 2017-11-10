using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class Sandbox
    {
        public AVehicle[] Vehicles;
        public bool IsMy = true; // TODO

        public void ApplyMove(Move move)
        {
            switch (move.Action)
            {
                case ActionType.ClearAndSelect:
                    foreach (var unit in Vehicles)
                    {
                        if (IsMy != unit.IsMy)
                            continue;

                        unit.IsSelected = Geom.Between(move.Left, move.Right, unit.X) &&
                                          Geom.Between(move.Top, move.Bottom, unit.Y);
                    }
                    break;
                case ActionType.Move:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsSelected)
                            continue;
                        unit.Speed = move.MaxSpeed;
                        unit.Target = unit + new Point(move.X, move.Y);
                        unit.AngularSpeed = 0;
                        unit.RotationAngle = 0;
                        unit.RotationCenter = null;
                    }
                    break;
                case ActionType.Rotate:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsSelected)
                            continue;
                        unit.Speed = 0;
                        unit.Target = null;
                        unit.AngularSpeed = move.MaxAngularSpeed;
                        unit.RotationAngle = move.Angle;
                        unit.RotationCenter = new Point(move.X, move.Y);
                    }
                    break;
            }
            AVehicle.Move(Vehicles);
        }
    }
}
