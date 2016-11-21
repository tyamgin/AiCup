using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        public void MasterSendMessages()
        {
            // TODO: не пашет
            if (World.TickIndex == 0)
            {
                //FinalMove.Messages = new[]
                //{
                //    new Message(LaneType.Bottom, null, new byte[] {}),
                //    new Message(LaneType.Bottom, null, new byte[] {}),
                //    new Message(LaneType.Bottom, null, new byte[] {}),
                //    new Message(LaneType.Bottom, null, new byte[] {}),
                //};

                var self = new AWizard(Self);
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

                
                FinalMove.Messages = Wizards
                    .Where(x => x.IsTeammate && x.Id != Self.Id)
                    .OrderBy(x => x.Id)
                    .Select(x =>
                    {
                        if (x == leftmost)
                            return new Message(LaneType.Top, SkillType.Shield, new byte[] {});
                        if (x == bottommost)
                            return new Message(LaneType.Bottom, SkillType.Shield, new byte[] { });

                        return new Message(LaneType.Middle, SkillType.Shield, new byte[] { });
                    })
                    .ToArray();
            }
        }
    }
}
