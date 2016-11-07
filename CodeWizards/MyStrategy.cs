using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk {
    public sealed class MyStrategy : IStrategy {
        public void Move(Wizard self, World world, Game game, Move move) {
            move.Speed = game.WizardForwardSpeed;
            move.StrafeSpeed = game.WizardStrafeSpeed;
            move.Turn = game.WizardMaxTurnAngle;
            move.Action = ActionType.MagicMissile;
        }
    }
}