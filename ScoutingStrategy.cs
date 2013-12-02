using System;
using System.Collections;
using System.Globalization;
using System.Linq;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private static Point circle_pos;
        private static Point circle_end_pos;
        private static ArrayList circle_stack = new ArrayList();
        private static ArrayList circle_best_stack;
        private static double circle_best_profit;
        private static bool[,,] circle_visible;
        private static double[,] circle_visible_profit;
        private static Point[] circle_extra_objects;

        private void SetVisible(double visionRange, TrooperStance stance, int x, int y)
        {
            for (var i = 0; i < Width; i++)
            {
                for (var j = 0; j < Height; j++)
                {
                    circle_visible[0, i, j] |= world.IsVisible(visionRange, x, y, stance, i, j, TrooperStance.Prone);
                    circle_visible[1, i, j] |= world.IsVisible(visionRange, x, y, stance, i, j, TrooperStance.Kneeling);
                    circle_visible[2, i, j] |= world.IsVisible(visionRange, x, y, stance, i, j, TrooperStance.Standing);
                }
            }
        }

        private void SetVisibleProfit(int x, int y)
        {
            var position = new Point(x, y);
            for(var i = 0; i < Width; i++)
                for (var j = 0; j < Height; j++)
                    if (x != i || y != j)
                        circle_visible_profit[i, j] += 1.0/position.GetDistanceTo(i, j);
        }

        private double getCircleProfit()
        {
            // circle_visible initializing
            for (var i = 0; i < Width; i++)
                for (var j = 0; j < Height; j++)
                    for (var k = 0; k < 3; k++)
                        circle_visible[k, i, j] = false;
            foreach (var tr in Team)
                SetVisible(tr.VisionRange, tr.Stance, tr.X, tr.Y);

            // profit calculation
            foreach (Move mv in circle_stack)
                SetVisible(self.VisionRange, self.Stance, mv.X, mv.Y);
            double profit = 0;
            for (var i = 0; i < Width; i++)
                for (var j = 0; j < Height; j++)
                    for(var k = 0; k < 3; k++)
                        if (circle_visible[k, i, j])
                            profit += circle_visible_profit[i, j] / 3.0;
            return profit;
        }

        private void circle_dfs(int actionPoints)
        {
            if (Equal(circle_pos, circle_end_pos))
            {
                var profit = getCircleProfit();
                if (profit > circle_best_profit)
                {
                    circle_best_profit = profit;
                    circle_best_stack = circle_stack.Clone() as ArrayList;
                }
            }
            if (actionPoints >= GetMoveCost())
            {
                for (var k = 0; k < 5; k++)
                {
                    var ni = circle_pos.X + _i_[k];
                    var nj = circle_pos.Y + _j_[k];
                    if (ni >= 0 && nj >= 0 && ni < Width && nj < Height && map[ni, nj] == 0)
                    {
                        circle_stack.Add(new Move { Action = ActionType.Move, X = ni, Y = nj });
                        circle_pos.X += _i_[k];
                        circle_pos.Y += _j_[k];
                        circle_dfs(actionPoints - GetMoveCost());
                        circle_pos.X -= _i_[k];
                        circle_pos.Y -= _j_[k];
                        circle_stack.RemoveAt(circle_stack.Count - 1);
                    }
                }
            }
        }

        public Move GetScoutingMove(Point goal, Point[] objects)
        {
            circle_end_pos = new Point(goal);
            circle_pos = new Point(self);
            circle_stack.Clear();
            circle_best_stack = null;
            circle_best_profit = -Inf;
            circle_extra_objects = objects;

            if (circle_visible == null)
                circle_visible = new bool[3, Width, Height];
            if (circle_visible_profit == null)
                circle_visible_profit = new double[Width, Height];

            // circle_visible_profit initializing
            for (var i = 0; i < Width; i++)
                for (var j = 0; j < Height; j++)
                    circle_visible_profit[i, j] = 0.01;
            foreach (var tr in Opponents)
                SetVisibleProfit(tr.X, tr.Y);
            foreach (var obj in circle_extra_objects)
                SetVisibleProfit(obj.X, obj.Y);

            map[self.X, self.Y] = 0;
            circle_dfs(self.ActionPoints);
            map[self.X, self.Y] = 1;
            if (circle_best_stack == null || circle_best_stack.Count < 1)
                return null;
            return circle_best_stack[0] as Move;
        }


        Point GoScouting(Point goal, Point lookAt)
        {
            var result = GetScoutingMove(goal, new []{lookAt});
            if (result == null)
            {
                // Значит нужно идти не обходным путем, а кратчайшим
                return GoToUnit(self, goal, map, beginFree: true, endFree: false);
            }
            return new Point(result.X, result.Y);
        }
    }
}