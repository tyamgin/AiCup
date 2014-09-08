using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public class Hockeyist : Unit {
        private readonly long playerId;
        private readonly int teammateIndex;
        private readonly bool isTeammate;
        private readonly HockeyistType type;
        private readonly int strength;
        private readonly int endurance;
        private readonly int dexterity;
        private readonly int agility;
        private readonly double stamina;
        private readonly HockeyistState state;
        private readonly int originalPositionIndex;
        private readonly int remainingKnockdownTicks;
        private readonly int remainingCooldownTicks;
        private readonly int swingTicks;
        private readonly ActionType? lastAction;
        private readonly int? lastActionTick;

        public Hockeyist(long id, long playerId, int teammateIndex, double mass, double radius, double x, double y,
                double speedX, double speedY, double angle, double angularSpeed, bool isTeammate, HockeyistType type,
                int strength, int endurance, int dexterity, int agility, double stamina, HockeyistState state,
                int originalPositionIndex, int remainingKnockdownTicks, int remainingCooldownTicks, int swingTicks,
                ActionType? lastAction, int? lastActionTick)
                : base(id, mass, radius, x, y, speedX, speedY, angle, angularSpeed) {
            this.playerId = playerId;
            this.teammateIndex = teammateIndex;
            this.isTeammate = isTeammate;
            this.type = type;
            this.strength = strength;
            this.endurance = endurance;
            this.dexterity = dexterity;
            this.agility = agility;
            this.stamina = stamina;
            this.state = state;
            this.originalPositionIndex = originalPositionIndex;
            this.remainingKnockdownTicks = remainingKnockdownTicks;
            this.remainingCooldownTicks = remainingCooldownTicks;
            this.swingTicks = swingTicks;
            this.lastAction = lastAction;
            this.lastActionTick = lastActionTick;
        }

        public long PlayerId {
            get { return playerId; }
        }

        public int TeammateIndex {
            get { return teammateIndex; }
        }

        public bool IsTeammate {
            get { return isTeammate; }
        }

        public HockeyistType Type {
            get { return type; }
        }

        public int Strength {
            get { return strength; }
        }

        public int Endurance {
            get { return endurance; }
        }

        public int Dexterity {
            get { return dexterity; }
        }

        public int Agility {
            get { return agility; }
        }

        public double Stamina {
            get { return stamina; }
        }

        public HockeyistState State {
            get { return state; }
        }

        public int OriginalPositionIndex {
            get { return originalPositionIndex; }
        }

        public int RemainingKnockdownTicks {
            get { return remainingKnockdownTicks; }
        }

        public int RemainingCooldownTicks {
            get { return remainingCooldownTicks; }
        }

        public int SwingTicks {
            get { return swingTicks; }
        }

        public ActionType? LastAction {
            get { return lastAction; }
        }

        public int? LastActionTick {
            get { return lastActionTick; }
        }
    }
}