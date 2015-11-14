using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Permissions;
using System.Text;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AMove
    {
        public double EnginePower;
        public bool IsBrake;
        public double WheelTurn;
        public bool IsThrowProjectile;
        public bool IsUseNitro;
        public bool IsSpillOil;

        public int Times;
        public bool NondetermWheelTurn;

        public AMove Clone()
        {
            return new AMove
            {
                EnginePower = EnginePower,
                IsBrake = IsBrake,
                WheelTurn = WheelTurn,
                IsThrowProjectile = IsThrowProjectile,
                IsUseNitro = IsUseNitro,
                IsSpillOil = IsSpillOil,
                Times = Times,
                NondetermWheelTurn = NondetermWheelTurn,
            };
        }

        public void Apply(Move move, ACar self, Point target)
        {
            move.EnginePower = EnginePower;
            move.IsBrake = IsBrake;
            move.WheelTurn = NondetermWheelTurn ? MyStrategy.TurnRound(self.GetAngleTo(target)) : WheelTurn;
            move.IsThrowProjectile = IsThrowProjectile;
            move.IsUseNitro = IsUseNitro;
            move.IsSpillOil = IsSpillOil;
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
            this.RemoveAll(m => m.Times == 0);
        }

        public int ComputeTime()
        {
            return this.Select(m => m.Times).Sum();
        }

        public void Pop()
        {
            this.RemoveAt(Count - 1);
        }
    }
}
