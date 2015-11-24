using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AMove
    {
        public double EnginePower;
        public bool IsBrake;
        public object WheelTurn; // Point or double
        public bool IsUseNitro;

        public int Times;

        public AMove Clone()
        {
            var ret = new AMove
            {
                EnginePower = EnginePower,
                IsBrake = IsBrake,
                IsUseNitro = IsUseNitro,
                Times = Times,
            };
            if (WheelTurn is Point)
                ret.WheelTurn = (WheelTurn as Point).Clone();
            else if (WheelTurn is TurnPattern)
                ret.WheelTurn = WheelTurn;
            else
                ret.WheelTurn = Convert.ToDouble(WheelTurn);
            return ret;
        }

        public void Apply(Move move, ACar self)
        {
            move.EnginePower = EnginePower;
            move.IsBrake = IsBrake;
            move.WheelTurn = WheelTurn is Point ? MyStrategy.TurnRound(self.GetAngleTo(WheelTurn as Point)) : Convert.ToDouble(WheelTurn);
            if (IsUseNitro)
                move.IsUseNitro = IsUseNitro;
        }

        public static bool ModelMove(ACar car, AMove m, PassedInfo total,
            ABonus[] bonusCandidates, AOilSlick[] slickCandidates, AProjectile[][] projCandidates, ACar[][] carCandidates)
        {
            var turn = m.WheelTurn is Point ? MyStrategy.TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            car.Move(m.EnginePower, turn, m.IsBrake, m.IsUseNitro, false);

            for(var i = 0; i < bonusCandidates.Length; i++)
            {
                if (total.Bonuses[i]) // бонус уже взят
                    continue;

                var bonus = bonusCandidates[i];
                if (car.TakeBonus(bonus))
                {
                    total.Importance += bonus.GetImportance(car.Original)*PathBruteForce.BonusImportanceCoeff;
                    total.Bonuses[i] = true;
                }
            }

            if (!total.Slicks) // если не въехал ни в одну лужу
            {
                foreach (var slick in slickCandidates)
                {
                    if (total.Slicks)
                        break;
                    slick.RemainingLifetime -= total.Time;
                    if (slick.Intersect(car, 9))
                    {
                        total.Importance -= slick.GetDanger()*PathBruteForce.OilSlickDangerCoeff;
                        total.Slicks = true;
                    }
                    slick.RemainingLifetime += total.Time;
                }
            }
            if (projCandidates.Length > 0 && total.Time < projCandidates[0].Length)
            {
                for(var i = 0; i < projCandidates.Length; i++)
                {
                    if (total.Projectiles[i])
                        continue;

                    var proj = projCandidates[i][total.Time];

                    if (proj.Intersect(car, 5))
                    {
                        total.Importance -= proj.GetDanger()*PathBruteForce.ProjectileDangerCoeff; //TODO: обработать шину отдельно
                        total.Projectiles[i] = true;
                    }
                }
            }
            if (carCandidates.Length > 0 && total.Time < carCandidates[0].Length && !total.Cars)
            {
                for (var i = 0; i < carCandidates.Length; i++)
                {
                    var opp = carCandidates[i][total.Time];

                    if (car.IntersectWith(opp))
                    {
                        total.Importance -= PathBruteForce.InactiveCarDangerCoeff;
                        total.Cars = true;
                        break;
                    }
                }
            }

            total.Time++;

            // проверка на стены
            return car.GetRect().All(p => !MyStrategy.IntersectTail(p));
        }
    }

    public class Moves : List<AMove>
    {
        public Moves Clone()
        {
            var clone = new Moves();
            clone.AddRange(this.Select(m => m.Clone()));
            return clone;
        }

        public void Normalize()
        {
            RemoveAll(m => m.Times == 0);
        }

        public int ComputeTime()
        {
            return this.Sum(x => x.Times);
        }

        public double ComputeImportance(ACar model)
        {
            model = model.Clone();
            var total = new PassedInfo();
            foreach (var move in this)
            {
                for (var t = 0; t < move.Times; t++)
                    _modelMove(model, move, total);
            }
#if DEBUG
            if (ComputeTime() != total.Time)
                throw new Exception("ComputeTime() != elapsedTime");
#endif
            return total.Time - total.Importance;
        }

        private bool _modelMove(ACar car, AMove m, PassedInfo total)
        {
            return AMove.ModelMove(car, m, total, 
                MyStrategy.Bonuses, MyStrategy.OilSlicks, MyStrategy.Projectiles, MyStrategy.OpponentsCars);
        }

        public void Pop()
        {
            RemoveAt(Count - 1);
        }
    }
}
