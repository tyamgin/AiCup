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
        public object WheelTurn; // Point or double
        public bool IsUseNitro;

        public int Times;

        public AMove Clone()
        {
            return new AMove
            {
                EnginePower = EnginePower,
                IsBrake = IsBrake,
                WheelTurn = WheelTurn,
                IsUseNitro = IsUseNitro,
                Times = Times,
            };
        }

        public void Apply(Move move, ACar self)
        {
            move.EnginePower = EnginePower;
            move.IsBrake = IsBrake;
            move.WheelTurn = WheelTurn is Point ? MyStrategy.TurnRound(self.GetAngleTo(WheelTurn as Point)) : Convert.ToDouble(WheelTurn);
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
