using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class NeutralMinionsObserver
    {
        private static Dictionary<long, AMinion> _neutrals = new Dictionary<long, AMinion>();
        private static HashSet<long> _aggressiveIds = new HashSet<long>();

        public static AMinion[] Aggressive;

        public static void Update()
        {
            var aggressive = new List<AMinion>(); 
            foreach (var neutral in MyStrategy.Minions.Where(x => x.IsNeutral))
            {
                if (_neutrals.ContainsKey(neutral.Id))
                {
                    var prevState = _neutrals[neutral.Id];
                    if (!Utility.Equals(prevState.Angle, neutral.Angle) ||
                        !Utility.Equals(prevState.X, neutral.X) ||
                        !Utility.Equals(prevState.Y, neutral.Y) ||
                        neutral.RemainingActionCooldownTicks > 0 ||
                        _aggressiveIds.Contains(neutral.Id)
                        )
                    {
                        aggressive.Add(neutral);
                        _aggressiveIds.Add(neutral.Id);
                        neutral.IsAggressiveNeutral = true;
                    }
                }
                _neutrals[neutral.Id] = neutral;
            }
            Aggressive = aggressive.ToArray();
        }
    }
}
