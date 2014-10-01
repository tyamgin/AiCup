using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Windows.Forms;
using System.Xml.XPath;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private Point fp_to;
        private double fp_need;

        private int fp_per;
        private double fp_okDist;
        private double fp_okAngle;
        private int fp_minTime;
        private int fp_spUps = 1;
        private int fp_maxDeep = 6;
        private int fp_sp_dir;
        private int fp_turn_dir;
        private ArrayList fp_stack = new ArrayList();
        private ArrayList fp_best_stack = new ArrayList();
        private double[][] fp_turns = {
            new [] {-1.0, -0.5, 0.0, 0.5, 1.0}, 
            new [] {0.0}, 
            new [] {1.0, 0.5, 0.0, -0.5, -1.0}
        };

        private void fp_Dfs(AHock hock, int time, int deep, double maxSpUp, int maxTurn)
        {
            if (deep > fp_maxDeep)
                return;
            if (time/* + Math.Min(GetTicksToUp(ho, fp_to, fp_okDist), GetTicksToDown(ho, fp_to, fp_okDist))*/ > fp_minTime)
                // TODO: + оценка снизу
            {
                return;
            }

            if (hock.GetDistanceTo2(fp_to) < fp_okDist*fp_okDist && Math.Abs(AngleNormalize(fp_need - hock.Angle)) < fp_okAngle)
            {
                fp_minTime = time;
                fp_best_stack = fp_stack.Clone() as ArrayList;
                return;
            }

            for (var i = 0; i <= maxTurn; i++)
            {
                var angle = fp_turns[fp_turn_dir + 1][i] * TurnRange(hock.BaseParams.Agility);
                for (var spUp = maxSpUp; spUp >= 0; spUp -= 1.0 / fp_spUps)
                {
                    var ho2 = hock.Clone();
                    fp_stack.Add(new Tuple<double, double, int>(spUp * fp_sp_dir, angle, fp_per));
                    ho2.Move(spUp * fp_sp_dir, angle, fp_per);
                    fp_Dfs(ho2, time + fp_per, deep + 1, spUp, i);
                    Pop(fp_stack);
                }
            }
        }
        public bool FindPath(Hockeyist self, Point to, double needAngle, Point goalie)
        {
            fp_minTime = Inf;
            fp_best_stack.Clear();
            fp_to = to;
            fp_need = needAngle;
            fp_okDist = HoRadius*1.3;
            fp_per = (int)(self.GetDistanceTo(to.X, to.Y)/fp_maxDeep + 2);
            fp_okAngle = self.GetDistanceTo(to.X, to.Y) < 70 ? Deg(5) : Deg(15);
            var state = new AHock(self);
            for (fp_sp_dir = -1; fp_sp_dir <= 1; fp_sp_dir += 2)
            {
                for (fp_turn_dir = 1; fp_turn_dir >= -1; fp_turn_dir -= 2)
                {
                    fp_stack.Clear();
                    fp_Dfs(state, 0, 0, 1.0, fp_turns[0].Count() - 1);
                }
            }
            if (fp_minTime == Inf)
                return false;
            if (fp_best_stack.Count > 0)
            {
                var ac = fp_best_stack[0] as Tuple<double, double, int>;
                move.SpeedUp = ac.First;
                move.Turn = ac.Second;
            }
            return true;
        }
    }
}
