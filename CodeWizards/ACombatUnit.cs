using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class ACombatUnit : ACircularUnit
    {
        public bool IsTeammate;
        public bool IsOpponent => !IsTeammate;

        public Faction Faction;


        public ACombatUnit(CircularUnit unit) : base(unit)
        {
            IsTeammate = unit.Faction == MyStrategy.self.Faction;
            Faction = unit.Faction;
        }
    }
}
