using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk {
    public interface IStrategy {
        void Move(Wizard self, World world, Game game, Move move);
    }
}