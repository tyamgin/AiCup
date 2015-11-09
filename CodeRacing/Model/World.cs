using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class World {
        private readonly int tick;
        private readonly int tickCount;
        private readonly int lastTickIndex;
        private readonly int width;
        private readonly int height;
        private readonly Player[] players;
        private readonly Car[] cars;
        private readonly Projectile[] projectiles;
        private readonly Bonus[] bonuses;
        private readonly OilSlick[] oilSlicks;
        private readonly string mapName;
        private readonly TileType[][] tilesXY;
        private readonly int[][] waypoints;
        private readonly Direction startingDirection;

        public World(int tick, int tickCount, int lastTickIndex, int width, int height, Player[] players, Car[] cars,
                Projectile[] projectiles, Bonus[] bonuses, OilSlick[] oilSlicks, string mapName, TileType[][] tilesXY,
                int[][] waypoints, Direction startingDirection) {
            this.tick = tick;
            this.tickCount = tickCount;
            this.lastTickIndex = lastTickIndex;
            this.width = width;
            this.height = height;

            this.players = new Player[players.Length];
            Array.Copy(players, this.players, players.Length);

            this.cars = new Car[cars.Length];
            Array.Copy(cars, this.cars, cars.Length);

            this.projectiles = new Projectile[projectiles.Length];
            Array.Copy(projectiles, this.projectiles, projectiles.Length);

            this.bonuses = new Bonus[bonuses.Length];
            Array.Copy(bonuses, this.bonuses, bonuses.Length);

            this.oilSlicks = new OilSlick[oilSlicks.Length];
            Array.Copy(oilSlicks, this.oilSlicks, oilSlicks.Length);

            this.mapName = mapName;

            this.tilesXY = new TileType[tilesXY.Length][];
            for (int i = tilesXY.Length - 1; i >= 0; --i) {
                this.tilesXY[i] = new TileType[tilesXY[i].Length];
                Array.Copy(tilesXY[i], this.tilesXY[i], tilesXY[i].Length);
            }

            this.waypoints = new int[waypoints.Length][];
            for (int i = waypoints.Length - 1; i >= 0; --i) {
                this.waypoints[i] = new int[waypoints[i].Length];
                Array.Copy(waypoints[i], this.waypoints[i], waypoints[i].Length);
            }

            this.startingDirection = startingDirection;
        }

        public int Tick {
            get { return tick; }
        }

        public int TickCount {
            get { return tickCount; }
        }

        public int LastTickIndex {
            get { return lastTickIndex; }
        }

        public int Width {
            get { return width; }
        }

        public int Height {
            get { return height; }
        }

        public Player[] Players {
            get {
                Player[] players = new Player[this.players.Length];
                Array.Copy(this.players, players, this.players.Length);
                return players;
            }
        }

        public Car[] Cars {
            get {
                Car[] cars = new Car[this.cars.Length];
                Array.Copy(this.cars, cars, this.cars.Length);
                return cars;
            }
        }

        public Projectile[] Projectiles {
            get {
                Projectile[] projectiles = new Projectile[this.projectiles.Length];
                Array.Copy(this.projectiles, projectiles, this.projectiles.Length);
                return projectiles;
            }
        }

        public Bonus[] Bonuses {
            get {
                Bonus[] bonuses = new Bonus[this.bonuses.Length];
                Array.Copy(this.bonuses, bonuses, this.bonuses.Length);
                return bonuses;
            }
        }

        public OilSlick[] OilSlicks {
            get {
                OilSlick[] oilSlicks = new OilSlick[this.oilSlicks.Length];
                Array.Copy(this.oilSlicks, oilSlicks, this.oilSlicks.Length);
                return oilSlicks;
            }
        }

        public string MapName {
            get { return mapName; }
        }

        public TileType[][] TilesXY {
            get {
                TileType[][] tilesXY = new TileType[this.tilesXY.Length][];
                for (int i = this.tilesXY.Length - 1; i >= 0; --i) {
                    tilesXY[i] = new TileType[this.tilesXY[i].Length];
                    Array.Copy(this.tilesXY[i], tilesXY[i], this.tilesXY[i].Length);
                }
                return tilesXY;
            }
        }

        public int[][] Waypoints {
            get {
                int[][] waypoints = new int[this.waypoints.Length][];
                for (int i = this.waypoints.Length - 1; i >= 0; --i) {
                    waypoints[i] = new int[this.waypoints[i].Length];
                    Array.Copy(this.waypoints[i], waypoints[i], this.waypoints[i].Length);
                }
                return waypoints;
            }
        }

        public Direction StartingDirection {
            get { return startingDirection; }
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