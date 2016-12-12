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
        private static int[] _lastVisibleStage = {0, 0};

        public static void Update()
        {
            foreach(var b in Bonuses)
                for(var t = MyStrategy.PrevTickIndex + 1; t < MyStrategy.World.TickIndex; t++) // это если убили
                    b.SkipTick();

            var interval = MyStrategy.Game.BonusAppearanceIntervalTicks;
            var newDict = new Dictionary<int, ABonus>();
            var curStage = (MyStrategy.World.TickIndex-1)/interval;
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
                    oldBonus.SkipTick();
                    newDict[oldBonus.Order] = oldBonus;
                }
                else
                {
                    _lastVisibleStage[oldBonus.Order] = curStage;
                    if (!oldBonus.Exists && !newDict.ContainsKey(oldBonus.Order))
                    {
                        // вижу, но он ещё не появился
                        oldBonus.SkipTick();
                        if (!oldBonus.Exists) // если он не должен был появиться только что
                            newDict[oldBonus.Order] = oldBonus;
                    }
                }
            }

            for (var i = 0; i < 2; i++)
            {
                if (!newDict.ContainsKey(i))
                {
                    var remains = curStage > _lastVisibleStage[i]
                        ? 0
                        : (MyStrategy.World.TickIndex == 0
                            ? interval + 1
                            : (interval - MyStrategy.World.TickIndex%interval)%interval + 1
                            );
                    if (MyStrategy.World.TickIndex + remains >= MyStrategy.Game.TickCount)
                        remains = 100500;

                    newDict[i] = new ABonus
                    {
                        X = Const.BonusAppearencePoints[i].X,
                        Y = Const.BonusAppearencePoints[i].Y,
                        Id = i - 2,
                        RemainingAppearanceTicks = remains,
                        Radius = MyStrategy.Game.BonusRadius,
                    };
                }
            }
            _bonuses = newDict;
        }

        public static IEnumerable<ABonus> Bonuses => _bonuses.Values;
    }
}
