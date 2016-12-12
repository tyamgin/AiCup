using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class SupportObserver
    {
        public class LastSeenInfo
        {
            public AWizard Wizard;
            public int Tick;
        }

        public static Dictionary<long, LastSeenInfo> LastSeen = new Dictionary<long,LastSeenInfo>(); 

        public static void Update()
        {
            foreach (var w in MyStrategy.Wizards)
                LastSeen[w.Id] = new LastSeenInfo {Tick = MyStrategy.World.TickIndex, Wizard = w};
        }

        public static int CountOpponentsOnLane(ALaneType lane)
        {
            return LastSeen.Values
                .Count(x => x.Wizard.IsOpponent && MyStrategy.World.TickIndex - x.Tick < 300 && RoadsHelper.GetLane(x.Wizard) == lane);
        }
    }
}
