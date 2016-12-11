using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting.Messaging;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        public void MasterSendMessages()
        {
            if (World.TickIndex == 0)
            {
                var self = new AWizard(ASelf);
                var sorted = MyWizards.ToList();
                sorted.Sort((a, b) => Utility.Equals(a.X, b.X) ? a.Y.CompareTo(b.Y) : a.X.CompareTo(b.X));
                var order = new Dictionary<long, int>();
                for (var i = 0; i < sorted.Count; i++)
                    order[sorted[i].Id] = i;

                Func<AWizard, ALaneType> selectLane = x =>
                {
                    if (order[x.Id] <= 1)
                        return ALaneType.Top;
                    if (order[x.Id] == 4)
                        return ALaneType.Bottom;

                    return ALaneType.Middle;
                };

                Func<AWizard, MessagesObserver.SkillsGroup> selectSkill = x =>
                {
                    switch (order[x.Id])
                    {
                        case 0:
                        case 2:
                            return MessagesObserver.SkillsGroup.Haster;
                        case 1:
                        case 3:
                            return MessagesObserver.SkillsGroup.Fireballer;
                        case 4:
                            return MessagesObserver.SkillsGroup.Round2;
                        default:
                            throw new Exception("Invalid state");
                    }
                };

                Func<AWizard, Message> selectMessage = x =>
                {
                    return new Message((LaneType) selectLane(x), (SkillType) selectSkill(x), new byte[] {/*(byte) selectSkill(x)*/});
                };

                FinalMove.Messages = MyWizards
                    .Where(x => x.Id != Self.Id)
                    .OrderBy(x => x.Id)
                    .Select(selectMessage)
                    .ToArray();

                MessagesObserver.LastMessage = selectMessage(self);
            }
        }
    }
}
