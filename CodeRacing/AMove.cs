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
            foreach (var move in this)
            {
                for(var t = 0; t < move.Times; t++)
                    _modelMove(model, move, ref totalImportance);
            }
            return ComputeTime() - totalImportance;
        }

        private bool _modelMove(ACar car, AMove m, ref double totalImportance)
        {
            var turn = m.WheelTurn is Point ? MyStrategy.TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            var prevCar = car.Clone();
            car.Move(m.EnginePower, turn, m.IsBrake, m.IsUseNitro, false);
            
            totalImportance += MyStrategy.world.Bonuses
                .Select(bonus => new ABonus(bonus))
                .Where(bonus => car.TakeBonus(bonus) && !prevCar.TakeBonus(bonus))
                .Sum(bonus => bonus.GetImportance(car.Original)) * PathBruteForce.BonusImportanceCoeff;
            totalImportance -= MyStrategy.world.OilSlicks
                .Select(slick => new AOilSlick(slick))
                .Where(slick => slick.Intersect(car, 9) && !slick.Intersect(prevCar, 9))
                .Sum(slick => slick.GetDanger())*PathBruteForce.OilSlickDangerCoeff;

            return car.GetRect().All(p => !MyStrategy.IntersectTail(p));
        }

        public void Pop()
        {
            RemoveAt(Count - 1);
        }
    }
}
