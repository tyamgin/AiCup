using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class ProjectilesObserver
    {
        private static Dictionary<long, AProjectile> _projectiles = new Dictionary<long, AProjectile>();
        private static Dictionary<long, AWizard> _wizardsLastSeen = new Dictionary<long, AWizard>();

        public static void Update()
        {
            var projectiles = MyStrategy.World.Projectiles
                .Select(x => new AProjectile(x))
                .Where(x => !x.IsFriendly && x.Type != ProjectileType.Dart) // можно и Dart реализовать
                .ToArray();

            var newDict = new Dictionary<long, AProjectile>();
            foreach (var w in MyStrategy.OpponentWizards)
                _wizardsLastSeen[w.Id] = w;

            foreach (var proj in projectiles)
            {
                if (_projectiles.ContainsKey(proj.Id))
                {
                    // был и сейчас есть
                    var p = _projectiles[proj.Id];
                    newDict[p.Id] = p;
                    p.Move();
                    // check: p.X == proj.X, p.Y = proj.Y
                }
                else
                {
                    // только появился
                    var owner = MyStrategy.OpponentWizards.FirstOrDefault(x => x.Id == proj.OwnerUnitId);
                    if (owner == null && _wizardsLastSeen.ContainsKey(proj.OwnerUnitId))
                    {
                        // его снаряд видно, но самого не видно
                        owner = _wizardsLastSeen[proj.OwnerUnitId];
                    }
                    var castRange = owner?.CastRange ?? (MyStrategy.Game.IsSkillsEnabled ? 600 : 500);

                    // proj.GetDistanceTo(owner) < proj.Speed * 1.4
                    
                    newDict[proj.Id] = proj;
                    proj.RemainingDistance = castRange - proj.Speed;
                }
            }
            // которые были и пропали не попадут в newDict

            _projectiles = newDict;

#if DEBUG
            if (MyStrategy.World.TickIndex == MyStrategy._lastProjectileTick + 1)
            {
                foreach (var pr in MyStrategy.World.Projectiles)
                {
                    if (pr.OwnerUnitId == MyStrategy.Self.Id)
                    {
                        Visualizer.Visualizer.Projectiles[pr.Id] = MyStrategy._lastProjectilePoints;
                    }
                }
            }
#endif
        }

        public static AProjectile[] Projectiles => _projectiles.Values.ToArray();
    }
}
