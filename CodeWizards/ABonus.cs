using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class ABonus : ACircularUnit
    {
        public BonusType Type;
        public int RemainingAppearanceTicks;
        public bool Skip;

        public ABonus(Bonus bonus) : base(bonus)
        {
            Type = bonus.Type;
            RemainingAppearanceTicks = 0;
        }

        public ABonus(ABonus bonus) : base(bonus)
        {
            Type = bonus.Type;
            RemainingAppearanceTicks = bonus.RemainingAppearanceTicks;
            Skip = bonus.Skip;
        }

        public ABonus()
        {
            
        }

        public void SkipTick()
        {
            Utility.Dec(ref RemainingAppearanceTicks);
        }

        public int Order
        {
            get
            {
                var first = Const.BonusAppearencePoints[0];
                if (Utility.Equals(first.X, X))
                    return 0;
                return 1;
            }
        }

        public bool Exists => RemainingAppearanceTicks == 0;
    }
}
