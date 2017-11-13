using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public class Move {
        private ActionType? action;
        private int group;
        private double left;
        private double top;
        private double right;
        private double bottom;
        private double x;
        private double y;
        private double angle;
        private double factor;
        private double maxSpeed;
        private double maxAngularSpeed;
        private VehicleType? vehicleType;
        private long facilityId = -1L;
        private long vehicleId = -1L;

        public ActionType? Action {
            get { return action; }
            set { action = value; }
        }

        public int Group {
            get { return group; }
            set { group = value; }
        }

        public double Left {
            get { return left; }
            set { left = value; }
        }

        public double Top {
            get { return top; }
            set { top = value; }
        }

        public double Right {
            get { return right; }
            set { right = value; }
        }

        public double Bottom {
            get { return bottom; }
            set { bottom = value; }
        }

        public double X {
            get { return x; }
            set { x = value; }
        }

        public double Y {
            get { return y; }
            set { y = value; }
        }

        public double Angle {
            get { return angle; }
            set { angle = value; }
        }

        public double Factor {
            get { return factor; }
            set { factor = value; }
        }

        public double MaxSpeed {
            get { return maxSpeed; }
            set { maxSpeed = value; }
        }

        public double MaxAngularSpeed {
            get { return maxAngularSpeed; }
            set { maxAngularSpeed = value; }
        }

        public VehicleType? VehicleType {
            get { return vehicleType; }
            set { vehicleType = value; }
        }

        public long FacilityId {
            get { return facilityId; }
            set { facilityId = value; }
        }

        public long VehicleId {
            get { return vehicleId; }
            set { vehicleId = value; }
        }
    }
}