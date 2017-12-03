using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public sealed partial class PlayerContext {
        private readonly Player player;
        private readonly World world;

        public PlayerContext(Player player, World world) {
            this.player = player;
            this.world = world;
        }

        public Player Player => player;
        public World World => world;
    }
}