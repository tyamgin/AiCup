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
        public MinionType Type;

        public AMinion(Minion unit) : base(unit)
        {
            Type = unit.Type;
        }

        public override bool IsOpponent => !IsTeammate && (Faction == Faction.Academy || Faction == Faction.Renegades);
    }
}
