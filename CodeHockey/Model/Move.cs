using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public class Move {
        private double speedUp;
        private double turn;
        private ActionType action = ActionType.None;
        private double passPower = 1.0D;
        private double passAngle;
        private int teammateIndex = -1;

        public double SpeedUp {
            get { return speedUp; }
            set { speedUp = value; }
        }

        public double Turn {
            get { return turn; }
            set { turn = value; }
        }

        public ActionType Action {
            get { return action; }
            set { action = value; }
        }

        public double PassPower {
            get { return passPower; }
            set { passPower = value; }
        }

        public double PassAngle {
            get { return passAngle; }
            set { passAngle = value; }
        }

        public int TeammateIndex {
            get { return teammateIndex; }
            set { teammateIndex = value; }
        }
    }
}