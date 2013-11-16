using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        bool ifFieldRationNeed()
        {
            if (!self.IsHoldingFieldRation || self.ActionPoints < game.FieldRationEatCost || self.ActionPoints + game.FieldRationBonusActionPoints - game.FieldRationEatCost > self.InitialActionPoints)
                return false;
            if (howManyCanShoot(new Point(self), self.Stance) != 0)
                return true;
            return false;
        }
    }
}
