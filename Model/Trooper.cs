using System;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public sealed class Trooper : Unit
    {
        private readonly long playerId;
        private readonly int teammateIndex;
        private readonly bool isTeammate;

        private readonly TrooperType type;
        private readonly TrooperStance stance;

        private readonly int hitpoints;
        private readonly int maximalHitpoints;

        private readonly int actionPoints;
        private readonly int initialActionPoints;

        private readonly double visionRange;
        private readonly double shootingRange;

        private readonly int shootCost;
        private readonly int standingDamage;
        private readonly int kneelingDamage;
        private readonly int proneDamage;
        private readonly int damage;

        private readonly bool isHoldingGrenade;
        private readonly bool isHoldingMedikit;
        private readonly bool isHoldingFieldRation;

        public Trooper(long id, int x, int y, long playerId,
            int teammateIndex, bool isTeammate, TrooperType type, TrooperStance stance,
            int hitpoints, int maximalHitpoints, int actionPoints, int initialActionPoints,
            double visionRange, double shootingRange, int shootCost,
            int standingDamage, int kneelingDamage, int proneDamage, int damage,
            bool isHoldingGrenade, bool isHoldingMedikit, bool isHoldingFieldRation)
            : base(id, x, y)
        {
            this.playerId = playerId;
            this.teammateIndex = teammateIndex;
            this.isTeammate = isTeammate;
            this.type = type;
            this.stance = stance;
            this.hitpoints = hitpoints;
            this.maximalHitpoints = maximalHitpoints;
            this.actionPoints = actionPoints;
            this.initialActionPoints = initialActionPoints;
            this.visionRange = visionRange;
            this.shootingRange = shootingRange;
            this.shootCost = shootCost;
            this.standingDamage = standingDamage;
            this.kneelingDamage = kneelingDamage;
            this.proneDamage = proneDamage;
            this.damage = damage;
            this.isHoldingGrenade = isHoldingGrenade;
            this.isHoldingMedikit = isHoldingMedikit;
            this.isHoldingFieldRation = isHoldingFieldRation;
        }

        public long PlayerId
        {
            get { return playerId; }
        }

        public int TeammateIndex
        {
            get { return teammateIndex; }
        }

        public bool IsTeammate
        {
            get { return isTeammate; }
        }

        public TrooperType Type
        {
            get { return type; }
        }

        public TrooperStance Stance
        {
            get { return stance; }
        }

        public int Hitpoints
        {
            get { return hitpoints; }
        }

        public int MaximalHitpoints
        {
            get { return maximalHitpoints; }
        }

        public int ActionPoints
        {
            get { return actionPoints; }
        }

        public int InitialActionPoints
        {
            get { return initialActionPoints; }
        }

        public double VisionRange
        {
            get { return visionRange; }
        }

        public double ShootingRange
        {
            get { return shootingRange; }
        }

        public int ShootCost
        {
            get { return shootCost; }
        }

        public int StandingDamage
        {
            get { return standingDamage; }
        }

        public int KneelingDamage
        {
            get { return kneelingDamage; }
        }

        public int ProneDamage
        {
            get { return proneDamage; }
        }

        public int Damage
        {
            get { return damage; }
        }

        public bool IsHoldingGrenade
        {
            get { return isHoldingGrenade; }
        }

        public bool IsHoldingMedikit
        {
            get { return isHoldingMedikit; }
        }

        public bool IsHoldingFieldRation
        {
            get { return isHoldingFieldRation; }
        }
        
        public int GetDamage(TrooperStance stance)
        {
            switch (stance)
            {
                case TrooperStance.Prone:
                    return proneDamage;
                case TrooperStance.Kneeling:
                    return kneelingDamage;
                case TrooperStance.Standing:
                    return standingDamage;
                default:
                    throw new ArgumentException("Unsupported stance: " + stance + '.');
            }
        }
    }
}