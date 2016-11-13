using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class ProjectilesObserver
    {
        private static Dictionary<long, AProjectile> _projectiles = new Dictionary<long, AProjectile>(); 

        public static void Update(World world)
        {
            var projectiles = world.Projectiles
                .Select(x => new AProjectile(x))
                .Where(x => !x.IsFriendly)
                .ToArray();

            var newDict = new Dictionary<long, AProjectile>();

            foreach (var proj in projectiles)
            {
                if (_projectiles.ContainsKey(proj.Id))
                {
                    // был и сейчас есть
                    var p = _projectiles[proj.Id];
                    p.Move();
                    // check: p.X == proj.X, p.Y = proj.Y
                }
                else
                {
                    // только появился
                    var owner = world.Wizards.FirstOrDefault(x => x.Id == proj.OwnerUnitId);
                    if (owner != null && proj.GetDistanceTo(owner) < proj.Speed * 1.2)
                    {
                        newDict[proj.Id] = proj;
                        proj.RemainingDistance -= proj.Speed;
                    }
                }
            }
            // которые были и пропали не попадут в newDict

            _projectiles = newDict;
        }

        public static AProjectile[] Projectiles => _projectiles.Values.ToArray();
    }
}
