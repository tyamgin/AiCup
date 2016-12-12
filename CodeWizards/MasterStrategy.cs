using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting.Messaging;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        public Dictionary<long, Message> LastMessages = new Dictionary<long, Message>(); 

        public void MasterSendMessages()
        {
            if (World.TickIndex == 0)
            {
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
                    return new Message((LaneType) selectLane(x), null, new byte[] {(byte) selectSkill(x)});
                };

                foreach (var x in MyWizards)
                    LastMessages[x.Id] = selectMessage(x);

                _sendMessages();
            }
        }

        private void _sendMessages()
        {
            FinalMove.Messages = MyWizards
                    .Where(x => x.Id != Self.Id)
                    .OrderBy(x => x.Id)
                    .Select(x => LastMessages[x.Id])
                    .ToArray();

            MessagesObserver.LastMessage = LastMessages[ASelf.Id];
        }

        public void MasterCheckRearrange()
        {
            if (World.TickIndex > 2000)
                return;

            var allLanes = new[] {ALaneType.Middle, ALaneType.Top, ALaneType.Bottom};
            foreach (var lane in allLanes)
            {
                var oppsCount = SupportObserver.CountOpponentsOnLane(lane);
                var minesCount = LastMessages.Values.Count(x => (ALaneType) x.Lane == lane);
                var target = RoadsHelper.GetLaneCenter(lane);
                if (oppsCount > minesCount + 1)
                {
                    long selSupportId = -1;
                    var maxDisbalance = int.MinValue;
                    foreach (var anotherLane in allLanes.Where(l => l != lane))
                    {
                        var oppsCount2 = SupportObserver.CountOpponentsOnLane(anotherLane);
                        var minesCount2 = LastMessages.Values.Count(x => (ALaneType)x.Lane == anotherLane);
                        var disbalance = minesCount2 - oppsCount2;

                        if (disbalance >= maxDisbalance)
                        {
                            maxDisbalance = disbalance;
                            foreach(var w in MyWizards.Where(x => (ALaneType) LastMessages[x.Id].Lane == anotherLane))
                                if (selSupportId == -1 || w.GetDistanceTo(target) < SupportObserver.LastSeen[selSupportId].Wizard.GetDistanceTo(target))
                                    selSupportId = w.Id;
                        }
                    }
                    if (selSupportId != -1)
                    {
                        var prev = LastMessages[selSupportId];
                        LastMessages[selSupportId] = new Message((LaneType) lane, prev.SkillToLearn, prev.RawMessage);
                        _sendMessages();
                    }
                }
            }
        }
    }
}
