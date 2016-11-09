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
        public virtual bool IsOpponent => !IsTeammate;

        public Faction Faction;
        public double Life;


        public ACombatUnit(CircularUnit unit) : base(unit)
        {
            IsTeammate = unit.Faction == MyStrategy.self.Faction;
            Faction = unit.Faction;
            if (unit is Wizard)
                Life = ((Wizard) unit).Life;
            if (unit is Building)
                Life = ((Building)unit).Life;
            if (unit is Minion)
                Life = ((Minion)unit).Life;
        }

        public ACombatUnit()
        {
            
        }
    }
}
