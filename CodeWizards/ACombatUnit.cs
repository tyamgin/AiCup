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
        public double VisionRange;
        public double CastRange;

        public ACombatUnit(CircularUnit unit) : base(unit)
        {
            IsTeammate = unit.Faction == MyStrategy.Self.Faction;
            Faction = unit.Faction;
            var wizard = unit as Wizard;
            if (wizard != null)
            {
                Life = wizard.Life;
                VisionRange = wizard.VisionRange;
                CastRange = wizard.CastRange;
                if (wizard.Id == MyStrategy.Self.Id)
                    CastRange -= 20; //TODO HACK 
            }
            var building = unit as Building;
            if (building != null)
            {
                Life = building.Life;
                VisionRange = building.VisionRange;
                CastRange = building.AttackRange;
            }
            var minion = unit as Minion;
            if (minion != null)
            {
                Life = minion.Life;
                VisionRange = minion.VisionRange;
                if (minion.Type == MinionType.FetishBlowdart)
                    CastRange = MyStrategy.Game.FetishBlowdartAttackRange;
            }
        }

        public ACombatUnit(ACombatUnit unit) : base(unit)
        {
            IsTeammate = unit.IsTeammate;
            Faction = unit.Faction;
            Life = unit.Life;
            VisionRange = unit.VisionRange;
            CastRange = unit.CastRange;
        }

        public ACombatUnit()
        {
            
        }
    }
}
