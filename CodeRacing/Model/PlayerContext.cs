using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public sealed class PlayerContext {
        private readonly Car[] cars;
        private readonly World world;

        public PlayerContext(Car[] cars, World world) {
            this.cars = new Car[cars.Length];
            Array.Copy(cars, this.cars, cars.Length);

            this.world = world;
        }

        public Car[] Cars {
            get {
                Car[] cars = new Car[this.cars.Length];
                Array.Copy(this.cars, cars, this.cars.Length);
                return cars;
            }
        }

        public World World {
            get { return world; }
        }
    }
}