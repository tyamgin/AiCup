using System;
using System.Collections.Generic;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class MessagesObserver
    {
        public static AMessage LastMessage;

        public static void Update()
        {
            var msges = MyStrategy.Self.Messages;
            if (msges != null && msges.Length > 0)
                LastMessage = new AMessage(msges[msges.Length - 1]);
        }


        public static ALaneType GetLane()
        {
            if (LastMessage != null)
                return LastMessage.Lane;

            var arr = new [] { LaneType.Top, LaneType.Bottom };
            return (ALaneType) arr[MyStrategy.Self.Id%arr.Length];
        }

        private static SkillType? _getNearestSkill(AWizard self, SkillType skill)
        {
            var skillGroup = Utility.GetSkillGroup(skill);
            var skillOrder = Utility.GetSkillOrder(skill);
            if (self.SkillsLearnedArr[skillGroup] > skillOrder)
                return null;

            return Utility.GetSkill(skillGroup, self.SkillsLearnedArr[skillGroup]);
        }

        public static SkillType GetSkill()
        {
            var self = MyStrategy.ASelf;

            var group = SkillsGroup.Round2;

            if (Const.IsFinal && LastMessage.RawMessage != null && LastMessage.RawMessage.Length > 0)
            {
                group = (SkillsGroup) LastMessage.RawMessage[0];
            }

            var skillsOrder = SkillGroups.Orders[group];

            foreach (var skill in skillsOrder)
            {
                var skillToLearn = _getNearestSkill(self, skill);
                if (skillToLearn != null)
                    return (SkillType)skillToLearn;
            }
            throw new Exception("An impossible state");
        }
    }
}
