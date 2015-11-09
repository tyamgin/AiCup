using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class Player {
        private readonly long id;
        private readonly bool isMe;
        private readonly string name;
        private readonly bool isStrategyCrashed;
        private readonly int score;

        public Player(long id, bool isMe, string name, bool isStrategyCrashed, int score) {
            this.id = id;
            this.isMe = isMe;
            this.name = name;
            this.isStrategyCrashed = isStrategyCrashed;
            this.score = score;
        }

        public long Id {
            get { return id; }
        }

        public bool IsMe {
            get { return isMe; }
        }

        public string Name {
            get { return name; }
        }

        public bool IsStrategyCrashed {
            get { return isStrategyCrashed; }
        }

        public int Score {
            get { return score; }
        }
    }
}