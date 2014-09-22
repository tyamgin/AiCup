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
        private double defend_maxProbab;
        private APuck[] defend_puckState;
        private int defend_spUps = 1;
        private int defend_angles = 3;
        private int defend_maxDeep = 9;
        private int defend_sp_dir;
        private int defend_turn_dir;
        private ArrayList defend_stack = new ArrayList();
        private ArrayList defend_best_stack = new ArrayList();

        private void defend_Dfs(AHo ho, int deep, double maxSpUp, double maxTurn)
        {
            if (deep > defend_maxDeep)
                return;
            var pk = defend_puckState[deep];
            if (MyRight() && pk.X > Game.RinkRight - HoRadius || MyLeft() && pk.X < Game.RinkLeft + HoRadius)
                return;
            if (CanStrike(ho, pk))
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
                angle += Game.HockeyistTurnAngleFactor/defend_angles)
            {
                for (var spUp = 0.0; spUp <= maxSpUp; spUp += 1.0 / defend_spUps)
                {
                    var ho2 = ho.Clone();
                    defend_stack.Add(new Pair<double, double>(spUp*defend_sp_dir, angle*defend_turn_dir));
                    ho2.Move(spUp * defend_sp_dir, angle * defend_turn_dir);
                    defend_Dfs(ho2, deep + 1, spUp, angle);
                    Pop(defend_stack);
                }
            }
        }

        public bool Defend(Hockeyist ho, bool tmp)
        {
            throw new NotImplementedException();
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
                    defend_Dfs(new AHo(Get(ho), GetSpeed(ho), ho.Angle, ho.AngularSpeed, ho), 0, 1.0, Game.HockeyistTurnAngleFactor);       
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