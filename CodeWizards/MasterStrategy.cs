using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting.Messaging;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        public delegate ALaneType SelectLaneFunc(AWizard w);
        public delegate SkillType? SelectSkillFunc(AWizard w);

        public void MasterSendMessages()
        {
            if (World.TickIndex == 0)
            {
                return;

                var self = new AWizard(ASelf);
                var sorted = MyWizards.ToList();
                sorted.Sort((a, b) => Utility.Equals(a.X, b.X) ? a.Y.CompareTo(b.Y) : a.X.CompareTo(b.X));
                var order = new Dictionary<long, int>();
                for (var i = 0; i < sorted.Count; i++)
                    order[sorted[i].Id] = i;

                SelectLaneFunc selectLane = x =>
                {
                    if (order[x.Id] <= 1)
                        return ALaneType.Top;
                    if (order[x.Id] == 4)
                        return ALaneType.Bottom;

                    return ALaneType.Middle;
                };

                SelectSkillFunc selectSkill = x =>
                {
                    // TODO
                    return null;
                };

                FinalMove.Messages = Wizards
                    .Where(x => x.IsTeammate && x.Id != Self.Id)
                    .OrderBy(x => x.Id)
                    .Select(x =>
                    {
                        var lane = (LaneType) selectLane(x);
                        var skill = selectSkill(x);
                        return new Message(lane, skill, new byte[] { });
                    })
                    .ToArray();

                MessagesObserver.LastMessage = new Message((LaneType) selectLane(self), SkillType.Shield, new byte[] {});
            }
        }
    }
}
