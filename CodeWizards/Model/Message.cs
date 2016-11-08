using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Message {
        private readonly LaneType lane;
        private readonly SkillType? skillToLearn;
        private readonly byte[] rawMessage;

        public Message(LaneType lane, SkillType? skillToLearn, byte[] rawMessage) {
            this.lane = lane;
            this.skillToLearn = skillToLearn;

            this.rawMessage = new byte[rawMessage.Length];
            Array.Copy(rawMessage, this.rawMessage, rawMessage.Length);
        }

        public LaneType Lane => lane;
        public SkillType? SkillToLearn => skillToLearn;

        public byte[] RawMessage {
            get {
                if (this.rawMessage == null) {
                    return null;
                }

                byte[] rawMessage = new byte[this.rawMessage.Length];
                Array.Copy(this.rawMessage, rawMessage, this.rawMessage.Length);
                return rawMessage;
            }
        }
    }
}