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

        public ABonus(Bonus bonus)
        {
            Type = bonus.Type;
        }

        public ABonus(ABonus bonus)
        {
            Type = bonus.Type;
        }
    }
}
