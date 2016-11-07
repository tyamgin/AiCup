using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Message {
        private readonly LineType line;
        private readonly SkillType? skillToLearn;
        private readonly byte[] rawMessage;

        public Message(LineType line, SkillType? skillToLearn, byte[] rawMessage) {
            this.line = line;
            this.skillToLearn = skillToLearn;

            this.rawMessage = new byte[rawMessage.Length];
            Array.Copy(rawMessage, this.rawMessage, rawMessage.Length);
        }

        public LineType Line => line;
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