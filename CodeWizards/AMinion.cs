using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AMinion : ACombatUnit
    {
        public AMinion(Minion unit) : base(unit)
        {
            // TODO
        }

        public new bool IsOpponent => !IsTeammate && (Faction == Faction.Academy || Faction == Faction.Renegades);
    }
}
