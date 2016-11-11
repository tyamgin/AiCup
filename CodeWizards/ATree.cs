using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
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
