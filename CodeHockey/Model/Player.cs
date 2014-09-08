using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public class Player {
        private readonly long id;
        private readonly bool isMe;
        private readonly string name;
        private readonly int goalCount;
        private readonly bool isStrategyCrashed;
        private readonly double netTop;
        private readonly double netLeft;
        private readonly double netBottom;
        private readonly double netRight;
        private readonly double netFront;
        private readonly double netBack;
        private readonly bool isJustScoredGoal;
        private readonly bool isJustMissedGoal;

        public Player(long id, bool isMe, string name, int goalCount, bool isStrategyCrashed, double netTop,
                double netLeft, double netBottom, double netRight, double netFront, double netBack,
                bool isJustScoredGoal, bool isJustMissedGoal) {
            this.id = id;
            this.isMe = isMe;
            this.name = name;
            this.goalCount = goalCount;
            this.isStrategyCrashed = isStrategyCrashed;
            this.netTop = netTop;
            this.netLeft = netLeft;
            this.netBottom = netBottom;
            this.netRight = netRight;
            this.netFront = netFront;
            this.netBack = netBack;
            this.isJustScoredGoal = isJustScoredGoal;
            this.isJustMissedGoal = isJustMissedGoal;
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

        public int GoalCount {
            get { return goalCount; }
        }

        public bool IsStrategyCrashed {
            get { return isStrategyCrashed; }
        }

        public double NetTop {
            get { return netTop; }
        }

        public double NetLeft {
            get { return netLeft; }
        }

        public double NetBottom {
            get { return netBottom; }
        }

        public double NetRight {
            get { return netRight; }
        }

        public double NetFront {
            get { return netFront; }
        }

        public double NetBack {
            get { return netBack; }
        }

        public bool IsJustScoredGoal {
            get { return isJustScoredGoal; }
        }

        public bool IsJustMissedGoal {
            get { return isJustMissedGoal; }
        }
    }
}