using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point GetDefendPos2(Hockeyist self, Hockeyist friend)
        {
            var y = RinkCenter.Y;
            const double offset = 0.09;
            return new Point(MyLeft() ? Game.RinkLeft + RinkWidth * offset : Game.RinkRight - RinkWidth * offset, y);
            //var a = new Point(MyLeft() ? Game.RinkLeft + RinkWidth * offset : Game.RinkRight - RinkWidth * offset, y - 1.5 * HoRadius);
            //var b = new Point(MyLeft() ? Game.RinkLeft + RinkWidth * offset : Game.RinkRight - RinkWidth * offset, y + 1.5 * HoRadius);
            //return GetTicksTo(a, self) < GetTicksTo(b, self) ? a : b;
        }

        public void StayOn(Hockeyist self, Point to, double needAngle)
        {
            if (to.GetDistanceTo(self) < 150)
            {
                if (FindPath(self, to, AngleNormalize(needAngle + self.Angle), Get(OppGoalie)))
                    return;
            }
            move.Turn = self.GetAngleTo(to.X, to.Y);
            move.SpeedUp = GetSpeedTo(move.Turn);
        }



        private double defend_maxProbab;
        private APuck[] defend_puckState;
        private int defend_spUps = 1;
        private int defend_angles = 3;
        private int defend_maxDeep = 9;
        private int defend_sp_dir;
        private int defend_turn_dir;
        private ArrayList defend_stack = new ArrayList();
        private ArrayList defend_best_stack = new ArrayList();

        private void defend_Dfs(AHock hock, int deep, double maxSpUp, double maxTurn)
        {
            if (deep > defend_maxDeep)
                return;
            var pk = defend_puckState[deep];
            if (MyRight() && pk.X > Game.RinkRight - HoRadius || MyLeft() && pk.X < Game.RinkLeft + HoRadius)
                return;
            if (CanStrike(hock, pk))
            {
                var p = 1.0/pk.Speed.Length;
                if (p > defend_maxProbab)
                {
                    defend_maxProbab = p;
                    defend_best_stack = (ArrayList)defend_stack.Clone();
                }
                return;
            }
            for (var angle = 0.0;
                angle <= maxTurn;
                angle += TurnRange(hock.BaseParams.Agility) / defend_angles)
            {
                for (var spUp = 0.0; spUp <= maxSpUp; spUp += 1.0 / defend_spUps)
                {
                    var ho2 = hock.Clone();
                    defend_stack.Add(new Pair<double, double>(spUp*defend_sp_dir, angle*defend_turn_dir));
                    ho2.Move(spUp * defend_sp_dir, angle * defend_turn_dir);
                    defend_Dfs(ho2, deep + 1, spUp, angle);
                    Pop(defend_stack);
                }
            }
        }

        public bool Defend(Hockeyist ho, bool tmp)
        {
            if (!tmp)
                return false;
            defend_puckState = new APuck[defend_maxDeep + 1];
            defend_puckState[0] = new APuck(Get(puck), GetSpeed(puck), Get(MyGoalie));
            for (var i = 1; i <= defend_maxDeep; i++)
            {
                defend_puckState[i] = defend_puckState[i - 1].Clone();
                defend_puckState[i].Move(1);
            }
            defend_maxProbab = 0.0;
            defend_best_stack.Clear();
            for (defend_sp_dir = -1; defend_sp_dir <= 1; defend_sp_dir += 2)
            {
                for (defend_turn_dir = -1; defend_turn_dir <= 1; defend_turn_dir += 2)
                {
                    defend_stack.Clear();
                    defend_Dfs(new AHock(ho), 0, 1.0, TurnRange(ho.Agility));       
                }
            }
            if (defend_best_stack.Count == 0)
                return false;
            move.SpeedUp = (defend_best_stack[0] as Pair<double, double>).First;
            move.Turn = (defend_best_stack[0] as Pair<double, double>).Second;
            return true;
        }
    }
}