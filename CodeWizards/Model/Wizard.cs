using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Wizard : LivingUnit {
        private readonly long ownerPlayerId;
        private readonly bool isMe;
        private readonly int mana;
        private readonly int maxMana;
        private readonly double visionRange;
        private readonly double castRange;
        private readonly int xp;
        private readonly int level;
        private readonly SkillType[] skills;
        private readonly int remainingActionCooldownTicks;
        private readonly int[] remainingCooldownTicksByAction;
        private readonly bool isMaster;
        private readonly Message[] messages;

        public Wizard(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius, int life, int maxLife, Status[] statuses, long ownerPlayerId, bool isMe, int mana,
                int maxMana, double visionRange, double castRange, int xp, int level, SkillType[] skills,
                int remainingActionCooldownTicks, int[] remainingCooldownTicksByAction, bool isMaster,
                Message[] messages)
                : base(id, x, y, speedX, speedY, angle, faction, radius, life, maxLife, statuses) {
            this.ownerPlayerId = ownerPlayerId;
            this.isMe = isMe;
            this.mana = mana;
            this.maxMana = maxMana;
            this.visionRange = visionRange;
            this.castRange = castRange;
            this.xp = xp;
            this.level = level;

            this.skills = new SkillType[skills.Length];
            Array.Copy(skills, this.skills, skills.Length);

            this.remainingActionCooldownTicks = remainingActionCooldownTicks;

            this.remainingCooldownTicksByAction = new int[remainingCooldownTicksByAction.Length];
            Array.Copy(remainingCooldownTicksByAction, this.remainingCooldownTicksByAction, remainingCooldownTicksByAction.Length);

            this.isMaster = isMaster;

            this.messages = new Message[messages.Length];
            Array.Copy(messages, this.messages, messages.Length);
        }

        public long OwnerPlayerId => ownerPlayerId;
        public bool IsMe => isMe;
        public int Mana => mana;
        public int MaxMana => maxMana;
        public double VisionRange => visionRange;
        public double CastRange => castRange;
        public int Xp => xp;
        public int Level => level;

        public SkillType[] Skills {
            get {
                if (this.skills == null) {
                    return null;
                }

                SkillType[] skills = new SkillType[this.skills.Length];
                Array.Copy(this.skills, skills, this.skills.Length);
                return skills;
            }
        }

        public int RemainingActionCooldownTicks => remainingActionCooldownTicks;

        public int[] RemainingCooldownTicksByAction {
            get {
                if (this.remainingCooldownTicksByAction == null) {
                    return null;
                }

                int[] remainingCooldownTicksByAction = new int[this.remainingCooldownTicksByAction.Length];
                Array.Copy(this.remainingCooldownTicksByAction, remainingCooldownTicksByAction, this.remainingCooldownTicksByAction.Length);
                return remainingCooldownTicksByAction;
            }
        }

        public bool IsMaster => isMaster;

        public Message[] Messages {
            get {
                if (this.messages == null) {
                    return null;
                }

                Message[] messages = new Message[this.messages.Length];
                Array.Copy(this.messages, messages, this.messages.Length);
                return messages;
            }
        }
    }
}