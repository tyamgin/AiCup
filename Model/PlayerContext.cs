namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public sealed class PlayerContext
    {
        private readonly Trooper trooper;
        private readonly World world;

        public PlayerContext(Trooper trooper, World world)
        {
            this.trooper = trooper;
            this.world = world;
        }

        public Trooper Trooper
        {
            get { return trooper; }
        }

        public World World
        {
            get { return world; }
        }
    }
}