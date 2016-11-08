using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class TreesObserver
    {
        public readonly static List<ACircularUnit> Trees = new List<ACircularUnit>();
        private static HashSet<long> _ids = new HashSet<long>();

        public static void Update(World world)
        {
            foreach(var tree in world.Trees)
            {
                if (_ids.Contains(tree.Id))
                    continue;

                _ids.Add(tree.Id);
                Trees.Add(new ACircularUnit(tree));
            }
        }
    }
}
