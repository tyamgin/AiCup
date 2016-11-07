using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World world;
        public static Game game;
        public static Wizard self;

        public void Move(Wizard self, World world, Game game, Move move)
        {
            MyStrategy.world = world;
            MyStrategy.game = game;
            MyStrategy.self = self;

            Const.Width = world.Width;
            Const.Height = world.Height;

            TreesObserver.Update(world);

#if DEBUG
            while (Visualizer.Pause)
            {
                // pause here
            }
            Visualizer.CreateForm();
#endif

            //move.Speed = game.WizardForwardSpeed;
            //move.StrafeSpeed = game.WizardStrafeSpeed;
            //move.Turn = game.WizardMaxTurnAngle;
            move.Action = ActionType.MagicMissile;

#if DEBUG
            Visualizer.LookUp(new Point(self.X, self.Y));
            Visualizer.Draw();
#endif
        }
    }
}