using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk {
    public sealed class MyStrategy : IStrategy {
        public void Move(Player me, World world, Game game, Move move) {
            if (world.TickIndex == 0) {
                move.Action = ActionType.ClearAndSelect;
                move.Right = world.Width;
                move.Bottom = world.Height;
                return;
            }

            if (world.TickIndex == 1) {
                move.Action = ActionType.Move;
                move.X = world.Width / 2.0D;
                move.Y = world.Height / 2.0D;
            }
        }
    }
}