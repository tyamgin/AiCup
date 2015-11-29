using System;
using System.Collections.Generic;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class ABonus : ARectangularUnit
    {
        public static Dictionary<long, Point[]> _computedRect = new Dictionary<long, Point[]>();
 
        public BonusType Type;
        public long Id;

        public ABonus(Bonus bonus)
        {
            Type = bonus.Type;
            X = bonus.X;
            Y = bonus.Y;
            Width = bonus.Width;
            Height = bonus.Height;
            Id = bonus.Id;
        }

        /// <summary>
        /// Важность бонуса для машины.
        /// Число от 0 до 1
        /// </summary>
        /// <param name="self"></param>
        /// <returns></returns>
        public double GetImportance(Car self)
        {
            double result;
            switch (Type)
            {
                case BonusType.RepairKit:
                    result = 0.4 - 0.6*(self.Durability - 1);
                    break;
                case BonusType.PureScore:
                    result = 2.0;
                    break;
                case BonusType.OilCanister:
                    if (self.OilCanisterCount == 0)
                        result = 0.8;
                    else if (self.OilCanisterCount == 1)
                        result = 0.6;
                    else
                        result = 0.5;
                    break;
                case BonusType.NitroBoost:
                    result = 1.0;
                    break;
                case BonusType.AmmoCrate:
                    if (self.ProjectileCount <= 2)
                        result = 1.0;
                    else
                        result = 0.8;
                    break;
                default:
                    throw new Exception("Unknown BonusType");
            }
            return result;
        }

        public new Point[] GetRect()
        {
            if (_computedRect.ContainsKey(Id))
                return _computedRect[Id];
            var result = base.GetRect();
            _computedRect[Id] = result;
            return result;
        }
    }
}
