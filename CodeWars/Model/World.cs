using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public class World {
        private readonly int tickIndex;
        private readonly int tickCount;
        private readonly double width;
        private readonly double height;
        private readonly Player[] players;
        private readonly Vehicle[] newVehicles;
        private readonly VehicleUpdate[] vehicleUpdates;
        private readonly TerrainType[][] terrainByCellXY;
        private readonly WeatherType[][] weatherByCellXY;
        private readonly Facility[] facilities;

        public World(int tickIndex, int tickCount, double width, double height, Player[] players, Vehicle[] newVehicles,
                VehicleUpdate[] vehicleUpdates, TerrainType[][] terrainByCellXY, WeatherType[][] weatherByCellXY,
                Facility[] facilities) {
            this.tickIndex = tickIndex;
            this.tickCount = tickCount;
            this.width = width;
            this.height = height;

            this.players = new Player[players.Length];
            Array.Copy(players, this.players, players.Length);

            this.newVehicles = new Vehicle[newVehicles.Length];
            Array.Copy(newVehicles, this.newVehicles, newVehicles.Length);

            this.vehicleUpdates = new VehicleUpdate[vehicleUpdates.Length];
            Array.Copy(vehicleUpdates, this.vehicleUpdates, vehicleUpdates.Length);

            this.terrainByCellXY = new TerrainType[terrainByCellXY.Length][];
            for (int i = terrainByCellXY.Length - 1; i >= 0; --i) {
                this.terrainByCellXY[i] = new TerrainType[terrainByCellXY[i].Length];
                Array.Copy(terrainByCellXY[i], this.terrainByCellXY[i], terrainByCellXY[i].Length);
            }

            this.weatherByCellXY = new WeatherType[weatherByCellXY.Length][];
            for (int i = weatherByCellXY.Length - 1; i >= 0; --i) {
                this.weatherByCellXY[i] = new WeatherType[weatherByCellXY[i].Length];
                Array.Copy(weatherByCellXY[i], this.weatherByCellXY[i], weatherByCellXY[i].Length);
            }

            this.facilities = new Facility[facilities.Length];
            Array.Copy(facilities, this.facilities, facilities.Length);
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

        public Vehicle[] NewVehicles {
            get {
                if (this.newVehicles == null) {
                    return null;
                }

                Vehicle[] newVehicles = new Vehicle[this.newVehicles.Length];
                Array.Copy(this.newVehicles, newVehicles, this.newVehicles.Length);
                return newVehicles;
            }
        }

        public VehicleUpdate[] VehicleUpdates {
            get {
                if (this.vehicleUpdates == null) {
                    return null;
                }

                VehicleUpdate[] vehicleUpdates = new VehicleUpdate[this.vehicleUpdates.Length];
                Array.Copy(this.vehicleUpdates, vehicleUpdates, this.vehicleUpdates.Length);
                return vehicleUpdates;
            }
        }

        public TerrainType[][] TerrainByCellXY {
            get {
                if (this.terrainByCellXY == null) {
                    return null;
                }

                TerrainType[][] terrainByCellXY = new TerrainType[this.terrainByCellXY.Length][];
                for (int i = this.terrainByCellXY.Length - 1; i >= 0; --i) {
                    terrainByCellXY[i] = new TerrainType[this.terrainByCellXY[i].Length];
                    Array.Copy(this.terrainByCellXY[i], terrainByCellXY[i], this.terrainByCellXY[i].Length);
                }
                return terrainByCellXY;
            }
        }

        public WeatherType[][] WeatherByCellXY {
            get {
                if (this.weatherByCellXY == null) {
                    return null;
                }

                WeatherType[][] weatherByCellXY = new WeatherType[this.weatherByCellXY.Length][];
                for (int i = this.weatherByCellXY.Length - 1; i >= 0; --i) {
                    weatherByCellXY[i] = new WeatherType[this.weatherByCellXY[i].Length];
                    Array.Copy(this.weatherByCellXY[i], weatherByCellXY[i], this.weatherByCellXY[i].Length);
                }
                return weatherByCellXY;
            }
        }

        public Facility[] Facilities {
            get {
                if (this.facilities == null) {
                    return null;
                }

                Facility[] facilities = new Facility[this.facilities.Length];
                Array.Copy(this.facilities, facilities, this.facilities.Length);
                return facilities;
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