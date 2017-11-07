using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk {
    public interface IStrategy {
        void Move(Player me, World world, Game game, Move move);
    }
}