using System;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class AVehicle : ACircularUnit
    {
        public bool IsMy;
        public VehicleType Type;
        public double Durability;
        public bool IsSelected;

        public Point Target;
        public double Speed;

        public AVehicle(Vehicle unit) : base(unit)
        {
            IsMy = unit.PlayerId == MyStrategy.Me.Id;
            Type = unit.Type;
            Durability = unit.Durability;
            IsSelected = unit.IsSelected;
            
        }

        public AVehicle(AVehicle unit) : base(unit)
        {
            IsMy = unit.IsMy;
            Type = unit.Type;
            Durability = unit.Durability;
            IsSelected = unit.IsSelected;
        }

        bool Move(Func<ACircularUnit, bool> checkCollisions = null)
        {
            if (Target == null)
                return true;

            var vec = Target - this;
            double length;
            if (vec.Length > Speed)
            {
                length = vec.Length;
            }
            else
            {
                length = Speed;
            }
            var delta = vec.Normalized()*length;
            //var prev = 
            if (checkCollisions != null && checkCollisions(to))
            {
                
            }

            return false;
        }
    }
}
