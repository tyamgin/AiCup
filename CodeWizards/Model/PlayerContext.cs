using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public sealed class PlayerContext {
        private readonly Wizard[] wizards;
        private readonly World world;

        public PlayerContext(Wizard[] wizards, World world) {
            this.wizards = new Wizard[wizards.Length];
            Array.Copy(wizards, this.wizards, wizards.Length);

            this.world = world;
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

        public World World => world;
    }
}