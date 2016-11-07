using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Move {
        private double speed;
        private double strafeSpeed;
        private double turn;
        private ActionType? action;
        private double castAngle;
        private double minCastDistance;
        private double maxCastDistance = 10000.0D;
        private long statusTargetId = -1L;
        private SkillType? skillToLearn;
        private Message[] messages;

        public double Speed {
            get { return speed; }
            set { speed = value; }
        }

        public double StrafeSpeed {
            get { return strafeSpeed; }
            set { strafeSpeed = value; }
        }

        public double Turn {
            get { return turn; }
            set { turn = value; }
        }

        public ActionType? Action {
            get { return action; }
            set { action = value; }
        }

        public double CastAngle {
            get { return castAngle; }
            set { castAngle = value; }
        }

        public double MinCastDistance {
            get { return minCastDistance; }
            set { minCastDistance = value; }
        }

        public double MaxCastDistance {
            get { return maxCastDistance; }
            set { maxCastDistance = value; }
        }

        public long StatusTargetId {
            get { return statusTargetId; }
            set { statusTargetId = value; }
        }

        public SkillType? SkillToLearn {
            get { return skillToLearn; }
            set { skillToLearn = value; }
        }

        public Message[] Messages {
            get {
                if (this.messages == null) {
                    return null;
                }

                Message[] messages = new Message[this.messages.Length];
                Array.Copy(this.messages, messages, this.messages.Length);
                return messages;
            }
            set {
                if (value == null) {
                    messages = null;
                    return;
                }

                messages = new Message[value.Length];
                Array.Copy(value, messages, value.Length);
            }
        }
    }
}