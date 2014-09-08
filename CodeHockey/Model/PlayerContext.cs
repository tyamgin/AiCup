using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public sealed class PlayerContext {
        private readonly Hockeyist[] hockeyists;
        private readonly World world;

        public PlayerContext(Hockeyist[] hockeyists, World world) {
            this.hockeyists = new Hockeyist[hockeyists.Length];
            Array.Copy(hockeyists, this.hockeyists, hockeyists.Length);

            this.world = world;
        }

        public Hockeyist[] Hockeyists {
            get {
                Hockeyist[] hockeyists = new Hockeyist[this.hockeyists.Length];
                Array.Copy(this.hockeyists, hockeyists, this.hockeyists.Length);
                return hockeyists;
            }
        }

        public World World {
            get { return world; }
        }
    }
}