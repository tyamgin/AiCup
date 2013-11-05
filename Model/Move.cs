namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public sealed class Move
    {
        private ActionType action = ActionType.EndTurn;
        private Direction? direction;
        private int x = -1;
        private int y = -1;

        public int Y
        {
            get { return y; }
            set { y = value; }
        }

        public int X
        {
            get { return x; }
            set { x = value; }
        }

        public Direction? Direction
        {
            get { return direction; }
            set { direction = value; }
        }

        public ActionType Action
        {
            get { return action; }
            set { action = value; }
        }
    }
}