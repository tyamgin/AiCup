using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class ATree : ACircularUnit
    {
        public ATree(Tree tree) : base(tree)
        {
        }

        public ATree(ATree tree) : base(tree)
        {
        }
    }
}
