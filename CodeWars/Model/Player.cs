using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public class Player {
        private readonly long id;
        private readonly bool isMe;
        private readonly bool isStrategyCrashed;
        private readonly int score;
        private readonly int remainingActionCooldownTicks;
        private readonly int remainingNuclearStrikeCooldownTicks;
        private readonly long nextNuclearStrikeVehicleId;
        private readonly int nextNuclearStrikeTickIndex;
        private readonly double nextNuclearStrikeX;
        private readonly double nextNuclearStrikeY;

        public Player(long id, bool isMe, bool isStrategyCrashed, int score, int remainingActionCooldownTicks,
                int remainingNuclearStrikeCooldownTicks, long nextNuclearStrikeVehicleId,
                int nextNuclearStrikeTickIndex, double nextNuclearStrikeX, double nextNuclearStrikeY) {
            this.id = id;
            this.isMe = isMe;
            this.isStrategyCrashed = isStrategyCrashed;
            this.score = score;
            this.remainingActionCooldownTicks = remainingActionCooldownTicks;
            this.remainingNuclearStrikeCooldownTicks = remainingNuclearStrikeCooldownTicks;
            this.nextNuclearStrikeVehicleId = nextNuclearStrikeVehicleId;
            this.nextNuclearStrikeTickIndex = nextNuclearStrikeTickIndex;
            this.nextNuclearStrikeX = nextNuclearStrikeX;
            this.nextNuclearStrikeY = nextNuclearStrikeY;
        }

        public long Id => id;
        public bool IsMe => isMe;
        public bool IsStrategyCrashed => isStrategyCrashed;
        public int Score => score;
        public int RemainingActionCooldownTicks => remainingActionCooldownTicks;
        public int RemainingNuclearStrikeCooldownTicks => remainingNuclearStrikeCooldownTicks;
        public long NextNuclearStrikeVehicleId => nextNuclearStrikeVehicleId;
        public int NextNuclearStrikeTickIndex => nextNuclearStrikeTickIndex;
        public double NextNuclearStrikeX => nextNuclearStrikeX;
        public double NextNuclearStrikeY => nextNuclearStrikeY;
    }
}