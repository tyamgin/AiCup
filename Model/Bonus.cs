namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public sealed class Bonus : Unit
    {
        private readonly BonusType type;

        public Bonus(long id, int x, int y, BonusType type) : base(id, x, y)
        {
            this.type = type;
        }

        public BonusType Type
        {
            get { return type; }
        }
    }
}