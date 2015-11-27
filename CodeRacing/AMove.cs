using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AMove
    {
        public const double BonusImportanceCoeff = 30;
        public const double OilSlickDangerCoeff = 40;
        public const double ProjectileDangerCoeff = 40;
        public const double InactiveCarDangerCoeff = 60;
        public const double ExactlyBorderDangerCoeff = 50;
        public const double OutOfBorderDangerCoeff = 60;

        public double EnginePower;
        public bool IsBrake;
        public object WheelTurn;
        public bool IsUseNitro;

        public int Times;
        public double SafeMargin = MyStrategy.SafeMargin;
        public double ExactlyMargin = 1;
        public double ExtraMargin = -20;
        public bool RangesMode;

        public AMove Clone()
        {
            var ret = new AMove
            {
                EnginePower = EnginePower,
                IsBrake = IsBrake,
                IsUseNitro = IsUseNitro,
                Times = Times,
                SafeMargin = SafeMargin,
                ExactlyMargin = ExactlyMargin,
                ExtraMargin = ExtraMargin,
                RangesMode = RangesMode,
            };
            if (WheelTurn is Point)
                ret.WheelTurn = (WheelTurn as Point).Clone();
            else if (WheelTurn is TurnPattern)
                ret.WheelTurn = WheelTurn;
            else
                ret.WheelTurn = Convert.ToDouble(WheelTurn);
            return ret;
        }

        public void Apply(Move move, ACar car)
        {
            move.EnginePower = EnginePower;
            move.IsBrake = IsBrake;
            move.WheelTurn = WheelTurn is Point ? MyStrategy.TurnRound(car.GetAngleTo(WheelTurn as Point)) : Convert.ToDouble(WheelTurn);

            if (EnginePower < 0 && car.EnginePower > 0)
                move.IsBrake = true;

            if (car.EnginePower < 0 && EnginePower > 0)
            {
                move.WheelTurn *= -1;
                move.IsBrake = true;
            }
            
            if (IsUseNitro)
                move.IsUseNitro = IsUseNitro;
        }

        public static bool ModelMove(ACar car, AMove m, PassedInfo total,
            ABonus[] bonusCandidates, AOilSlick[] slickCandidates, AProjectile[][] projCandidates, ACar[][] carCandidates)
        {
            double prevStateX = 0, prevStateY = 0, prevStateAngle = 0;
            if (m.RangesMode)
            {
                prevStateX = car.X;
                prevStateY = car.Y;
                prevStateAngle = car.Angle;   
            }

            var turn = m.WheelTurn is Point ? MyStrategy.TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            var isBreak = m.IsBrake;

            // если сдаю назад но кочусь вперед
//            if (!isBreak && m.EnginePower < 0 && Math.Abs(Geom.GetAngleBetween(Point.ByAngle(car.Angle), car.Speed)) < Math.PI/3)
            if (m.EnginePower < 0 && car.EnginePower > 0)
                isBreak = true;

            // если еду вперед но кочусь назад
            //if (car.EnginePower < 0 && Math.Abs(Geom.GetAngleBetween(Point.ByAngle(car.Angle), car.Speed)) > Math.PI/2)
            if (car.EnginePower < 0 && m.EnginePower > 0)
            {
                turn *= -1;
                isBreak = true;
            }

            car.Move(m.EnginePower, turn, isBreak, m.IsUseNitro, false);

            for(var i = 0; i < bonusCandidates.Length; i++)
            {
                if (total.Bonuses[i]) // бонус уже взят
                    continue;

                var bonus = bonusCandidates[i];
                if (car.TakeBonus(bonus))
                {
                    total.Importance += bonus.GetImportance(car.Original)*BonusImportanceCoeff;
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
                        total.Importance -= slick.GetDanger()*OilSlickDangerCoeff*(car.RemainingNitroTicks > 0 ? 2 : 1);
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
                        total.Importance -= proj.GetDanger()*ProjectileDangerCoeff; //TODO: обработать шину отдельно
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
                        total.Importance -= InactiveCarDangerCoeff;
                        total.Cars = true;
                        break;
                    }
                }
            }

            total.Time++;

            // проверка на стены
            var res = car.GetRect().All(p => !MyStrategy.IntersectTail(p, m.SafeMargin));

            // проверка что можно проехать точно возле стены
            if (!res && car.RemainingNitroTicks == 0 && car.GetRect().All(p => !MyStrategy.IntersectTail(p, m.ExactlyMargin)))
            {
                if (!total.ExactlyBorder)
                    total.Importance -= ExactlyBorderDangerCoeff;
                total.ExactlyBorder = true;
                res = true;
            }

            // проверка что можно потереться вдоль стены
            if (!m.RangesMode && !res && car.RemainingNitroTicks == 0 && car.GetRect().All(p => !MyStrategy.IntersectTail(p, m.ExtraMargin)))
            {
                if (!total.OutOfBoreder)
                    total.Importance -= OutOfBorderDangerCoeff;
                total.OutOfBoreder = true;
                res = true;
            }

            if (!res && m.RangesMode)
            {
                res = true;

                // HACK
                car.X = prevStateX;
                car.Y = prevStateY;
                car.Angle = prevStateAngle;
                car.Speed = Point.Zero;
                car.AngularSpeed = 0;
            }
            return res;
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
