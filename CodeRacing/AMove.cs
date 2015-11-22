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

        public static bool ModelMove(ACar car, AMove m, int elapsedTime, ref double totalImportance,
            ABonus[] bonusCandidates, AOilSlick[] slickCandidates, AProjectile[][] projCandidates)
        {
            var turn = m.WheelTurn is Point ? MyStrategy.TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            var prevCar = car.Clone();
            car.Move(m.EnginePower, turn, m.IsBrake, m.IsUseNitro, false);
            foreach (var bonus in bonusCandidates)
            {
                if (car.TakeBonus(bonus) && !prevCar.TakeBonus(bonus))
                    totalImportance += bonus.GetImportance(car.Original) * PathBruteForce.BonusImportanceCoeff;
            }
            foreach (var slick in slickCandidates)
            {
                slick.RemainingLifetime -= elapsedTime;
                if (slick.Intersect(car, 9) && !slick.Intersect(prevCar, 9))
                    totalImportance -= slick.GetDanger() * PathBruteForce.OilSlickDangerCoeff;
                slick.RemainingLifetime += elapsedTime;
            }
            if (projCandidates.Length > 0 && elapsedTime < projCandidates[0].Length)
            {
                foreach (var projTimeline in projCandidates)
                {
                    var proj = projTimeline[elapsedTime];

                    if (proj.Intersect(car, 5) && !proj.Intersect(prevCar, 5))
                        totalImportance -= proj.GetDanger()*PathBruteForce.WasherDangerCoeff; //TODO
                }
            }
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
            var totalImportance = 0.0;
            var elapsedTime = 0;
            foreach (var move in this)
            {
                for (var t = 0; t < move.Times; t++)
                {
                    _modelMove(model, move, elapsedTime, ref totalImportance);
                    elapsedTime++;
                }
            }
#if DEBUG
            if (ComputeTime() != elapsedTime)
                throw new Exception("ComputeTime() != elapsedTime");
#endif
            return elapsedTime - totalImportance;
        }

        private bool _modelMove(ACar car, AMove m, int elapsedTime, ref double totalImportance)
        {
            return AMove.ModelMove(car, m, elapsedTime, ref totalImportance, 
                MyStrategy.Bonuses, MyStrategy.OilSlicks, MyStrategy.Projectiles);
        }

        public void Pop()
        {
            RemoveAt(Count - 1);
        }
    }
}
