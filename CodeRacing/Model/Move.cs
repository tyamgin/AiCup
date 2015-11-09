using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class Move {
        private double enginePower;
        private bool isBrake;
        private double wheelTurn;
        private bool isThrowProjectile;
        private bool isUseNitro;
        private bool isSpillOil;

        public double EnginePower {
            get { return enginePower; }
            set { enginePower = value; }
        }

        public bool IsBrake {
            get { return isBrake; }
            set { isBrake = value; }
        }

        public double WheelTurn {
            get { return wheelTurn; }
            set { wheelTurn = value; }
        }

        public bool IsThrowProjectile {
            get { return isThrowProjectile; }
            set { isThrowProjectile = value; }
        }

        public bool IsUseNitro {
            get { return isUseNitro; }
            set { isUseNitro = value; }
        }

        public bool IsSpillOil {
            get { return isSpillOil; }
            set { isSpillOil = value; }
        }
    }
}