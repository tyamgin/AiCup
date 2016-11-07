using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Status {
        private readonly long id;
        private readonly StatusType type;
        private readonly long wizardId;
        private readonly long playerId;
        private readonly int remainingDurationTicks;

        public Status(long id, StatusType type, long wizardId, long playerId, int remainingDurationTicks) {
            this.id = id;
            this.type = type;
            this.wizardId = wizardId;
            this.playerId = playerId;
            this.remainingDurationTicks = remainingDurationTicks;
        }

        public long Id => id;
        public StatusType Type => type;
        public long WizardId => wizardId;
        public long PlayerId => playerId;
        public int RemainingDurationTicks => remainingDurationTicks;
    }
}