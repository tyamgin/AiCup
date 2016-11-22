using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class TargetsSelector
    {
        private ACombatUnit[] _combats;
        private bool _enableMinionsCache;
        private Dictionary<long, ACombatUnit> _minionsSelectionsCache = new Dictionary<long, ACombatUnit>(); 

        public bool EnableMinionsCache
        {
            get { return _enableMinionsCache; }
            set
            {
                _enableMinionsCache = value;
                if (value == false)
                    _minionsSelectionsCache.Clear();
            }
        }

        public TargetsSelector(ACombatUnit[] combats)
        {
            _combats = combats;
        }

        public ACombatUnit Select(ACombatUnit unit)
        {
            if (_minionsSelectionsCache.ContainsKey(unit.Id))
                return _minionsSelectionsCache[unit.Id];

            var res = unit.SelectTarget(_combats);
            if (EnableMinionsCache && unit is AMinion)
                _minionsSelectionsCache[unit.Id] = res;
            return res;
        }
    }
}
