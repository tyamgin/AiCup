using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class BonusesObserver
    {
        public static ABonus[] Bonuses;

        public static void Update()
        {
            Bonuses = MyStrategy.World.Bonuses
                .Select(b => new ABonus(b))
                .ToArray();

            
        }
    }
}
