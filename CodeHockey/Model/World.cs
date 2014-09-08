using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public class World {
        private readonly int tick;
        private readonly int tickCount;
        private readonly double width;
        private readonly double height;
        private readonly Player[] players;
        private readonly Hockeyist[] hockeyists;
        private readonly Puck puck;

        public World(int tick, int tickCount, double width, double height, Player[] players, Hockeyist[] hockeyists,
                Puck puck) {
            this.tick = tick;
            this.tickCount = tickCount;
            this.width = width;
            this.height = height;

            this.players = new Player[players.Length];
            Array.Copy(players, this.players, players.Length);

            this.hockeyists = new Hockeyist[hockeyists.Length];
            Array.Copy(hockeyists, this.hockeyists, hockeyists.Length);

            this.puck = puck;
        }

        public int Tick {
            get { return tick; }
        }

        public int TickCount {
            get { return tickCount; }
        }

        public double Width {
            get { return width; }
        }

        public double Height {
            get { return height; }
        }

        public Player[] Players {
            get {
                Player[] players = new Player[this.players.Length];
                Array.Copy(this.players, players, this.players.Length);
                return players;
            }
        }

        public Hockeyist[] Hockeyists {
            get {
                Hockeyist[] hockeyists = new Hockeyist[this.hockeyists.Length];
                Array.Copy(this.hockeyists, hockeyists, this.hockeyists.Length);
                return hockeyists;
            }
        }

        public Puck Puck {
            get { return puck; }
        }

        public Player GetMyPlayer() {
            for (int playerIndex = players.Length - 1; playerIndex >= 0; --playerIndex) {
                Player player = players[playerIndex];
                if (player.IsMe) {
                    return player;
                }
            }

            return null;
        }

        public Player GetOpponentPlayer() {
            for (int playerIndex = players.Length - 1; playerIndex >= 0; --playerIndex) {
                Player player = players[playerIndex];
                if (!player.IsMe) {
                    return player;
                }
            }

            return null;
        }
    }
}