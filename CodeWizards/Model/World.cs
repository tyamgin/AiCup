using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class World {
        private readonly int tickIndex;
        private readonly int tickCount;
        private readonly double width;
        private readonly double height;
        private readonly Player[] players;
        private readonly Wizard[] wizards;
        private readonly Minion[] minions;
        private readonly Projectile[] projectiles;
        private readonly Bonus[] bonuses;
        private readonly Building[] buildings;
        private readonly Tree[] trees;

        public World(int tickIndex, int tickCount, double width, double height, Player[] players, Wizard[] wizards,
                Minion[] minions, Projectile[] projectiles, Bonus[] bonuses, Building[] buildings, Tree[] trees) {
            this.tickIndex = tickIndex;
            this.tickCount = tickCount;
            this.width = width;
            this.height = height;

            this.players = new Player[players.Length];
            Array.Copy(players, this.players, players.Length);

            this.wizards = new Wizard[wizards.Length];
            Array.Copy(wizards, this.wizards, wizards.Length);

            this.minions = new Minion[minions.Length];
            Array.Copy(minions, this.minions, minions.Length);

            this.projectiles = new Projectile[projectiles.Length];
            Array.Copy(projectiles, this.projectiles, projectiles.Length);

            this.bonuses = new Bonus[bonuses.Length];
            Array.Copy(bonuses, this.bonuses, bonuses.Length);

            this.buildings = new Building[buildings.Length];
            Array.Copy(buildings, this.buildings, buildings.Length);

            this.trees = new Tree[trees.Length];
            Array.Copy(trees, this.trees, trees.Length);
        }

        public int TickIndex => tickIndex;
        public int TickCount => tickCount;
        public double Width => width;
        public double Height => height;

        public Player[] Players {
            get {
                if (this.players == null) {
                    return null;
                }

                Player[] players = new Player[this.players.Length];
                Array.Copy(this.players, players, this.players.Length);
                return players;
            }
        }

        public Wizard[] Wizards {
            get {
                if (this.wizards == null) {
                    return null;
                }

                Wizard[] wizards = new Wizard[this.wizards.Length];
                Array.Copy(this.wizards, wizards, this.wizards.Length);
                return wizards;
            }
        }

        public Minion[] Minions {
            get {
                if (this.minions == null) {
                    return null;
                }

                Minion[] minions = new Minion[this.minions.Length];
                Array.Copy(this.minions, minions, this.minions.Length);
                return minions;
            }
        }

        public Projectile[] Projectiles {
            get {
                if (this.projectiles == null) {
                    return null;
                }

                Projectile[] projectiles = new Projectile[this.projectiles.Length];
                Array.Copy(this.projectiles, projectiles, this.projectiles.Length);
                return projectiles;
            }
        }

        public Bonus[] Bonuses {
            get {
                if (this.bonuses == null) {
                    return null;
                }

                Bonus[] bonuses = new Bonus[this.bonuses.Length];
                Array.Copy(this.bonuses, bonuses, this.bonuses.Length);
                return bonuses;
            }
        }

        public Building[] Buildings {
            get {
                if (this.buildings == null) {
                    return null;
                }

                Building[] buildings = new Building[this.buildings.Length];
                Array.Copy(this.buildings, buildings, this.buildings.Length);
                return buildings;
            }
        }

        public Tree[] Trees {
            get {
                if (this.trees == null) {
                    return null;
                }

                Tree[] trees = new Tree[this.trees.Length];
                Array.Copy(this.trees, trees, this.trees.Length);
                return trees;
            }
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
    }
}