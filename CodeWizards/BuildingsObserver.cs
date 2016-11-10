using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class BuildingsObserver
    {
        public readonly static List<ACircularUnit> Buildings = new List<ACircularUnit>();
        private static readonly HashSet<long> _ids = new HashSet<long>();

        public static void Update(World world)
        {
            foreach (var building in world.Buildings)
            {
                if (_ids.Contains(building.Id))
                    continue;

                _ids.Add(building.Id);
                Buildings.Add(new ACircularUnit(building));
            }
        }
    }
}
