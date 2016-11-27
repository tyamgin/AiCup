using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        public delegate ALaneType SelectLaneFunc(AWizard w);

        public void MasterSendMessages()
        {
            if (World.TickIndex == 0)
            {
                var self = new AWizard(ASelf);
                AWizard
                    leftmost = self,
                    bottommost = self;
                foreach (var w in Wizards)
                {
                    if (!w.IsTeammate)
                        continue;

                    if (w.X < leftmost.X)
                        leftmost = w;
                    if (w.Y > bottommost.Y)
                        bottommost = w;
                }

                SelectLaneFunc selectLane = x =>
                {
                    if (x == leftmost)
                        return ALaneType.Top;
                    if (x == bottommost)
                        return ALaneType.Bottom;

                    return ALaneType.Middle;
                };

                FinalMove.Messages = Wizards
                    .Where(x => x.IsTeammate && x.Id != Self.Id)
                    .OrderBy(x => x.Id)
                    .Select(x =>
                    {
                        var lane = (LaneType) selectLane(x);
                        if (x == leftmost)
                            return new Message(lane, SkillType.Shield, new byte[] {});
                        if (x == bottommost)
                            return new Message(lane, SkillType.Shield, new byte[] { });

                        return new Message(lane, SkillType.Shield, new byte[] { });
                    })
                    .ToArray();

                MessagesObserver.LastMessage = new Message((LaneType) selectLane(self), SkillType.Shield, new byte[] {});
            }
        }
    }
}
