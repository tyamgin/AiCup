using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk {
    public interface IStrategy {
        void Move(Car self, World world, Game game, Move move);
    }
}