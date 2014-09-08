using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk {
    public interface IStrategy {
        void Move(Hockeyist self, World world, Game game, Move move);
    }
}