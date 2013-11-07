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
            if (!self.IsHoldingFieldRation || self.ActionPoints < game.FieldRationEatCost)
                return false;
            if (danger[self.X, self.Y] != DangerNothing)
                return true;
            if (self.Id != commander.Id)
                return false;
            return getTeamRadius() > 5;
            // TODO: если возможно взять ещё 1 такой бонус
        }
    }
}
