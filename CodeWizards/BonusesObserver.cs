using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class BonusesObserver
    {
        private static Dictionary<int, ABonus> _bonuses = new Dictionary<int, ABonus>();

        // этап, когда последний раз был виден бонус (или было видно что там его нет)
        private static int[] _lastVisibleStage = {1, 1};

        public static void Update()
        {
            var interval = MyStrategy.Game.BonusAppearanceIntervalTicks;
            var newDict = new Dictionary<int, ABonus>();
            var curStage = MyStrategy.World.TickIndex/interval;
            foreach (var b in MyStrategy.World.Bonuses)
            {
                var visibleBonus = new ABonus(b);
                newDict[visibleBonus.Order] = visibleBonus;
                _lastVisibleStage[visibleBonus.Order] = curStage;
            }
            foreach (var oldBonus in _bonuses.Values)
            {
                if (!MyStrategy.IsPointVisible(oldBonus))
                {
                    // он точно не прилетел в World, т.к. не виден
                    // остается на текущей тик
                    if (oldBonus.RemainingAppearanceTicks > 0)
                        oldBonus.RemainingAppearanceTicks--;
                    newDict[oldBonus.Order] = oldBonus;
                    _lastVisibleStage[oldBonus.Order] = curStage;
                }
                else if (!oldBonus.Exists && !newDict.ContainsKey(oldBonus.Order))
                {
                    // вижу, но он ещё не появился
                    oldBonus.RemainingAppearanceTicks--;
                    newDict[oldBonus.Order] = oldBonus;
                }
            }

            for (var i = 0; i < 2; i++)
            {
                if (!_bonuses.ContainsKey(i))
                {
                    var remains = curStage > _lastVisibleStage[i]
                        ? 0
                        : (interval - MyStrategy.World.TickIndex%interval)/*%interval*/;

                    newDict[i] = new ABonus
                    {
                        X = Const.BonusAppearencePoints[i].X,
                        Y = Const.BonusAppearencePoints[i].Y,
                        Id = -1,
                        RemainingAppearanceTicks = remains,
                        Radius = MyStrategy.Game.BonusRadius,
                        //Type = BonusType.Empower,
                        //Angle = 0,
                    };
                }
            }
            _bonuses = newDict;
        }

        public static IEnumerable<ABonus> Bonuses => _bonuses.Values;
    }
}
