namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public sealed class Player
    {
        private readonly long id;
        private readonly string name;
        private readonly int score;
        private readonly bool isStrategyCrashed;

        public Player(long id, string name, int score, bool isStrategyCrashed)
        {
            this.id = id;
            this.name = name;
            this.score = score;
            this.isStrategyCrashed = isStrategyCrashed;
        }

        public long Id
        {
            get { return id; }
        }

        public string Name
        {
            get { return name; }
        }

        public int Score
        {
            get { return score; }
        }

        public bool IsStrategyCrashed
        {
            get { return isStrategyCrashed; }
        }
    }
}