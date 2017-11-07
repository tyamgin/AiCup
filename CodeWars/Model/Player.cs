using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public class Player {
        private readonly long id;
        private readonly bool isMe;
        private readonly bool isStrategyCrashed;
        private readonly int score;
        private readonly int remainingActionCooldownTicks;

        public Player(long id, bool isMe, bool isStrategyCrashed, int score, int remainingActionCooldownTicks) {
            this.id = id;
            this.isMe = isMe;
            this.isStrategyCrashed = isStrategyCrashed;
            this.score = score;
            this.remainingActionCooldownTicks = remainingActionCooldownTicks;
        }

        public long Id => id;
        public bool IsMe => isMe;
        public bool IsStrategyCrashed => isStrategyCrashed;
        public int Score => score;
        public int RemainingActionCooldownTicks => remainingActionCooldownTicks;
    }
}