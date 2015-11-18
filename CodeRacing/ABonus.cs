using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    class ABonus : ARectUnit
    {
        public BonusType Type;

        public ABonus(Bonus bonus)
        {
            Type = bonus.Type;
            X = bonus.X;
            Y = bonus.Y;
            Width = bonus.Width;
            Height = bonus.Height;
        }
    }
}
