using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Player {
        private readonly long id;
        private readonly bool isMe;
        private readonly string name;
        private readonly bool isStrategyCrashed;
        private readonly int score;
        private readonly Faction faction;

        public Player(long id, bool isMe, string name, bool isStrategyCrashed, int score, Faction faction) {
            this.id = id;
            this.isMe = isMe;
            this.name = name;
            this.isStrategyCrashed = isStrategyCrashed;
            this.score = score;
            this.faction = faction;
        }

        public long Id => id;
        public bool IsMe => isMe;
        public string Name => name;
        public bool IsStrategyCrashed => isStrategyCrashed;
        public int Score => score;
        public Faction Faction => faction;
    }
}