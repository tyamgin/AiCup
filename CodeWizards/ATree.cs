using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class ATree : ACircularUnit
    {
        public double Life;

        public ATree(Tree tree) : base(tree)
        {
            Life = tree.Life;
        }

        public ATree(ATree tree) : base(tree)
        {
            Life = tree.Life;
        }
    }
}
